package com.blog.controller;

import com.blog.common.Result;
import com.blog.dto.UserDto;
import com.blog.dto.request.RegisterRequest;
import com.blog.security.SecurityUtils;
import com.blog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    @Operation(summary = "获取个人信息")
    public Result<UserDto> profile() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.ok(userService.getProfile(userId));
    }

    @PutMapping("/profile")
    @Operation(summary = "修改个人信息")
    public Result<?> updateProfile(@RequestBody RegisterRequest req) {
        Long userId = SecurityUtils.getCurrentUserId();
        userService.updateProfile(userId, req);
        return Result.ok();
    }
}
