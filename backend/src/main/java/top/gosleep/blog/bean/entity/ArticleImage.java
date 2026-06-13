package top.gosleep.blog.bean.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@TableName("article_image")
@AllArgsConstructor
public class ArticleImage {
    private Long articleId;
    private Long fileId;
}
