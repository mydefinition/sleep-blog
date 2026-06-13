package top.gosleep.blog.controller;

import top.gosleep.blog.bean.dto.UserDTO;
import top.gosleep.blog.bean.dto.request.LoginRequest;
import top.gosleep.blog.bean.dto.request.ProfileUpdateRequest;
import top.gosleep.blog.bean.dto.request.RegisterRequest;
import top.gosleep.blog.bean.dto.request.VerifyRequest;
import top.gosleep.blog.bean.entity.User;
import top.gosleep.blog.common.BusinessException;
import top.gosleep.blog.common.Result;
import top.gosleep.blog.common.ResultCode;
import top.gosleep.blog.common.context.SessionContext;
import top.gosleep.blog.common.context.UserContext;
import top.gosleep.blog.mapper.UserMapper;
import top.gosleep.blog.service.MailService;
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
    private final MailService mailService;

    public UserController(UserService userService, MailService mailService, UserMapper userMapper) {
        this.userService = userService;
        this.mailService = mailService;
    }

    @PostMapping("/verify")
    @Operation(summary = "发送验证码")
    public Result<?> verify(@RequestBody VerifyRequest req) {
        mailService.sendVerifyCode(req.getEmail());
        return Result.ok();
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<?> register(@RequestBody RegisterRequest req) {
        return Result.ok(userService.register(req));
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<?> login(@RequestBody LoginRequest req) {
        userService.login(req);
        return Result.ok();
    }

    @GetMapping("/profile")
    @Operation(summary = "获取个人信息")
    public Result<UserDTO> profile() {
        return Result.ok(userService.getProfile());
    }

    @PutMapping("/profile")
    @Operation(summary = "修改个人信息")
    public Result<?> updateProfile(@RequestBody ProfileUpdateRequest req) {
        userService.updateProfile(req);
        return Result.ok();
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public Result<?> logout(HttpSession session) {
        session.invalidate();
        return Result.ok();
    }
}
