package top.gosleep.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import top.gosleep.blog.bean.dto.CategoryDTO;
import top.gosleep.blog.bean.dto.UserDTO;
import top.gosleep.blog.bean.dto.request.ArticleRequest;
import top.gosleep.blog.bean.entity.Article;
import top.gosleep.blog.bean.entity.User;
import top.gosleep.blog.bean.vo.ArticleDetailVO;
import top.gosleep.blog.bean.vo.TagVO;
import top.gosleep.blog.common.BusinessException;
import top.gosleep.blog.common.ResultCode;
import top.gosleep.blog.common.context.UserContext;
import top.gosleep.blog.mapper.ArticleMapper;
import top.gosleep.blog.service.ArticleService;
import top.gosleep.blog.service.CategoryService;
import top.gosleep.blog.service.TagService;
import top.gosleep.blog.service.UserService;

import java.util.*;

@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleMapper articleMapper;

    private final CategoryService categoryService;
    private final TagService tagService;
    private final UserService userService;

    public ArticleServiceImpl(ArticleMapper articleMapper, CategoryService categoryService, TagService tagService, UserService userService) {
        this.articleMapper = articleMapper;
        this.categoryService = categoryService;
        this.tagService = tagService;
        this.userService = userService;
    }

    @Override
    public ArticleDetailVO getById(Long articleId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null)
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        String userName = Optional.ofNullable(userService.getUserById(article.getAuthorId()))
                .map(UserDTO::getNickname).orElse("未知用户");
        CategoryDTO categoryDTO = categoryService.findById(article.getCategoryId());
        List<TagVO> tags = TagVO.from(tagService.listByArticle(articleId));
        return new ArticleDetailVO(article, userName, categoryDTO, tags);
    }

    @Override
    public ArticleDetailVO create(ArticleRequest req) {
        UserContext.requireAdmin(); // 授权

        Article article = new Article();
        ArticleRequest.to(req, article);
        CategoryDTO categoryDTO = categoryService.makePathExist(req.getCategoryPath());
        article.setCategoryId(CategoryDTO.getLastItem(categoryDTO).getId());    // todo 无用
        if (UserContext.getRole() != User.Role.SUPER &&
                !req.getAuthorId().equals(UserContext.getUser().getId()))
            throw new BusinessException(ResultCode.FORBIDDEN, "无法为别人创建文章");
        articleMapper.insert(article);

        List<Long> tagIds = req.getTagIds() == null ? new ArrayList<>() : req.getTagIds();
        List<String> tagNames = req.getTagNames();
        if (tagNames != null)
            tagNames.forEach(tagName -> tagIds.add(tagService.makeExist(tagName).getId()));
        tagService.updateArticleTags(article.getId(), tagIds);
        return new ArticleDetailVO(article,
                UserContext.getUser().getNickname(),
                categoryDTO,
                TagVO.from(tagService.listByArticle(article.getId())));
    }

    @Override
    public void update(Long articleId, ArticleRequest req) {
        UserContext.requireAdminId(articleMapper.selectAuthor(articleId).getId());  // 授权

        Article article = new Article();
        ArticleRequest.to(req, article);
        article.setId(articleId);
        CategoryDTO categoryDTO = categoryService.makePathExist(req.getCategoryPath());
        article.setCategoryId(CategoryDTO.getLastItem(categoryDTO).getId());    // todo 无用
        if (UserContext.getRole() != User.Role.SUPER &&
                !req.getAuthorId().equals(UserContext.getUser().getId()))
            throw new BusinessException(ResultCode.FORBIDDEN, "无法为别人创建文章");
        articleMapper.updateById(article);

        List<Long> tagIds = req.getTagIds() == null ? new ArrayList<>() : req.getTagIds();
        for (String tagName : req.getTagNames())
            tagIds.add(tagService.makeExist(tagName).getId());
        List<String> tagNames = req.getTagNames();
        if (tagNames != null)
            tagNames.forEach(tagName -> tagIds.add(tagService.makeExist(tagName).getId()));
        tagService.updateArticleTags(article.getId(), tagIds);
    }

    @Override
    public void modifyPublish(Long articleId, boolean publish) {
        UserContext.requireAdminId(articleMapper.selectAuthor(articleId).getId());  // 授权

        LambdaUpdateWrapper<Article> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(Article::getId, articleId)
                .set(Article::getIsPublished, publish ? 1 : 0);
        if (articleMapper.update(null, wrapper) == 0)
            throw new BusinessException(ResultCode.NOT_FOUND, "无指定文章 id=" + articleId);
    }

    @Override
    public void delete(Long articleId) {
        UserContext.requireAdminId(articleMapper.selectAuthor(articleId).getId());  // 授权

        articleMapper.deleteById(articleId);
    }

}
