package com.blog.controller;

import com.blog.common.Result;
import com.blog.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@Tag(name = "图片上传")
public class ImageController {

    private final FileService fileService;

    public ImageController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    @Operation(summary = "上传图片")
    public Result<?> upload(@RequestParam MultipartFile file) throws IOException {
        String url = fileService.upload(file);
        return Result.ok(Map.of("url", url));
    }
}
