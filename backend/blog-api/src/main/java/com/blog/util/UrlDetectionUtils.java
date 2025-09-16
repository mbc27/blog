package com.blog.util;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * URL动态检测工具类
 * 自动检测当前请求的服务器地址，确保跨平台兼容性
 */
@Component
public class UrlDetectionUtils {
    
    /**
     * 获取当前请求的基础URL
     * @return 基础URL，格式如：http://192.168.175.1:8080
     */
    public String getCurrentBaseUrl() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String scheme = request.getScheme(); // http 或 https
                String serverName = request.getServerName(); // 服务器名称或IP
                int serverPort = request.getServerPort(); // 端口号
                
                StringBuilder baseUrl = new StringBuilder();
                baseUrl.append(scheme).append("://").append(serverName);
                
                // 只有在非标准端口时才添加端口号
                if ((scheme.equals("http") && serverPort != 80) || 
                    (scheme.equals("https") && serverPort != 443)) {
                    baseUrl.append(":").append(serverPort);
                }
                
                return baseUrl.toString();
            }
        } catch (Exception e) {
            System.err.println("获取当前请求URL失败: " + e.getMessage());
        }
        
        // 如果无法获取当前请求，返回默认值
        return "http://localhost:8080";
    }
    
    /**
     * 获取当前请求的图片URL前缀
     * @return 图片URL前缀，格式如：http://192.168.175.1:8080/images/
     */
    public String getCurrentImageUrlPrefix() {
        return getCurrentBaseUrl() + "/images/";
    }
    
    /**
     * 获取当前请求的上传URL前缀（向后兼容）
     * @return 上传URL前缀，格式如：http://192.168.175.1:8080/images/
     */
    public String getCurrentUploadUrlPrefix() {
        return getCurrentImageUrlPrefix();
    }
    
    /**
     * 将相对路径转换为完整的URL
     * @param relativePath 相对路径，如：2025/09/13/image.jpg
     * @return 完整URL，如：http://192.168.175.1:8080/images/2025/09/13/image.jpg
     */
    public String toFullUrl(String relativePath) {
        if (relativePath == null || relativePath.trim().isEmpty()) {
            return "";
        }
        
        // 如果已经是完整URL，直接返回
        if (relativePath.startsWith("http://") || relativePath.startsWith("https://")) {
            return relativePath;
        }
        
        // 确保相对路径不以/开头
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
        
        return getCurrentImageUrlPrefix() + relativePath;
    }
    
    /**
     * 从完整URL中提取相对路径
     * @param fullUrl 完整URL
     * @return 相对路径
     */
    public String extractRelativePath(String fullUrl) {
        if (fullUrl == null || fullUrl.trim().isEmpty()) {
            return "";
        }
        
        // 如果不是完整URL，直接返回
        if (!fullUrl.startsWith("http://") && !fullUrl.startsWith("https://")) {
            return fullUrl;
        }
        
        // 提取/images/后面的部分
        if (fullUrl.contains("/images/")) {
            return fullUrl.substring(fullUrl.indexOf("/images/") + 8);
        }
        
        // 向后兼容：提取/uploads/后面的部分
        if (fullUrl.contains("/uploads/")) {
            return fullUrl.substring(fullUrl.indexOf("/uploads/") + 9);
        }
        
        return fullUrl;
    }
    
    /**
     * 获取当前服务器的IP地址
     * 优先获取非回环地址（非127.0.0.1）
     * @return 服务器IP地址
     */
    public String getServerIpAddress() {
        try {
            // 首先尝试从当前请求获取
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String serverName = request.getServerName();
                
                // 如果不是localhost或127.0.0.1，直接返回
                if (!serverName.equals("localhost") && !serverName.equals("127.0.0.1")) {
                    return serverName;
                }
            }
            
            // 如果从请求中无法获取有效IP，尝试获取本机IP
            return getLocalIpAddress();
        } catch (Exception e) {
            System.err.println("获取服务器IP地址失败: " + e.getMessage());
            return "localhost";
        }
    }
    
    /**
     * 获取本机的IP地址（非127.0.0.1）
     * @return 本机IP地址
     */
    private String getLocalIpAddress() {
        try {
            // 获取所有网络接口
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                // 排除回环接口、虚拟接口等
                if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()) {
                    continue;
                }
                
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    String ipAddress = addr.getHostAddress();
                    
                    // 只获取IPv4地址
                    if (ipAddress != null && ipAddress.matches("\\d+\\.\\d+\\.\\d+\\.\\d+") && !ipAddress.equals("127.0.0.1")) {
                        return ipAddress;
                    }
                }
            }
        } catch (SocketException e) {
            System.err.println("获取本机IP地址失败: " + e.getMessage());
        }
        
        return "localhost";
    }
}