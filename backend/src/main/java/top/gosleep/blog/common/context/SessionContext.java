package top.gosleep.blog.common.context;

import jakarta.servlet.http.HttpSession;
import top.gosleep.blog.bean.dto.UserDTO;
import top.gosleep.blog.bean.entity.User;
import top.gosleep.blog.common.BusinessException;
import top.gosleep.blog.common.ResultCode;

/** session 全局存储封装 */
public class SessionContext {
    private final HttpSession session;

    private SessionContext(HttpSession session) {
        this.session = session;
    }

    /** 线程存储上下文（session） */
    private static final ThreadLocal<SessionContext> currentContext = new ThreadLocal<>();

    public static void init(HttpSession session) {
        currentContext.set(new SessionContext(session));
    }

    public static SessionContext current() {
        return currentContext.get();
    }

    public static void clear() {
        currentContext.remove();
    }

    public <T> T get(SessionKey<T> key) {
        Object value = session.getAttribute(key.toString());
        return value == null ? null : key.getType().cast(value);
    }

    public <T> void set(SessionKey<T> key, T value) {
        session.setAttribute(key.toString(), value);
    }

    public <T> void remove(SessionKey<T> key) {
        session.removeAttribute(key.toString());
    }

}
