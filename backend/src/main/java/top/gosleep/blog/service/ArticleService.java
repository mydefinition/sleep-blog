package top.gosleep.blog.service;

import top.gosleep.blog.bean.dto.request.ArticleRequest;
import top.gosleep.blog.bean.vo.ArticleDetailVO;
import top.gosleep.blog.bean.vo.ArticleListCheckVO;

public interface ArticleService {
    ArticleListCheckVO list();
    ArticleListCheckVO checkList(long token);
    ArticleDetailVO getById(Long id);
    void create(Long authorId, ArticleRequest req);
    void update(Long id, ArticleRequest req);
    void delete(Long id);
}
