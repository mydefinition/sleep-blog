package com.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.dto.ArticleDto;
import com.blog.dto.ArticleListDto;
import com.blog.dto.request.ArticleRequest;

public interface ArticleService {
    IPage<ArticleListDto> list(String tagIdsStr, int pageNum, int pageSize);
    ArticleDto getById(Long id);
    void create(Long authorId, ArticleRequest req);
    void update(Long id, ArticleRequest req);
    void delete(Long id);
}
