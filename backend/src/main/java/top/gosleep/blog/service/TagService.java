package com.blog.service;

import com.blog.dto.TagDto;
import java.util.List;

public interface TagService {
    List<TagDto> list();
    List<TagDto> getByArticleId(Long articleId);
    TagDto create(String name);
}
