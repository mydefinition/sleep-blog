package top.gosleep.blog.controller;

import top.gosleep.blog.common.BusinessException;
import top.gosleep.blog.common.Result;
import top.gosleep.blog.common.ResultCode;
import top.gosleep.blog.context.UserContext;
import top.gosleep.blog.dto.UserDto;
import top.gosleep.blog.dto.request.LoginRequest;
import top.gosleep.blog.dto.request.RegisterRequest;
import top.gosleep.blog.service.UserService;
import jakarta.servlet.http.HttpSession;
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

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<?> register(@RequestBody RegisterRequest req) {
        userService.register(req);
        return Result.ok();
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<?> login(@RequestBody LoginRequest req) {
        userService.login(req);
        return Result.ok();
    }

    @GetMapping("/profile")
    @Operation(summary = "获取个人信息")
    public Result<UserDto> profile() {
        UserDto user = UserContext.getCurrentUser();
        if (user == null)
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        return Result.ok(userService.getProfile(user.getId()));
    }

    @PutMapping("/profile")
    @Operation(summary = "修改个人信息")
    public Result<?> updateProfile(@RequestBody RegisterRequest req) {
        Long userId = UserContext.getCurrentUser().getId();
        if (userId == null)
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        userService.updateProfile(userId, req);
        return Result.ok();
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public Result<?> logout(HttpSession session) {
        session.invalidate();
        return Result.ok();
    }
}
