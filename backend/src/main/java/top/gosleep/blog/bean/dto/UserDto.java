package top.gosleep.blog.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.gosleep.blog.bean.entity.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String nickname;
    private String email;
    private User.Role role;
    private LocalDateTime createdAt;

    public static UserDTO from(User user) {
        return new UserDTO(user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt());
    }
}
