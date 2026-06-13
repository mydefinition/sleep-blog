package top.gosleep.blog.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.gosleep.blog.bean.dto.CategoryDTO;
import top.gosleep.blog.bean.entity.Article;
import top.gosleep.blog.bean.entity.Category;

import java.time.LocalDateTime;
import java.util.List;

/** 文章详情页面，展示文章所有数据 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDetailVO {
    private Long id;
    private String title;
    private String content;
    private String summary;
    private Long authorId;
    private String authorName;
    private Integer isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CategoryDTO categoryLevel;
    private List<TagVO> tags;

    public ArticleDetailVO(Article article, String authorName, CategoryDTO category, List<TagVO> tags) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.summary = article.getSummary();
        this.authorId = article.getAuthorId();
        this.isPublished = article.getIsPublished();
        this.createdAt = article.getCreatedAt();
        this.updatedAt = article.getUpdatedAt();
        this.authorName = authorName;
        this.categoryLevel = category;
        this.tags = tags;
    }
}
