package top.gosleep.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.gosleep.blog.bean.dto.UserDTO;
import top.gosleep.blog.bean.entity.Article;

public interface ArticleMapper extends BaseMapper<Article> {
    UserDTO selectAuthor(Long articleId);
}
