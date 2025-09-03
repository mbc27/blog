package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.dao.ArticleLikeMapper;
import com.blog.dao.ArticleMapper;
import com.blog.mapper.UserMapper;
import com.blog.entity.Article;
import com.blog.entity.ArticleLike;
import com.blog.entity.User;
import com.blog.service.ArticleService;
import com.blog.utils.SecurityUtils;
import com.blog.vo.ArticleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private ArticleLikeMapper articleLikeMapper;

    @Override
    public Page<ArticleVo> listArticles(Page<Article> page, Long categoryId) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        
        // 获取当前用户ID
        Long currentUserId = SecurityUtils.getCurrentUserId();
        // 获取当前用户角色
        boolean isAdmin = SecurityUtils.isAdmin();
        
        // 如果是管理员，可以看到所有已发布的文章
        if (isAdmin) {
            // 已发布的文章
            wrapper.eq(Article::getStatus, 1);
        } else {
            // 普通用户只能看到已发布的文章
            wrapper.eq(Article::getStatus, 1);
        }
        
        // 按分类查询
        if (categoryId != null && categoryId > 0) {
            wrapper.eq(Article::getCategoryId, categoryId);
        }
        // 按创建时间倒序
        wrapper.orderByDesc(Article::getIsTop);
        wrapper.orderByDesc(Article::getCreateTime);

        // 查询文章列表
        Page<Article> articlePage = page(page, wrapper);
        
        // 转换为ArticleVo
        Page<ArticleVo> voPage = new Page<>(articlePage.getCurrent(), articlePage.getSize(), articlePage.getTotal());
        
        if (articlePage.getRecords().isEmpty()) {
            voPage.setRecords(new ArrayList<>());
            return voPage;
        }
        
        // 获取所有文章的作者ID
        List<Long> userIds = articlePage.getRecords().stream()
                .map(Article::getUserId)
                .distinct()
                .collect(Collectors.toList());
        
        // 批量查询作者信息
        List<User> users = userMapper.selectBatchIds(userIds);
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        
        // 转换为ArticleVo
        List<ArticleVo> voList = articlePage.getRecords().stream()
                .map(article -> {
                    User user = userMap.get(article.getUserId());
                    return ArticleVo.fromArticleAndUser(article, user);
                })
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }
    
    @Override
    public Page<ArticleVo> listMyArticles(Page<Article> page, Long categoryId) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        
        // 获取当前用户ID
        Long currentUserId = SecurityUtils.getCurrentUserId();
        
        // 检查当前用户ID是否为空
        if (currentUserId == null) {
            // 如果用户ID为空，返回空结果
            Page<ArticleVo> emptyPage = new Page<>(page.getCurrent(), page.getSize(), 0);
            emptyPage.setRecords(new ArrayList<>());
            return emptyPage;
        }
        
        // 只查询当前用户的文章
        wrapper.eq(Article::getUserId, currentUserId);
        
        // 按分类查询
        if (categoryId != null && categoryId > 0) {
            wrapper.eq(Article::getCategoryId, categoryId);
        }
        // 按创建时间倒序
        wrapper.orderByDesc(Article::getIsTop);
        wrapper.orderByDesc(Article::getCreateTime);

        // 查询文章列表
        Page<Article> articlePage = page(page, wrapper);
        
        // 转换为ArticleVo
        Page<ArticleVo> voPage = new Page<>(articlePage.getCurrent(), articlePage.getSize(), articlePage.getTotal());
        
        if (articlePage.getRecords().isEmpty()) {
            voPage.setRecords(new ArrayList<>());
            return voPage;
        }
        
        // 获取当前用户信息
        User currentUser = userMapper.selectById(currentUserId);
        
        // 转换为ArticleVo
        List<ArticleVo> voList = articlePage.getRecords().stream()
                .map(article -> ArticleVo.fromArticleAndUser(article, currentUser))
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }
    
    @Override
    public Page<ArticleVo> listPendingArticles(Page<Article> page) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        
        // 只查询待审核的文章
        wrapper.eq(Article::getStatus, 2);
        
        // 按创建时间倒序
        wrapper.orderByDesc(Article::getCreateTime);

        // 查询文章列表
        Page<Article> articlePage = page(page, wrapper);
        
        // 转换为ArticleVo
        Page<ArticleVo> voPage = new Page<>(articlePage.getCurrent(), articlePage.getSize(), articlePage.getTotal());
        
        if (articlePage.getRecords().isEmpty()) {
            voPage.setRecords(new ArrayList<>());
            return voPage;
        }
        
        // 获取所有文章的作者ID
        List<Long> userIds = articlePage.getRecords().stream()
                .map(Article::getUserId)
                .distinct()
                .collect(Collectors.toList());
        
        // 批量查询作者信息
        List<User> users = userMapper.selectBatchIds(userIds);
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        
        // 转换为ArticleVo
        List<ArticleVo> voList = articlePage.getRecords().stream()
                .map(article -> {
                    User user = userMap.get(article.getUserId());
                    return ArticleVo.fromArticleAndUser(article, user);
                })
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public ArticleVo getArticleById(Long id) {
        Article article = getById(id);
        if (article == null) {
            return null;
        }
        
        // 获取作者信息
        User user = userMapper.selectById(article.getUserId());
        
        // 转换为ArticleVo
        return ArticleVo.fromArticleAndUser(article, user);
    }

    @Override
    public boolean addArticle(Article article) {
        // 设置创建时间和更新时间
        Date now = new Date();
        article.setCreateTime(now);
        article.setUpdateTime(now);
        // 设置初始浏览量、评论数和点赞数
        article.setViewCount(0);
        article.setCommentCount(0);
        article.setLikeCount(0);
        
        // 检查是否已经设置了用户ID，如果没有，则尝试从SecurityContext中获取
        if (article.getUserId() == null) {
            // 获取当前用户ID
            Long currentUserId = SecurityUtils.getCurrentUserId();
            if (currentUserId == null) {
                // 如果无法获取用户ID，则返回失败
                return false;
            }
            article.setUserId(currentUserId);
        }
        
        // 判断用户角色，设置文章状态
        boolean isAdmin = SecurityUtils.isAdmin();
        if (isAdmin) {
            // 管理员发布的文章直接设置为已发布状态
            article.setStatus(1);
        } else {
            // 普通用户发布的文章设置为待审核状态
            article.setStatus(2);
        }

        return save(article);
    }
    
    @Override
    public boolean approveArticle(Long id) {
        Article article = getById(id);
        if (article != null) {
            article.setStatus(1); // 设置为已发布状态
            article.setUpdateTime(new Date());
            return updateById(article);
        }
        return false;
    }
    
    @Override
    public boolean rejectArticle(Long id) {
        Article article = getById(id);
        if (article != null) {
            article.setStatus(3); // 设置为审核不通过状态
            article.setUpdateTime(new Date());
            return updateById(article);
        }
        return false;
    }

    @Override
    public boolean updateArticle(Article article) {
        // 设置更新时间
        article.setUpdateTime(new Date());

        return updateById(article);
    }

    @Override
    public boolean deleteArticle(Long id) {
        return removeById(id);
    }

    @Override
    public boolean incrementViewCount(Long id) {
        Article article = getById(id);
        if (article != null) {
            article.setViewCount(article.getViewCount() + 1);
            return updateById(article);
        }
        return false;
    }
    
    @Override
    @Transactional
    public boolean toggleLike(Long id) {
        // 获取当前用户ID
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return false;
        }
        
        // 检查文章是否存在
        Article article = getById(id);
        if (article == null) {
            return false;
        }
        
        // 查询是否已点赞
        LambdaQueryWrapper<ArticleLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleLike::getArticleId, id);
        queryWrapper.eq(ArticleLike::getUserId, currentUserId);
        ArticleLike like = articleLikeMapper.selectOne(queryWrapper);
        
        if (like == null) {
            // 未点赞，添加点赞记录
            ArticleLike newLike = new ArticleLike();
            newLike.setArticleId(id);
            newLike.setUserId(currentUserId);
            newLike.setCreateTime(new Date());
            articleLikeMapper.insert(newLike);
            
            // 更新文章点赞数
            article.setLikeCount(article.getLikeCount() + 1);
            updateById(article);
            return true;
        } else {
            // 已点赞，取消点赞
            articleLikeMapper.deleteById(like.getId());
            
            // 更新文章点赞数
            if (article.getLikeCount() > 0) {
                article.setLikeCount(article.getLikeCount() - 1);
                updateById(article);
            }
            return false;
        }
    }
    
    @Override
    public boolean hasLiked(Long id) {
        // 获取当前用户ID
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return false;
        }
        
        // 查询是否已点赞
        LambdaQueryWrapper<ArticleLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleLike::getArticleId, id);
        queryWrapper.eq(ArticleLike::getUserId, currentUserId);
        return articleLikeMapper.selectCount(queryWrapper) > 0;
    }
    
    /**
     * @deprecated 已被toggleLike方法替代
     */
    public int incrementLikeCount(Long id) {
        // 调用新方法
        toggleLike(id);
        Article article = getById(id);
        return article != null ? article.getLikeCount() : 0;
    }
}