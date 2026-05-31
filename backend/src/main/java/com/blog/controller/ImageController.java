package com.blog.controller;

import com.blog.common.Result;
import com.blog.service.ImageService;
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

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/upload")
    @Operation(summary = "上传图片")
    public Result<?> upload(@RequestParam MultipartFile file) throws IOException {
        String url = imageService.upload(file);
        return Result.ok(Map.of("url", url));
    }
}
