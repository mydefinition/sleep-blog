package com.blog.common;

import com.blog.service.impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Debug {
    public static final Logger log = LoggerFactory.getLogger(Debug.class);

    public static void atDebug(String message) {
        log.error(message);
    }
}
