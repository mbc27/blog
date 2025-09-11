-- 重置所有表的自增ID脚本
-- 执行前请确保已备份数据库
-- 数据库密码: 1234

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

-- 显示重置结果
SELECT 'tb_user' as table_name, COUNT(*) as record_count, MAX(id) as max_id FROM tb_user WHERE deleted = 0
UNION ALL
SELECT 'tb_category', COUNT(*), MAX(id) FROM tb_category WHERE deleted = 0
UNION ALL
SELECT 'tb_tag', COUNT(*), MAX(id) FROM tb_tag WHERE deleted = 0
UNION ALL
SELECT 'tb_article', COUNT(*), MAX(id) FROM tb_article WHERE deleted = 0
UNION ALL
SELECT 'tb_article_tag', COUNT(*), MAX(id) FROM tb_article_tag WHERE deleted = 0
UNION ALL
SELECT 'tb_comment', COUNT(*), MAX(id) FROM tb_comment WHERE deleted = 0
UNION ALL
SELECT 'tb_photo_category', COUNT(*), MAX(id) FROM tb_photo_category WHERE deleted = 0
UNION ALL
SELECT 'tb_photo', COUNT(*), MAX(id) FROM tb_photo WHERE deleted = 0
UNION ALL
SELECT 'tb_friend_link', COUNT(*), MAX(id) FROM tb_friend_link WHERE deleted = 0
UNION ALL
SELECT 'tb_message', COUNT(*), MAX(id) FROM tb_message WHERE deleted = 0
UNION ALL
SELECT 'tb_article_like', COUNT(*), MAX(id) FROM tb_article_like
UNION ALL
SELECT 'tb_system_settings', COUNT(*), MAX(id) FROM tb_system_settings
UNION ALL
SELECT 'tb_config', COUNT(*), MAX(id) FROM tb_config WHERE deleted = 0;

SELECT '所有表的自增ID已重置完成！' as message;