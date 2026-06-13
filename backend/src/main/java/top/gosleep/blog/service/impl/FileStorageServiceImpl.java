package top.gosleep.blog.service.impl;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import top.gosleep.blog.bean.entity.FileStorage;
import top.gosleep.blog.common.PathUtil;
import top.gosleep.blog.common.context.UserContext;
import top.gosleep.blog.mapper.FileStorageMapper;
import top.gosleep.blog.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageServiceImpl.class);

    private final FileStorageMapper fileStorageMapper;

    @Value("${app.storage-path}")
    private String storagePath;
    private Path storageDir;

    public FileStorageServiceImpl(FileStorageMapper fileStorageMapper) {
        this.fileStorageMapper = fileStorageMapper;
    }

    @PostConstruct
    void init() {
        storagePath = PathUtil.absolute(storagePath);
        storageDir = Paths.get(storagePath);
    }

    @Override
    public FileStorage store(MultipartFile file) throws IOException {
        UserContext.requireAdmin();

        if (!Files.exists(storageDir))
            Files.createDirectories(storageDir);
        FileStorage recode = new FileStorage();
        recode.setMineType(file.getContentType());
        String originalName = file.getOriginalFilename();
        String ext = originalName != null && originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf(".")) : "";
        String filename = UUID.randomUUID() + ext;
        recode.setName(filename);
        recode.setSize(file.getSize());
        file.transferTo(storageDir.resolve(filename).toFile());
        fileStorageMapper.insert(recode);
        return recode;
    }

    @Override
    public Path getPath(FileStorage fileStorage) {
        return Paths.get(storagePath).resolve(fileStorage.getName());
    }

    @Override
    public void deleteById(Long fileId) {
        FileStorage fs = fileStorageMapper.selectById(fileId);
        if (fs != null) {
            try {
                Files.deleteIfExists(storageDir.resolve(fs.getName()));
            } catch (IOException e) {
                log.warn("删除物理文件失败: {}", fs.getName(), e);
            }
        }
        fileStorageMapper.deleteById(fileId);
    }

    @Override
    public FileStorage queryById(Long fileId) {
        return fileStorageMapper.selectById(fileId);
    }

}