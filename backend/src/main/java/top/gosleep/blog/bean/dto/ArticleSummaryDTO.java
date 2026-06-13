package top.gosleep.blog.bean.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleSummaryDTO {
    private Long id;
    private String title;
    private String summary;
    private Long authorId;
    private String authorName;
    private Long isPublished;
    private LocalDateTime createdAt;
    private Long categoryId;
}
