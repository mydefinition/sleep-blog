package top.gosleep.blog.bean.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    public static final Role DEFAULT_ROLE = Role.USER;

    public enum Role {
        USER,
        ADMIN
    }

    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private Role role;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
