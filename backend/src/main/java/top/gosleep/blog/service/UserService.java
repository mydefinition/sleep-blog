package top.gosleep.blog.service;

import top.gosleep.blog.bean.dto.UserDTO;
import top.gosleep.blog.bean.dto.request.LoginRequest;
import top.gosleep.blog.bean.dto.request.ProfileUpdateRequest;
import top.gosleep.blog.bean.dto.request.RegisterRequest;

public interface UserService {

    UserDTO register(RegisterRequest req);

    void login(LoginRequest req);

    UserDTO getProfile();

    void updateProfile(ProfileUpdateRequest req);

    UserDTO getUserById(Long userId);
}
