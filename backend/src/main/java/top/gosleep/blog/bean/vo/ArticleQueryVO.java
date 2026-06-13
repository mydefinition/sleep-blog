package top.gosleep.blog.bean.vo;

import lombok.Data;
import top.gosleep.blog.bean.dto.ArticleSummaryDTO;
import top.gosleep.blog.bean.dto.CategoryDTO;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArticleQueryVO {
    private Long id;
    private String title;
    private String summary;
    private Long authorId;
    private String authorName;
    private Long isPublished;
    private LocalDateTime createdAt;
    private CategoryDTO category;
    private List<TagVO> tags;

    public ArticleQueryVO fillFrom(ArticleSummaryDTO dto) {
        setId(dto.getId());
        setTitle(dto.getTitle());
        setSummary(dto.getSummary());
        setAuthorId(dto.getAuthorId());
        setAuthorName(dto.getAuthorName());
        setIsPublished(dto.getIsPublished());
        setCreatedAt(dto.getCreatedAt());
        return this;
    }
}
