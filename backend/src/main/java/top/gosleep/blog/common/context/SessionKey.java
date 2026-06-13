package top.gosleep.blog.common.context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import top.gosleep.blog.bean.dto.UserDTO;

import java.time.LocalDateTime;

/** session key 统一管理 */
@Getter
@AllArgsConstructor
public final class SessionKey<T> {

    public static final SessionKey<UserDTO> USER = new SessionKey<>(UserDTO.class);
    public static final SessionKey<String> VERIFY_CODE = new SessionKey<>(String.class);
    public static final SessionKey<LocalDateTime> VERIFY_SENT_TIME = new SessionKey<>(LocalDateTime.class);
    public static final SessionKey<String> VERIFY_EMAIL = new SessionKey<>(String.class);

    private final Class<T> type;

}
