-- 关于我页面相关数据表

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