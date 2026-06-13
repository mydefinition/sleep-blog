package top.gosleep.blog.bean.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String idOrEmail;
    private String password;
}
