package top.gosleep.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import top.gosleep.blog.dto.ArticleDto;
import top.gosleep.blog.dto.ArticleListDto;
import top.gosleep.blog.dto.request.ArticleRequest;

public interface ArticleService {
    IPage<ArticleListDto> list(String tagIdsStr, int pageNum, int pageSize);
    ArticleDto getById(Long id);
    void create(Long authorId, ArticleRequest req);
    void update(Long id, ArticleRequest req);
    void delete(Long id);
}
