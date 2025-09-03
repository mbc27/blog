-- 创建系统设置表
CREATE TABLE IF NOT EXISTS `tb_system_settings` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_name` varchar(100) NOT NULL DEFAULT '' COMMENT '网站名称',
  `site_description` text COMMENT '网站描述',
  `site_keywords` varchar(500) DEFAULT '' COMMENT '网站关键词',
  `site_logo` varchar(255) DEFAULT '' COMMENT '网站Logo',
  `site_favicon` varchar(255) DEFAULT '' COMMENT '网站图标',
  `site_icp` varchar(100) DEFAULT '' COMMENT '网站备案号',
  `site_copyright` varchar(255) DEFAULT '' COMMENT '网站版权信息',
  `site_author` varchar(100) DEFAULT '' COMMENT '网站作者',
  `site_email` varchar(100) DEFAULT '' COMMENT '网站邮箱',
  `site_qq` varchar(50) DEFAULT '' COMMENT '网站QQ',
  `site_wechat` varchar(100) DEFAULT '' COMMENT '网站微信',
  `site_github` varchar(255) DEFAULT '' COMMENT '网站GitHub',
  `site_gitee` varchar(255) DEFAULT '' COMMENT '网站Gitee',
  `comment_enabled` tinyint(1) DEFAULT 1 COMMENT '是否开启评论',
  `message_enabled` tinyint(1) DEFAULT 1 COMMENT '是否开启留言',
  `register_enabled` tinyint(1) DEFAULT 1 COMMENT '是否开启注册',
  `email_verify_enabled` tinyint(1) DEFAULT 0 COMMENT '是否开启邮箱验证',
  `articles_per_page` int(11) DEFAULT 10 COMMENT '每页显示文章数量',
  `comments_per_page` int(11) DEFAULT 10 COMMENT '每页显示评论数量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统设置表';

-- 插入默认设置
INSERT INTO `tb_system_settings` (
  `site_name`, `site_description`, `site_keywords`, `site_logo`, `site_favicon`,
  `site_icp`, `site_copyright`, `site_author`, `site_email`, `site_qq`,
  `site_wechat`, `site_github`, `site_gitee`, `comment_enabled`, `message_enabled`,
  `register_enabled`, `email_verify_enabled`, `articles_per_page`, `comments_per_page`
) VALUES (
  '我的博客',
  '一个基于Spring Boot + Vue的个人博客系统',
  '博客,个人博客,技术博客,Spring Boot,Vue',
  '/images/logo.png',
  '/images/favicon.ico',
  '',
  '© 2025 我的博客. All rights reserved.',
  '博主',
  'admin@example.com',
  '',
  '',
  '',
  '',
  1,
  1,
  1,
  0,
  10,
  10
) ON DUPLICATE KEY UPDATE `id` = `id`;