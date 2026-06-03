package top.gosleep.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import top.gosleep.blog.converter.TagConverter;
import top.gosleep.blog.dto.TagDto;
import top.gosleep.blog.entity.ArticleTag;
import top.gosleep.blog.entity.Tag;
import top.gosleep.blog.mapper.ArticleTagMapper;
import top.gosleep.blog.mapper.TagMapper;
import top.gosleep.blog.service.TagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;

    public TagServiceImpl(TagMapper tagMapper, ArticleTagMapper articleTagMapper) {
        this.tagMapper = tagMapper;
        this.articleTagMapper = articleTagMapper;
    }

    public List<TagDto> list() {
        return tagMapper.selectList(null).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<TagDto> getByArticleId(Long articleId) {
        List<Long> tagIds = articleTagMapper.selectList(
                new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, articleId)
        ).stream().map(ArticleTag::getTagId).toList();
        if (tagIds.isEmpty()) return List.of();
        return tagMapper.selectBatchIds(tagIds).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TagDto create(String name) {
        Tag tag = tagMapper.selectOne(
                new LambdaQueryWrapper<Tag>().eq(Tag::getName, name));
        if (tag != null) return toDto(tag);
        tag = new Tag();
        tag.setName(name);
        tagMapper.insert(tag);
        return toDto(tag);
    }

    private TagDto toDto(Tag tag) {
        return TagConverter.toDto(tag);
    }
}
