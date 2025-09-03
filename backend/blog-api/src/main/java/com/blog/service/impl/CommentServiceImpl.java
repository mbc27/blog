package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.dao.ArticleMapper;
import com.blog.dao.CommentMapper;
import com.blog.mapper.UserMapper;
import com.blog.entity.Article;
import com.blog.entity.Comment;
import com.blog.entity.User;
import com.blog.service.CommentService;
import com.blog.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public Page<Comment> listCommentsByArticleId(Page<Comment> page, Long articleId) {
        // 查询评论
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getArticleId, articleId);
        wrapper.orderByDesc(Comment::getCreateTime);
        Page<Comment> commentPage = this.page(page, wrapper);
        
        // 如果没有评论，直接返回
        if (commentPage.getRecords().isEmpty()) {
            return commentPage;
        }
        
        // 获取所有评论的用户ID
        List<Long> userIds = commentPage.getRecords().stream()
                .map(Comment::getUserId)
                .distinct()
                .collect(Collectors.toList());
        
        // 批量查询用户信息
        List<User> users = userMapper.selectBatchIds(userIds);
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        
        // 填充用户信息
        for (Comment comment : commentPage.getRecords()) {
            comment.setUser(userMap.get(comment.getUserId()));
        }
        
        // 查询子评论
        List<Long> parentIds = commentPage.getRecords().stream()
                .map(Comment::getId)
                .collect(Collectors.toList());
        
        if (!parentIds.isEmpty()) {
            // 查询所有子评论
            LambdaQueryWrapper<Comment> childWrapper = new LambdaQueryWrapper<>();
            childWrapper.in(Comment::getParentId, parentIds);
            childWrapper.orderByAsc(Comment::getCreateTime);
            List<Comment> childComments = this.list(childWrapper);
            
            // 如果有子评论
            if (!childComments.isEmpty()) {
                // 获取子评论的用户ID
                List<Long> childUserIds = childComments.stream()
                        .map(Comment::getUserId)
                        .distinct()
                        .collect(Collectors.toList());
                
                // 批量查询子评论的用户信息
                List<User> childUsers = userMapper.selectBatchIds(childUserIds);
                Map<Long, User> childUserMap = childUsers.stream()
                        .collect(Collectors.toMap(User::getId, user -> user));
                
                // 填充子评论的用户信息
                for (Comment childComment : childComments) {
                    childComment.setUser(childUserMap.get(childComment.getUserId()));
                }
                
                // 将子评论按父评论ID分组
                Map<Long, List<Comment>> childCommentMap = childComments.stream()
                        .collect(Collectors.groupingBy(Comment::getParentId));
                
                // 将子评论设置到父评论中
                for (Comment comment : commentPage.getRecords()) {
                    comment.setChildren(childCommentMap.getOrDefault(comment.getId(), new ArrayList<>()));
                }
            }
        }
        
        return commentPage;
    }

    @Override
    public Comment getCommentById(Long id) {
        Comment comment = this.getById(id);
        if (comment != null) {
            // 填充用户信息
            User user = userMapper.selectById(comment.getUserId());
            comment.setUser(user);
            
            // 如果是顶级评论，查询其子评论
            if (comment.getLevel() == 1) {
                LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Comment::getParentId, comment.getId());
                wrapper.orderByAsc(Comment::getCreateTime);
                List<Comment> children = this.list(wrapper);
                
                if (!children.isEmpty()) {
                    // 获取子评论的用户ID
                    List<Long> userIds = children.stream()
                            .map(Comment::getUserId)
                            .distinct()
                            .collect(Collectors.toList());
                    
                    // 批量查询子评论的用户信息
                    List<User> users = userMapper.selectBatchIds(userIds);
                    Map<Long, User> userMap = users.stream()
                            .collect(Collectors.toMap(User::getId, user1 -> user1));
                    
                    // 填充子评论的用户信息
                    for (Comment child : children) {
                        child.setUser(userMap.get(child.getUserId()));
                    }
                    
                    comment.setChildren(children);
                }
            }
        }
        return comment;
    }

    @Override
    public boolean addComment(Comment comment) {
        // 设置创建时间和更新时间
        Date now = new Date();
        comment.setCreateTime(now);
        comment.setUpdateTime(now);
        
        // 设置评论层级和父评论ID
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            comment.setLevel(1); // 顶级评论
            comment.setParentId(null); // 设置为null而不是0，避免外键约束错误
        } else {
            comment.setLevel(2); // 回复评论
            // 检查父评论是否存在
            Comment parentComment = this.getById(comment.getParentId());
            if (parentComment == null) {
                // 如果父评论不存在，则设置为顶级评论
                comment.setLevel(1);
                comment.setParentId(null);
            }
        }
        
        // 设置当前用户ID
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            // 如果无法获取用户ID，则返回失败
            return false;
        }
        comment.setUserId(currentUserId);
        
        return this.save(comment);
    }

    @Override
    public boolean updateComment(Comment comment) {
        // 设置更新时间
        comment.setUpdateTime(new Date());
        return this.updateById(comment);
    }

    @Override
    public boolean deleteComment(Long id) {
        // 删除评论及其所有回复
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getId, id).or().eq(Comment::getParentId, id);
        return this.remove(wrapper);
    }

    @Override
    public List<Comment> getCommentTree(Long articleId) {
        // 获取文章所有评论
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getArticleId, articleId);
        wrapper.orderByDesc(Comment::getCreateTime);
        List<Comment> allComments = this.list(wrapper);
        
        if (allComments.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取所有评论的用户ID
        List<Long> userIds = allComments.stream()
                .map(Comment::getUserId)
                .distinct()
                .collect(Collectors.toList());
        
        // 批量查询用户信息
        List<User> users = userMapper.selectBatchIds(userIds);
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        
        // 填充用户信息
        for (Comment comment : allComments) {
            comment.setUser(userMap.get(comment.getUserId()));
        }
        
        // 构建评论树
        // 1. 获取所有顶级评论
        List<Comment> rootComments = allComments.stream()
                .filter(comment -> comment.getLevel() == 1)
                .collect(Collectors.toList());
        
        // 2. 获取所有回复评论
        List<Comment> replyComments = allComments.stream()
                .filter(comment -> comment.getLevel() == 2)
                .collect(Collectors.toList());
        
        // 3. 将回复评论按照parentId分组
        for (Comment rootComment : rootComments) {
            List<Comment> children = new ArrayList<>();
            for (Comment replyComment : replyComments) {
                if (replyComment.getParentId() != null && replyComment.getParentId().equals(rootComment.getId())) {
                    children.add(replyComment);
                }
            }
            // 设置子评论
            rootComment.setChildren(children);
        }
        
        return rootComments;
    }

    @Override
    public Page<Comment> listAllComments(Page<Comment> page, String articleTitle, String content, Integer status) {
        // 查询所有评论
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        
        // 根据评论内容模糊查询
        if (content != null && !content.trim().isEmpty()) {
            wrapper.like(Comment::getContent, content);
        }
        
        // Comment实体暂时没有status字段，跳过状态筛选
        // if (status != null) {
        //     wrapper.eq(Comment::getStatus, status);
        // }
        
        // 如果需要按文章标题筛选，需要先查询文章ID
        if (articleTitle != null && !articleTitle.trim().isEmpty()) {
            LambdaQueryWrapper<Article> articleWrapper = new LambdaQueryWrapper<>();
            articleWrapper.like(Article::getTitle, articleTitle);
            List<Article> articles = articleMapper.selectList(articleWrapper);
            if (articles.isEmpty()) {
                // 如果没有匹配的文章，返回空结果
                return new Page<>(page.getCurrent(), page.getSize());
            }
            List<Long> articleIds = articles.stream()
                    .map(Article::getId)
                    .collect(Collectors.toList());
            wrapper.in(Comment::getArticleId, articleIds);
        }
        
        wrapper.orderByDesc(Comment::getCreateTime);
        Page<Comment> commentPage = this.page(page, wrapper);
        
        // 如果没有评论，直接返回
        if (commentPage.getRecords().isEmpty()) {
            return commentPage;
        }
        
        // 获取所有评论的用户ID
        List<Long> userIds = commentPage.getRecords().stream()
                .map(Comment::getUserId)
                .distinct()
                .collect(Collectors.toList());
        
        // 批量查询用户信息
        List<User> users = userMapper.selectBatchIds(userIds);
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        
        // 填充用户信息
        // 获取所有评论的文章ID
        List<Long> articleIds = commentPage.getRecords().stream()
                .map(Comment::getArticleId)
                .distinct()
                .collect(Collectors.toList());
        
        // 批量查询文章信息
        List<Article> articles = articleMapper.selectBatchIds(articleIds);
        Map<Long, Article> articleMap = articles.stream()
                .collect(Collectors.toMap(Article::getId, article -> article));
        
        // 填充用户信息和文章标题
        // 填充用户信息和文章标题
        for (Comment comment : commentPage.getRecords()) {
            User user = userMap.get(comment.getUserId());
            if (user != null) {
                comment.setUser(user);
                comment.setNickname(user.getNickname());
            }
            
            Article article = articleMap.get(comment.getArticleId());
            if (article != null) {
                comment.setArticleTitle(article.getTitle());
            }
        }
        
        return commentPage;
    }

    @Override
    public boolean approveComment(Long id) {
        Comment comment = this.getById(id);
        if (comment != null) {
            // 由于Comment实体没有status字段，这里只更新时间表示已处理
            comment.setUpdateTime(new Date());
            return this.updateById(comment);
        }
        return false;
    }

    @Override
    public List<Comment> getCommentReplies(Long id) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getParentId, id);
        wrapper.orderByAsc(Comment::getCreateTime);
        List<Comment> replies = this.list(wrapper);
        
        if (!replies.isEmpty()) {
            // 获取回复的用户ID
            List<Long> userIds = replies.stream()
                    .map(Comment::getUserId)
                    .distinct()
                    .collect(Collectors.toList());
            
            // 批量查询用户信息
            List<User> users = userMapper.selectBatchIds(userIds);
            Map<Long, User> userMap = users.stream()
                    .collect(Collectors.toMap(User::getId, user -> user));
            
            // 填充用户信息
            for (Comment reply : replies) {
                User user = userMap.get(reply.getUserId());
                if (user != null) {
                    reply.setUser(user);
                }
            }
        }
        
        return replies;
    }

    @Override
    public int getCommentCountByArticleId(Long articleId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getArticleId, articleId);
        return Math.toIntExact(this.count(wrapper));
    }
}