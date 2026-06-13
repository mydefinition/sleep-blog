package top.gosleep.blog.config;

import jakarta.validation.constraints.NotNull;
import top.gosleep.blog.common.PathUtil;
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

    @Value("${app.storage-path}")
    private String storageDir;

    public static final String STORAGE_URL_PREFIX = "/storage/";

    @PostConstruct
    void init() {
        storageDir = PathUtil.absolute(storageDir);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 文件存储路径映射（文章图片+文件存储）
        registry.addResourceHandler(STORAGE_URL_PREFIX + "**")
                .addResourceLocations("file:" + storageDir + "/");

        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        // 放行 API 请求，由 Controller 处理
                        if (resourcePath.startsWith("api/"))
                            return null;
                        Resource resource = location.createRelative(resourcePath);
                        // 如果资源存在且可读，则直接返回
                        if (resource.exists() && resource.isReadable())
                            return resource;
                        // 检查 Knife4j 相关资源，放行 doc.html 和 webjars 目录
                        if (resourcePath.equals("doc.html") || resourcePath.startsWith("webjars/")) {
                            Resource knife4jResource = new ClassPathResource("META-INF/resources/" + resourcePath);
                            if (knife4jResource.exists())
                                return knife4jResource;
                            return null;
                        }
                        // 其他情况均返回 index.html，由前端路由处理
                        return new ClassPathResource("static/index.html");
                    }
                });
    }
}
