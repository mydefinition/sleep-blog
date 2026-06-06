package top.gosleep.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.Getter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.gosleep.blog.bean.dto.ArticleItemDTO;
import top.gosleep.blog.bean.entity.Article;
import top.gosleep.blog.bean.entity.ArticleTag;
import top.gosleep.blog.bean.dto.request.ArticleRequest;
import top.gosleep.blog.bean.entity.Tag;
import top.gosleep.blog.bean.vo.ArticleDetailVO;
import top.gosleep.blog.bean.vo.ArticleItemVO;
import top.gosleep.blog.bean.vo.ArticleListCheckVO;
import top.gosleep.blog.bean.vo.TagVO;
import top.gosleep.blog.common.BusinessException;
import top.gosleep.blog.common.ResultCode;
import top.gosleep.blog.event.ArticleChangedEvent;
import top.gosleep.blog.event.ChangeType;
import top.gosleep.blog.mapper.ArticleMapper;
import top.gosleep.blog.mapper.ArticleTagMapper;
import top.gosleep.blog.mapper.UserMapper;
import top.gosleep.blog.service.ArticleService;
import top.gosleep.blog.service.TagService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleMapper articleMapper;
    private final ArticleTagMapper articleTagMapper;
    private final TagService tagService;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher eventPublisher;

    public ArticleServiceImpl(ArticleMapper articleMapper, ArticleTagMapper articleTagMapper, TagService tagService,
                              UserMapper userMapper, ApplicationEventPublisher eventPublisher) {
        this.articleMapper = articleMapper;
        this.articleTagMapper = articleTagMapper;
        this.tagService = tagService;
        this.userMapper = userMapper;
        this.eventPublisher = eventPublisher;

        handleArticleChanged(null);
        updateAll();
    }

    /** 用于前端文章列表页面展示的缓冲 */

    @Getter
    private long cacheToken;
    private boolean isCacheDirty;
    private ArticleListCheckVO articleListCache;

    /** 更新缓存（全量刷新） */
    private synchronized void updateAll() {
        List<ArticleItemDTO> items = articleMapper.selectArticleListWithAuthor();
        Map<Long, String> tagMap = tagService.map();
        Map<Long, List<ArticleTag>> tagGroup = articleTagMapper.selectList(null).stream().collect(
                Collectors.groupingBy(ArticleTag::getArticleId));
        articleListCache = new ArticleListCheckVO(
                cacheToken,
                items.stream().map(article -> {
                    ArticleItemVO vo = ArticleItemVO.fromDTO(article);
                    vo.setTags(tagGroup.getOrDefault(vo.getId(), Collections.emptyList())
                            .stream()
                            .map(at -> new TagVO(at.getTagId(), tagMap.get(at.getTagId())))
                            .toList());
                    return vo;
                }).toList(),
                tagService.list()
        );
        isCacheDirty = false;
    }

    public ArticleListCheckVO getAll() {
        if (isCacheDirty) updateAll();
        return articleListCache;
    }

    @EventListener
    protected void handleArticleChanged(ArticleChangedEvent event) {
        cacheToken = System.currentTimeMillis();
        isCacheDirty = true;
    }

    public ArticleListCheckVO list() {
        return getAll();
    }

    public ArticleListCheckVO checkList(long token) {
        return token == cacheToken ? new ArticleListCheckVO(token, null, null) : list();
    }

    public ArticleDetailVO getById(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        ArticleDetailVO vo = ArticleDetailVO.fromEntity(article);
        vo.setAuthorName(userMapper.selectById(article.getAuthorId()).getUsername());
        vo.setTags(tagService.getByArticleId(id));
        return vo;
    }

    @Transactional
    public void create(Long authorId, ArticleRequest req) {
        List<Long> tagIds = req.getTagIds() == null ? new ArrayList<>() : req.getTagIds();
        if (req.getTagNames() != null)
            for (String name : req.getTagNames())
                tagIds.add(tagService.create(name).getId());
        Article article = new Article();
        article.setTitle(req.getTitle());
        article.setContent(req.getContent());
        article.setSummary(req.getSummary() != null
                ? req.getSummary() : extractSummary(req.getContent()));
        article.setAuthorId(authorId);
        articleMapper.insert(article);
        tagService.saveTags(article.getId(), tagIds);
        eventPublisher.publishEvent(new ArticleChangedEvent(ChangeType.CREATE, article.getId()));
    }

    @Transactional
    public void update(Long id, ArticleRequest req) {
        List<String> tagNames = req.getTagNames() == null ? Collections.emptyList() : req.getTagNames();
        List<Long> tagIds = req.getTagIds() == null ? new ArrayList<>() : req.getTagIds();
        for (String name : tagNames)
            req.getTagIds().add(tagService.create(name).getId());
        Article article = articleMapper.selectById(id);
        if (article == null) throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        article.setTitle(req.getTitle());
        article.setContent(req.getContent());
        article.setSummary(req.getSummary() != null
                ? req.getSummary() : extractSummary(req.getContent()));
        articleMapper.updateById(article);
        articleTagMapper.delete(
                new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, id));
        tagService.saveTags(id, tagIds);
        eventPublisher.publishEvent(new ArticleChangedEvent(ChangeType.UPDATE, article.getId()));
    }

    @Transactional
    public void delete(Long id) {
        if (articleMapper.deleteById(id) != 0) {
            eventPublisher.publishEvent(new ArticleChangedEvent(ChangeType.DELETE, id));
        } else {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }
    }

    private String extractSummary(String content) {
        if (content == null) return "";
        String plain = content
                .replaceAll("\\$\\$[\\s\\S]*?\\$\\$", "")
                .replaceAll("\\$[^$]+?\\$", "")
                .replaceAll("#{1,6}\\s", "")
                .replaceAll("[*_~`>\\-\\[\\]()!]", "")
                .replaceAll("\\n+", " ")
                .replaceAll("\\s+", " ")
                .trim();
        return plain.length() > 200 ? plain.substring(0, 200) + "..." : plain;
    }
}
