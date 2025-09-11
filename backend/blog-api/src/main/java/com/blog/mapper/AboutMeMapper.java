package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.AboutMe;
import org.apache.ibatis.annotations.Mapper;

/**
 * 个人基本信息Mapper接口
 */
@Mapper
public interface AboutMeMapper extends BaseMapper<AboutMe> {
}