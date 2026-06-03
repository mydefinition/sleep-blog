package top.gosleep.blog.dto.request;

import lombok.Data;

/**
 * 前端 → Controller（登录入参）
 */
@Data
public class LoginRequest {
    private String username;
    private String password;
}
