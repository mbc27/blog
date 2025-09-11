package com.blog.service.impl;

import com.blog.dto.ContactMessageDto;
import com.blog.service.EmailService;
import com.blog.service.SystemSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Properties;

@Service
public class UniversalEmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(UniversalEmailServiceImpl.class);

    @Autowired
    private SystemSettingsService systemSettingsService;

    @Override
    public boolean sendContactMessage(ContactMessageDto contactMessage) {
        try {
            Map<String, String> settings = systemSettingsService.getAllSettings();
            EmailConfig emailConfig = parseEmailConfig(settings);
            
            if (!emailConfig.isValid()) {
                logger.error("Email configuration is incomplete");
                return false;
            }

            JavaMailSender mailSender = createMailSender(emailConfig);
            SimpleMailMessage message = createMessage(contactMessage, emailConfig);
            mailSender.send(message);
            
            logger.info("Email sent successfully to: {}", emailConfig.getToEmail());
            return true;
            
        } catch (Exception e) {
            logger.error("Failed to send email", e);
            return false;
        }
    }

    @Override
    public boolean testEmailConfiguration() {
        try {
            Map<String, String> settings = systemSettingsService.getAllSettings();
            EmailConfig emailConfig = parseEmailConfig(settings);
            return emailConfig.isValid();
        } catch (Exception e) {
            logger.error("Failed to test email configuration", e);
            return false;
        }
    }

    @Override
    public boolean sendVerificationCode(String email, String code, String purpose) {
        try {
            Map<String, String> settings = systemSettingsService.getAllSettings();
            EmailConfig emailConfig = parseEmailConfig(settings);
            
            if (!emailConfig.isValid()) {
                logger.error("Email configuration is incomplete");
                return false;
            }

            JavaMailSender mailSender = createMailSender(emailConfig);
            SimpleMailMessage message = createVerificationMessage(email, code, purpose, emailConfig);
            mailSender.send(message);
            
            logger.info("Verification code email sent successfully to: {}, purpose: {}", email, purpose);
            return true;
            
        } catch (Exception e) {
            logger.error("Failed to send verification code email", e);
            return false;
        }
    }

    private EmailConfig parseEmailConfig(Map<String, String> settings) {
        EmailConfig config = new EmailConfig();
        
        config.setHost(settings.get("email_host"));
        config.setPort(parsePort(settings.get("email_port")));
        config.setUsername(settings.get("email_username"));
        config.setPassword(settings.get("email_password"));
        config.setFromEmail(settings.get("email_from"));
        config.setToEmail(settings.get("about_email"));
        
        logger.info("Email config - Host: {}, Port: {}, Username: {}, FromEmail: {}, ToEmail: {}", 
                   config.getHost(), config.getPort(), config.getUsername(), 
                   config.getFromEmail(), config.getToEmail());
        
        if ((config.getFromEmail() == null || config.getFromEmail().trim().isEmpty()) && 
            config.getUsername() != null && !config.getUsername().trim().isEmpty()) {
            config.setFromEmail(config.getUsername());
            logger.info("Using username as from email: {}", config.getFromEmail());
        }
        
        autoConfigureEmailProvider(config);
        return config;
    }

    private void autoConfigureEmailProvider(EmailConfig config) {
        String host = config.getHost();
        String username = config.getUsername();
        String fromEmail = config.getFromEmail();
        
        if (host == null || host.trim().isEmpty()) {
            host = inferHostFromEmail(fromEmail != null ? fromEmail : username);
            config.setHost(host);
        }
        
        configureByHost(config, host);
        
        if (isSendGrid(config)) {
            configureSendGrid(config);
        }
    }

    private String inferHostFromEmail(String email) {
        if (email == null) return null;
        
        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();
        
        switch (domain) {
            case "qq.com":
                return "smtp.qq.com";
            case "163.com":
                return "smtp.163.com";
            case "126.com":
                return "smtp.126.com";
            case "gmail.com":
                return "smtp.gmail.com";
            case "outlook.com":
            case "hotmail.com":
                return "smtp-mail.outlook.com";
            case "yahoo.com":
                return "smtp.mail.yahoo.com";
            default:
                if (domain.contains(".")) {
                    return "smtp." + domain;
                }
                return null;
        }
    }

    private void configureByHost(EmailConfig config, String host) {
        if (host == null) return;
        
        host = host.toLowerCase();
        
        if (config.getPort() == 0) {
            if (host.contains("sendgrid")) {
                config.setPort(587);
            } else if (host.contains("qq.com")) {
                config.setPort(587);
            } else if (host.contains("163.com") || host.contains("126.com")) {
                config.setPort(465);
                config.setSslEnable(true);
            } else if (host.contains("gmail.com")) {
                config.setPort(587);
            } else if (host.contains("outlook.com")) {
                config.setPort(587);
            } else {
                config.setPort(587);
            }
        }
        
        if (host.contains("163.com") || host.contains("126.com") || config.getPort() == 465) {
            config.setSslEnable(true);
        } else {
            config.setStartTlsEnable(true);
        }
    }

    private boolean isSendGrid(EmailConfig config) {
        return (config.getHost() != null && config.getHost().contains("sendgrid")) ||
               (config.getUsername() != null && config.getUsername().equals("apikey")) ||
               (config.getPassword() != null && config.getPassword().startsWith("SG."));
    }

    private void configureSendGrid(EmailConfig config) {
        config.setHost("smtp.sendgrid.net");
        config.setPort(587);
        config.setUsername("apikey");
        config.setStartTlsEnable(true);
        config.setSslEnable(false);
        
        if (config.getFromEmail() == null || config.getFromEmail().trim().isEmpty()) {
            logger.error("SendGrid requires from email configuration");
            throw new RuntimeException("SendGrid requires from email configuration");
        } else {
            logger.info("SendGrid config with verified from email: {}", config.getFromEmail());
        }
    }

    private JavaMailSender createMailSender(EmailConfig config) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        mailSender.setHost(config.getHost());
        mailSender.setPort(config.getPort());
        mailSender.setUsername(config.getUsername());
        mailSender.setPassword(config.getPassword());
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "false");
        
        if (config.isStartTlsEnable()) {
            props.put("mail.smtp.starttls.enable", "true");
        }
        
        if (config.isSslEnable()) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.port", String.valueOf(config.getPort()));
        }
        
        props.put("mail.smtp.timeout", "25000");
        props.put("mail.smtp.connectiontimeout", "25000");
        
        logger.info("Mail sender configured - Host: {}, Port: {}, Username: {}, SSL: {}, StartTLS: {}", 
                   config.getHost(), config.getPort(), config.getUsername(), 
                   config.isSslEnable(), config.isStartTlsEnable());
        
        return mailSender;
    }

    private SimpleMailMessage createMessage(ContactMessageDto contactMessage, EmailConfig config) {
        SimpleMailMessage message = new SimpleMailMessage();
        
        String fromEmail = cleanEmailAddress(config.getFromEmail());
        if (fromEmail == null || fromEmail.trim().isEmpty()) {
            fromEmail = cleanEmailAddress(config.getUsername());
            logger.info("Using username as from email: {}", fromEmail);
        }
        
        String toEmail = cleanEmailAddress(config.getToEmail());
        String replyToEmail = cleanEmailAddress(contactMessage.getEmail());
        
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("[网站联系] " + contactMessage.getSubject());
        message.setText(buildEmailContent(contactMessage));
        message.setReplyTo(replyToEmail);
        
        logger.info("Email message created - From: {}, To: {}, ReplyTo: {}", fromEmail, toEmail, replyToEmail);
        
        return message;
    }

    private SimpleMailMessage createVerificationMessage(String email, String code, String purpose, EmailConfig config) {
        SimpleMailMessage message = new SimpleMailMessage();
        
        String fromEmail = cleanEmailAddress(config.getFromEmail());
        if (fromEmail == null || fromEmail.trim().isEmpty()) {
            fromEmail = cleanEmailAddress(config.getUsername());
            logger.info("Using username as from email: {}", fromEmail);
        }
        
        String toEmail = cleanEmailAddress(email);
        
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("[博客系统] " + purpose + "验证码");
        message.setText(buildVerificationContent(code, purpose));
        
        logger.info("Verification email message created - From: {}, To: {}, Purpose: {}", fromEmail, toEmail, purpose);
        
        return message;
    }
    
    private String cleanEmailAddress(String email) {
        if (email == null) {
            return null;
        }
        
        String cleaned = email.trim();
        if (cleaned.toLowerCase().startsWith("mailto:")) {
            cleaned = cleaned.substring(7);
        }
        
        cleaned = cleaned.trim();
        return cleaned.isEmpty() ? null : cleaned;
    }

    private String buildEmailContent(ContactMessageDto contactMessage) {
        StringBuilder content = new StringBuilder();
        
        content.append("您收到了来自网站的联系消息\n");
        for (int i = 0; i < 50; i++) content.append("=");
        content.append("\n\n");
        
        content.append("发件人信息：\n");
        content.append("姓名：").append(contactMessage.getName()).append("\n");
        content.append("邮箱：").append(contactMessage.getEmail()).append("\n\n");
        
        content.append("消息详情：\n");
        content.append("主题：").append(contactMessage.getSubject()).append("\n");
        content.append("时间：").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");
        
        content.append("消息内容：\n");
        for (int i = 0; i < 30; i++) content.append("-");
        content.append("\n");
        content.append(contactMessage.getContent()).append("\n");
        for (int i = 0; i < 30; i++) content.append("-");
        content.append("\n\n");
        
        content.append("请及时回复此消息。\n");
        content.append("回复至：").append(contactMessage.getEmail()).append("\n\n");
        
        content.append("此邮件由博客系统自动发送，请勿直接回复此邮件。");
        
        return content.toString();
    }

    private String buildVerificationContent(String code, String purpose) {
        StringBuilder content = new StringBuilder();
        
        content.append("您好！\n\n");
        content.append("您正在进行").append(purpose).append("操作，您的验证码是：\n\n");
        
        for (int i = 0; i < 30; i++) content.append("=");
        content.append("\n");
        content.append("验证码：").append(code).append("\n");
        for (int i = 0; i < 30; i++) content.append("=");
        content.append("\n\n");
        
        content.append("验证码有效期为10分钟，请及时使用。\n");
        content.append("如果这不是您的操作，请忽略此邮件。\n\n");
        
        content.append("发送时间：").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");
        
        content.append("此邮件由博客系统自动发送，请勿直接回复。");
        
        return content.toString();
    }

    private int parsePort(String portStr) {
        if (portStr == null || portStr.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(portStr.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static class EmailConfig {
        private String host;
        private int port;
        private String username;
        private String password;
        private String fromEmail;
        private String toEmail;
        private boolean sslEnable = false;
        private boolean startTlsEnable = true;

        public boolean isValid() {
            if (toEmail == null || toEmail.trim().isEmpty()) {
                logger.error("Email config validation failed: missing recipient email");
                return false;
            }
            
            if (username == null || username.trim().isEmpty()) {
                logger.error("Email config validation failed: missing username");
                return false;
            }
            
            if (password == null || password.trim().isEmpty()) {
                logger.error("Email config validation failed: missing password");
                return false;
            }
            
            if (host == null || host.trim().isEmpty()) {
                logger.error("Email config validation failed: missing host");
                return false;
            }
            
            if (isServiceWithOptionalFromEmail()) {
                logger.info("Email config validation passed: using special service config");
                return true;
            }
            
            if (fromEmail == null || fromEmail.trim().isEmpty()) {
                logger.error("Email config validation failed: missing from email");
                return false;
            }
            
            logger.info("Email config validation passed: all required fields configured");
            return true;
        }
        
        private boolean isServiceWithOptionalFromEmail() {
            if ((host != null && host.toLowerCase().contains("sendgrid")) ||
                (username != null && username.equals("apikey")) ||
                (password != null && password.startsWith("SG."))) {
                return true;
            }
            return false;
        }

        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }
        
        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getFromEmail() { return fromEmail; }
        public void setFromEmail(String fromEmail) { this.fromEmail = fromEmail; }
        
        public String getToEmail() { return toEmail; }
        public void setToEmail(String toEmail) { this.toEmail = toEmail; }
        
        public boolean isSslEnable() { return sslEnable; }
        public void setSslEnable(boolean sslEnable) { this.sslEnable = sslEnable; }
        
        public boolean isStartTlsEnable() { return startTlsEnable; }
        public void setStartTlsEnable(boolean startTlsEnable) { this.startTlsEnable = startTlsEnable; }
    }
}