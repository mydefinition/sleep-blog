package com.blog.config;

import com.blog.entity.User;
import com.blog.entity.User.Role;
import com.blog.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userMapper.selectById(1L) == null) {
            User admin = new User();
            admin.setId(1L);
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userMapper.insert(admin);
            log.warn("============================================");
            log.warn("  Default admin account created:");
            log.warn("  Username: admin   Password: admin123");
            log.warn("  Please change the password immediately!");
            log.warn("============================================");
        }
    }
}