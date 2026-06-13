package top.gosleep.blog.service;

import top.gosleep.blog.bean.dto.CategoryDTO;
import top.gosleep.blog.bean.entity.Category;
import top.gosleep.blog.bean.vo.CategoryVO;

import java.util.List;

public interface CategoryService {
    CategoryDTO findById(Long categoryId);

    /** 创建诸如 ?/ C1 ?/C2 ?/C3 ?/ 的分类路径 */
    CategoryDTO makePathExist(String rawPath);

    /** 在指定父分类下创建具有指定名的子分类，若存在则直接返回该分类 */
    Category makeCategoryExist(String name, Long parentId);

    /** 返回分类树，堆形式 */
    CategoryVO list();
}
