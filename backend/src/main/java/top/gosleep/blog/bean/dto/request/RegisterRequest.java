package top.gosleep.blog.bean.dto.request;

import lombok.Data;

/**
 * 前端 → Controller（注册入参）
 */
@Data
public class RegisterRequest {
    private String username;
    private String password;
}
