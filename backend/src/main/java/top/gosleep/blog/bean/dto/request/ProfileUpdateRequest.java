package top.gosleep.blog.bean.dto.request;

import lombok.Data;

@Data
public class ProfileUpdateRequest {
    private Long id;
    private String nickname;
}
