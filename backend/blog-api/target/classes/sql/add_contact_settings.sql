-- 添加联系信息相关的系统设置
INSERT INTO `system_settings` (`setting_key`, `setting_value`, `description`, `create_time`, `update_time`) VALUES
('about_email', 'contact@example.com', '关于我页面的邮箱地址', NOW(), NOW()),
('about_github', 'https://github.com', '关于我页面的GitHub地址', NOW(), NOW()),
('about_wechat', '微信号', '关于我页面的微信号', NOW(), NOW()),
('about_wechat_public', '公众号名称', '关于我页面的微信公众号', NOW(), NOW()),
('email_host', 'smtp.qq.com', '邮件服务器地址', NOW(), NOW()),
('email_port', '587', '邮件服务器端口', NOW(), NOW()),
('email_username', '', '邮件服务器用户名', NOW(), NOW()),
('email_password', '', '邮件服务器密码', NOW(), NOW()),
('email_from', '', '发件人邮箱地址', NOW(), NOW())
ON DUPLICATE KEY UPDATE 
`setting_value` = VALUES(`setting_value`),
`update_time` = NOW();