package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.AiChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI聊天消息Mapper接口
 */
@Mapper
public interface AiChatMessageMapper extends BaseMapper<AiChatMessage> {

    /**
     * 根据会话ID获取消息列表
     */
    List<AiChatMessage> selectBySessionId(@Param("sessionId") Long sessionId);

    /**
     * 获取会话的最新消息
     */
    AiChatMessage selectLatestBySessionId(@Param("sessionId") Long sessionId);

    /**
     * 批量插入消息
     */
    int insertBatch(@Param("messages") List<AiChatMessage> messages);
}