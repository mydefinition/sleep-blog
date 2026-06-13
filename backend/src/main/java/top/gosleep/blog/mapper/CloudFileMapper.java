package top.gosleep.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.gosleep.blog.bean.entity.CloudFile;
import top.gosleep.blog.bean.vo.CloudFileVO;

import java.util.List;

public interface CloudFileMapper extends BaseMapper<CloudFile> {
    List<CloudFileVO> queryByParentId(Long parentId);
}
