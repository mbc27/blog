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