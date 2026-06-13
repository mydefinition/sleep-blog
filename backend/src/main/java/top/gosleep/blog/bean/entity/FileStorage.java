package top.gosleep.blog.bean.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("file_storage")
public class FileStorage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String mineType;
    private String name;
    private Long size;
    private LocalDateTime createdAt;
}