package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.Interest;

import java.util.List;

/**
 * 个人兴趣服务接口
 */
public interface InterestService extends IService<Interest> {
    
    /**
     * 获取所有兴趣爱好
     */
    List<Interest> getAllInterests();
    
    /**
     * 添加兴趣爱好
     */
    boolean addInterest(Interest interest);
    
    /**
     * 更新兴趣爱好
     */
    boolean updateInterest(Interest interest);
    
    /**
     * 删除兴趣爱好
     */
    boolean deleteInterest(Long id);
}