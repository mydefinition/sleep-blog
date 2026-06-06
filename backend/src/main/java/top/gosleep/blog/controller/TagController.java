package top.gosleep.blog.controller;

import top.gosleep.blog.common.Result;
import top.gosleep.blog.context.UserContext;
import top.gosleep.blog.bean.vo.TagVO;
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
    public Result<List<TagVO>> list() {
        return Result.ok(tagService.list());
    }
}