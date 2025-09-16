package com.blog.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 图片资源控制器
 * 注意：图片静态资源由WebConfig处理，此控制器保留用于其他图片相关API
 */
@RestController
@RequestMapping("/api/images")
@CrossOrigin
public class ImageController {

    // 图片静态资源访问由WebConfig的addResourceHandlers处理
    // 路径映射: /images/** -> file:项目根目录/images/
    
    // 此控制器可用于图片上传、删除等API操作
}