package top.gosleep.blog.service;

import top.gosleep.blog.bean.dto.UserDto;
import top.gosleep.blog.bean.dto.request.LoginRequest;
import top.gosleep.blog.bean.dto.request.RegisterRequest;

public interface UserService {
    void register(RegisterRequest req);
    void login(LoginRequest req);
    UserDto getProfile(Long userId);
    void updateProfile(Long userId, RegisterRequest req);
}
