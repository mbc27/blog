package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.dao.PhotoMapper;
import com.blog.entity.Photo;
import com.blog.service.PhotoService;
import com.blog.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 相册服务实现类
 */
@Service
public class PhotoServiceImpl extends ServiceImpl<PhotoMapper, Photo> implements PhotoService {

    @Autowired
    private PhotoMapper photoMapper;
    
    @Autowired
    private FileUtils fileUtils;

    @Override
    public Page<Photo> listPhotos(Page<Photo> page, Long categoryId) {
        try {
            // 使用自定义查询方法避免字段连接问题
            List<Photo> photos;
            if (categoryId != null) {
                photos = photoMapper.selectPageByCategoryWithFields(categoryId, page.getSize());
            } else {
                photos = photoMapper.selectPageWithFields(page.getSize());
            }
            
            // 手动设置分页结果
            page.setRecords(photos);
            
            // 查询总数
            LambdaQueryWrapper<Photo> countWrapper = new LambdaQueryWrapper<>();
            if (categoryId != null) {
                countWrapper.eq(Photo::getCategoryId, categoryId);
            }
            long total = this.count(countWrapper);
            page.setTotal(total);
            
            return page;
        } catch (Exception e) {
            // 如果自定义查询失败，回退到原始查询
            LambdaQueryWrapper<Photo> wrapper = new LambdaQueryWrapper<>();
            if (categoryId != null) {
                wrapper.eq(Photo::getCategoryId, categoryId);
            }
            wrapper.orderByDesc(Photo::getCreateTime);
            return this.page(page, wrapper);
        }
    }

    @Override
    public Photo getPhotoById(Long id) {
        return this.getById(id);
    }

    @Override
    public boolean addPhoto(Photo photo) {
        // 设置初始浏览量
        photo.setViewCount(0);
        
        // 设置创建时间和更新时间
        Date now = new Date();
        photo.setCreateTime(now);
        photo.setUpdateTime(now);
        
        return this.save(photo);
    }

    @Override
    public boolean updatePhoto(Photo photo) {
        // 设置更新时间
        photo.setUpdateTime(new Date());
        return this.updateById(photo);
    }

    @Override
    public boolean deletePhoto(Long id) {
        // 先获取照片信息，以便删除对应的文件
        Photo photo = this.getById(id);
        if (photo == null) {
            return false;
        }
        
        // 删除数据库记录
        boolean dbDeleted = this.removeById(id);
        
        if (dbDeleted) {
            // 删除服务器上的文件
            String photoUrl = photo.getUrl();
            if (photoUrl != null && !photoUrl.trim().isEmpty()) {
                try {
                    boolean fileDeleted = fileUtils.deleteFile(photoUrl);
                    if (!fileDeleted) {
                        System.err.println("警告: 数据库记录已删除，但文件删除失败: " + photoUrl);
                    }
                } catch (Exception e) {
                    System.err.println("删除文件时发生异常: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        
        return dbDeleted;
    }

    @Override
    public boolean incrementViewCount(Long id) {
        Photo photo = this.getById(id);
        if (photo != null) {
            photo.setViewCount(photo.getViewCount() + 1);
            photo.setUpdateTime(new Date());
            return this.updateById(photo);
        }
        return false;
    }
}