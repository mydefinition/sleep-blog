package top.gosleep.blog.service.impl;

import org.springframework.stereotype.Service;
import top.gosleep.blog.bean.dto.ArticleSummaryDTO;
import top.gosleep.blog.bean.dto.ArticleTagDTO;
import top.gosleep.blog.bean.dto.CategoryDTO;
import top.gosleep.blog.bean.dto.CategoryItemDTO;
import top.gosleep.blog.bean.entity.Category;
import top.gosleep.blog.bean.vo.ArticleQueryVO;
import top.gosleep.blog.bean.vo.PageResult;
import top.gosleep.blog.bean.vo.TagVO;
import top.gosleep.blog.common.context.UserContext;
import top.gosleep.blog.mapper.ArticleQueryMapper;
import top.gosleep.blog.mapper.CategoryMapper;
import top.gosleep.blog.service.ArticleQueryService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ArticleQueryServiceImpl implements ArticleQueryService {
    private final ArticleQueryMapper articleQueryMapper;
    private final CategoryMapper categoryMapper;

    public ArticleQueryServiceImpl(ArticleQueryMapper articleQueryMapper, CategoryMapper categoryMapper) {
        this.articleQueryMapper = articleQueryMapper;
        this.categoryMapper = categoryMapper;
    }


    @Override
    public PageResult<ArticleQueryVO> queryPublishArticles(Long categoryId, List<Long> tagIds, Long page, Long size, boolean and) {
        return queryArticles(null, 1, categoryId, tagIds, page, size, and);
    }

    @Override
    public PageResult<ArticleQueryVO> queryPersonArticles(Long userId, Integer isPublished, Long categoryId, List<Long> tagIds, Long page, Long size, boolean and) {
        UserContext.requireAdminId(userId);
        return queryArticles(userId, isPublished, categoryId, tagIds, page, size, and);
    }

    /**
     * 按条件分页查询文章
     * @param userId 文章所有者od，null则查询所有用户的文章
     * @param isPublished 0:查询未公开文章，1:查询公开文章，null:均查询
     * @param categoryId 所在分类id
     * @param tagIds 标签列表（标签id）
     * @param page 页码
     * @param size 页大小
     * @param and true:标签按与逻辑查询，false:标签按或逻辑查询
     * @return
     */
    private PageResult<ArticleQueryVO> queryArticles(
            Long userId, Integer isPublished, Long categoryId,
            List<Long> tagIds, Long page, Long size, boolean and) {

        long total = and
                ? articleQueryMapper.countArticlesAnd(userId, isPublished, categoryId, tagIds, (long) tagIds.size())
                : articleQueryMapper.countArticlesOr(userId, isPublished, categoryId, tagIds);
        if (total == 0) return PageResult.empty(page, size);

        long offset = Math.max(page - 1, 0) * size;
        List<ArticleSummaryDTO> summaries = and
                ? articleQueryMapper.queryPageWhitAndTag(userId, isPublished, categoryId, tagIds, (long) tagIds.size(), offset, size)
                : articleQueryMapper.queryPageWhitOrTag(userId, isPublished, categoryId, tagIds, offset, size);
        if (summaries.isEmpty())
            return new PageResult<>(Collections.emptyList(), total, page, size);
        return new PageResult<>(combine(summaries), total, page, size);
    }

    private List<ArticleQueryVO> combine(List<ArticleSummaryDTO> summaries) {
        List<ArticleTagDTO> tagList = articleQueryMapper.queryTags(
                summaries.stream()
                        .map(ArticleSummaryDTO::getId)
                        .toList());
        Map<Long, List<ArticleTagDTO>> tagMap = tagList.stream()
                .collect(Collectors.groupingBy(ArticleTagDTO::getArticleId));

        List<Category> categoryList = categoryMapper.selectList(null);
        Map<Long, Category> categoryMap = categoryList.stream()
                .collect(Collectors
                        .toMap(Category::getId, Function.identity()));

        List<ArticleQueryVO> res = new ArrayList<>(summaries.size());

        for (ArticleSummaryDTO summary : summaries) {
            ArticleQueryVO vo = new ArticleQueryVO();

            vo.fillFrom(summary);

            vo.setTags(tagMap.getOrDefault(vo.getId(), Collections.emptyList()).stream()
                    .map(at -> new TagVO(at.getTagId(), at.getTagName()))
                    .toList());

            CategoryDTO dto = new CategoryDTO();
            Category last = categoryMap.get(summary.getCategoryId());
            switch (Category.getLevel(last)) {
                case 1:
                    dto.setLevel1(CategoryItemDTO.from(last));
                    break;
                case 2:
                    dto.setLevel1(CategoryItemDTO.from(categoryMap.get(last.getLevel1Id())));
                    dto.setLevel2(CategoryItemDTO.from(last));
                    break;
                case 3:
                    dto.setLevel1(CategoryItemDTO.from(categoryMap.get(last.getLevel1Id())));
                    dto.setLevel2(CategoryItemDTO.from(categoryMap.get(last.getLevel2Id())));
                    dto.setLevel3(CategoryItemDTO.from(last));
                    break;
            }
            vo.setCategory(dto);

            res.add(vo);
        }
        return res;
    }
}
