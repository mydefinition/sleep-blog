package top.gosleep.blog.controller;

import top.gosleep.blog.common.Result;
import top.gosleep.blog.context.UserContext;
import top.gosleep.blog.dto.ArticleDto;
import top.gosleep.blog.dto.ArticleListDto;
import top.gosleep.blog.dto.request.ArticleRequest;
import top.gosleep.blog.service.ArticleService;
import com.baomidou.mybatisplus.core.metadata.IPage;
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

    @GetMapping
    @Operation(summary = "获取文章列表")
    public Result<?> list(@RequestParam(required = false) String tagIds,
                          @RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size) {
        IPage<ArticleListDto> result = articleService.list(tagIds, page, size);
        return Result.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取文章详情")
    public Result<?> getById(@PathVariable Long id) {
        ArticleDto article = articleService.getById(id);
        return Result.ok(article);
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
