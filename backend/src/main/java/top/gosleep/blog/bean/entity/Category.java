package top.gosleep.blog.bean.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("category")
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    /** 一级分类父亲，为本身为一级分类则为 0 */
    private Long level1Id;
    /** 二级分类父亲，为本身为一/二级分类则为 0 */
    private Long level2Id;

    public static int getLevel(Category category) {
        if (category.id == 0L) return 0;
        if (category.level1Id == 0L) return 1;
        if (category.level2Id == 0L) return 2;
        return 3;
    }

    public static Long getParent(Category category) {
        if (category.level1Id == 0L) return 0L;
        if (category.level2Id == 0L) return category.getLevel1Id();
        return category.getLevel2Id();
    }

    public static Category getRoot() {
        return new Category(0L, "", 0L, 0L);
    }
}
