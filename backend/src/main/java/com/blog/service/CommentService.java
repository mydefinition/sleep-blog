package com.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.dto.CommentDto;
import com.blog.entity.Article;
import com.blog.entity.Comment;
import com.blog.entity.User;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.CommentMapper;
import com.blog.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentMapper commentMapper;
    private final UserMapper userMapper;
    private final ArticleMapper articleMapper;

    public CommentService(CommentMapper commentMapper, UserMapper userMapper, ArticleMapper articleMapper) {
        this.commentMapper = commentMapper;
        this.userMapper = userMapper;
        this.articleMapper = articleMapper;
    }

    public List<CommentDto> listByArticleId(Long articleId) {
        List<Comment> comments = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getArticleId, articleId)
                        .eq(Comment::getIsDeleted, 0)
                        .orderByAsc(Comment::getCreatedAt));
        return comments.stream().map(this::toDto).collect(Collectors.toList());
    }

    public void create(Long userId, Long articleId, String content) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setArticleId(articleId);
        comment.setContent(content);
        comment.setIsDeleted(0);
        commentMapper.insert(comment);
    }

    public void delete(Long id) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        comment.setIsDeleted(1);
        commentMapper.updateById(comment);
    }

    private CommentDto toDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setArticleId(comment.getArticleId());
        dto.setUserId(comment.getUserId());
        dto.setUsername(getUsername(comment.getUserId()));
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }

    private String getUsername(Long userId) {
        User user = userMapper.selectById(userId);
        return user != null ? user.getUsername() : "未知";
    }
}
