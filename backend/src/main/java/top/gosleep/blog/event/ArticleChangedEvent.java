package top.gosleep.blog.event;

import lombok.Data;

@Data
public class ArticleChangedEvent {
    private ChangeType changeType;
    private long articleId;

    public ArticleChangedEvent(ChangeType changeType, long articleId) {
        this.changeType = changeType;
        this.articleId = articleId;
    }
}
