package top.gosleep.blog.bean.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("file_storage")
public class FileStorage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Boolean isDir;
    private Long size;
    private Long localId;
    private String hash;
    private Long userId;
    private LocalDateTime uploadAt;
}