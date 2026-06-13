package top.gosleep.blog.service;

import top.gosleep.blog.bean.vo.CommentVO;

import java.util.List;

public interface CommentService {
    List<CommentVO> listByArticleId(Long articleId);
    void create(Long userId, Long articleId, String content);
    void delete(Long id);
}
