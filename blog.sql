-- 博客系统数据库脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE blog;

-- 用户表
CREATE TABLE IF NOT EXISTS `tb_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `status` int(1) DEFAULT '1' COMMENT '状态 0:禁用，1:正常',
  `role` int(1) DEFAULT '1' COMMENT '角色 0:管理员，1:普通用户',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 分类表
CREATE TABLE IF NOT EXISTS `tb_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类极速',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `description` varchar(255) DEFAULT NULL COMMENT '分类描述',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- 标签表
CREATE TABLE IF NOT EXISTS `tb_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` varchar(50) NOT NULL COMMENT '标签名称',
  `color` varchar(20) DEFAULT NULL COMMENT '标签颜色',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- 文章表
CREATE TABLE IF NOT EXISTS `tb_article` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `title` varchar(100) NOT NULL COMMENT '文章标题',
  `summary` varchar(255) DEFAULT NULL COMMENT '文章摘要',
  `content` longtext COMMENT '文章内容',
  `cover` varchar(255) DEFAULT NULL COMMENT '文章封面',
  `category_id` bigint(20) DEFAULT NULL COMMENT '分类ID',
  `user_id` bigint(20) NOT NULL COMMENT '作者ID',
  `view_count` int(11) DEFAULT '0' COMMENT '浏览量',
  `comment_count` int(11) DEFAULT '0' COMMENT '评论数',
  `like_count` int(11) DEFAULT '0' COMMENT '点赞数',
  `is_top` int(1) DEFAULT '0' COMMENT '是否置顶 0:否 1:是',
  `status` int(1) DEFAULT '1' COMMENT '状态 0:草稿 1:已发布',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

