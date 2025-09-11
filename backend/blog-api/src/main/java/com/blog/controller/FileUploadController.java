package com.blog.controller;

import com.blog.utils.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    @Value("${file.upload.path:./uploads/}")
    private String uploadPath;

    @PostMapping("/upload")
    public Result uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        return uploadFileInternal(file, "general", request);
    }
    
    private Result uploadFileInternal(MultipartFile file, String type, HttpServletRequest request) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        try {
            // 获取文件原始名称和扩展名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(".")) {
                return Result.error("文件名无效");
            }
            
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            
            // 验证文件类型（仅允许图片）
            if (!isImageFile(extension)) {
                return Result.error("只允许上传图片文件");
            }
            
            // 验证文件大小（最大2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                return Result.error("文件大小不能超过2MB");
            }
            
            // 生成文件名
            String fileName = type + "_" + UUID.randomUUID().toString() + extension;
            
            // 创建按日期分组的目录结构
            LocalDate now = LocalDate.now();
            String datePath = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            
            // 确保上传目录存在
            File uploadDir = new File(uploadPath + datePath);
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                System.out.println("创建上传目录: " + uploadDir.getAbsolutePath() + ", 结果: " + created);
            }
            
            // 保存文件
            File destFile = new File(uploadDir, fileName);
            file.transferTo(destFile);
            
            // 构建访问URL
            String scheme = request.getScheme();
            String serverName = request.getServerName();
            int serverPort = request.getServerPort();
            String contextPath = request.getContextPath();
            
            String baseUrl = scheme + "://" + serverName;
            if (serverPort != 80 && serverPort != 443) {
                baseUrl += ":" + serverPort;
            }
            baseUrl += contextPath;
            
            String fileUrl = baseUrl + "/uploads/" + datePath + "/" + fileName;
            
            System.out.println("文件上传成功: " + destFile.getAbsolutePath());
            System.out.println("访问URL: " + fileUrl);
            
            // 返回详细信息
            Map<String, Object> data = new HashMap<>();
            data.put("url", fileUrl);
            data.put("filename", fileName);
            data.put("originalName", originalFilename);
            data.put("size", file.getSize());
            data.put("type", type);
            
            return Result.success(data);
            
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
    
    private boolean isImageFile(String extension) {
        String[] allowedExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};
        String lowerExtension = extension.toLowerCase();
        for (String allowed : allowedExtensions) {
            if (lowerExtension.equals(allowed)) {
                return true;
            }
        }
        return false;
    }
}