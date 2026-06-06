package top.gosleep.blog.context;

import top.gosleep.blog.bean.dto.UserDto;
import top.gosleep.blog.bean.entity.User;
import org.springframework.security.access.AccessDeniedException;
import top.gosleep.blog.common.BusinessException;
import top.gosleep.blog.common.ResultCode;

import java.util.concurrent.atomic.AtomicReference;

public class UserContext {
    private UserContext() {
        // Private constructor to prevent instantiation
    }

    private static final ThreadLocal<AtomicReference<UserDto>> currentUser = new ThreadLocal<>();

    public static void init(AtomicReference<UserDto> ref) {
        currentUser.set(ref);
    }

    public static void setCurrentUser(UserDto user) {
        currentUser.get().set(user);
    }

    public static UserDto getCurrentUser() {
        return currentUser.get().get();
    }

    public static void requireAdmin() {
        UserDto user = getCurrentUser();
        if (user == null || user.getRole() != User.Role.ADMIN) {
            throw new BusinessException(ResultCode.FORBIDDEN, "需要管理员权限");
        }
    }

    public static void clear() {
        currentUser.remove();
    }

}
