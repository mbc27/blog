package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.FriendLink;
import org.apache.ibatis.annotations.Mapper;

/**
 * 友链Mapper接口
 */
@Mapper
public interface FriendLinkMapper extends BaseMapper<FriendLink> {
}