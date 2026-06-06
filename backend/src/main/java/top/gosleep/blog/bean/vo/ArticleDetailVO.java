package top.gosleep.blog.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.gosleep.blog.bean.entity.Article;

import java.time.LocalDateTime;
import java.util.List;

/** 前端文章详情页 VO（不含tag名） */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDetailVO {
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private String authorName;
    private List<TagVO> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ArticleDetailVO(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.authorId = article.getAuthorId();
        this.createdAt = article.getCreatedAt();
        this.updatedAt = article.getUpdatedAt();
    }

    /** 缺失 {@code authorName} 与 {@code tags} 字段 */
    public static  ArticleDetailVO fromEntity(Article article) {
        return new ArticleDetailVO(article);
    }
}
