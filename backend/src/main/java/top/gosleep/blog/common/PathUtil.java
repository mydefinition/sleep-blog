package com.blog.common;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathUtil {
    public static String absolute(String path) {
        Path p = Paths.get(path);
        if (!p.isAbsolute()) {
            return Paths.get("").toAbsolutePath().resolve(path).normalize().toString();
        }
        return path;
    }
}
