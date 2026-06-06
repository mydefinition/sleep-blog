package top.gosleep.blog.service;

import top.gosleep.blog.bean.entity.FileStorage;
import top.gosleep.blog.model.FileTreeNode;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface FileStorageService {
    /** 获取文件树 JSON（缓存） */
    String getTreeJson();

    /** 上传文件 */
    FileTreeNode upload(Long userId, MultipartFile file, Long localId) throws IOException;

    /** 新建文件夹 */
    FileTreeNode mkdir(String name, Long localId);

    /** 删除文件或文件夹（递归） */
    void delete(Long id);

    /** 查单个节点（供下载） */
    FileStorage getById(Long id);

    /** 计算磁盘绝对路径 */
    String absolutePath(Long id);
}