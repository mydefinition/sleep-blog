package com.blog.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Controller → 前端（已屏蔽 password）
 */
@Data
public class UserDto {
    private Long id;
    private String username;
    private String role;
    private LocalDateTime createdAt;
}
