package com.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.Photo;

/**
 * 相册服务接口
 */
public interface PhotoService extends IService<Photo> {

    /**
     * 分页查询相册列表
     *
     * @param page       分页参数
     * @param categoryId 分类ID
     * @return 相册分页列表
     */
    Page<Photo> listPhotos(Page<Photo> page, Long categoryId);

    /**
     * 获取相册详情
     *
     * @param id 相册ID
     * @return 相册信息
     */
    Photo getPhotoById(Long id);

    /**
     * 新增相册
     *
     * @param photo 相册信息
     * @return 是否成功
     */
    boolean addPhoto(Photo photo);

    /**
     * 更新相册
     *
     * @param photo 相册信息
     * @return 是否成功
     */
    boolean updatePhoto(Photo photo);

    /**
     * 删除相册
     *
     * @param id 相册ID
     * @return 是否成功
     */
    boolean deletePhoto(Long id);

    /**
     * 增加相册浏览量
     *
     * @param id 相册ID
     * @return 是否成功
     */
    boolean incrementViewCount(Long id);
}