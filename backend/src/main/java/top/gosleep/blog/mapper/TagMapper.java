package top.gosleep.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.gosleep.blog.bean.entity.Tag;

import java.util.List;

public interface TagMapper extends BaseMapper<Tag> {
    List<Tag> listByArticle(Long articleId);
}
