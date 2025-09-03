package com.blog.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 文件上传工具类
 */
@Component
public class FileUtils {

    /**
     * 上传文件保存的本地目录
     */
    @Value("${file.upload.path:D:/Trae-AI/Project/blog/backend/blog-api/src/main/resources/static/uploads/}")
    private String uploadPath;

    /**
     * 上传文件的访问URL前缀
     */
    @Value("${upload.url-prefix:http://localhost:8080/uploads/}")
    private String urlPrefix;

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL或相对路径
     * @return 是否删除成功
     */
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            return false;
        }
        
        try {
            String filePath;
            
            // 如果是完整URL，提取相对路径
            if (fileUrl.startsWith("http://") || fileUrl.startsWith("https://")) {
                // 从URL中提取文件路径
                if (fileUrl.contains("/uploads/")) {
                    filePath = fileUrl.substring(fileUrl.indexOf("/uploads/") + 9); // 去掉 "/uploads/"
                } else if (fileUrl.contains("/upload/")) {
                    filePath = fileUrl.substring(fileUrl.indexOf("/upload/") + 8); // 去掉 "/upload/"
                } else {
                    return false;
                }
            } else if (fileUrl.startsWith("/uploads/")) {
                filePath = fileUrl.substring(9); // 去掉 "/uploads/"
            } else if (fileUrl.startsWith("/upload/")) {
                filePath = fileUrl.substring(8); // 去掉 "/upload/"
            } else {
                filePath = fileUrl;
            }
            
            // 构建完整的文件路径，确保路径分隔符正确
            String fullPath;
            if (uploadPath.endsWith("/") || uploadPath.endsWith("\\")) {
                fullPath = uploadPath + filePath;
            } else {
                fullPath = uploadPath + File.separator + filePath;
            }
            
            File file = new File(fullPath);
            
            // 检查文件是否存在并删除
            if (file.exists() && file.isFile()) {
                boolean deleted = file.delete();
                if (deleted) {
                    System.out.println("成功删除文件: " + file.getAbsolutePath());
                } else {
                    System.err.println("删除文件失败: " + file.getAbsolutePath());
                }
                return deleted;
            } else {
                System.out.println("文件不存在: " + file.getAbsolutePath());
                return true; // 文件不存在也算删除成功
            }
        } catch (Exception e) {
            System.err.println("删除文件时发生异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 上传文件
     *
     * @param file 文件对象
     * @return 文件访问URL
     * @throws IOException IO异常
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // 获取文件原始名称
        String originalFilename = file.getOriginalFilename();
        // 获取文件后缀
        String suffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        // 使用UUID重命名文件
        String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;
        
        // 按日期生成目录
        String dateDir = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        File targetDir = new File(uploadPath + dateDir);
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        
        // 保存文件
        File targetFile = new File(targetDir, fileName);
        file.transferTo(targetFile);
        
        // 返回文件访问URL
        return urlPrefix + dateDir + "/" + fileName;
    }
}