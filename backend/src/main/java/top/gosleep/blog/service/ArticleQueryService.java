package top.gosleep.blog.service;

import top.gosleep.blog.bean.vo.ArticleQueryVO;
import top.gosleep.blog.bean.vo.PageResult;

import java.util.List;

/** 负责文章的分页查询，适配前端文章列表逻辑 */
public interface ArticleQueryService {

    /**
     * 公用文章列表分页查询
     * @param categoryId 分类 ID，筛选包括该分类及其所有子分类下的文章
     * @param tagIds 标签集合，用于按与/或逻辑筛选
     * @param page 分页页数，按 1 索引，小于 1 则取1
     * @param size 每页大小
     * @param and true 则标签按与逻辑查询，要求具有列表内全部标签；反之按或逻辑查询，要求具有列表内标签之一
     * @return 页
     */
    PageResult<ArticleQueryVO> queryPublishArticles(Long categoryId, List<Long> tagIds, Long page, Long size, boolean and);

    /**
     * 个人文章列表分页查询
     * @param categoryId 分类 ID，筛选包括该分类及其所有子分类下的文章
     * @param tagIds 标签集合，用于按与/或逻辑筛选
     * @param page 分页页数，按 1 索引，小于 1 则取1
     * @param size 每页大小
     * @param and true 则标签按与逻辑查询，要求具有列表内全部标签；反之按或逻辑查询，要求具有列表内标签之一
     * @return 页
     */
    PageResult<ArticleQueryVO> queryPersonArticles(Long userId, Integer isPublished, Long categoryId, List<Long> tagIds, Long page, Long size, boolean and);

}
