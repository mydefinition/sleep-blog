package com.blog.service;

import com.blog.dto.UserDto;
import com.blog.dto.request.LoginRequest;
import com.blog.dto.request.RegisterRequest;

public interface UserService {
    void register(RegisterRequest req);
    String login(LoginRequest req);
    UserDto getProfile(Long userId);
    void updateProfile(Long userId, RegisterRequest req);
}
