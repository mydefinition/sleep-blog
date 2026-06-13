package top.gosleep.blog.bean.dto.request;

import lombok.Data;
import top.gosleep.blog.bean.entity.Article;

import java.util.List;

@Data
public class ArticleRequest {
    private String title;
    private String content;
    private String summary;
    private Long authorId;
    private Integer isPublished;
    /** 形如 c1/c2/c3 的 path，未验证，不确保创建 */
    private String categoryPath;
    private List<Long> tagIds;
    /** 未验证标签名，用于创建新标签 */
    private List<String> tagNames;

    public static void to(ArticleRequest request, Article article) {
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setSummary(request.getSummary());
        article.setAuthorId(request.getAuthorId());
        article.setIsPublished(request.getIsPublished());
    }
}
