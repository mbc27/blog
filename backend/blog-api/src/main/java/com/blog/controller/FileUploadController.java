package com.blog.controller;

import com.blog.utils.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    @PostMapping("/upload")
    public Result uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        try {
            // 获取文件原始名称
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            
            // 生成新的文件名
            String fileName = UUID.randomUUID().toString() + extension;
            
            // 确保上传目录存在
            File uploadDir = new File("src/main/resources/static/images");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 保存文件
            File destFile = new File(uploadDir, fileName);
            file.transferTo(destFile);
            
            // 返回文件访问路径
            String fileUrl = "/images/" + fileName;
            return Result.success(fileUrl);
            
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败");
        }
    }
}