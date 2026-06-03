package top.gosleep.blog.dto.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private Long id;
    private String password;
}
