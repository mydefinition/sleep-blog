package top.gosleep.blog.bean.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import top.gosleep.blog.bean.entity.User;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private User.Role role;
    private LocalDateTime createdAt;

    public UserDto(User user) {
        this.username = user.getUsername();
        this.id = user.getId();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
    }

    public static UserDto fromEntity(User user) {
        return new UserDto(user);
    }
}
