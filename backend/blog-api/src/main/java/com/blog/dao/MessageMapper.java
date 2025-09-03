package com.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * 留言数据访问接口
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}