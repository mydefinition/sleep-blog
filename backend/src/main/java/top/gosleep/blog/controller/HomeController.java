package top.gosleep.blog.controller;

import top.gosleep.blog.bean.vo.HomeVO;
import top.gosleep.blog.common.Result;
import top.gosleep.blog.common.ResultCode;
import top.gosleep.blog.service.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
@Tag(name = "首页")
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService taskService) {
        this.homeService = taskService;
    }

    @GetMapping("/daily")
    @Operation(summary = "获取今日任务")
    public Result<HomeVO> daily() {
        return Result.ok(homeService.getDailyTask());
    }

    @GetMapping("/random")
    @Operation(summary = "随机一篇文章ID")
    public Result<Long> randomArticle() {
        Long id = homeService.getRandomArticleId();
        if (id == null) return Result.error(ResultCode.NOT_FOUND, "暂无文章");
        return Result.ok(id);
    }
}