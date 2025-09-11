-- 关于我页面初始化数据

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