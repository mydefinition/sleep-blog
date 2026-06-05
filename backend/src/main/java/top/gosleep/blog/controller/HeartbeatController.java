package top.gosleep.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.gosleep.blog.common.Result;

@RestController
@RequestMapping("/api/heartbeat")
@Tag(name = "心跳接口")
public class HeartbeatController {

    @GetMapping
    @Operation(summary = "发送心跳")
    public Result<?> heartbeat() {
        return Result.ok();
    }
}