-- 文章标签关联表
CREATE TABLE IF NOT EXISTS `tb_article_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `article_id` bigint(20) NOT NULL COMMENT '文章ID',
  `tag_id` bigint(20) NOT NULL COMMENT '标签ID',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_article_tag` (`article_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章标签关联表';

-- 评论表
CREATE TABLE IF NOT EXISTS `tb_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `content` varchar(500) NOT NULL COMMENT '评论内容',
  `article_id` bigint(20) NOT NULL COMMENT '文章ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父评论ID',
  `to_user_id` bigint(20) DEFAULT NULL COMMENT '回复用户ID',
  `level` int(1) DEFAULT '1' COMMENT '评论层级',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  PRIMARY KEY (`id`),
  KEY `idx_article_id` (`article_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 相册分类表
CREATE TABLE IF NOT EXISTS `tb_photo_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `description` varchar(255) DEFAULT NULL COMMENT '分类描述',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name` (`name`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COMMENT='相册分类表';

-- 相册表
CREATE TABLE IF NOT EXISTS `tb_photo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '相册ID',
  `title` varchar(100) DEFAULT NULL COMMENT '相册标题',
  `description` varchar(255) DEFAULT NULL COMMENT '相册描述',
  `url` varchar(255) NOT NULL COMMENT '图片地址',
  `category_id` bigint(20) DEFAULT NULL COMMENT '分类ID',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序',
  `view_count` int(11) DEFAULT '0' COMMENT '浏览量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='相册表';

-- 友链表
CREATE TABLE IF NOT EXISTS `tb_friend_link` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '友链ID',
  `name` varchar(50) NOT NULL COMMENT '友链名称',
  `description` varchar(255) DEFAULT NULL COMMENT '友链描述',
  `url` varchar(255) NOT NULL COMMENT '友链极速',
  `avatar` varchar(255) DEFAULT NULL COMMENT '友链头像',
  `status` int(2) DEFAULT '0' COMMENT '友链状态 极速:待审核 1:通过 2:拒绝',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='友链表';

-- 留言表
CREATE TABLE IF NOT EXISTS `tb_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '留言ID',
  `content` varchar(500) NOT NULL COMMENT '留言内容',
  `nickname` varchar(50) NOT NULL COMMENT '留言人昵极速',
  `email` varchar(100) DEFAULT NULL COMMENT '留言人邮箱',
  `avatar` varchar(255) DEFAULT NULL COMMENT '留言人头像',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父留言ID',
  `to_nickname` varchar(50) DEFAULT NULL COMMENT '回复用户昵称',
  `level` int(1) DEFAULT '1' COMMENT '留言层级',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `location` varchar(100) DEFAULT NULL COMMENT '地理位置',
  `browser` varchar(100) DEFAULT NULL COMMENT '浏览器',
  `os` varchar(100) DEFAULT NULL COMMENT '操作系统',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='留言表';

-- 网站配置表
CREATE TABLE IF NOT EXISTS `tb_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(50) NOT NULL COMMENT '配置键',
  `config_value` text COMMENT '配置值',
  `description` varchar(255) DEFAULT NULL COMMENT '配置描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网站配置表';

-- 添加外键约束
ALTER TABLE `tb_article_tag` 
ADD CONSTRAINT `fk_article_tag_article` FOREIGN KEY (`article_id`) REFERENCES `tb_article` (`id`) ON DELETE CASCADE,
ADD CONSTRAINT `fk_article_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tb_tag` (`id`) ON DELETE CASCADE;

ALTER TABLE `tb_article` 
ADD CONSTRAINT `fk_article_category` FOREIGN KEY (`category_id`) REFERENCES `tb_category` (`id`) ON DELETE SET NULL,
ADD CONSTRAINT `fk_article_user` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE;

ALTER TABLE `tb_comment` 
ADD CONSTRAINT `fk极速_article` FOREIGN KEY (`article_id`) REFERENCES `tb_article` (`id`) ON DELETE CASCADE,
ADD CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE,
ADD CONSTRAINT `fk_comment_parent` FOREIGN KEY (`parent_id`) REFERENCES `tb_comment` (`id`) ON DELETE CASCADE;

ALTER TABLE `tb_photo` 
ADD CONSTRAINT `fk_photo_category` FOREIGN KEY (`category_id`) REFERENCES `tb_photo_category` (`id`) ON DELETE SET NULL;

-- 初始化数据
-- 管理员账号 (密码: 123456)
INSERT INTO `tb_user` (`username`, `password`, `nickname`, `avatar`, `email`, `phone`, `status`, `role`) 
VALUES ('admin', '$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm', '管理员', 'https://example.com/avatar.png', 'admin@example.com', '13800138000', 1, 0);

-- 分类数据
INSERT INTO `tb_category` (`name`, `description`, `sort_order`) VALUES 
('技术', '技术相关文章', 1),
('生活', '生活随笔', 2),
('资源', '资源分享', 3);

-- 标签数据
INSERT INTO `tb_tag` (`name`, `color`, `sort_order`) VALUES 
('Java', '#007bff', 1),
('Spring', '#28a745', 2),
('Vue', '#17a2b8', 3),
('MySQL', '#dc3545', 4),
('Linux', '#fd7e14', 5);

-- 相册分类
INSERT INTO `tb_photo_category` (`name`, `description`, `sort_order`) VALUES 
('风景', '风景照片', 1),
('人物', '人物照片', 2),
('美食', '美食照片', 3);

-- 网站配置
INSERT INTO `tb_config` (`config_key`, `config_value`, `description`) VALUES 
('site_name', '我的博客', '网站名称'),
('site_description', '一个基于Spring Boot和Vue的博客系统', '网站描述'),
('site_keywords', '博客,Spring Boot,Vue,Java', '网站关键词'),
('site_author', '管理员', '网站作者'),
('site_icp', '', 'ICP备案号'),
('site_copyright', 'Copyright © 2024 我的博客', '版权信息'),
('site_logo', '', '网站Logo'),
('site_favicon', '', '网站图标');

-- 测试文章数据
INSERT INTO `tb_article` (`title`, `summary`, `content`, `cover`, `category_id`, `user_id`, `view_count`, `comment_count`, `like_count`, `is_top`, `status`) VALUES 
('欢迎使用博客系统', '这是系统的第一篇文章，欢迎使用本博客系统', '## 欢迎使用博客系统\n\n这是一个基于Spring Boot和Vue.js开发的前后端分离博客系统。\n\n### 功能特性\n- 文章管理\n- 分类标签\n- 评论系统\n- 相册功能\n- 友链管理\n- 后台管理\n\n### 技术栈\n- 后端：Spring Boot + MyBatis Plus极速 前端：Vue.js + Element UI\n- 数据库：MySQL\n- 安全：JWT认证\n\n希望您使用愉快！', NULL, 1, 1, 100, 5, 10, 1, 1),
('Spring Boot入门教程', 'Spring Boot基础入门教程，适合初学者', '## Spring Boot入门\n\nSpring Boot让Spring应用开发变得简单。\n\n### 主要特性\n- 自动配置\n- 起步依赖\n- 命令行界面\n- Actuator监控\n\n### 快速开始\n```java\n@SpringBootApplication\npublic class Application {\n    public static void main(String[] args) {\n        SpringApplication.run(Application.class, args);\n    }\n}\n```', NULL, 1, 1, 50, 3, 8, 0, 1);

-- 文章标签关联
INSERT INTO `tb_article_tag` (`article_id`, `tag_id`) VALUES 
(1, 1), (1, 2), (1, 3),
(2, 1), (2, 2);

-- 测试评论
INSERT INTO `tb_comment` (`content`, `article_id`, `user_id`, `parent_id`, `level`) VALUES 
('很好的文章！', 1, 1, NULL, 1),
('谢谢分享！', 1, 1, NULL, 1),
('学习了，很有帮助', 2, 1, NULL, 1);

-- 测试相册数据
INSERT INTO `tb_photo` (`title`, `description`, `url`, `category_id`, `view_count`) VALUES 
('美丽风景', '一张美丽的风景照片', '/images/landscape1.jpg', 1, 20),
('人物肖像', '精美的人物肖像', '/images/portrait1.jpg', 2, 15),
('美食展示', '诱人的美食照片', '/images/food1.jpg', 3, 25);

-- 测试友链
INSERT INTO `tb_friend_link` (`name`, `description`, `url`, `avatar`, `status`, `sort_order`) VALUES 
('示例友链1', '这是一个示例友链', 'https://example1.com', '/images/friend1.png', 1, 1),
('示例友链2', '这是另一个示例友链', 'https://example2.com', '/images/friend2.png', 1, 2);