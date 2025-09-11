package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Interest;
import com.blog.mapper.InterestMapper;
import com.blog.service.InterestService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 个人兴趣服务实现类
 */
@Service
public class InterestServiceImpl extends ServiceImpl<InterestMapper, Interest> implements InterestService {

    @Override
    public List<Interest> getAllInterests() {
        QueryWrapper<Interest> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort_order");
        return this.list(queryWrapper);
    }
    
    @Override
    public boolean addInterest(Interest interest) {
        return this.save(interest);
    }
    
    @Override
    public boolean updateInterest(Interest interest) {
        return this.updateById(interest);
    }
    
    @Override
    public boolean deleteInterest(Long id) {
        return this.removeById(id);
    }
}