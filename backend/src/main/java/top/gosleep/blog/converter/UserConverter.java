package top.gosleep.blog.converter;

import top.gosleep.blog.dto.UserDto;
import top.gosleep.blog.entity.User;

public class UserConverter {
    public static UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    public static User toEntity(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setRole(dto.getRole());
        user.setCreatedAt(dto.getCreatedAt());
        return user;
    }
}
