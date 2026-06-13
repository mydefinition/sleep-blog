package top.gosleep.blog.bean.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName("cloud_file")
@AllArgsConstructor
@NoArgsConstructor
public class CloudFile {
    private Long id;
    private Long fileId;
    private Long parentId;
    private Long ownerId;
    private String name;
    private Integer isDir;
    private LocalDateTime createdAt;
}
