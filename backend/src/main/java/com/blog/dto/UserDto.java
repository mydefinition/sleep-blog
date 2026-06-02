package com.blog.dto;

import com.blog.entity.User;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Controller → 前端（已屏蔽 password）
 */
@Data
public class UserDto {
    private Long id;
    private String username;
    private User.Role role;
    private LocalDateTime createdAt;
}
