package top.gosleep.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.gosleep.blog.bean.entity.CloudFile;
import top.gosleep.blog.bean.entity.FileStorage;
import top.gosleep.blog.bean.vo.CloudFileVO;
import top.gosleep.blog.common.BusinessException;
import top.gosleep.blog.common.ResultCode;
import top.gosleep.blog.common.context.UserContext;
import top.gosleep.blog.mapper.CloudFileMapper;
import top.gosleep.blog.service.CloudStorageService;
import top.gosleep.blog.service.FileStorageService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@Service
public class CloudStorageServiceImpl implements CloudStorageService  {

    private final FileStorageService fileStorageService;
    private final CloudFileMapper cloudFileMapper;

    public CloudStorageServiceImpl(FileStorageService fileStorageService, CloudFileMapper cloudFileMapper) {
        this.fileStorageService = fileStorageService;
        this.cloudFileMapper = cloudFileMapper;
    }

    @Override
    public CloudFileVO upload(MultipartFile file, Long localId) throws IOException {
        UserContext.requireAdmin();

        CloudFile parent = cloudFileMapper.selectById(localId);
        if (parent == null || parent.getIsDir().equals(0))
            throw new BusinessException(ResultCode.BAD_REQUEST, "父目录不存在 id=" + localId);

        CloudFile cloudFile = cloudFileMapper.selectOne(new LambdaQueryWrapper<CloudFile>()
                .eq(CloudFile::getParentId, localId)
                .eq(CloudFile::getName, file.getOriginalFilename()));
        if (cloudFile != null && cloudFile.getIsDir().equals(0))
            throw new BusinessException(ResultCode.BAD_REQUEST, "文件已存在");

        FileStorage recode = fileStorageService.store(file);
        cloudFile = new CloudFile();
        cloudFile.setName(file.getOriginalFilename());
        cloudFile.setFileId(recode.getId());
        cloudFile.setIsDir(0);
        cloudFile.setParentId(localId);
        cloudFile.setOwnerId(UserContext.getUser().getId());
        cloudFileMapper.insert(cloudFile);
        return CloudFileVO.from(cloudFile);
    }

    @Override
    public CloudFileVO mkdir(Long localId, String dirName) {
        UserContext.requireAdmin();

        CloudFile parent = cloudFileMapper.selectById(localId);
        if (parent == null || parent.getIsDir().equals(0))
            throw new BusinessException(ResultCode.BAD_REQUEST, "父目录不存在 id=" + localId);

        CloudFile cloudFile = cloudFileMapper.selectOne(new LambdaQueryWrapper<CloudFile>()
                .eq(CloudFile::getParentId, localId)
                .eq(CloudFile::getName, dirName));
        if (cloudFile != null && cloudFile.getIsDir().equals(1))
            throw new BusinessException(ResultCode.BAD_REQUEST, "目录已存在");

        cloudFile = new CloudFile();
        cloudFile.setOwnerId(UserContext.getUser().getId());
        cloudFile.setIsDir(1);
        cloudFile.setParentId(localId);
        cloudFile.setName(dirName);
        cloudFileMapper.insert(cloudFile);
        return CloudFileVO.from(cloudFile);
    }

    @Override
    public void delete(Long localId) {
        CloudFile cloudFile = cloudFileMapper.selectById(localId);
        if (cloudFile == null)
            throw new BusinessException(ResultCode.NOT_FOUND, "文件不存在");
        delete(cloudFile);
    }

    private void delete(CloudFile cloudFile) {
        if (cloudFile.getIsDir().equals(0)) {
            UserContext.requireAdminId(cloudFile.getOwnerId());

            cloudFileMapper.deleteById(cloudFile);
            fileStorageService.deleteById(cloudFile.getFileId());
        } else {
            List<CloudFile> children = list(cloudFile.getId());
            for (CloudFile child : children)
                delete(child);
            cloudFileMapper.deleteById(cloudFile);
        }
    }

    @Override
    public ResponseEntity<Resource> getFile(Long localId) {
        try {
            CloudFile cloudFile = cloudFileMapper.selectById(localId);
            if (cloudFile == null || cloudFile.getIsDir().equals(1))
                throw new BusinessException(ResultCode.NOT_FOUND, "文件不存在 id=" + localId);
            FileStorage fileStorage = fileStorageService.queryById(cloudFile.getFileId());
            Resource resource = new UrlResource(fileStorageService.getPath(fileStorage).toUri());
            if (!resource.exists())
                return ResponseEntity.notFound().build();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + cloudFile.getName() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR);
        }
    }

    @Override
    public List<CloudFile> list(Long localId) {
        CloudFile parent = cloudFileMapper.selectById(localId);
        if (parent == null || parent.getIsDir().equals(0))
            throw new BusinessException(ResultCode.NOT_FOUND, "目录不存在 id=" + localId);
        List<CloudFile> res = cloudFileMapper.selectList(new LambdaQueryWrapper<CloudFile>().eq(CloudFile::getParentId, localId));
        if (localId == 0)
            res = res.stream().filter(cloudFile -> !cloudFile.getId().equals(0L)).toList();
        return res;
    }

    @Override
    public List<CloudFileVO> listVO(Long localId) {
        CloudFile parent = cloudFileMapper.selectById(localId);
        if (parent == null || parent.getIsDir().equals(0))
            throw new BusinessException(ResultCode.NOT_FOUND, "目录不存在 id=" + localId);
        List<CloudFileVO> res = cloudFileMapper.queryByParentId(localId);
        if (localId == 0)
             res = res.stream().filter(cloudFile -> !cloudFile.getId().equals(0L)).toList();
        return res;
    }
}
