package top.gosleep.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.gosleep.blog.bean.entity.ArticleImage;
import top.gosleep.blog.bean.entity.FileStorage;

import java.util.List;

public interface ArticleImageMapper extends BaseMapper<ArticleImage> {
    List<FileStorage> queryByArticleId(Long articleId);
}
