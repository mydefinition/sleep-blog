package top.gosleep.blog.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import top.gosleep.blog.bean.entity.CloudFile;
import top.gosleep.blog.bean.vo.CloudFileVO;

import java.io.IOException;
import java.util.List;

public interface CloudStorageService {

    CloudFileVO upload(MultipartFile file, Long localId) throws IOException;

    CloudFileVO mkdir(Long localId, String dirName);

    void delete(Long localId);

    ResponseEntity<Resource> getFile(Long localId);

    List<CloudFile> list(Long localId);

    List<CloudFileVO> listVO(Long localId);
}
