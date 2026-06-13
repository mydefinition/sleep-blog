package top.gosleep.blog.service.impl;

import top.gosleep.blog.bean.vo.HomeVO;
import top.gosleep.blog.common.DefaultFileUtil;
import top.gosleep.blog.mapper.ArticleMapper;
import top.gosleep.blog.mapper.ArticleQueryMapper;
import top.gosleep.blog.service.ArticleQueryService;
import top.gosleep.blog.service.HomeService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class HomeServiceImpl implements HomeService {

    public static final String FILE_NAME = "daily_tasks.json";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String SALT = "daily task";

    private final ArticleQueryMapper articleQueryMapper;

    private List<String> tasks;
    private LocalDate cacheTaskDate;
    private String cacheTask;

    public HomeServiceImpl(ArticleQueryMapper articleQueryMapper) {
        this.articleQueryMapper = articleQueryMapper;
    }

    @PostConstruct
    void init() throws IOException {
        tasks = DefaultFileUtil.getContent(FILE_NAME);
    }

    @Override
    public HomeVO getDailyTask() {
        return new HomeVO(todayTask());
    }

    @Override
    public Long getRandomArticleId() {
        return articleQueryMapper.randomPublishedArticle();
    }

    private String todayTask() {
        LocalDate today = getDate();
        if (cacheTaskDate == null || !cacheTaskDate.equals(today)) {
            cacheTaskDate = today;
            cacheTask = generate(today);
        }
        return cacheTask;
    }

    /** 今日凌晨4点至次日凌晨4点视为一日 */
    private LocalDate getDate() {
        return LocalDateTime.now().minusHours(4).toLocalDate();
    }

    private String generate(LocalDate date) {
        long seed = (date.toString() + SALT).hashCode();
        return tasks.get(new Random(seed).nextInt(tasks.size()));
    }
}