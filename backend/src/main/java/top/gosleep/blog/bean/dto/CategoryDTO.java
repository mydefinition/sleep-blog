package top.gosleep.blog.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    private CategoryItemDTO level1;
    private CategoryItemDTO level2;
    private CategoryItemDTO level3;

    /** 返回分类级别 0~3 */
    public static int getLevel(CategoryDTO dto) {
        if (dto.getLevel1() == null) return 0;
        if (dto.getLevel2() == null) return 1;
        if (dto.getLevel3() == null) return 2;
        return 3;
    }

    /** 返回最末尾分类 */
    public static CategoryItemDTO getLastItem(CategoryDTO dto) {
        if (dto.getLevel3() != null) return dto.getLevel3();
        if (dto.getLevel2() != null) return dto.getLevel2();
        if (dto.getLevel1() != null) return dto.getLevel1();
        return CategoryItemDTO.ROOT_ITEM;
    }
}
