package top.gosleep.blog.service;

import top.gosleep.blog.dto.CommentDto;
import java.util.List;

public interface CommentService {
    List<CommentDto> listByArticleId(Long articleId);
    void create(Long userId, Long articleId, String content);
    void delete(Long id);
}
