package top.gosleep.blog.service;

import top.gosleep.blog.bean.dto.request.ArticleRequest;
import top.gosleep.blog.bean.vo.ArticleDetailVO;

/** 负责文章本身的 CURD；其中ADMIN 具有自己创建的文章权限，SUPER 具有所以文章权限。 */
public interface ArticleService {
    /** 根据文章 ID 获取 VO */
    ArticleDetailVO getById(Long articleId);

    /** 创建文章，默认不发布。*/
    ArticleDetailVO create(ArticleRequest req);

    /** 更新整个文章 */
    void update(Long id, ArticleRequest req);

    /** 仅修改文章发布状态 */
    void modifyPublish(Long articleId, boolean publish);

    /** 删除文章 */
    void delete(Long id);
}
