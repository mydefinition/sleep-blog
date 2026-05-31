package com.blog.controller;

import com.blog.common.Result;
import com.blog.model.FileTreeNode;
import com.blog.security.SecurityUtils;
import com.blog.dto.request.MkdirRequest;
import com.blog.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")
@Tag(name = "文件存储")
public class FileStorageController {

    private final FileStorageService fileStorageService;

    public FileStorageController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "获取完整文件树")
    public ResponseEntity<String> tree() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"code\":200,\"message\":\"success\",\"data\":" + fileStorageService.getTreeJson() + "}");
    }

    @PostMapping("/upload")
    @Operation(summary = "上传文件")
    public Result<FileTreeNode> upload(@RequestParam MultipartFile file,
                                       @RequestParam(required = false) Long localId) throws IOException {
        SecurityUtils.requireAdmin();
        return Result.ok(fileStorageService.upload(SecurityUtils.getCurrentUserId(), file, localId));
    }

    @PostMapping("/mkdir")
    @Operation(summary = "新建文件夹")
    public Result<FileTreeNode> mkdir(@RequestBody MkdirRequest req) {
        SecurityUtils.requireAdmin();
        return Result.ok(fileStorageService.mkdir(req.getName(), req.getLocalId()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除文件或文件夹")
    public Result<?> delete(@PathVariable Long id) {
        SecurityUtils.requireAdmin();
        fileStorageService.delete(id);
        return Result.ok();
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "下载文件")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws IOException {
        String abs = fileStorageService.absolutePath(id);
        com.blog.entity.FileStorage fs = fileStorageService.getById(id);
        Resource resource = new UrlResource(Paths.get(abs).toUri());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + URLEncoder.encode(fs.getName(), StandardCharsets.UTF_8) + "\"")
                .body(resource);
    }
}