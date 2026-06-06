package top.gosleep.blog.bean.dto.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private Long id;
    private String password;
}
