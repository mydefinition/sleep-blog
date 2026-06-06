package top.gosleep.blog.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/** 前端文章列表页 轮询 VO */
@Data
@AllArgsConstructor
public class ArticleListCheckVO {
    private long token;
    private List<ArticleItemVO> list = null;
    private List<TagVO> tags = null;
}
