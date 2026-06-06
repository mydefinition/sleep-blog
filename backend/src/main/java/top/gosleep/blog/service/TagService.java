package top.gosleep.blog.service;

import top.gosleep.blog.bean.entity.Tag;
import top.gosleep.blog.bean.vo.TagVO;
import java.util.List;
import java.util.Map;

public interface TagService {
    List<TagVO> list();
    List<TagVO> getByArticleId(Long articleId);
    Map<Long, String> map();
    Tag create(String name);
    void saveTags(Long articleId, List<Long> tagIds);
}
