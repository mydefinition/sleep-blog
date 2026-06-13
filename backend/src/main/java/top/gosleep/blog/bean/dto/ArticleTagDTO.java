package top.gosleep.blog.bean.dto;

import lombok.Data;

@Data
public class ArticleTagDTO {
    private Long articleId;
    private Long tagId;
    private String tagName;
}
