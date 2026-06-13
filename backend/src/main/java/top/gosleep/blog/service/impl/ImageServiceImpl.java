package top.gosleep.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import top.gosleep.blog.bean.entity.ArticleImage;
import top.gosleep.blog.bean.entity.FileStorage;
import top.gosleep.blog.common.context.UserContext;
import top.gosleep.blog.config.WebMvcConfig;
import top.gosleep.blog.mapper.ArticleImageMapper;
import top.gosleep.blog.mapper.ArticleMapper;
import top.gosleep.blog.service.FileStorageService;
import top.gosleep.blog.service.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {

    private final FileStorageService fileStorageService;
    private final ArticleImageMapper articleImageMapper;
    private final ArticleMapper articleMapper;

    public ImageServiceImpl(FileStorageService fileStorageService, ArticleImageMapper articleImageMapper, ArticleMapper articleMapper) {
        this.fileStorageService = fileStorageService;
        this.articleImageMapper = articleImageMapper;
        this.articleMapper = articleMapper;
    }

    @Override
    public String upload(Long articleId, MultipartFile file) throws IOException {
        UserContext.requireAdminId(articleMapper.selectAuthor(articleId).getId());

        FileStorage recode = fileStorageService.store(file);
        ArticleImage ai = new ArticleImage(articleId, recode.getId());
        articleImageMapper.insert(ai);
        return WebMvcConfig.STORAGE_URL_PREFIX + recode.getName();
    }

    @Override
    public List<FileStorage> queryByArticleId(Long articleId) {
        return articleImageMapper.queryByArticleId(articleId);
    }

    public void deleteById(Long fileStorageId) {
        articleImageMapper.delete(
                new LambdaQueryWrapper<ArticleImage>().eq(ArticleImage::getFileId, fileStorageId)
        );
        fileStorageService.deleteById(fileStorageId);
    }

}
