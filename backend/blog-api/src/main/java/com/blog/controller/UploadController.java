package com.blog.controller;

import com.blog.utils.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin
public class UploadController {

    @Value("${file.upload.path:/uploads/}")
    private String uploadPath;

    @Value("${file.upload.url:http://localhost:8080/uploads/}")
    private String uploadUrl;

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
            // 创建上传目录
            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String uploadDir = uploadPath + datePath;
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
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
            file.transferTo(targetFile);

            // 返回文件访问URL
            String fileUrl = uploadUrl + datePath + "/" + filename;
            
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
    public Result uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.<Map<String, String>>error("请选择要上传的头像");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只能上传图片文件");
        }

        // 检查文件大小 (1MB)
        if (file.getSize() > 1024 * 1024) {
            return Result.<Map<String, String>>error("头像文件大小不能超过1MB");
        }

        try {
            // 创建头像上传目录
            String uploadDir = uploadPath + "avatars";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
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
            file.transferTo(targetFile);

            // 返回文件访问URL
            String fileUrl = uploadUrl + "avatars/" + filename;
            
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", filename);
            result.put("originalName", originalFilename);
            result.put("size", String.valueOf(file.getSize()));

            return Result.success(result);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.<Map<String, String>>error("头像上传失败: " + e.getMessage());
        }
    }
}