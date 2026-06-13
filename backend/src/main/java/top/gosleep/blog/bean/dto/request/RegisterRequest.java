package top.gosleep.blog.bean.dto.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nickname;
    private String password;
    private String email;
    private String verifyCode;
}
