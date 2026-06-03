package com.blog.service.impl;

import com.blog.common.PathUtil;
import com.blog.service.ImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {

    @Value("${app.storage.image-upload}")
    private String imageDir;

    @PostConstruct
    void init() {
        imageDir = PathUtil.absolute(imageDir);
    }

    public String upload(MultipartFile file) throws IOException {
        Path dir = Paths.get(imageDir);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        String originalName = file.getOriginalFilename();
        String ext = originalName != null && originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf(".")) : "";
        String filename = UUID.randomUUID().toString() + ext;

        Path target = dir.resolve(filename);
        file.transferTo(target.toFile());

        return "/storage/" + filename;
    }
}
