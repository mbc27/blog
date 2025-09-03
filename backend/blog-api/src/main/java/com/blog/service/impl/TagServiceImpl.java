package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.dao.ArticleTagMapper;
import com.blog.dao.TagMapper;
import com.blog.entity.ArticleTag;
import com.blog.entity.Tag;
import com.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 标签服务实现类
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Override
    public List<Tag> listAllTags() {
        return this.list();
    }

    @Override
    public Page<Tag> listTagsWithPagination(Page<Tag> page) {
        return this.page(page);
    }

    @Override
    public Tag getTagById(Long id) {
        return this.getById(id);
    }

    @Override
    public boolean addTag(Tag tag) {
        // 设置创建时间和更新时间
        Date now = new Date();
        tag.setCreateTime(now);
        tag.setUpdateTime(now);
        return this.save(tag);
    }

    @Override
    public boolean updateTag(Tag tag) {
        // 设置更新时间
        tag.setUpdateTime(new Date());
        return this.updateById(tag);
    }

    @Override
    public boolean deleteTag(Long id) {
        // 删除标签前，先删除文章标签关联
        LambdaQueryWrapper<ArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleTag::getTagId, id);
        articleTagMapper.delete(wrapper);
        
        // 删除标签
        return this.removeById(id);
    }

    @Override
    public List<Tag> listTagsByArticleId(Long articleId) {
        return baseMapper.selectTagsByArticleId(articleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveArticleTags(Long articleId, List<Long> tagIds) {
        // 先删除原有关联
        LambdaQueryWrapper<ArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleTag::getArticleId, articleId);
        articleTagMapper.delete(wrapper);
        
        // 保存新关联
        if (tagIds != null && !tagIds.isEmpty()) {
            List<ArticleTag> articleTags = new ArrayList<>();
            for (Long tagId : tagIds) {
                ArticleTag articleTag = new ArticleTag();
                articleTag.setArticleId(articleId);
                articleTag.setTagId(tagId);
                articleTags.add(articleTag);
            }
            // 批量插入
            for (ArticleTag articleTag : articleTags) {
                articleTagMapper.insert(articleTag);
            }
        }
        return true;
    }
}