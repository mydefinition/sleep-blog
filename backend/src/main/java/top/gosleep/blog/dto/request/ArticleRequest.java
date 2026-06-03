package com.blog.dto.request;

import lombok.Data;
import java.util.List;

/**
 * 前端 → Controller（发布/编辑文章入参）
 */
@Data
public class ArticleRequest {
    private String title;
    private String content;
    private String summary;
    private List<Long> tagIds;
}
