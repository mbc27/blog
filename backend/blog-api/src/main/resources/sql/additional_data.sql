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