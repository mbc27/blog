-- 修复友链头像数据
UPDATE tb_friend_link SET avatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png' WHERE avatar = '/images/friend1.png';
UPDATE tb_friend_link SET avatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png' WHERE avatar = '/images/friend2.png';
UPDATE tb_friend_link SET avatar = 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png' WHERE avatar = '贵航股份';
UPDATE tb_friend_link SET avatar = 'https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48png.png' WHERE avatar = '测试友链';
UPDATE tb_friend_link SET avatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png' WHERE avatar = '' OR avatar IS NULL;