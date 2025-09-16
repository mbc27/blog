package com.blog.controller;

import com.blog.util.UrlDetectionUtils;
import com.blog.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin
public class UploadController {

    @Value("${file.upload.path:images/}")
    private String uploadPath;

    @Autowired
    private UrlDetectionUtils urlDetectionUtils;
    
    /**
     * 获取博客项目根目录下的上传目录绝对路径
     */
    private String getUploadDirectory() {
        String projectRoot = System.getProperty("user.dir");
        // 如果当前在backend/blog-api目录，需要回到博客项目根目录
        if (projectRoot.endsWith("backend" + File.separator + "blog-api")) {
            projectRoot = Paths.get(projectRoot).getParent().getParent().toString();
        }
        return Paths.get(projectRoot, uploadPath).toString();
    }

    @PostMapping("/image")
    public Result uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.<Map<String, String>>error("请选择要上传的文件");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.<Map<String, String>>error("只能上传图片文件");
        }

        // 检查文件大小 (2MB)
        if (file.getSize() > 2 * 1024 * 1024) {
            return Result.<Map<String, String>>error("文件大小不能超过2MB");
        }

        try {
            // 创建上传目录 - 使用项目根目录的绝对路径
            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String baseUploadDir = getUploadDirectory();
            String uploadDir = Paths.get(baseUploadDir, datePath).toString();
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                System.out.println("Created upload directory: " + created + " at " + dir.getAbsolutePath());
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;

            // 保存文件
            File targetFile = new File(dir, filename);
            System.out.println("Saving file to: " + targetFile.getAbsolutePath());
            file.transferTo(targetFile);

            // 返回文件访问URL - 使用统一的images路径
            String fileUrl = urlDetectionUtils.getCurrentBaseUrl() + "/images/" + datePath + "/" + filename;
            
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", filename);
            result.put("originalName", originalFilename);
            result.put("size", String.valueOf(file.getSize()));

            return Result.success(result);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.<Map<String, String>>error("文件上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/avatar")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Result uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的头像");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只能上传图片文件");
        }

        // 检查文件大小 (1MB)
        if (file.getSize() > 1024 * 1024) {
            return Result.error("头像文件大小不能超过1MB");
        }

        try {
            // 创建头像上传目录 - 使用项目根目录的绝对路径
            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String baseUploadDir = getUploadDirectory();
            String uploadDir = Paths.get(baseUploadDir, datePath).toString();
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                System.out.println("Created avatar upload directory: " + created + " at " + dir.getAbsolutePath());
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = "avatar_" + UUID.randomUUID().toString() + extension;

            // 保存文件
            File targetFile = new File(dir, filename);
            System.out.println("Saving avatar to: " + targetFile.getAbsolutePath());
            file.transferTo(targetFile);

            // 返回文件访问URL - 使用统一的images路径
            String fileUrl = urlDetectionUtils.getCurrentBaseUrl() + "/images/" + datePath + "/" + filename;
            
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", filename);
            result.put("originalName", originalFilename);
            result.put("size", String.valueOf(file.getSize()));

            return Result.success(result);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("头像上传失败: " + e.getMessage());
        }
    }
}