package top.gosleep.blog.bean.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("article_tag")
@AllArgsConstructor
@NoArgsConstructor
public class ArticleTag {
    private Long articleId;
    private Long tagId;
}
