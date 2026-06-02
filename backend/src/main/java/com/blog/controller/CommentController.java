package com.blog.controller;

import com.blog.common.Result;
import com.blog.context.UserContext;
import com.blog.dto.CommentDto;
import com.blog.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "评论管理")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/api/articles/{articleId}/comments")
    @Operation(summary = "获取文章评论")
    public Result<List<CommentDto>> list(@PathVariable Long articleId) {
        return Result.ok(commentService.listByArticleId(articleId));
    }

    @PostMapping("/api/articles/{articleId}/comments")
    @Operation(summary = "发表评论")
    public Result<?> create(@PathVariable Long articleId, @RequestBody Map<String, String> body) {
        Long userId = UserContext.getCurrentUser().getId();
        commentService.create(userId, articleId, body.get("content"));
        return Result.ok();
    }

    @DeleteMapping("/api/comments/{id}")
    @Operation(summary = "删除评论")
    public Result<?> deleteComment(@PathVariable Long id) {
        UserContext.requireAdmin();
        commentService.delete(id);
        return Result.ok();
    }
}
