package top.gosleep.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import top.gosleep.blog.bean.entity.ArticleTag;
import top.gosleep.blog.bean.entity.Tag;
import top.gosleep.blog.common.context.UserContext;
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

    @Override
    public List<Tag> listAll() {
        return tagMapper.selectList(null);
    }

    @Override
    public List<Tag> listByArticle(Long articleId) {
        List<ArticleTag> ats = articleTagMapper.selectList(new LambdaQueryWrapper<ArticleTag>()
                .eq(ArticleTag::getArticleId, articleId));
        return tagMapper.selectBatchIds(ats.stream().map(ArticleTag::getTagId).toList());
    }

    @Override
    public Tag makeExist(String name) {
        UserContext.requireAdmin(); // 授权

        Tag tag = tagMapper.selectOne(new LambdaQueryWrapper<Tag>().eq(Tag::getName, name));
        if (tag == null) {
            tag = new Tag();
            tag.setName(name);
            tagMapper.insert(tag);
        }
        return tag;
    }

    @Override
    public void updateArticleTags(Long articleId, List<Long> tagIds) {
        // todo 优化，比较是否真更新等，表内字段复用，避免频繁 delete
        articleTagMapper.delete(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, articleId));
        articleTagMapper.insert(tagIds.stream().map(tagId -> new ArticleTag(articleId, tagId)).toList());
    }
}
