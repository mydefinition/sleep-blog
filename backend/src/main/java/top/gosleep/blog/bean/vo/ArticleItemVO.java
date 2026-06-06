package top.gosleep.blog.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.gosleep.blog.bean.dto.ArticleItemDTO;

import java.time.LocalDateTime;
import java.util.List;

/** 前端文章列表页单项 VO（含tag名） */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleItemVO {
    private Long id;
    private String title;
    private String summary;
    private Long authorId;
    private String authorName;
    private List<TagVO> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** 不含 tags */
    public static ArticleItemVO fromDTO(ArticleItemDTO articleItemDTO) {
        return new ArticleItemVO(
                articleItemDTO.getId(),
                articleItemDTO.getTitle(),
                articleItemDTO.getSummary(),
                articleItemDTO.getAuthorId(),
                articleItemDTO.getAuthorName(),
                null,
                articleItemDTO.getCreatedAt(),
                articleItemDTO.getUpdatedAt()
        );
    }
}
