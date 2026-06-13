package top.gosleep.blog.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import top.gosleep.blog.bean.entity.Category;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class CategoryVO {
    private Long id;
    private String name;
    private List<CategoryVO> children;

    public static CategoryVO from(Category category) {
        return new CategoryVO(category.getId(), category.getName(), new ArrayList<>());
    }
}
