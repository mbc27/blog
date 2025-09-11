package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.AboutMe;

/**
 * 个人基本信息服务接口
 */
public interface AboutMeService extends IService<AboutMe> {
    
    /**
     * 获取个人信息
     */
    AboutMe getAboutMe();
    
    /**
     * 更新个人信息
     */
    boolean updateAboutMe(AboutMe aboutMe);
}