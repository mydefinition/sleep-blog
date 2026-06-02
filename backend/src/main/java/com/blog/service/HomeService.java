package com.blog.service;

import com.blog.dto.HomeDto;

public interface HomeService {
    /** 获取今日任务文字 */
    HomeDto getDailyTask();
}