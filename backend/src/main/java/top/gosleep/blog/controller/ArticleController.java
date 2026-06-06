package top.gosleep.blog.controller;

import top.gosleep.blog.common.Result;
import top.gosleep.blog.context.UserContext;
import top.gosleep.blog.bean.dto.request.ArticleRequest;
import top.gosleep.blog.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")
@Tag(name = "文章管理")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }
    
    @GetMapping("/checklist")
    @Operation(summary = "检查文章列表更新")
    public Result<?> checkList(@RequestParam(defaultValue = "0") long token) {
        return Result.ok(articleService.checkList(token));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取文章详情")
    public Result<?> getById(@PathVariable Long id) {
        return Result.ok(articleService.getById(id));
    }

    @PostMapping
    @Operation(summary = "发布文章")
    public Result<?> create(@RequestBody ArticleRequest req) {
        UserContext.requireAdmin();
        articleService.create(UserContext.getCurrentUser().getId(), req);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @Operation(summary = "编辑文章")
    public Result<?> update(@PathVariable Long id, @RequestBody ArticleRequest req) {
        UserContext.requireAdmin();
        articleService.update(id, req);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除文章")
    public Result<?> deleteArticle(@PathVariable Long id) {
        UserContext.requireAdmin();
        articleService.delete(id);
        return Result.ok();
    }
}
