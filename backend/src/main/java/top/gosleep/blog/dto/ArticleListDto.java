package top.gosleep.blog.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/** 前端：文章列表用 */
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
