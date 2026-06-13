package top.gosleep.blog.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.gosleep.blog.bean.dto.ArticleSummaryDTO;
import top.gosleep.blog.bean.dto.ArticleTagDTO;

import java.util.List;

@Mapper
public interface ArticleQueryMapper {

    List<ArticleSummaryDTO> queryPageWhitOrTag(Long userId, Integer isPublished, Long categoryId, List<Long> tagIds, Long offset, Long size);

    List<ArticleSummaryDTO> queryPageWhitAndTag(Long userId, Integer isPublished, Long categoryId, List<Long> tagIds, Long tagSize, Long offset, Long size);

    List<ArticleTagDTO> queryTags(List<Long> articleIds);

    long countArticlesOr(Long userId, Integer isPublished, Long categoryId, List<Long> tagIds);

    long countArticlesAnd(Long userId, Integer isPublished, Long categoryId, List<Long> tagIds, Long tagSize);

    /** 随机一篇已发布的文章的 ID */
    Long randomPublishedArticle();
}
