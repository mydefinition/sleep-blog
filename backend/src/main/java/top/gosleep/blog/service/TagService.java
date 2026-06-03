package top.gosleep.blog.service;

import top.gosleep.blog.dto.TagDto;
import java.util.List;

public interface TagService {
    List<TagDto> list();
    List<TagDto> getByArticleId(Long articleId);
    TagDto create(String name);
}
