package top.gosleep.blog.common.context;

import top.gosleep.blog.bean.dto.UserDTO;
import top.gosleep.blog.bean.entity.User;
import top.gosleep.blog.common.BusinessException;
import top.gosleep.blog.common.ResultCode;

/** 根据 SessionContext 自动鉴权（SessionContext 的 User 封装），并实现常用授权逻辑， */
public class UserContext {

    public static void setUser(UserDTO user) {
        SessionContext.current().set(SessionKey.USER, user);
    }

    public static UserDTO getUser() {
        return SessionContext.current().get(SessionKey.USER);
    }

    /** ADMIN 或 SUPER 权限授权 */
    public static void requireAdmin() {
        UserDTO user = SessionContext.current().get(SessionKey.USER);
        if (user != null &&
                (user.getRole() == User.Role.ADMIN || user.getRole() == User.Role.SUPER)) return;
        throw new BusinessException(ResultCode.FORBIDDEN, "需要管理员权限");
    }

    /** 指定 ID 或 SUPER 权限授权 */
    public static void requireId(Long id) {
        UserDTO user = getUser();
        if (user != null &&
                (user.getId().equals(id)  || user.getRole() == User.Role.SUPER)) return;
        throw new BusinessException(ResultCode.FORBIDDEN, "需要指定用户");
    }

    public static User.Role getRole() {
        return getUser().getRole();
    }

    /** 指定 ID 的 ADMIN 或 SUPER 权限授权 */
    public static void requireAdminId(Long id) {
        requireAdmin();
        requireId(id);
    }
}
