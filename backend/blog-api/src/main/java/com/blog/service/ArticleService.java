package com.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.Article;
import com.blog.vo.ArticleVo;

/**
 * 文章服务接口
 */
public interface ArticleService extends IService<Article> {

    /**
     * 分页查询文章列表（所有已发布的文章）
     *
     * @param page     分页参数
     * @param categoryId 分类ID
     * @return 文章列表
     */
    Page<ArticleVo> listArticles(Page<Article> page, Long categoryId);
    
    /**
     * 分页查询当前用户的文章列表
     *
     * @param page     分页参数
     * @param categoryId 分类ID
     * @return 文章列表
     */
    Page<ArticleVo> listMyArticles(Page<Article> page, Long categoryId);
    
    /**
     * 分页查询待审核的文章列表（管理员使用）
     *
     * @param page     分页参数
     * @return 待审核的文章列表
     */
    Page<ArticleVo> listPendingArticles(Page<Article> page);

    /**
     * 获取文章详情
     *
     * @param id 文章ID
     * @return 文章详情
     */
    ArticleVo getArticleById(Long id);

    /**
     * 新增文章
     *
     * @param article 文章信息
     * @return 新增结果
     */
    boolean addArticle(Article article);

    /**
     * 更新文章
     *
     * @param article 文章信息
     * @return 更新结果
     */
    boolean updateArticle(Article article);

    /**
     * 删除文章
     *
     * @param id 文章ID
     * @return 删除结果
     */
    boolean deleteArticle(Long id);

    /**
     * 增加文章浏览量
     *
     * @param id 文章ID
     * @return 更新结果
     */
    boolean incrementViewCount(Long id);
    
    /**
     * 审核通过文章
     *
     * @param id 文章ID
     * @return 审核结果
     */
    boolean approveArticle(Long id);
    
    /**
     * 审核拒绝文章
     *
     * @param id 文章ID
     * @return 审核结果
     */
    boolean rejectArticle(Long id);
    
    /**
     * 点赞或取消点赞文章
     *
     * @param id 文章ID
     * @return 操作结果，true表示点赞，false表示取消点赞
     */
    boolean toggleLike(Long id);
    
    /**
     * 检查用户是否已点赞文章
     *
     * @param id 文章ID
     * @return 是否已点赞
     */
    boolean hasLiked(Long id);
}
