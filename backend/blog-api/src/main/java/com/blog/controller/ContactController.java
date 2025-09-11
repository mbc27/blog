package com.blog.controller;

import com.blog.dto.ContactMessageDto;
import com.blog.service.EmailService;
import com.blog.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 联系控制器
 */
@RestController
@RequestMapping("/api/contact")
@CrossOrigin
public class ContactController {

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private EmailService emailService;

    /**
     * 发送联系消息
     *
     * @param contactMessage 联系消息
     * @param bindingResult 验证结果
     * @return 发送结果
     */
    @PostMapping("/send")
    public Result sendContactMessage(@Valid @RequestBody ContactMessageDto contactMessage, 
                                   BindingResult bindingResult) {
        try {
            // 验证输入参数
            if (bindingResult.hasErrors()) {
                String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
                return Result.error(errorMessage);
            }

            logger.info("收到联系消息，发件人: {}, 邮箱: {}, 主题: {}", 
                       contactMessage.getName(), contactMessage.getEmail(), contactMessage.getSubject());

            // 发送邮件
            boolean success = emailService.sendContactMessage(contactMessage);
            
            if (success) {
                logger.info("联系消息发送成功");
                return Result.success("消息发送成功！我们会尽快回复您。");
            } else {
                logger.error("联系消息发送失败");
                return Result.error("消息发送失败，请稍后重试或直接发送邮件联系我们。");
            }
            
        } catch (Exception e) {
            logger.error("发送联系消息时发生异常", e);
            return Result.error("消息发送失败：" + e.getMessage());
        }
    }

    /**
     * 测试邮件配置
     *
     * @return 测试结果
     */
    @GetMapping("/test-email")
    public Result testEmailConfiguration() {
        try {
            boolean isConfigured = emailService.testEmailConfiguration();
            if (isConfigured) {
                return Result.success("邮件配置正常");
            } else {
                return Result.error("邮件配置不完整，请检查系统设置");
            }
        } catch (Exception e) {
            logger.error("测试邮件配置时发生异常", e);
            return Result.error("邮件配置测试失败：" + e.getMessage());
        }
    }
}