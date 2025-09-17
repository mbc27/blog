package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.AiChatSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI聊天会话Mapper接口
 */
@Mapper
public interface AiChatSessionMapper extends BaseMapper<AiChatSession> {

    /**
     * 根据用户ID获取会话列表
     */
    List<AiChatSession> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据会话ID获取会话详情
     */
    AiChatSession selectBySessionId(@Param("sessionId") String sessionId);

    /**
     * 更新会话状态
     */
    int updateSessionStatus(@Param("sessionId") String sessionId, @Param("status") Integer status);
}