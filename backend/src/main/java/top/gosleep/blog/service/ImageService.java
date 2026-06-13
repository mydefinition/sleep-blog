package top.gosleep.blog.service;

import org.springframework.web.multipart.MultipartFile;
import top.gosleep.blog.bean.entity.FileStorage;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public interface ImageService {

    String upload(Long articleId, MultipartFile file) throws IOException;

    List<FileStorage> queryByArticleId(Long articleId);

    void deleteById(Long fileStorageId);

}
