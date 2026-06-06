package top.gosleep.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.gosleep.blog.bean.dto.ArticleItemDTO;
import top.gosleep.blog.bean.entity.Article;

import java.util.List;

public interface ArticleMapper extends BaseMapper<Article> {
    /** 查询文章列表及作者信息 */
    List<ArticleItemDTO> selectArticleListWithAuthor();
}
