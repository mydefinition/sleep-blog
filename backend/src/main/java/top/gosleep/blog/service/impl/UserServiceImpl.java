package top.gosleep.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import top.gosleep.blog.bean.dto.UserDTO;
import top.gosleep.blog.bean.dto.request.LoginRequest;
import top.gosleep.blog.bean.dto.request.ProfileUpdateRequest;
import top.gosleep.blog.bean.dto.request.RegisterRequest;
import top.gosleep.blog.bean.entity.User;
import top.gosleep.blog.common.BusinessException;
import top.gosleep.blog.common.ResultCode;
import top.gosleep.blog.common.context.SessionContext;
import top.gosleep.blog.common.context.SessionKey;
import top.gosleep.blog.common.context.UserContext;
import top.gosleep.blog.mapper.UserMapper;
import top.gosleep.blog.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO register(RegisterRequest req) {
        SessionContext context = SessionContext.current();
        String email = context.get(SessionKey.VERIFY_EMAIL);
        String code = context.get(SessionKey.VERIFY_CODE);
        legalNickname(req.getNickname());
        if (email == null || req.getEmail() == null || !email.equals(req.getEmail()))
            throw new BusinessException(ResultCode.BAD_REQUEST, "邮箱不匹配");
        if (code == null || req.getVerifyCode() == null || !code.equals(req.getVerifyCode()))
            throw new BusinessException(ResultCode.BAD_REQUEST, "验证码错误");
        if (userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail, email)) != null)
            throw new BusinessException(ResultCode.BAD_REQUEST, "邮箱已被占用");
        String password = passwordEncoder.encode(req.getPassword());
        User user = new User(0L, req.getNickname(), password, email, User.DEFAULT_ROLE, LocalDateTime.now());
        userMapper.insert(user);
        context.remove(SessionKey.VERIFY_EMAIL);
        context.remove(SessionKey.VERIFY_CODE);
        return UserDTO.from(user);
    }

    public void login(LoginRequest req) {
        User user;
        try {
            String str = req.getIdOrEmail();
            if (str.contains("@"))
                user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail, str));
            else {
                user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getId, Long.parseLong(str)));
            }
            Objects.requireNonNull(user);
        } catch (Exception e) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "不存在指定用户");
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            throw new BusinessException(ResultCode.BAD_REQUEST, "密码错误");
        UserContext.setUser(UserDTO.from(user));
    }

    public UserDTO getProfile() {
        UserDTO user = UserContext.getUser();
        if (user == null)
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        return user;
    }

    public void updateProfile(ProfileUpdateRequest req) {
        UserContext.requireAdminId(req.getId());

        User user = userMapper.selectById(req.getId());
        if (req.getNickname() != null) {
            legalNickname(req.getNickname());
            user.setNickname(req.getNickname());
        }
        userMapper.updateById(user);
        UserContext.setUser(UserDTO.from(user));
    }

    @Override
    public UserDTO getUserById(Long userId) {
        return UserDTO.from(userMapper.selectById(userId));
    }

    private void legalNickname(String nickname) {
        if (nickname == null || nickname.isBlank())
            throw new BusinessException(ResultCode.BAD_REQUEST, "昵称不能为空");
        if (nickname.length() < 2 || nickname.length() > 20)
            throw new BusinessException(ResultCode.BAD_REQUEST, "昵称需2-20个字符");
        if (!nickname.matches("^[\\u4e00-\\u9fa5a-zA-Z0-9_]+$"))
            throw new BusinessException(ResultCode.BAD_REQUEST, "昵称只能包含中文、字母、数字和下划线");
    }
}
