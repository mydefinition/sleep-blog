package top.gosleep.blog.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileTreeNode {
    public Long id;
    public String name;
    public Boolean isDir;
    public Long size;
    public String hash;
    public String uploaderName;
    public String uploadAt;
    public String path;
    public List<FileTreeNode> children;

    public FileTreeNode(Long id, String name, Boolean isDir) {
        this.id = id;
        this.name = name;
        this.isDir = isDir;
        this.children = new ArrayList<>();
    }
}