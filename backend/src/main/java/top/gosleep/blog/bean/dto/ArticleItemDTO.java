package top.gosleep.blog.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.gosleep.blog.bean.vo.TagVO;

import java.time.LocalDateTime;
import java.util.List;

/** 前端文章列表页单项 VO（不含tag名） */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleItemDTO {
    private Long id;
    private String title;
    private String summary;
    private Long authorId;
    private String authorName;
    private List<Long> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
