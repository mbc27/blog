package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Interest;
import org.apache.ibatis.annotations.Mapper;

/**
 * 个人兴趣Mapper接口
 */
@Mapper
public interface InterestMapper extends BaseMapper<Interest> {
}