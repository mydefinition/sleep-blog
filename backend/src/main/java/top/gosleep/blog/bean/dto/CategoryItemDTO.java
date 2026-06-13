package top.gosleep.blog.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.gosleep.blog.bean.entity.Category;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryItemDTO {

    public static final CategoryItemDTO ROOT_ITEM = new CategoryItemDTO(0L, "");

    private Long id;
    private String name;

    public static CategoryItemDTO from(Category category) {
        return new CategoryItemDTO(category.getId(), category.getName());
    }
}
