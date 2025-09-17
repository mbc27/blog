-- =====================================================
-- 博客系统完整数据库脚本
-- 包含所有表结构、约束、索引和初始化数据
-- 创建时间: 2025年1月
-- =====================================================

-- =====================================================
-- 1. 核心业务表结构
-- =====================================================

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
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类',
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

-- 文章点赞记录表
CREATE TABLE IF NOT EXISTS `tb_article_like` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `article_id` bigint(20) NOT NULL COMMENT '文章ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_article_user` (`article_id`,`user_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_article_like_article` FOREIGN KEY (`article_id`) REFERENCES `tb_article` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_article_like_user` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章点赞记录表';

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

-- =====================================================
-- 2. 相册功能表
-- =====================================================

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

-- =====================================================
-- 3. 社交功能表
-- =====================================================

-- 友链表
CREATE TABLE IF NOT EXISTS `tb_friend_link` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '友链ID',
  `name` varchar(50) NOT NULL COMMENT '友链名称',
  `description` varchar(255) DEFAULT NULL COMMENT '友链描述',
  `url` varchar(255) NOT NULL COMMENT '友链',
  `avatar` varchar(255) DEFAULT NULL COMMENT '友链头像',
  `status` int(2) DEFAULT '0' COMMENT '友链状态 :待审核 1:通过 2:拒绝',
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
  `nickname` varchar(50) NOT NULL COMMENT '留言人昵',
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

-- =====================================================
-- 4. 系统配置表
-- =====================================================

-- 系统设置表
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

-- =====================================================
-- 5. AI助手功能表
-- =====================================================

-- AI配置表
CREATE TABLE IF NOT EXISTS `ai_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(100) NOT NULL COMMENT '配置键',
  `config_value` text COMMENT '配置值',
  `config_name` varchar(200) DEFAULT NULL COMMENT '配置名称',
  `description` varchar(500) DEFAULT NULL COMMENT '配置描述',
  `config_group` varchar(50) DEFAULT NULL COMMENT '配置分组',
  `enabled` tinyint(1) DEFAULT '1' COMMENT '是否启用：1-启用，0-禁用',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  KEY `idx_config_group` (`config_group`),
  KEY `idx_enabled` (`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI配置表';

