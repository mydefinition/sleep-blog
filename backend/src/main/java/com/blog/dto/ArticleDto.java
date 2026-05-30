package com.blog.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller → 前端：文章详情（含正文）
 */
@Data
public class ArticleDto {
    private Long id;
    private String title;
    private String content;
    private String summary;
    private Long authorId;
    private String authorName;
    private List<TagDto> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
