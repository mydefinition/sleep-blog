package top.gosleep.blog.service.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.gosleep.blog.bean.entity.Article;
import top.gosleep.blog.bean.entity.FileStorage;
import top.gosleep.blog.common.PathUtil;
import top.gosleep.blog.mapper.ArticleMapper;

import jakarta.annotation.PostConstruct;
import top.gosleep.blog.service.ImageService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 每隔 30 分钟扫描 10~45 分钟内更新过的文章，
 * 删除 article_image 中有记录但正文 markdown 中未引用的图片（含物理文件）。
 */
@Component
public class ImageCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(ImageCleanupScheduler.class);
    private static final Pattern IMAGE_PATTERN = Pattern.compile("!\\[[^]]*]\\(([^)]+)\\)");

    private final ImageService imageService;
    private final ArticleMapper articleMapper;

    @Value("${app.storage-path}")
    private String storagePath;
    private Path storageDir;

    public ImageCleanupScheduler(ImageService imageService, ArticleMapper articleMapper) {
        this.imageService = imageService;
        this.articleMapper = articleMapper;
    }

    @PostConstruct
    void init() {
        storagePath = PathUtil.absolute(storagePath);
        storageDir = Paths.get(storagePath);
    }

    public static Set<String> extractImageUrls(String markdown) {
        Set<String> result = new HashSet<>();
        Matcher matcher = IMAGE_PATTERN.matcher(markdown);
        while (matcher.find())
            result.add(matcher.group(1));
        return result;
    }

    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void cleanupUnusedImages() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusMinutes(45);
        LocalDateTime end = now.minusMinutes(10);

        articleMapper.selectList(
                new LambdaQueryWrapper<Article>()
                        .isNotNull(Article::getUpdatedAt)
                        .between(Article::getUpdatedAt, start, end)
        ).forEach(this::cleanupArticle);
    }

    private void cleanupArticle(Article article) {
        // 正文中引用的图片文件名集合
        Set<String> referenced = extractImageUrls(article.getContent())
                .stream()
                .map(url -> {
                    int i = url.lastIndexOf('/');
                    return i >= 0 ? url.substring(i + 1) : url;
                })
                .collect(Collectors.toSet());

        List<FileStorage> links = imageService.queryByArticleId(article.getId());
        links.stream().filter(fs -> !referenced.contains(fs.getName())).forEach(fs -> {
            imageService.deleteById(fs.getId());
            log.info("清理未引用图片: article={} file={}", article.getId(), fs.getName());
        });
    }
}