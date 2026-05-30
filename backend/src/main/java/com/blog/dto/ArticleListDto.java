package com.blog.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller → 前端：文章列表（不含正文）
 */
@Data
public class ArticleListDto {
    private Long id;
    private String title;
    private String summary;
    private Long authorId;
    private String authorName;
    private List<TagDto> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
