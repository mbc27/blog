package com.blog.service;

import com.blog.dto.ContactMessageDto;

/**
 * 邮件服务接口
 */
public interface EmailService {
    
    /**
     * 发送联系消息邮件
     * 
     * @param contactMessage 联系消息
     * @return 是否发送成功
     */
    boolean sendContactMessage(ContactMessageDto contactMessage);
    
    /**
     * 测试邮件配置
     * 
     * @return 是否配置正确
     */
    boolean testEmailConfiguration();
    
    /**
     * 发送验证码邮件
     * @param email 收件人邮箱
     * @param code 验证码
     * @param purpose 用途（如：忘记密码）
     * @return 发送结果
     */
    boolean sendVerificationCode(String email, String code, String purpose);
}