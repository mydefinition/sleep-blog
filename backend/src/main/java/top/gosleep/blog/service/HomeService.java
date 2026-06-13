package top.gosleep.blog.service;

import top.gosleep.blog.bean.vo.HomeVO;

public interface HomeService {
    /** 获取今日任务文字 */
    HomeVO getDailyTask();

    /** 随机获取一篇已发布文章的 ID，无则返回 null */
    Long getRandomArticleId();
}