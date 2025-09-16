package com.blog.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * 统一图片资源处理器
 * 支持统一的images目录和向后兼容的uploads目录
 */
@Slf4j
@Component
public class MultiUploadsResourceHandler extends ResourceHttpRequestHandler {

    private List<String> imageDirectories = new ArrayList<>();
    private boolean initialized = false;

    /**
     * 初始化图片目录列表
     */
    private synchronized void initializeImageDirectories() {
        if (initialized) {
            return;
        }

        try {
            String projectRoot = System.getProperty("user.dir");
            log.info("初始化图片资源目录: {}", projectRoot);
            
            imageDirectories.clear();
            
            // 添加统一的images目录
            Path imagesPath = Paths.get(projectRoot, "images");
            if (Files.exists(imagesPath)) {
                imageDirectories.add(imagesPath.toAbsolutePath().toString());
                log.info("添加主图片目录: {}", imagesPath.toAbsolutePath());
            }
            
            // 为了向后兼容，扫描现有的uploads目录
            scanForUploadsDirectories(Paths.get(projectRoot));
            
            log.info("找到 {} 个图片目录: {}", imageDirectories.size(), imageDirectories);
            initialized = true;
            
        } catch (Exception e) {
            log.error("初始化图片目录列表失败", e);
        }
    }

    /**
     * 递归扫描uploads目录（向后兼容）
     */
    private void scanForUploadsDirectories(Path rootPath) {
        try (Stream<Path> paths = Files.walk(rootPath, 4)) { // 限制扫描深度为4层
            paths.filter(Files::isDirectory)
                 .filter(path -> "uploads".equals(path.getFileName().toString()))
                 .forEach(uploadsPath -> {
                     String absolutePath = uploadsPath.toAbsolutePath().toString();
                     imageDirectories.add(absolutePath);
                     log.debug("发现uploads目录（向后兼容）: {}", absolutePath);
                 });
        } catch (Exception e) {
            log.warn("扫描目录 {} 时出错: {}", rootPath, e.getMessage());
        }
    }

    @Override
    protected Resource getResource(HttpServletRequest request) throws IOException {
        if (!initialized) {
            initializeImageDirectories();
        }

        String path = (String) request.getAttribute("org.springframework.web.servlet.HandlerMapping.pathWithinHandlerMapping");
        
        if (path == null) {
            return null;
        }

        // 移除开头的斜杠
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        log.debug("查找图片资源: {}", path);

        // 策略1：在所有图片目录中按原路径查找文件
        for (String imageDir : imageDirectories) {
            File file = new File(imageDir, path);
            if (file.exists() && file.isFile()) {
                log.debug("找到文件: {} -> {}", path, file.getAbsolutePath());
                return new FileSystemResource(file);
            }
        }

        // 策略2：如果路径包含uploads或images，尝试从相应路径开始查找
        if (path.contains("uploads/")) {
            int uploadsIndex = path.indexOf("uploads/");
            String relativePath = path.substring(uploadsIndex + "uploads/".length());
            
            for (String imageDir : imageDirectories) {
                File file = new File(imageDir, relativePath);
                if (file.exists() && file.isFile()) {
                    log.debug("通过uploads路径找到文件: {} -> {}", relativePath, file.getAbsolutePath());
                    return new FileSystemResource(file);
                }
            }
        }
        
        if (path.contains("images/")) {
            int imagesIndex = path.indexOf("images/");
            String relativePath = path.substring(imagesIndex + "images/".length());
            
            for (String imageDir : imageDirectories) {
                File file = new File(imageDir, relativePath);
                if (file.exists() && file.isFile()) {
                    log.debug("通过images路径找到文件: {} -> {}", relativePath, file.getAbsolutePath());
                    return new FileSystemResource(file);
                }
            }
        }

        // 策略3：智能匹配文件名
        String fileName = extractFileName(path);
        if (fileName != null) {
            for (String imageDir : imageDirectories) {
                File foundFile = findFileByName(new File(imageDir), fileName);
                if (foundFile != null) {
                    log.debug("通过文件名匹配找到文件: {} -> {}", fileName, foundFile.getAbsolutePath());
                    return new FileSystemResource(foundFile);
                }
            }
        }

        log.debug("未找到图片文件: {}", path);
        return null;
    }

    /**
     * 从路径中提取文件名
     */
    private String extractFileName(String path) {
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash >= 0 && lastSlash < path.length() - 1) {
            return path.substring(lastSlash + 1);
        }
        return path;
    }

    /**
     * 在目录中递归查找指定文件名的文件
     */
    private File findFileByName(File directory, String fileName) {
        if (!directory.exists() || !directory.isDirectory()) {
            return null;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return null;
        }

        // 首先在当前目录查找
        for (File file : files) {
            if (file.isFile() && fileName.equals(file.getName())) {
                return file;
            }
        }

        // 然后在子目录中递归查找（限制深度）
        for (File file : files) {
            if (file.isDirectory() && !file.getName().startsWith(".")) {
                File found = findFileByName(file, fileName);
                if (found != null) {
                    return found;
                }
            }
        }

        return null;
    }
}