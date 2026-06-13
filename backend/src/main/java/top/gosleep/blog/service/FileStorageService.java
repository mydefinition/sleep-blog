package top.gosleep.blog.service;

import org.springframework.web.multipart.MultipartFile;
import top.gosleep.blog.bean.entity.FileStorage;

import java.io.IOException;
import java.nio.file.Path;

/**负责本地文件存储  */
public interface FileStorageService {

    FileStorage queryById(Long fileId);

    FileStorage store(MultipartFile file) throws IOException;

    Path getPath(FileStorage fileStorage);

    void deleteById(Long fileId);
}