-- AI聊天会话表
CREATE TABLE IF NOT EXISTS `ai_chat_session` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `session_id` varchar(64) NOT NULL COMMENT '会话标识符（UUID）',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID（游客为null）',
  `title` varchar(200) DEFAULT NULL COMMENT '会话标题',
  `session_type` tinyint(4) DEFAULT '1' COMMENT '会话类型：1-普通对话，2-写作辅助，3-文章润色',
  `article_id` bigint(20) DEFAULT NULL COMMENT '关联文章ID（写作辅助时使用）',
  `status` tinyint(4) DEFAULT '1' COMMENT '会话状态：1-活跃，0-已结束',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_session_id` (`session_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_session_type` (`session_type`),
  KEY `idx_status` (`status`),
  KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI聊天会话表';

-- AI聊天消息表
CREATE TABLE IF NOT EXISTS `ai_chat_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `session_id` bigint(20) NOT NULL COMMENT '会话ID',
  `role` varchar(20) NOT NULL COMMENT '消息角色：user-用户，assistant-AI助手，system-系统',
  `content` longtext NOT NULL COMMENT '消息内容',
  `message_type` tinyint(4) DEFAULT '1' COMMENT '消息类型：1-文本，2-图片，3-文件',
  `status` tinyint(4) DEFAULT '1' COMMENT '消息状态：1-正常，0-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_session_id` (`session_id`),
  KEY `idx_role` (`role`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI聊天消息表';

-- =====================================================
-- 6. 关于我页面相关表
-- =====================================================

-- 个人基本信息表
CREATE TABLE IF NOT EXISTS `tb_about_me` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `title` varchar(100) DEFAULT NULL COMMENT '职位头衔',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `introduction` text COMMENT '个人简介',
  `location` varchar(100) DEFAULT NULL COMMENT '所在地',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '电话',
  `github` varchar(255) DEFAULT NULL COMMENT 'GitHub地址',
  `linkedin` varchar(255) DEFAULT NULL COMMENT 'LinkedIn地址',
  `website` varchar(255) DEFAULT NULL COMMENT '个人网站',
  `wechat` varchar(100) DEFAULT NULL COMMENT '微信号',
  `qq` varchar(20) DEFAULT NULL COMMENT 'QQ号',
  `birth_date` date DEFAULT NULL COMMENT '出生日期',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态 0禁用 1启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='个人基本信息表';

-- 工作经历表
CREATE TABLE IF NOT EXISTS `tb_work_experience` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `company_name` varchar(100) NOT NULL COMMENT '公司名称',
  `position` varchar(100) NOT NULL COMMENT '职位',
  `department` varchar(100) DEFAULT NULL COMMENT '部门',
  `start_date` date NOT NULL COMMENT '开始时间',
  `end_date` date DEFAULT NULL COMMENT '结束时间',
  `is_current` tinyint(1) DEFAULT 0 COMMENT '是否当前工作 0否 1是',
  `location` varchar(100) DEFAULT NULL COMMENT '工作地点',
  `company_logo` varchar(255) DEFAULT NULL COMMENT '公司Logo',
  `job_description` text COMMENT '工作描述',
  `achievements` text COMMENT '主要成就',
  `technologies` varchar(500) DEFAULT NULL COMMENT '使用技术栈',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作经历表';

-- 实习经历表
CREATE TABLE IF NOT EXISTS `tb_internship_experience` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `company_name` varchar(100) NOT NULL COMMENT '公司名称',
  `position` varchar(100) NOT NULL COMMENT '实习职位',
  `department` varchar(100) DEFAULT NULL COMMENT '部门',
  `start_date` date NOT NULL COMMENT '开始时间',
  `end_date` date NOT NULL COMMENT '结束时间',
  `location` varchar(100) DEFAULT NULL COMMENT '实习地点',
  `company_logo` varchar(255) DEFAULT NULL COMMENT '公司Logo',
  `job_description` text COMMENT '实习内容描述',
  `achievements` text COMMENT '实习成果',
  `technologies` varchar(500) DEFAULT NULL COMMENT '使用技术栈',
  `mentor` varchar(50) DEFAULT NULL COMMENT '指导老师导师',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实习经历表';

-- 教育背景表
CREATE TABLE IF NOT EXISTS `tb_education` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `school_name` varchar(100) NOT NULL COMMENT '学校名称',
  `degree` varchar(50) NOT NULL COMMENT '学位',
  `major` varchar(100) NOT NULL COMMENT '专业',
  `start_date` date NOT NULL COMMENT '入学时间',
  `end_date` date DEFAULT NULL COMMENT '毕业时间',
  `is_current` tinyint(1) DEFAULT 0 COMMENT '是否在读 0否 1是',
  `location` varchar(100) DEFAULT NULL COMMENT '学校地点',
  `school_logo` varchar(255) DEFAULT NULL COMMENT '学校Logo',
  `gpa` decimal(3,2) DEFAULT NULL COMMENT 'GPA成绩',
  `description` text COMMENT '教育经历描述',
  `achievements` text COMMENT '主要成就荣誉',
  `courses` text COMMENT '主要课程',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教育背景表';

-- 技能分类表
CREATE TABLE IF NOT EXISTS `tb_skill_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '技能分类名称',
  `description` varchar(255) DEFAULT NULL COMMENT '分类描述',
  `icon` varchar(100) DEFAULT NULL COMMENT '分类图标',
  `color` varchar(20) DEFAULT NULL COMMENT '分类颜色',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能分类表';

-- 技能表
CREATE TABLE IF NOT EXISTS `tb_skill` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '技能名称',
  `category_id` bigint(20) NOT NULL COMMENT '技能分类ID',
  `proficiency` int(3) NOT NULL DEFAULT 0 COMMENT '熟练度0-100',
  `years_experience` decimal(3,1) DEFAULT NULL COMMENT '使用年限',
  `description` text COMMENT '技能描述',
  `icon` varchar(100) DEFAULT NULL COMMENT '技能图标',
  `color` varchar(20) DEFAULT NULL COMMENT '技能颜色',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  CONSTRAINT `fk_skill_category` FOREIGN KEY (`category_id`) REFERENCES `tb_skill_category` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能表';

-- 个人兴趣表
CREATE TABLE IF NOT EXISTS `tb_interest` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '兴趣名称',
  `description` text COMMENT '兴趣描述',
  `icon` varchar(100) DEFAULT NULL COMMENT '兴趣图标',
  `color` varchar(20) DEFAULT NULL COMMENT '颜色',
  `level` varchar(20) DEFAULT NULL COMMENT '兴趣程度初级中级高级专业',
  `years_experience` decimal(3,1) DEFAULT NULL COMMENT '接触年限',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='个人兴趣表';

-- 项目分类表
CREATE TABLE IF NOT EXISTS `tb_project_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '项目分类名称',
  `description` varchar(255) DEFAULT NULL COMMENT '分类描述',
  `icon` varchar(100) DEFAULT NULL COMMENT '分类图标',
  `color` varchar(20) DEFAULT NULL COMMENT '分类颜色',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目分类表';

-- 开发项目表
CREATE TABLE IF NOT EXISTS `tb_project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '项目名称',
  `title` varchar(150) DEFAULT NULL COMMENT '项目标题',
  `description` text COMMENT '项目描述',
  `summary` varchar(500) DEFAULT NULL COMMENT '项目简介',
  `category_id` bigint(20) DEFAULT NULL COMMENT '项目分类ID',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '项目封面图',
  `images` text COMMENT '项目截图JSON数组',
  `technologies` varchar(500) DEFAULT NULL COMMENT '使用技术栈',
  `start_date` date DEFAULT NULL COMMENT '开始时间',
  `end_date` date DEFAULT NULL COMMENT '结束时间',
  `status` varchar(20) DEFAULT 'completed' COMMENT '项目状态planning developing completed maintenance',
  `project_type` varchar(20) DEFAULT 'personal' COMMENT '项目类型personal work school open-source',
  `team_size` int(3) DEFAULT 1 COMMENT '团队规模',
  `my_role` varchar(100) DEFAULT NULL COMMENT '我的角色',
  `github_url` varchar(255) DEFAULT NULL COMMENT 'GitHub地址',
  `demo_url` varchar(255) DEFAULT NULL COMMENT '演示地址',
  `download_url` varchar(255) DEFAULT NULL COMMENT '下载地址',
  `documentation_url` varchar(255) DEFAULT NULL COMMENT '文档地址',
  `features` text COMMENT '主要功能特性',
  `challenges` text COMMENT '技术难点挑战',
  `achievements` text COMMENT '项目成果收获',
  `view_count` int(11) DEFAULT 0 COMMENT '浏览量',
  `like_count` int(11) DEFAULT 0 COMMENT '点赞数',
  `is_featured` tinyint(1) DEFAULT 0 COMMENT '是否精选项目 0否 1是',
  `is_public` tinyint(1) DEFAULT 1 COMMENT '是否公开 0否 1是',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_project_type` (`project_type`),
  CONSTRAINT `fk_project_category` FOREIGN KEY (`category_id`) REFERENCES `tb_project_category` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='开发项目表';

-- 项目技术标签关联表
CREATE TABLE IF NOT EXISTS `tb_project_tech` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `project_id` bigint(20) NOT NULL COMMENT '项目ID',
  `tech_name` varchar(50) NOT NULL COMMENT '技术名称',
  `tech_type` varchar(20) DEFAULT 'framework' COMMENT '技术类型language framework database tool other',
  `proficiency` varchar(20) DEFAULT 'intermediate' COMMENT '使用熟练度beginner intermediate advanced expert',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
  PRIMARY KEY (`id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_tech_name` (`tech_name`),
  CONSTRAINT `fk_project_tech_project` FOREIGN KEY (`project_id`) REFERENCES `tb_project` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目技术标签关联表';

-- 项目点赞记录表
CREATE TABLE IF NOT EXISTS `tb_project_like` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `project_id` bigint(20) NOT NULL COMMENT '项目ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_project_user` (`project_id`,`user_id`),
  UNIQUE KEY `idx_project_ip` (`project_id`,`ip_address`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_project_like_project` FOREIGN KEY (`project_id`) REFERENCES `tb_project` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目点赞记录表';

-- =====================================================
-- 7. 外键约束
-- =====================================================

-- 文章相关外键约束
ALTER TABLE `tb_article_tag` 
ADD CONSTRAINT `fk_article_tag_article` FOREIGN KEY (`article_id`) REFERENCES `tb_article` (`id`) ON DELETE CASCADE,
ADD CONSTRAINT `fk_article_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tb_tag` (`id`) ON DELETE CASCADE;

ALTER TABLE `tb_article` 
ADD CONSTRAINT `fk_article_category` FOREIGN KEY (`category_id`) REFERENCES `tb_category` (`id`) ON DELETE SET NULL,
ADD CONSTRAINT `fk_article_user` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE;

ALTER TABLE `tb_comment` 
ADD CONSTRAINT `fk_comment_article` FOREIGN KEY (`article_id`) REFERENCES `tb_article` (`id`) ON DELETE CASCADE,
ADD CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE,
ADD CONSTRAINT `fk_comment_parent` FOREIGN KEY (`parent_id`) REFERENCES `tb_comment` (`id`) ON DELETE CASCADE;

-- 相册相关外键约束
ALTER TABLE `tb_photo` 
ADD CONSTRAINT `fk_photo_category` FOREIGN KEY (`category_id`) REFERENCES `tb_photo_category` (`id`) ON DELETE SET NULL;

-- =====================================================
-- 8. 基础数据初始化
-- =====================================================

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

-- 系统设置默认数据
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

-- AI配置数据（优化后的通用配置）
INSERT INTO `ai_config` (`config_key`, `config_value`, `config_name`, `description`, `config_group`, `enabled`, `sort_order`) VALUES
-- 基础功能配置
('ai.enabled', '0', 'AI功能启用', '是否启用AI助手功能：1-启用，0-禁用', 'basic', 1, 1),
('ai.provider', 'deepseek', 'AI服务提供商', '当前使用的AI服务提供商标识', 'basic', 1, 2),

-- API接口配置
('ai.api.key', '', 'API密钥', 'AI服务的API密钥，请在管理后台配置', 'api', 1, 1),
('ai.api.url', 'https://api.deepseek.com/v1/chat/completions', 'API接口地址', 'AI服务的API请求地址', 'api', 1, 2),
('ai.model', 'deepseek-chat', 'AI模型', '使用的AI模型名称', 'api', 1, 3),
('ai.api.timeout', '30', 'API超时时间', 'API请求超时时间（秒）', 'api', 1, 4),

-- 模型参数配置
('ai.max.tokens', '1800', '最大Token数', 'AI回复的最大Token数量', 'model', 1, 1),
('ai.temperature', '0.6', '温度参数', 'AI回复的创造性程度，0-1之间', 'model', 1, 2),

-- 提示词配置
('ai.system.prompt', '你是一个智能的博客助手，可以帮助用户回答问题和提供写作建议。请以友好和专业的方式回应。', '系统提示词', 'AI助手的系统提示词', 'prompt', 1, 1),
('ai.writing.prompt', '你是一个专业的写作助手。请根据用户提供的内容和要求，提供具体的写作建议和改进意见。', '写作助手提示词', '写作辅助功能的提示词', 'prompt', 1, 2),
('ai.polish.prompt', '你是一个专业的文章编辑。请对用户提供的文章内容进行润色，提升语言表达、逻辑结构和可读性。', '文章润色提示词', '文章润色功能的提示词', 'prompt', 1, 3)
ON DUPLICATE KEY UPDATE 
`config_value` = VALUES(`config_value`),
`config_name` = VALUES(`config_name`),
`description` = VALUES(`description`),
`config_group` = VALUES(`config_group`),
`enabled` = VALUES(`enabled`),
`sort_order` = VALUES(`sort_order`);

-- =====================================================
-- 9. 测试数据
-- =====================================================

-- 测试文章数据
INSERT INTO `tb_article` (`title`, `summary`, `content`, `cover`, `category_id`, `user_id`, `view_count`, `comment_count`, `like_count`, `is_top`, `status`) VALUES 
('欢迎使用博客系统', '这是系统的第一篇文章，欢迎使用本博客系统', '## 欢迎使用博客系统\n\n这是一个基于Spring Boot和Vue.js开发的前后端分离博客系统。\n\n### 功能特性\n- 文章管理\n- 分类标签\n- 评论系统\n- 相册功能\n- 友链管理\n- 后台管理\n\n### 技术栈\n- 后端：Spring Boot + MyBatis Plus 前端：Vue.js + Element UI\n- 数据库：MySQL\n- 安全：JWT认证\n\n希望您使用愉快！', NULL, 1, 1, 100, 5, 10, 1, 1),
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

-- =====================================================
-- 10. 扩展测试数据
-- =====================================================

-- 添加测试用户
INSERT INTO `tb_user` (`username`, `password`, `nickname`, `avatar`, `email`, `phone`, `status`, `role`) 
VALUES ('testuser', '$2a$10$KYi8n7vnr2.2OK7IFdiklO3Ex9elWDhch7fX.etQe1c5gtTjHBKQC', '测试用户', 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png', 'test@example.com', '13900139000', 1, 1);

-- 添加更多文章
INSERT INTO `tb_article` (`title`, `summary`, `content`, `cover`, `category_id`, `user_id`, `view_count`, `comment_count`, `like_count`, `is_top`, `status`) VALUES 
('Vue.js 实战教程', 'Vue.js 从入门到精通，包含实战案例', '## Vue.js 实战教程\n\n本教程将带你从零开始学习 Vue.js，并通过实战案例掌握 Vue.js 的核心概念和高级特性。\n\n### 核心概念\n- 组件化开发\n- 响应式数据绑定\n- 指令系统\n- 生命周期钩子\n- 路由管理\n- 状态管理\n\n### 实战案例\n```javascript\nnew Vue({\n  el: "#app",\n  data: {\n    message: "Hello Vue!"\n  },\n  methods: {\n    greet() {\n      alert(this.message);\n    }\n  }\n});\n```', '/images/vue-cover.jpg', 1, 1, 80, 4, 12, 0, 1),
('MySQL 性能优化指南', 'MySQL 数据库性能优化的实用技巧', '## MySQL 性能优化指南\n\n本文将分享 MySQL 数据库性能优化的实用技巧，帮助你提升数据库性能。\n\n### 优化方向\n- 索引优化\n- SQL 语句优化\n- 表结构优化\n- 配置参数调优\n- 硬件升级\n\n### 索引优化示例\n```sql\n-- 为常用查询字段创建索引\nCREATE INDEX idx_username ON users(username);\n\n-- 为多字段查询创建复合索引\nCREATE INDEX idx_name_status ON products(name, status);\n```', '/images/mysql-cover.jpg', 1, 1, 65, 3, 9, 0, 1),
('旅行日记：丽江古城', '记录丽江古城的人文风景和美食体验', '## 丽江古城旅行日记\n\n丽江古城，这座拥有800多年历史的古城，是中国为数不多保存完好的少数民族古城之一。\n\n### 景点推荐\n- 四方街：古城的中心，也是纳西族传统的集市\n- 木府：丽江木氏土司的官邸，是丽江古城的标志性建筑\n- 黑龙潭公园：可以看到玉龙雪山的倒影，是拍照的绝佳地点\n\n### 美食推荐\n- 纳西烤鱼：鲜嫩多汁，香气四溢\n- 丽江粑粑：口感松软，甜而不腻\n- 鸡豆凉粉：清爽可口，开胃解腻\n\n旅行是一种放松身心的方式，也是一种认识世界的方式。希望我的旅行日记能给你带来一些启发和帮助。', '/images/lijiang-cover.jpg', 2, 1, 120, 8, 20, 0, 1);

-- 添加更多评论
INSERT INTO `tb_comment` (`content`, `article_id`, `user_id`, `parent_id`, `level`) VALUES 
('这篇文章写得非常好，内容详实，讲解清晰！', 3, 1, NULL, 1),
('我也是 Vue.js 的爱好者，这篇教程对我帮助很大！', 3, 2, NULL, 1),
('请问作者，Vue 3 和 Vue 2 的主要区别是什么？', 3, 2, NULL, 1),
('主要是 Composition API 的引入，以及性能的提升。', 3, 1, 3, 2),
('感谢解答！', 3, 2, 4, 2),
('MySQL 的索引优化这部分讲得很实用！', 4, 2, NULL, 1),
('我在工作中用到了这些技巧，效果很明显！', 4, 1, 6, 2),
('丽江确实是个美丽的地方，我去年去过一次。', 5, 2, NULL, 1);

-- 添加相册分类
INSERT INTO `tb_photo_category` (`name`, `description`, `sort_order`) VALUES 
('旅行', '旅行照片集', 4),
('建筑', '建筑摄影作品', 5),
('动物', '可爱的动物们', 6);

-- 添加相册照片
INSERT INTO `tb_photo` (`title`, `description`, `url`, `category_id`, `view_count`) VALUES 
('雪山日出', '玉龙雪山的壮丽日出', 'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b', 1, 45),
('古镇小巷', '江南水乡的幽静小巷', 'https://images.unsplash.com/photo-1470115636492-6d2b56f9146d', 1, 38),
('现代建筑', '城市中的现代建筑群', 'https://images.unsplash.com/photo-1486325212027-8081e485255e', 5, 27),
('古典建筑', '欧洲古典主义建筑风格', 'https://images.unsplash.com/photo-1459679749680-18eb1eb37418', 5, 31),
('可爱猫咪', '一只慵懒的橘猫', 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba', 6, 60),
('小狗特写', '一只好奇的小狗', 'https://images.unsplash.com/photo-1561037404-61cd46aa615b', 6, 52),
('美食摄影', '精致的法式甜点', 'https://images.unsplash.com/photo-1551024506-0bccd828d307', 3, 42),
('旅行风光', '托斯卡纳的田园风光', 'https://images.unsplash.com/photo-1512917774080-9991f1c4c750', 4, 36);

-- 添加友链
INSERT INTO `tb_friend_link` (`name`, `description`, `url`, `avatar`, `status`, `sort_order`) VALUES 
('技术博客', '分享前端和后端开发技术，包括Vue、React、Node.js等', 'https://example.com/tech-blog', 'https://images.unsplash.com/photo-1542831371-29b0f74f9713?w=80&h=80&fit=crop', 1, 3),
('设计分享', 'UI/UX设计作品展示和设计理念分享', 'https://example.com/design-blog', 'https://images.unsplash.com/photo-1558655146-d09347e92766?w=80&h=80&fit=crop', 1, 4),
('学习笔记', '记录学习过程中的笔记和心得体会', 'https://example.com/study-notes', 'https://images.unsplash.com/photo-1550592704-6c76defa9985?w=80&h=80&fit=crop', 1, 5),
('摄影作品', '分享风景、人像等摄影作品和摄影技巧', 'https://example.com/photography', 'https://images.unsplash.com/photo-1554080353-a576cf803bda?w=80&h=80&fit=crop', 1, 6),
('旅行日记', '记录世界各地的旅行经历和美食推荐', 'https://example.com/travel-diary', 'https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=80&h=80&fit=crop', 1, 7),
('生活随笔', '分享生活中的点滴感悟和思考', 'https://example.com/life-essays', 'https://images.unsplash.com/photo-1512236258305-32fb314d9a61?w=80&h=80&fit=crop', 1, 8);

-- 添加留言
INSERT INTO `tb_message` (`content`, `nickname`, `email`, `avatar`, `parent_id`, `level`) VALUES 
('博客做得很漂亮，内容也很丰富！', '访客A', 'visitor_a@example.com', 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=80&h=80&fit=crop', NULL, 1),
('请问博主是做什么工作的呀？', '访客B', 'visitor_b@example.com', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=80&h=80&fit=crop', NULL, 1),
('我是一名全栈开发工程师，主要使用Java和Vue技术栈。', '博主', 'admin@example.com', 'https://example.com/avatar.png', 2, 2),
('博客更新得很勤快，支持！', '访客C', 'visitor_c@example.com', 'https://images.unsplash.com/photo-1599566150163-29194dcaad36?w=80&h=80&fit=crop', NULL, 1),
('文章写得很专业，学到了很多知识！', '访客D', 'visitor_d@example.com', 'https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=80&h=80&fit=crop', NULL, 1),
('感谢支持！', '博主', 'admin@example.com', 'https://example.com/avatar.png', 5, 2);

-- =====================================================
-- 11. 关于我页面数据初始化
-- =====================================================

-- 插入个人基本信息
INSERT INTO `tb_about_me` (
  `name`, `title`, `introduction`, `location`, `email`, `github`, `website`
) VALUES (
  '张三', 
  '全栈开发工程师', 
  '热爱编程，专注于Web开发和移动应用开发。', 
  '北京市', 
  'zhangsan@example.com', 
  'https://github.com/zhangsan', 
  'https://zhangsan.dev'
) ON DUPLICATE KEY UPDATE `id` = `id`;

-- 插入技能分类
INSERT INTO `tb_skill_category` (`name`, `description`, `icon`, `color`, `sort_order`) VALUES 
('编程语言', '掌握的编程语言', 'el-icon-cpu', '#409EFF', 1),
('前端技术', '前端开发相关技术', 'el-icon-monitor', '#67C23A', 2),
('后端技术', '后端开发相关技术', 'el-icon-server', '#E6A23C', 3),
('数据库', '数据库相关技术', 'el-icon-coin', '#F56C6C', 4),
('开发工具', '开发工具和环境', 'el-icon-tools', '#909399', 5),
('其他技能', '其他相关技能', 'el-icon-star-off', '#606266', 6)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 插入技能数据
INSERT INTO `tb_skill` (`name`, `category_id`, `proficiency`, `years_experience`, `description`, `sort_order`) VALUES 
-- 编程语言
('Java', 1, 90, 3.5, 'Spring Boot企业级开发', 1),
('JavaScript', 1, 85, 3.0, 'ES6+、Node.js、TypeScript', 2),
('Python', 1, 75, 2.0, '数据分析、爬虫开发', 3),
('HTML/CSS', 1, 90, 4.0, '响应式布局、CSS3动画', 4),

-- 前端技术
('Vue.js', 2, 88, 2.5, 'Vue2/Vue3、Vuex、Vue Router', 1),
('React', 2, 75, 1.5, 'React Hooks、Redux', 2),
('Element UI', 2, 90, 2.0, '企业级UI组件库', 3),
('Webpack', 2, 70, 1.5, '前端构建工具', 4),

-- 后端技术
('Spring Boot', 3, 90, 3.0, '微服务架构、RESTful API', 1),
('Spring Cloud', 3, 80, 2.0, '分布式系统开发', 2),
('MyBatis Plus', 3, 85, 2.5, 'ORM框架', 3),
('Redis', 3, 75, 2.0, '缓存、分布式锁', 4),

-- 数据库
('MySQL', 4, 85, 3.0, '数据库设计、性能优化', 1),
('MongoDB', 4, 70, 1.0, 'NoSQL数据库', 2),

-- 开发工具
('Git', 5, 90, 4.0, '版本控制、团队协作', 1),
('Docker', 5, 75, 1.5, '容器化部署', 2),
('IntelliJ IDEA', 5, 95, 4.0, 'Java开发IDE', 3),
('VS Code', 5, 90, 3.0, '前端开发编辑器', 4)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 插入个人兴趣
INSERT INTO `tb_interest` (`name`, `description`, `icon`, `color`, `level`, `years_experience`, `sort_order`) VALUES 
('编程', '热爱编程，享受解决问题的过程', 'el-icon-cpu', '#409EFF', '专业', 5.0, 1),
('阅读', '喜欢阅读技术书籍和小说', 'el-icon-reading', '#67C23A', '高级', 10.0, 2),
('摄影', '记录生活中的美好瞬间', 'el-icon-camera', '#E6A23C', '中级', 3.0, 3),
('旅行', '探索不同的城市和文化', 'el-icon-map-location', '#F56C6C', '中级', 8.0, 4),
('音乐', '听音乐放松心情', 'el-icon-headset', '#909399', '初级', 15.0, 5),
('运动', '保持身体健康', 'el-icon-bicycle', '#606266', '中级', 5.0, 6)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 插入项目分类
INSERT INTO `tb_project_category` (`name`, `description`, `icon`, `color`, `sort_order`) VALUES 
('Web应用', 'Web应用程序项目', 'el-icon-monitor', '#409EFF', 1),
('移动应用', '移动端应用项目', 'el-icon-mobile-phone', '#67C23A', 2),
('桌面应用', '桌面应用程序项目', 'el-icon-desktop', '#E6A23C', 3),
('开源项目', '开源贡献项目', 'el-icon-share', '#F56C6C', 4),
('学习项目', '学习练习项目', 'el-icon-reading', '#909399', 5),
('工具类', '实用工具项目', 'el-icon-tools', '#606266', 6)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 插入示例项目数据
INSERT INTO `tb_project` (
  `name`, `title`, `description`, `summary`, `category_id`, `technologies`, 
  `start_date`, `end_date`, `status`, `project_type`, `team_size`, `my_role`,
  `github_url`, `demo_url`, `features`, `challenges`, `achievements`, `is_featured`, `sort_order`
) VALUES 
(
  'blog-system', 
  '个人博客系统', 
  '基于Spring Boot + Vue.js开发的前后端分离博客系统',
  '一个功能完整的个人博客系统',
  1,
  'Spring Boot,Vue.js,MySQL,Redis,Element UI,MyBatis Plus',
  '2024-01-01',
  '2024-03-01',
  'completed',
  'personal',
  1,
  '全栈开发',
  'https://github.com/zhangsan/blog-system',
  'https://blog.zhangsan.dev',
  '文章管理,分类标签,评论系统,相册功能',
  'JWT认证实现,文件上传处理',
  '完成了一个功能完整的博客系统',
  1,
  1
),
(
  'task-manager', 
  '任务管理系统', 
  '基于React + Node.js开发的任务管理系统',
  '一个高效的团队任务管理和协作平台',
  1,
  'React,Node.js,MongoDB,Socket.io,Ant Design',
  '2024-04-01',
  '2024-06-01',
  'completed',
  'work',
  3,
  '前端开发',
  'https://github.com/zhangsan/task-manager',
  'https://task.zhangsan.dev',
  '任务管理,团队协作,实时通信',
  '实时通信实现,复杂状态管理',
  '提升了React开发技能',
  1,
  2
)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 插入项目技术标签
INSERT INTO `tb_project_tech` (`project_id`, `tech_name`, `tech_type`, `proficiency`) VALUES 
-- 博客系统
(1, 'Spring Boot', 'framework', 'advanced'),
(1, 'Vue.js', 'framework', 'advanced'),
(1, 'MySQL', 'database', 'intermediate'),
(1, 'Redis', 'database', 'intermediate'),
(1, 'Element UI', 'framework', 'advanced'),

-- 任务管理系统
(2, 'React', 'framework', 'intermediate'),
(2, 'Node.js', 'framework', 'intermediate'),
(2, 'MongoDB', 'database', 'intermediate'),
(2, 'Socket.io', 'framework', 'beginner')
ON DUPLICATE KEY UPDATE `project_id` = VALUES(`project_id`);

-- 插入教育背景示例
INSERT INTO `tb_education` (
  `school_name`, `degree`, `major`, `start_date`, `end_date`, 
  `location`, `gpa`, `description`, `achievements`, `sort_order`
) VALUES 
(
  '北京理工大学', 
  '本科', 
  '计算机科学与技术', 
  '2020-09-01', 
  '2024-06-01', 
  '北京市', 
  3.8, 
  '主修计算机科学与技术专业',
  '获得校级奖学金,参与ACM竞赛',
  1
)
ON DUPLICATE KEY UPDATE `school_name` = VALUES(`school_name`);

-- 插入工作经历示例
INSERT INTO `tb_work_experience` (
  `company_name`, `position`, `department`, `start_date`, `end_date`, `is_current`,
  `location`, `job_description`, `achievements`, `technologies`, `sort_order`
) VALUES 
(
  '腾讯科技有限公司', 
  '前端开发工程师', 
  '技术部', 
  '2024-07-01', 
  NULL, 
  1,
  '深圳市', 
  '负责公司Web产品的前端开发工作',
  '完成了3个重要项目的前端开发',
  'Vue.js,React,TypeScript,Webpack,Element UI',
  1
)
ON DUPLICATE KEY UPDATE `company_name` = VALUES(`company_name`);

-- 插入实习经历示例
INSERT INTO `tb_internship_experience` (
  `company_name`, `position`, `department`, `start_date`, `end_date`,
  `location`, `job_description`, `achievements`, `technologies`, `mentor`, `sort_order`
) VALUES 
(
  '字节跳动', 
  '前端开发实习生', 
  '产品研发部', 
  '2024-03-01', 
  '2024-06-01',
  '北京市', 
  '参与公司内部管理系统的前端开发',
  '独立完成了用户管理模块的开发',
  'React,TypeScript,Ant Design,Git',
  '李导师',
  1
)
ON DUPLICATE KEY UPDATE `company_name` = VALUES(`company_name`);

-- =====================================================
-- 12. 系统配置补丁和修复脚本
-- =====================================================

-- 添加联系信息相关的系统设置
INSERT INTO `tb_system_settings` (`site_email`, `site_github`, `site_wechat`) VALUES
('contact@example.com', 'https://github.com', '微信号')
ON DUPLICATE KEY UPDATE 
`site_email` = VALUES(`site_email`),
`site_github` = VALUES(`site_github`),
`site_wechat` = VALUES(`site_wechat`);

-- 添加邮件服务器配置到config表
INSERT INTO `tb_config` (`config_key`, `config_value`, `description`) VALUES
('email_host', 'smtp.qq.com', '邮件服务器地址'),
('email_port', '587', '邮件服务器端口'),
('email_username', '', '邮件服务器用户名'),
('email_password', '', '邮件服务器密码'),
('email_from', '', '发件人邮箱地址'),
('wechat_qr_code', '', '微信二维码图片地址'),
('wechat_official_qr_code', '', '微信公众号二维码图片地址')
ON DUPLICATE KEY UPDATE 
`config_value` = VALUES(`config_value`);

-- 修复管理员用户角色
UPDATE `tb_user` SET `role` = 0 WHERE `username` = 'admin';

-- 修复友链头像数据
UPDATE `tb_friend_link` SET `avatar` = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png' 
WHERE `avatar` = '/images/friend1.png' OR `avatar` = '/images/friend2.png' OR `avatar` = '' OR `avatar` IS NULL;

-- =====================================================
-- 13. 可选维护脚本（谨慎使用）
-- =====================================================

/*
-- 重置所有表的自增ID脚本（仅在需要时使用）
-- ⚠️ 警告：执行前请务必备份数据库！

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 重置用户表
SET @row_number = 0;
UPDATE tb_user SET id = (@row_number := @row_number + 1) WHERE deleted = 0 ORDER BY id;
ALTER TABLE tb_user AUTO_INCREMENT = 1;

-- 重置分类表
SET @row_number = 0;
UPDATE tb_category SET id = (@row_number := @row_number + 1) WHERE deleted = 0 ORDER BY id;
ALTER TABLE tb_category AUTO_INCREMENT = 1;

-- 重置标签表
SET @row_number = 0;
UPDATE tb_tag SET id = (@row_number := @row_number + 1) WHERE deleted = 0 ORDER BY id;
ALTER TABLE tb_tag AUTO_INCREMENT = 1;

-- 重置文章表
SET @row_number = 0;
UPDATE tb_article SET id = (@row_number := @row_number + 1) WHERE deleted = 0 ORDER BY id;
ALTER TABLE tb_article AUTO_INCREMENT = 1;

-- 重置文章标签关联表
SET @row_number = 0;
UPDATE tb_article_tag SET id = (@row_number := @row_number + 1) WHERE deleted = 0 ORDER BY id;
ALTER TABLE tb_article_tag AUTO_INCREMENT = 1;

-- 重置评论表
SET @row_number = 0;
UPDATE tb_comment SET id = (@row_number := @row_number + 1) WHERE deleted = 0 ORDER BY id;
ALTER TABLE tb_comment AUTO_INCREMENT = 1;

-- 重置相册分类表
SET @row_number = 0;
UPDATE tb_photo_category SET id = (@row_number := @row_number + 1) WHERE deleted = 0 ORDER BY id;
ALTER TABLE tb_photo_category AUTO_INCREMENT = 1;

-- 重置相册表
SET @row_number = 0;
UPDATE tb_photo SET id = (@row_number := @row_number + 1) WHERE deleted = 0 ORDER BY id;
ALTER TABLE tb_photo AUTO_INCREMENT = 1;

-- 重置友链表
SET @row_number = 0;
UPDATE tb_friend_link SET id = (@row_number := @row_number + 1) WHERE deleted = 0 ORDER BY id;
ALTER TABLE tb_friend_link AUTO_INCREMENT = 1;

-- 重置留言表
SET @row_number = 0;
UPDATE tb_message SET id = (@row_number := @row_number + 1) WHERE deleted = 0 ORDER BY id;
ALTER TABLE tb_message AUTO_INCREMENT = 1;

-- 重置文章点赞记录表
SET @row_number = 0;
UPDATE tb_article_like SET id = (@row_number := @row_number + 1) ORDER BY id;
ALTER TABLE tb_article_like AUTO_INCREMENT = 1;

-- 重置系统设置表
SET @row_number = 0;
UPDATE tb_system_settings SET id = (@row_number := @row_number + 1) ORDER BY id;
ALTER TABLE tb_system_settings AUTO_INCREMENT = 1;

-- 重置网站配置表
SET @row_number = 0;
UPDATE tb_config SET id = (@row_number := @row_number + 1) WHERE deleted = 0 ORDER BY id;
ALTER TABLE tb_config AUTO_INCREMENT = 1;

-- 启用外键检查
SET FOREIGN_KEY_CHECKS = 1;
*/

-- =====================================================
-- 数据库脚本执行完成
-- 
-- 使用说明：
-- 1. 基础安装：执行第1-10部分即可
-- 2. 系统配置：第11部分包含必要的配置补丁
-- 3. 维护工具：第12部分仅在需要重置ID时使用（需要取消注释）
-- 
-- 注意事项：
-- - 执行前请备份数据库
-- - 确保MySQL版本兼容（推荐5.7+）
-- - 检查字符集设置为utf8mb4
-- =====================================================