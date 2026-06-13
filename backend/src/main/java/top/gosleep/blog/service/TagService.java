package top.gosleep.blog.service;

import top.gosleep.blog.bean.entity.Tag;
import top.gosleep.blog.bean.vo.TagVO;

import java.util.List;
import java.util.Map;

public interface TagService {
    List<Tag> listAll();

    List<Tag> listByArticle(Long articleId);

    Tag makeExist(String name);

    void updateArticleTags(Long articleId, List<Long> tagIds);
}
