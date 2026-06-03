package top.gosleep.blog.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private String content;
    private Long articleId;
    private Long userId;
    private String username;
    private LocalDateTime createdAt;
}
