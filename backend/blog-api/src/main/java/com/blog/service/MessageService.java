package com.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.Message;

import java.util.List;

/**
 * 留言服务接口
 */
public interface MessageService extends IService<Message> {

    /**
     * 分页查询留言列表
     *
     * @param page 分页参数
     * @return 留言分页列表
     */
    Page<Message> listMessages(Page<Message> page);

    /**
     * 分页查询留言列表（支持关键词搜索）
     *
     * @param page 分页参数
     * @param keyword 搜索关键词
     * @return 留言分页列表
     */
    Page<Message> listMessages(Page<Message> page, String keyword);

    /**
     * 获取留言详情
     *
     * @param id 留言ID
     * @return 留言信息
     */
    Message getMessageById(Long id);

    /**
     * 新增留言
     *
     * @param message 留言信息
     * @return 是否成功
     */
    boolean addMessage(Message message);

    /**
     * 更新留言
     *
     * @param message 留言信息
     * @return 是否成功
     */
    boolean updateMessage(Message message);

    /**
     * 删除留言
     *
     * @param id 留言ID
     * @return 是否成功
     */
    boolean deleteMessage(Long id);

    /**
     * 获取留言树
     *
     * @return 留言树
     */
    List<Message> getMessageTree();

    /**
     * 批量删除留言
     *
     * @param ids 留言ID数组
     * @return 是否成功
     */
    boolean batchDeleteMessages(Long[] ids);
}
