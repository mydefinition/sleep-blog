package com.blog.context;

import com.blog.dto.UserDto;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicReference;

@Getter
public enum SessionKey {
    USER("user");

    private final String key;

    SessionKey(String key) {
        this.key = key;
    }
}
