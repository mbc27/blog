package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.AboutMe;
import com.blog.mapper.AboutMeMapper;
import com.blog.service.AboutMeService;
import org.springframework.stereotype.Service;

/**
 * 个人基本信息服务实现类
 */
@Service
public class AboutMeServiceImpl extends ServiceImpl<AboutMeMapper, AboutMe> implements AboutMeService {

    @Override
    public AboutMe getAboutMe() {
        QueryWrapper<AboutMe> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        queryWrapper.last("LIMIT 1");
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean updateAboutMe(AboutMe aboutMe) {
        if (aboutMe.getId() != null) {
            return this.updateById(aboutMe);
        } else {
            return this.save(aboutMe);
        }
    }
}