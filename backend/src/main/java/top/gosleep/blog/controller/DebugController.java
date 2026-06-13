package top.gosleep.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import top.gosleep.blog.bean.dto.UserDTO;
import top.gosleep.blog.bean.dto.request.ArticleRequest;
import top.gosleep.blog.bean.entity.User;
import top.gosleep.blog.common.Result;
import top.gosleep.blog.common.context.UserContext;
import top.gosleep.blog.mapper.UserMapper;
import top.gosleep.blog.service.ArticleService;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/debug")
@Tag(name = "Debug专用")
public class DebugController {
    private final ArticleService articleService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public DebugController(ArticleService articleService, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.articleService = articleService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    @Operation(summary = "初始化")
    public Result<?> initData() {
        Random random = new Random(114514);
        List<UserDTO> userIds = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            userIds.add(UserDTO.from(addUser("USER" + i, "test" + i + "@email", "KFCvivo50", User.Role.ADMIN)));

        // 构造分类目录
        Set<String> categoryPaths = new HashSet<>();
        categoryPaths.add("");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    char l1 = (char) ('A' + i), l2 = (char) ('A' + j), l3 = (char) ('A' + k);
                    categoryPaths.add(String.format("/1%c/2%c/3%c", l1, l2, l3));
                    categoryPaths.add(String.format("/1%c/2%c", l1, l2));
                    categoryPaths.add(String.format("/1%c", l1));
                }
            }
        }
        List<String> categoryList = categoryPaths.stream().toList();

        for (int i = 0; i < 1000; i++) {
            ArticleRequest request = new ArticleRequest();
            UserDTO user = userIds.get(random.nextInt(userIds.size()));
            UserContext.setUser(user);
            request.setAuthorId(user.getId());
            request.setTitle("tArt" + i);
            request.setSummary("tSum" + i);
            request.setContent("tContent" + i);
            request.setIsPublished(1);

            request.setCategoryPath(categoryList.get(random.nextInt(categoryList.size())));

            List<String> tags = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                if (random.nextBoolean()) tags.add("tag" + j);
            }
            request.setTagNames(tags);
            request.setTagIds(new ArrayList<>());
            articleService.create(request);
        }

        return Result.ok();
    }

    private User addUser(String nickname, String email, String rawPassword, User.Role role) {
        String password = passwordEncoder.encode(rawPassword);
        User user = new User(), temp;
        user.setNickname(nickname);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        temp = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail, email));
        if (temp != null) return user;
        userMapper.insert(user);
        return user;
    }
}
