package com.blog.controller;

import com.blog.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 上传重定向控制器 - 为了向后兼容
 * 将 /upload/* 请求重定向到 /api/upload/*
 */
@RestController
@RequestMapping("/upload")
@CrossOrigin
public class UploadRedirectController {

    @Autowired
    private UploadController uploadController;

    @PostMapping("/image")
    public Result uploadImage(@RequestParam("file") MultipartFile file) {
        // 直接调用原有的上传控制器
        return uploadController.uploadImage(file);
    }

    @PostMapping("/avatar")
    public Result uploadAvatar(@RequestParam("file") MultipartFile file) {
        // 直接调用原有的上传控制器
        return uploadController.uploadAvatar(file);
    }
}