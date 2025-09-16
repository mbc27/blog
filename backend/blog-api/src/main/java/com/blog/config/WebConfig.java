package com.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.path:images/}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 使用统一的images目录 - 确保指向博客项目根目录
        String projectRoot = System.getProperty("user.dir");
        // 如果当前在backend/blog-api目录，需要回到博客项目根目录
        if (projectRoot.endsWith("backend" + File.separator + "blog-api")) {
            projectRoot = Paths.get(projectRoot).getParent().getParent().toString();
        }
        File imagesDir = Paths.get(projectRoot, "images").toFile();
        String absolutePath = imagesDir.getAbsolutePath().replace("\\", "/");
        if (!absolutePath.endsWith("/")) {
            absolutePath += "/";
        }
        
        // 主要的图片资源映射 - 统一使用/images路径
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + absolutePath);
        
        // 为了向后兼容，保留/uploads映射到images目录
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absolutePath);
        
        // 添加调试日志
        System.out.println("=== 统一图片路径配置 ===");
        System.out.println("Project root: " + projectRoot);
        System.out.println("Images directory: " + absolutePath);
        System.out.println("Resource mapping: /images/** -> file:" + absolutePath);
        System.out.println("Backward compatibility: /uploads/** -> file:" + absolutePath);
        
        // 确保images目录存在
        if (!imagesDir.exists()) {
            boolean created = imagesDir.mkdirs();
            System.out.println("Created images directory: " + created + " at " + imagesDir.getAbsolutePath());
        }
    }


}