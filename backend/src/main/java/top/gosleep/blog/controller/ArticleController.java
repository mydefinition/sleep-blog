package top.gosleep.blog.controller;

import org.springframework.web.multipart.MultipartFile;
import top.gosleep.blog.bean.dto.request.ArticleRequest;
import top.gosleep.blog.bean.entity.User;
import top.gosleep.blog.bean.vo.ArticleDetailVO;
import top.gosleep.blog.common.Result;
import top.gosleep.blog.common.context.SessionContext;
import top.gosleep.blog.common.context.UserContext;
import top.gosleep.blog.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import top.gosleep.blog.service.ImageService;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/article")
@Tag(name = "文章")
public class ArticleController {

    private final ArticleService articleService;
    private final ImageService imageService;

    public ArticleController(ArticleService articleService, ImageService imageService) {
        this.articleService = articleService;
        this.imageService = imageService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取文章详情")
    public Result<ArticleDetailVO> getById(@PathVariable Long id) {
        return Result.ok(articleService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建文章")
    public Result<ArticleDetailVO> create(@RequestBody ArticleRequest req) {
        return Result.ok(articleService.create(req));
    }

    @PutMapping("/{id}")
    @Operation(summary = "编辑文章")
    public Result<ArticleDetailVO> update(@PathVariable Long id, @RequestBody ArticleRequest req) {
        articleService.update(id, req);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除文章")
    public Result<?> deleteArticle(@PathVariable Long id) {
        articleService.delete(id);
        return Result.ok();
    }

    @PostMapping("/upload")
    @Operation(summary = "上传文章图片")
    public Result<?> upload(@RequestParam MultipartFile file, Long articleId) throws IOException {
        UserContext.requireAdmin();
        String url = imageService.upload(articleId, file);
        return Result.ok(Map.of("url", url));
    }
}
