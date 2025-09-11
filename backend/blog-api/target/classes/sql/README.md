# 数据库自增ID重置工具

## 功能说明
此工具用于重置博客系统所有数据表的自增ID，让ID从1开始连续排列。

## 包含的表
- tb_user (用户表)
- tb_category (分类表) 
- tb_tag (标签表)
- tb_article (文章表)
- tb_article_tag (文章标签关联表)
- tb_comment (评论表)
- tb_photo_category (相册分类表)
- tb_photo (相册表)
- tb_friend_link (友链表)
- tb_message (留言表)
- tb_article_like (文章点赞记录表)
- tb_system_settings (系统设置表)
- tb_config (网站配置表)

## 使用方法

### 方法一：使用批处理脚本（推荐）
1. 确保MySQL服务正在运行
2. 双击运行 `execute_reset.bat`
3. 输入 `YES` 确认执行
4. 等待操作完成

### 方法二：手动执行SQL
1. 打开MySQL命令行或客户端工具
2. 连接到blog数据库
3. 执行 `reset_auto_increment.sql` 脚本

## 注意事项
⚠️ **重要警告**：
- 执行前请务必备份数据库
- 此操作会修改所有记录的ID
- 建议在测试环境先验证
- 确保应用程序已停止运行

## 数据库连接信息
- 主机：localhost
- 端口：3306
- 数据库：blog
- 用户：root
- 密码：1234

## 执行结果
脚本执行完成后会显示各表的记录数和最大ID值，用于验证重置结果。

## 故障排除
如果执行失败，请检查：
1. MySQL服务是否运行
2. 数据库连接参数是否正确
3. 用户是否有足够权限
4. 数据库中是否存在外键约束冲突