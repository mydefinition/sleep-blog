package com.blog.service.impl;

import com.blog.service.HomeService;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class HomeServiceImpl implements HomeService {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String SALT = "sample-blog-daily-task";
    private static final String FILE_NAME = "tasks.json";

    private List<String> pool;
    private LocalDate cacheTaskDate;
    private String cacheTask;

    @PostConstruct
    void init() throws IOException {
        Path external = Paths.get(FILE_NAME);
        if (!Files.exists(external)) {
            try (InputStream in = new ClassPathResource(FILE_NAME).getInputStream()) {
                Files.copy(in, external);
            }
        }
        pool = MAPPER.readValue(external.toFile(), new TypeReference<List<String>>() {});
        if (pool == null || pool.isEmpty()) {
            throw new IllegalStateException("tasks.json is empty or missing");
        }
    }

    @Override
    public String getDailyTask() {
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

    /** 以 (日期 + 固定salt) 为种子随机选取 */
    private String generate(LocalDate date) {
        long seed = (date.toString() + SALT).hashCode();
        Random rand = new Random(seed);
        return pool.get(rand.nextInt(pool.size()));
    }
}