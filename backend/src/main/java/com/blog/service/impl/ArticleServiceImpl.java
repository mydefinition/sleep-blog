package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.dto.ArticleDto;
import com.blog.dto.ArticleListDto;
import com.blog.dto.request.ArticleRequest;
import com.blog.entity.*;
import com.blog.mapper.*;
import com.blog.service.ArticleService;
import com.blog.service.TagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;
    private final ArticleTagMapper articleTagMapper;
    private final TagService tagService;
    private final UserMapper userMapper;

    public ArticleServiceImpl(ArticleMapper articleMapper, ArticleTagMapper articleTagMapper,
                              TagService tagService, UserMapper userMapper) {
        this.articleMapper = articleMapper;
        this.articleTagMapper = articleTagMapper;
        this.tagService = tagService;
        this.userMapper = userMapper;
    }

    public IPage<ArticleListDto> list(String tagIdsStr, int pageNum, int pageSize) {
        Page<Article> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .select(Article::getId, Article::getTitle, Article::getSummary,
                        Article::getAuthorId, Article::getCreatedAt, Article::getUpdatedAt)
                .orderByDesc(Article::getCreatedAt);

        if (tagIdsStr != null && !tagIdsStr.isBlank()) {
            String[] parts = tagIdsStr.split(",");
            List<Long> tagIds = new java.util.ArrayList<>();
            for (String p : parts) {
                try { tagIds.add(Long.parseLong(p.trim())); } catch (NumberFormatException ignored) {}
            }
            if (!tagIds.isEmpty()) {
                List<Long> articleIds = articleTagMapper.selectList(
                        new LambdaQueryWrapper<ArticleTag>().in(ArticleTag::getTagId, tagIds)
                ).stream().map(ArticleTag::getArticleId).distinct().toList();
                if (articleIds.isEmpty()) return page.convert(this::toListDto);
                wrapper.in(Article::getId, articleIds);
            }
        }

        articleMapper.selectPage(page, wrapper);
        return page.convert(this::toListDto);
    }

    public ArticleDto getById(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        return toDto(article);
    }

    @Transactional
    public void create(Long authorId, ArticleRequest req) {
        Article article = new Article();
        article.setTitle(req.getTitle());
        article.setContent(req.getContent());
        article.setSummary(req.getSummary() != null
                ? req.getSummary() : extractSummary(req.getContent()));
        article.setAuthorId(authorId);
        articleMapper.insert(article);
        saveTags(article.getId(), req.getTagIds());
    }

    @Transactional
    public void update(Long id, ArticleRequest req) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        article.setTitle(req.getTitle());
        article.setContent(req.getContent());
        article.setSummary(req.getSummary() != null
                ? req.getSummary() : extractSummary(req.getContent()));
        articleMapper.updateById(article);

        articleTagMapper.delete(
                new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, id));
        saveTags(id, req.getTagIds());
    }

    @Transactional
    public void delete(Long id) {
        articleMapper.deleteById(id);
    }

    private void saveTags(Long articleId, List<Long> tagIds) {
        if (tagIds != null) {
            for (Long tagId : tagIds) {
                ArticleTag at = new ArticleTag();
                at.setArticleId(articleId);
                at.setTagId(tagId);
                articleTagMapper.insert(at);
            }
        }
    }

    private String extractSummary(String content) {
        if (content == null) return "";
        String plain = content
                .replaceAll("\\$\\$[\\s\\S]*?\\$\\$", "")
                .replaceAll("\\$[^\\$]+?\\$", "")
                .replaceAll("#{1,6}\\s", "")
                .replaceAll("[*_~`>\\-\\[\\]()!]", "")
                .replaceAll("\\n+", " ")
                .replaceAll("\\s+", " ")
                .trim();
        return plain.length() > 200 ? plain.substring(0, 200) + "\u2026" : plain;
    }

    private ArticleListDto toListDto(Article article) {
        ArticleListDto dto = new ArticleListDto();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setSummary(article.getSummary());
        dto.setAuthorId(article.getAuthorId());
        dto.setAuthorName(getAuthorName(article.getAuthorId()));
        dto.setTags(tagService.getByArticleId(article.getId()));
        dto.setCreatedAt(article.getCreatedAt());
        dto.setUpdatedAt(article.getUpdatedAt());
        return dto;
    }

    private ArticleDto toDto(Article article) {
        ArticleDto dto = new ArticleDto();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());
        dto.setSummary(article.getSummary());
        dto.setAuthorId(article.getAuthorId());
        dto.setAuthorName(getAuthorName(article.getAuthorId()));
        dto.setTags(tagService.getByArticleId(article.getId()));
        dto.setCreatedAt(article.getCreatedAt());
        dto.setUpdatedAt(article.getUpdatedAt());
        return dto;
    }

    private String getAuthorName(Long authorId) {
        User user = userMapper.selectById(authorId);
        return user != null ? user.getUsername() : "未知";
    }
}
