package top.gosleep.blog.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import top.gosleep.blog.bean.dto.request.MkdirRequest;
import top.gosleep.blog.bean.vo.CloudFileVO;
import top.gosleep.blog.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.gosleep.blog.service.CloudStorageService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/cloud")
@Tag(name = "云文件")
public class CloudStorageController {

    private final CloudStorageService cloudStorageService;

    public CloudStorageController(CloudStorageService cloudStorageService) {
        this.cloudStorageService = cloudStorageService;
    }

    @PostMapping("/upload")
    @Operation(summary = "上传文件")
    public Result<?> upload(@RequestParam MultipartFile file,
                            @RequestParam(required = false) Long localId) throws IOException {
        cloudStorageService.upload(file, localId);
        return Result.ok();
    }

    @PostMapping("/mkdir")
    @Operation(summary = "新建文件夹")
    public Result<?> mkdir(@RequestBody MkdirRequest req) {
        cloudStorageService.mkdir(req.getLocalId(), req.getDirName());
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除文件或文件夹")
    public Result<?> delete(@PathVariable Long id) {
        cloudStorageService.delete(id);
        return Result.ok();
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "下载文件")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        return cloudStorageService.getFile(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "显示目录")
    public Result<List<CloudFileVO>> list(@PathVariable Long id) {
        return Result.ok(cloudStorageService.listVO(id));
    }
}