package top.gosleep.blog.controller;

import top.gosleep.blog.common.Result;
import top.gosleep.blog.context.UserContext;
import top.gosleep.blog.dto.TagDto;
import top.gosleep.blog.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tags")
@Tag(name = "标签管理")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    @Operation(summary = "获取所有标签")
    public Result<List<TagDto>> list() {
        return Result.ok(tagService.list());
    }

    @PostMapping
    @Operation(summary = "创建标签")
    public Result<TagDto> create(@RequestBody Map<String, Object> body) {
        UserContext.requireAdmin();
        String name = (String) body.get("name");
        return Result.ok(tagService.create(name));
    }
}