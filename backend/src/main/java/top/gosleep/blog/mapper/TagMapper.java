package top.gosleep.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.gosleep.blog.bean.entity.Tag;
import top.gosleep.blog.bean.vo.TagVO;

import java.util.List;

public interface TagMapper extends BaseMapper<Tag> {
    List<Tag> getByArticleId(Long articleId);
}
