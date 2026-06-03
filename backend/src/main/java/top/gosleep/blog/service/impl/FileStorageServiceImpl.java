package top.gosleep.blog.service.impl;

import top.gosleep.blog.common.PathUtil;
import top.gosleep.blog.common.BusinessException;
import top.gosleep.blog.common.ResultCode;
import top.gosleep.blog.entity.FileStorage;
import top.gosleep.blog.entity.User;
import top.gosleep.blog.mapper.FileStorageMapper;
import top.gosleep.blog.mapper.UserMapper;
import top.gosleep.blog.model.FileTreeNode;
import top.gosleep.blog.service.FileStorageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Value("${app.storage.filestorage-root}")
    private String baseDir;

    private final FileStorageMapper mapper;
    private final UserMapper userMapper;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock rLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock wLock = lock.writeLock();

    /** id=0 根节点 */
    private volatile FileTreeNode root;
    /** 缓存：id → FileTreeNode */
    private volatile Map<Long, FileTreeNode> cache = new HashMap<>();
    /** 缓存 JSON */
    private volatile String cachedJson;

    public FileStorageServiceImpl(FileStorageMapper mapper, UserMapper userMapper) {
        this.mapper = mapper;
        this.userMapper = userMapper;
    }

    @PostConstruct
    void init() {
        baseDir = PathUtil.absolute(baseDir);
        reload();
    }

    /* ========== 缓存 ========== */

    private void reload() {
        wLock.lock();
        try {
            root = new FileTreeNode(0L, "", true);
            Map<Long, FileTreeNode> newCache = new HashMap<>();
            newCache.put(0L, root);

            List<FileStorage> all = mapper.selectList(null);
            for (FileStorage fs : all) {
                if (fs.getId() == 0) continue;
                FileTreeNode node = toNode(fs);
                newCache.put(fs.getId(), node);
            }
            for (FileStorage fs : all) {
                if (fs.getId() == 0) continue;
                FileTreeNode node = newCache.get(fs.getId());
                Long pid = fs.getLocalId() != null ? fs.getLocalId() : 0L;
                FileTreeNode parent = newCache.get(pid);
                if (parent != null) {
                    if (parent.children == null) parent.children = new ArrayList<>();
                    parent.children.add(node);
                }
            }
            buildPath(root, "");
            sortTree(root.children);
            cache = newCache;
            cachedJson = buildJson();
        } finally {
            wLock.unlock();
        }
    }

    private void buildPath(FileTreeNode node, String parentPath) {
        if (node.id != 0) {
            node.path = parentPath + "/" + node.name;
        }
        if (node.children != null) {
            String childParent = node.id == 0 ? "" : node.path;
            for (FileTreeNode child : node.children) {
                buildPath(child, childParent);
            }
        }
    }

    private void sortTree(List<FileTreeNode> nodes) {
        if (nodes == null) return;
        nodes.sort(Comparator.comparing((FileTreeNode n) -> !n.isDir)
                .thenComparing(n -> n.name));
        for (FileTreeNode n : nodes) {
            if (n.children != null) sortTree(n.children);
        }
    }

    private FileTreeNode toNode(FileStorage fs) {
        FileTreeNode n = new FileTreeNode(fs.getId(), fs.getName(), fs.getIsDir());
        n.size = fs.getSize();
        n.hash = fs.getHash();
        n.uploaderName = fs.getUserId() != null ? getUsername(fs.getUserId()) : null;
        n.uploadAt = fs.getUploadAt() != null
                ? fs.getUploadAt().format(DateTimeFormatter.ISO_LOCAL_DATE)
                : null;
        return n;
    }

    private String getUsername(Long userId) {
        User u = userMapper.selectById(userId);
        return u != null ? u.getUsername() : null;
    }

    private String buildJson() {
        try {
            return MAPPER.writeValueAsString(root.children);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    /* ========== 公开方法 ========== */

    @Override
    public String getTreeJson() {
        String json = cachedJson;
        if (json != null) return json;
        rLock.lock();
        try {
            json = cachedJson;
            if (json != null) return json;
            cachedJson = buildJson();
            return cachedJson;
        } finally {
            rLock.unlock();
        }
    }

    @Override
    @Transactional
    public FileTreeNode upload(Long userId, MultipartFile file, Long localId) throws IOException {
        // 校验目标存在且是目录
        FileTreeNode parent = validateDir(localId);

        String originalName = file.getOriginalFilename();
        String name = originalName != null ? originalName : "file";

        // 重名校验（DB）
        checkDuplicateName(name, localId);

        // SHA-256
        String sha256 = sha256(file.getBytes());

        // 磁盘路径
        String dirRel = localId == null || localId == 0 ? "" : parent.path;
        Path dir = Paths.get(baseDir, dirRel);
        Files.createDirectories(dir);
        Path target = dir.resolve(name);
        // 磁盘重名校验（双重保险）
        if (Files.exists(target))
            throw new RuntimeException("文件已存在: " + dirRel + "/" + name);
        file.transferTo(target.toFile());

        // 写库
        FileStorage fs = new FileStorage();
        fs.setName(name);
        fs.setIsDir(false);
        fs.setSize(file.getSize());
        fs.setLocalId(localId == null || localId == 0 ? 0L : localId);
        fs.setHash(sha256);
        fs.setUserId(userId);
        fs.setUploadAt(LocalDateTime.now());
        mapper.insert(fs);

        // 更新缓存
        FileTreeNode node = toNode(fs);
        node.path = (dirRel.isEmpty() ? "" : dirRel) + "/" + name;

        wLock.lock();
        try {
            cache.put(fs.getId(), node);
            addChildToCache(parent, node);
            sortTree(parent.children);
            cachedJson = null;
        } finally {
            wLock.unlock();
        }

        return node;
    }

    @Override
    @Transactional
    public FileTreeNode mkdir(String name, Long localId) {
        FileTreeNode parent = validateDir(localId);
        checkDuplicateName(name, localId);

        String dirRel = localId == null || localId == 0 ? "" : parent.path;
        Path dir = Paths.get(baseDir, dirRel, name);
        try { Files.createDirectories(dir); } catch (IOException e) {
            throw new RuntimeException("创建文件夹失败", e);
        }

        FileStorage fs = new FileStorage();
        fs.setName(name);
        fs.setIsDir(true);
        fs.setLocalId(localId == null || localId == 0 ? 0L : localId);
        mapper.insert(fs);

        FileTreeNode node = toNode(fs);
        node.path = (dirRel.isEmpty() ? "" : dirRel) + "/" + name;
        node.children = new ArrayList<>();

        wLock.lock();
        try {
            cache.put(fs.getId(), node);
            addChildToCache(parent, node);
            sortTree(parent.children);
            cachedJson = null;
        } finally {
            wLock.unlock();
        }

        return node;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (id == null || id == 0) throw new BusinessException(ResultCode.BAD_REQUEST, "不能删除根目录");

        FileTreeNode node;
        rLock.lock();
        try { node = cache.get(id); } finally { rLock.unlock(); }

        if (node == null) throw new BusinessException(ResultCode.NOT_FOUND, "节点不存在");

        // 先收集要删的所有节点 ID
        List<Long> ids = new ArrayList<>();
        collectIds(node, ids);

        // 查父节点
        FileStorage self = mapper.selectById(id);
        Long pid = self != null && self.getLocalId() != null && self.getLocalId() != 0
                ? self.getLocalId() : 0L;

        // 删 DB
        for (Long i : ids) {
            if (i != 0) mapper.deleteById(i);
        }

        // 删磁盘（从最深开始）
        for (int i = ids.size() - 1; i >= 0; i--) {
            Long cid = ids.get(i);
            if (cid == 0) continue;
            FileTreeNode cn = cache.get(cid);
            if (cn == null) continue;
            String relPath = cn.path != null ? cn.path : "";
            try {
                Path p = Paths.get(baseDir, relPath);
                Files.deleteIfExists(p);
            } catch (IOException ignored) {}
        }

        // 更新缓存
        wLock.lock();
        try {
            for (Long i : ids) cache.remove(i);
            FileTreeNode pNode = cache.get(pid);
            if (pNode != null && pNode.children != null) {
                pNode.children.removeIf(n -> ids.contains(n.id));
            }
            cachedJson = null;
        } finally {
            wLock.unlock();
        }
    }

    private void collectIds(FileTreeNode node, List<Long> ids) {
        ids.add(node.id);
        if (node.children != null) {
            for (FileTreeNode child : node.children) collectIds(child, ids);
        }
    }

    @Override
    public FileStorage getById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public String absolutePath(Long id) {
        rLock.lock();
        try {
            FileTreeNode node = cache.get(id);
            if (node == null || node.isDir) throw new BusinessException(ResultCode.NOT_FOUND, "文件不存在");
            String rel = node.path != null ? node.path : "";
            if (rel.startsWith("/")) rel = rel.substring(1);
            return Paths.get(baseDir, rel).toString();
        } finally {
            rLock.unlock();
        }
    }

    /* ========== 内部工具 ========== */

    private FileTreeNode validateDir(Long localId) {
        if (localId == null || localId == 0) return root;
        rLock.lock();
        try {
            FileTreeNode node = cache.get(localId);
            if (node == null || !node.isDir)
                throw new BusinessException(ResultCode.NOT_FOUND, "目标目录不存在");
            return node;
        } finally {
            rLock.unlock();
        }
    }

    private void checkDuplicateName(String name, Long localId) {
        FileTreeNode parent = localId == null || localId == 0 ? root : cache.get(localId);
        if (parent != null && parent.children != null) {
            boolean exists = parent.children.stream()
                    .anyMatch(n -> n.name.equals(name));
            if (exists) throw new RuntimeException("同名文件或文件夹已存在: " + name);
        }
    }

    private void addChildToCache(FileTreeNode parent, FileTreeNode child) {
        // parent 是缓存引用，可能不是最新的。重新从 cache 取。
        FileTreeNode fresh = cache.get(parent.id);
        if (fresh == null) fresh = parent;
        if (fresh.children == null) fresh.children = new ArrayList<>();
        fresh.children.add(child);
    }

    private String sha256(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}