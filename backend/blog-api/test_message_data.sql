INSERT INTO tb_message (nickname, email, content, ip, level, parent_id, create_time, update_time) VALUES
('测试用户1', 'test1@example.com', '这是第一条测试留言', '192.168.1.100', 1, 0, NOW(), NOW()),
('测试用户2', 'test2@example.com', '这是第二条测试留言', '192.168.1.101', 1, 0, NOW(), NOW()),
('回复用户', 'reply@example.com', '这是对第一条留言的回复', '192.168.1.102', 2, 1, NOW(), NOW());