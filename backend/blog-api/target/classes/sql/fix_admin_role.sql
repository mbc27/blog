-- 修复管理员用户角色
-- 确保admin用户的角色值为0（管理员）
UPDATE `tb_user` SET `role` = 0 WHERE `username` = 'admin';

-- 添加一个新的管理员用户（密码: admin123）
INSERT INTO `tb_user` (`username`, `password`, `nickname`, `avatar`, `email`, `phone`, `status`, `role`) 
VALUES ('superadmin', '$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm', '超级管理员', 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png', 'superadmin@example.com', '13800138001', 1, 0);