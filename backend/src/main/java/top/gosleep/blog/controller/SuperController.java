package top.gosleep.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import top.gosleep.blog.common.BusinessException;
import top.gosleep.blog.common.Result;
import top.gosleep.blog.common.ResultCode;
import top.gosleep.blog.context.UserContext;
import top.gosleep.blog.converter.UserConverter;
import top.gosleep.blog.dto.request.IdRequest;
import top.gosleep.blog.dto.request.ResetPasswordRequest;
import top.gosleep.blog.entity.User;
import top.gosleep.blog.entity.User.Role;
import top.gosleep.blog.mapper.UserMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/super")
@Tag(name = "权限管理")
public class SuperController {
    
    @Value("${app.super-secret}")
    private String superSecret;

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public SuperController(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    private void checkSecret(String secret) {
        if (secret == null || !secret.equals(superSecret)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "密钥错误");
        }
    }

    @GetMapping("/users")
    @Operation(summary = "列出所有用户")
    public Result<List<User>> listUsers(@RequestHeader("super-token") String secret) {
        checkSecret(secret);
        List<User> users = userMapper.selectList(
                new LambdaQueryWrapper<User>()
                        .select(User::getId, User::getUsername, User::getRole, User::getCreatedAt)
                        .orderByAsc(User::getId));
        return Result.ok(users);
    }

    @GetMapping("/users/find")
    @Operation(summary = "按用户名查用户")
    public Result<User> findByUsername(@RequestHeader("super-token") String secret,
                                       @RequestParam String username) {
        checkSecret(secret);
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .select(User::getId, User::getUsername, User::getRole, User::getCreatedAt)
                        .eq(User::getUsername, username));
        if (user == null) throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        return Result.ok(user);
    }

    @PutMapping("/users/promote")
    @Operation(summary = "提升为管理员")
    public Result<?> promote(@RequestHeader("super-token") String secret,
                             @RequestBody IdRequest body) {
        checkSecret(secret);
        User user = userMapper.selectById(body.getId());
        if (user == null) throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        user.setRole(Role.ADMIN);
        userMapper.updateById(user);
        UserContext.setCurrentUser(UserConverter.toDto(user));
        return Result.ok();
    }

    @PutMapping("/users/revoke")
    @Operation(summary = "撤销管理员")
    public Result<?> revoke(@RequestHeader("super-token") String secret,
                            @RequestBody IdRequest body) {
        checkSecret(secret);
        User user = userMapper.selectById(body.getId());
        if (user == null) throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        user.setRole(Role.USER);
        userMapper.updateById(user);
        UserContext.setCurrentUser(UserConverter.toDto(user));
        return Result.ok();
    }

    @PutMapping("/users/reset-password")
    @Operation(summary = "重置用户密码")
    public Result<?> resetPassword(@RequestHeader("super-token") String secret,
                                   @RequestBody ResetPasswordRequest body) {
        checkSecret(secret);
        User user = userMapper.selectById(body.getId());
        if (user == null) throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        if (body.getPassword() == null || body.getPassword().isBlank()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "密码不能为空");
        }
        user.setPassword(passwordEncoder.encode(body.getPassword()));
        userMapper.updateById(user);
        return Result.ok();
    }
}
