package top.gosleep.blog.config;

import top.gosleep.blog.bean.entity.Category;
import top.gosleep.blog.bean.entity.CloudFile;
import top.gosleep.blog.bean.entity.User;
import top.gosleep.blog.bean.entity.User.Role;
import top.gosleep.blog.mapper.CategoryMapper;
import top.gosleep.blog.mapper.CloudFileMapper;
import top.gosleep.blog.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

/**
 * 初始化初始用户
 */
@Configuration
public class DataInitializer implements CommandLineRunner {

    public static final String DEFAULT_PASSWORD = "admin123456";
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserMapper userMapper;
    private final CloudFileMapper cloudFileMapper;
    private final CategoryMapper categoryMapper;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserMapper userMapper, CloudFileMapper cloudFileMapper, CategoryMapper categoryMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.cloudFileMapper = cloudFileMapper;
        this.categoryMapper = categoryMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (cloudFileMapper.selectById(0L) == null) {
            cloudFileMapper.insert(new CloudFile(
                    0L, null, 0L, null, "根目录", 1, null));
        }

        if (categoryMapper.selectById(0L) == null) {
            categoryMapper.insert(new Category(0L, "", 0L, 0L));
        }

        if (userMapper.selectById(1L) == null) {
            User admin = new User();
            admin.setId(1L);
            admin.setNickname("admin");
            admin.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
            admin.setRole(Role.ADMIN);
            admin.setEmail("");
            userMapper.insert(admin);
            log.warn("============================================");
            log.warn("  Default admin account created:");
            log.warn("  Username: admin   Password: " + DEFAULT_PASSWORD);
            log.warn("  Please change the password immediately!");
            log.warn("============================================");
        }
    }
}