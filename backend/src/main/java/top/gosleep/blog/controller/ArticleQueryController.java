package top.gosleep.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import top.gosleep.blog.bean.vo.ArticleQueryVO;
import top.gosleep.blog.bean.vo.CategoryVO;
import top.gosleep.blog.bean.vo.PageResult;
import top.gosleep.blog.bean.vo.TagVO;
import top.gosleep.blog.common.Result;
import top.gosleep.blog.service.ArticleQueryService;
import top.gosleep.blog.service.CategoryService;
import top.gosleep.blog.service.TagService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/query")
@Tag(name = "文章查询")
public class ArticleQueryController {
    private final ArticleQueryService articleQueryService;
    private final CategoryService categoryService;
    private final TagService tagService;

    public ArticleQueryController(ArticleQueryService articleQueryService, CategoryService categoryService, TagService tagService) {
        this.articleQueryService = articleQueryService;
        this.categoryService = categoryService;
        this.tagService = tagService;
    }

    @GetMapping("/articles")
    @Operation(summary = "获取文章列表页")
    public Result<PageResult<ArticleQueryVO>> getPublishPage(
            @RequestParam(value = "categoryId", defaultValue = "0") Long categoryId,
            @RequestParam(value = "tagIds", required = false) List<Long> tagIds,
            @RequestParam(value = "page", defaultValue = "0") Long page,
            @RequestParam(value = "size", defaultValue = "10") Long size,
            @RequestParam(value = "and", defaultValue = "false") boolean and) {
        if (tagIds == null)
            tagIds = new ArrayList<>();
        return Result.ok(articleQueryService.queryPublishArticles(categoryId, tagIds, page, size, and));
    }

    @GetMapping("/articles/{id}")
    @Operation(summary = "获取个人文章列表页")
    public Result<PageResult<ArticleQueryVO>> getPersonPage(
            @PathVariable Long id,
            @RequestParam(value = "isPublished", required = false) Integer isPublished,
            @RequestParam(value = "categoryId", defaultValue = "0") Long categoryId,
            @RequestParam(value = "tagIds", required = false) List<Long> tagIds,
            @RequestParam(value = "page", defaultValue = "1") Long page,
            @RequestParam(value = "size", defaultValue = "10") Long size,
            @RequestParam(value = "and", defaultValue = "false") boolean and) {
        if (tagIds == null)
            tagIds = new ArrayList<>();
        return Result.ok(articleQueryService.queryPersonArticles(id, isPublished, categoryId, tagIds, page, size, and));
    }

    @GetMapping("/tags")
    @Operation(summary = "获取标签列表")
    public Result<List<TagVO>> tagList() {
        return Result.ok(TagVO.from(tagService.listAll()));
    }

    @GetMapping("/categories")
    @Operation(summary = "获取分类列表")
    public Result<CategoryVO> categoryList() {
        return Result.ok(categoryService.list());
    }

}
