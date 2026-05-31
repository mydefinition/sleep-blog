package com.blog.config;

import com.blog.common.PathUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import jakarta.annotation.PostConstruct;
import java.io.IOException;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.image.dir}")
    private String imageDir;

    @PostConstruct
    void init() {
        imageDir = PathUtil.absolute(imageDir);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + imageDir + "/");

        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        if (resourcePath.startsWith("api/")) {
                            return null;
                        }
                        Resource resource = location.createRelative(resourcePath);
                        if (resource.exists() && resource.isReadable()) {
                            return resource;
                        }
                        if (resourcePath.equals("doc.html") || resourcePath.startsWith("webjars/")) {
                            Resource knife4jResource = new ClassPathResource("META-INF/resources/" + resourcePath);
                            if (knife4jResource.exists()) {
                                return knife4jResource;
                            }
                            return null;
                        }
                        return new ClassPathResource("static/index.html");
                    }
                });
    }
}
