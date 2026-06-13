package top.gosleep.blog.bean.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user")
public class User {
    public static final Role DEFAULT_ROLE = Role.USER;

    public enum Role {
        USER,
        ADMIN,
        SUPER
    }

    @TableId(type = IdType.AUTO)
    private Long id;
    private String nickname;
    private String password;
    private String email;
    private Role role;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
