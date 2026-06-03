package top.gosleep.blog.context;

import lombok.Getter;

@Getter
public enum SessionKey {
    USER("user");

    private final String key;

    SessionKey(String key) {
        this.key = key;
    }
}
