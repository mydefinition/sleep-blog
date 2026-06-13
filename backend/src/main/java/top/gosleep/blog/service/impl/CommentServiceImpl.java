package top.gosleep.blog.service.impl;

import top.gosleep.blog.bean.vo.CommentVO;
import top.gosleep.blog.service.CommentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Override
    public List<CommentVO> listByArticleId(Long articleId) {
        return List.of();
    }

    @Override
    public void create(Long userId, Long articleId, String content) {

    }

    @Override
    public void delete(Long id) {

    }
}
