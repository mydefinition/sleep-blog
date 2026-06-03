package top.gosleep.blog.service;

import top.gosleep.blog.dto.HomeDto;

public interface HomeService {
    /** 获取今日任务文字 */
    HomeDto getDailyTask();
}