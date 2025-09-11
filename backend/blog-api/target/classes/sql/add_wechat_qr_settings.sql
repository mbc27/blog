-- 为系统设置表添加微信二维码和微信公众号二维码配置项
-- 注意：system_settings表使用键值对存储方式

INSERT INTO `system_settings` (setting_key, setting_value, description, create_time, update_time) 
VALUES 
('wechat_qr_code', '', '微信二维码图片地址', NOW(), NOW()),
('wechat_official_qr_code', '', '微信公众号二维码图片地址', NOW(), NOW())
ON DUPLICATE KEY UPDATE 
setting_value = VALUES(setting_value), 
update_time = NOW();