package com.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.path:./uploads/}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保路径以斜杠结尾
        String normalizedPath = uploadPath;
        if (!normalizedPath.endsWith("/") && !normalizedPath.endsWith("\\")) {
            normalizedPath += "/";
        }
        
        // 创建File对象处理路径
        File uploadDir = new File(normalizedPath);
        
        // 获取绝对路径并标准化
        String absolutePath = uploadDir.getAbsolutePath().replace("\\", "/");
        if (!absolutePath.endsWith("/")) {
            absolutePath += "/";
        }
        
        // 配置上传文件的静态资源访问
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absolutePath);
        
        // 添加调试日志
        System.out.println("Upload path config: " + uploadPath);
        System.out.println("Normalized path: " + normalizedPath);
        System.out.println("Absolute path: " + absolutePath);
        System.out.println("Static resource mapping: /uploads/** -> file:" + absolutePath);
        System.out.println("Upload directory exists: " + uploadDir.exists());
        
        // 确保上传目录存在
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            System.out.println("Created upload directory: " + created + " at " + uploadDir.getAbsolutePath());
        }
    }
}