package top.gosleep.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import top.gosleep.blog.bean.dto.CategoryDTO;
import top.gosleep.blog.bean.dto.CategoryItemDTO;
import top.gosleep.blog.bean.entity.Category;
import top.gosleep.blog.bean.vo.CategoryVO;
import top.gosleep.blog.common.BusinessException;
import top.gosleep.blog.common.ResultCode;
import top.gosleep.blog.common.context.UserContext;
import top.gosleep.blog.mapper.CategoryMapper;
import top.gosleep.blog.service.CategoryService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public CategoryDTO findById(Long categoryId) {
        if (categoryId == 0L) return new CategoryDTO();

        Category last = categoryMapper.selectById(categoryId);
        CategoryDTO dto = new CategoryDTO();
        switch (Category.getLevel(last)) {
            case 3:
                dto.setLevel3(CategoryItemDTO.from(last));
                dto.setLevel2(CategoryItemDTO.from(categoryMapper.selectById(last.getLevel2Id())));
                dto.setLevel1(CategoryItemDTO.from(categoryMapper.selectById(last.getLevel1Id())));
                break;
            case 2:
                dto.setLevel2(CategoryItemDTO.from(last));
                dto.setLevel1(CategoryItemDTO.from(categoryMapper.selectById(last.getLevel1Id())));
                break;
            case 1:
                dto.setLevel1(CategoryItemDTO.from(last));
                break;
        }
        return dto;
    }

    @Override
    public CategoryDTO makePathExist(String rawPath) {
        UserContext.requireAdmin(); // 授权

        if (rawPath == null || rawPath.isBlank()) return new CategoryDTO();
        String[] paths = Arrays.stream(rawPath.split("/"))
                .filter(s -> !s.isEmpty())  // 过滤空字符串
                .toArray(String[]::new);
        if (paths.length > 3)
            throw new BusinessException(ResultCode.BAD_REQUEST, "分类最多支持三级");
        if (paths.length == 0)
            return new CategoryDTO();   // 根
        CategoryDTO res = new CategoryDTO();
        Category cur = Category.getRoot();
        res.setLevel1(CategoryItemDTO.from(cur = makeCategoryExist(paths[0], cur.getId())));
        if (paths.length >= 2) res.setLevel2(CategoryItemDTO.from(cur = makeCategoryExist(paths[1], cur.getId())));
        if (paths.length == 3) res.setLevel3(CategoryItemDTO.from(cur = makeCategoryExist(paths[2], cur.getId())));
        return res;
    }

    @Override
    public Category makeCategoryExist(String name, Long parentId) {
        UserContext.requireAdmin(); // 授权

        Category category = categoryMapper.selectOne(new LambdaQueryWrapper<Category>().eq(Category::getId, parentId));
        if (category == null)
            throw new BusinessException(ResultCode.BAD_REQUEST, "无 id=" + parentId + " 的分类");
        // 父Category复用为新建Category
        switch (Category.getLevel(category)) {
            case 1:
                category.setLevel1Id(parentId);
                break;
            case 2:
                category.setLevel2Id(parentId);
                break;
            case 3:
                throw new BusinessException(ResultCode.BAD_REQUEST, "分类仅支持三级，无法在三级分类下创建分类");
        }
        category.setName(name);
        insert(category);
        return category;
    }

    @Override
    public CategoryVO list() {
        List<Category> all = categoryMapper.selectList(null);
        Map<Long, CategoryVO> res = all.stream()
                .map(CategoryVO::from)
                .collect(Collectors.toMap(CategoryVO::getId, Function.identity()));

        for (Category category : all)
            if (!category.getId().equals(0L))
                res.get(Category.getParent(category)).getChildren().add(res.get(category.getId()));

        return res.get(0L);
    }

    private void insert(Category category) {
        Category c = categoryMapper.selectOne(new LambdaQueryWrapper<Category>()
                .eq(Category::getName, category.getName())
                .eq(Category::getLevel1Id, category.getLevel1Id())
                .eq(Category::getLevel2Id, category.getLevel2Id()));
        if (c != null) {
            category.setId(c.getId());
        } else {
            category.setId(null);
            categoryMapper.insert(category);
        }
    }

}
