package top.gosleep.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import top.gosleep.blog.bean.entity.ArticleTag;
import top.gosleep.blog.bean.vo.TagVO;
import top.gosleep.blog.bean.entity.Tag;
import top.gosleep.blog.event.ArticleChangedEvent;
import top.gosleep.blog.event.ChangeType;
import top.gosleep.blog.mapper.ArticleMapper;
import top.gosleep.blog.mapper.ArticleTagMapper;
import top.gosleep.blog.mapper.TagMapper;
import top.gosleep.blog.service.TagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;

    public TagServiceImpl(TagMapper tagMapper, ArticleTagMapper articleTagMapper) {
        this.tagMapper = tagMapper;
        this.articleTagMapper = articleTagMapper;
    }

    public List<TagVO> list() {
        return tagMapper.selectList(null).stream()
                .map(TagVO::fromEntity)
                .toList();
    }

    @Override
    public List<TagVO> getByArticleId(Long articleId) {
        return tagMapper.getByArticleId(articleId).stream().map(TagVO::fromEntity).toList();
    }

    public Map<Long, String> map() {
        return tagMapper.selectList(null)
                .stream()
                .collect(Collectors.toMap(Tag::getId, Tag::getName));
    }

    @Transactional
    public Tag create(String name) {
        Tag tag = tagMapper.selectOne(new LambdaQueryWrapper<Tag>().eq(Tag::getName, name));
        if (tag != null) return tag;
        tag = new Tag();
        tag.setName(name);
        tagMapper.insert(tag);
        return tag;
    }

    public void saveTags(Long articleId, List<Long> tagIds) {
        if (tagIds != null) {
            for (Long tagId : tagIds) {
                ArticleTag at = new ArticleTag();
                at.setArticleId(articleId);
                at.setTagId(tagId);
                articleTagMapper.insert(at);
            }
        }
    }
}
