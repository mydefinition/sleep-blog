package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.BusinessException;
import com.blog.common.ResultCode;
import com.blog.context.UserContext;
import com.blog.converter.UserConverter;
import com.blog.dto.UserDto;
import com.blog.dto.request.LoginRequest;
import com.blog.dto.request.RegisterRequest;
import com.blog.entity.User;
import com.blog.mapper.UserMapper;
import com.blog.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequest req) {
        if (findByUsername(req.getUsername()) != null) {
            throw new BusinessException(ResultCode.CONFLICT, "用户名已存在");
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(User.DEFAULT_ROLE);
        userMapper.insert(user);
        UserContext.setCurrentUser(UserConverter.toDto(user));
    }

    public void login(LoginRequest req) {
        User user = findByUsername(req.getUsername());
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "用户名或密码错误");
        }
        UserContext.setCurrentUser(UserConverter.toDto(user));
    }

    public UserDto getProfile(Long userId) {
        return UserConverter.toDto(userMapper.selectById(userId));
    }

    public void updateProfile(Long userId, RegisterRequest req) {
        User user = userMapper.selectById(userId);
        if (req.getUsername() != null && !req.getUsername().isEmpty()) {
            user.setUsername(req.getUsername());
        }
        if (req.getPassword() != null && !req.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        userMapper.updateById(user);
    }

    private User findByUsername(String username) {
        return userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }
}
