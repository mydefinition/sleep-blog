package top.gosleep.blog.controller;

import top.gosleep.blog.common.Result;
import top.gosleep.blog.dto.HomeDto;
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

    private final HomeService taskService;

    public HomeController(HomeService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/daily")
    @Operation(summary = "获取今日任务")
    public Result<HomeDto> daily() {
        return Result.ok(taskService.getDailyTask());
    }
}