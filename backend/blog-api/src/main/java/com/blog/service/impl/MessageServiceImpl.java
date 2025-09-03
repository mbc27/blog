package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.dao.MessageMapper;
import com.blog.entity.Message;
import com.blog.service.MessageService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 留言服务实现类
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Override
    public Page<Message> listMessages(Page<Message> page) {
        System.out.println("开始查询留言列表 - 当前页: " + page.getCurrent() + ", 每页大小: " + page.getSize());
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Message::getCreateTime);
        Page<Message> result = this.page(page, wrapper);
        System.out.println("查询留言列表完成 - 总数: " + result.getTotal() + ", 当前页数据: " + result.getRecords().size());
        return result;
    }

    @Override
    public Page<Message> listMessages(Page<Message> page, String keyword) {
        System.out.println("开始查询留言列表 - 当前页: " + page.getCurrent() + ", 每页大小: " + page.getSize() + ", 关键词: " + keyword);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        
        // 如果有关键词，添加搜索条件
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Message::getContent, keyword)
                    .or().like(Message::getNickname, keyword)
                    .or().like(Message::getEmail, keyword));
        }
        
        wrapper.orderByDesc(Message::getCreateTime);
        Page<Message> result = this.page(page, wrapper);
        System.out.println("查询留言列表完成 - 总数: " + result.getTotal() + ", 当前页数据: " + result.getRecords().size());
        return result;
    }

    @Override
    public Message getMessageById(Long id) {
        return this.getById(id);
    }

    @Override
    public boolean addMessage(Message message) {
        // 设置创建时间和更新时间
        Date now = new Date();
        message.setCreateTime(now);
        message.setUpdateTime(now);
        
        // 设置留言层级
        if (message.getParentId() == null || message.getParentId() == 0) {
            message.setLevel(1); // 顶级留言
            message.setParentId(0L);
        } else {
            message.setLevel(2); // 回复留言
        }
        
        return this.save(message);
    }

    @Override
    public boolean updateMessage(Message message) {
        // 设置更新时间
        message.setUpdateTime(new Date());
        return this.updateById(message);
    }

    @Override
    public boolean deleteMessage(Long id) {
        // 删除留言及其所有回复
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getId, id).or().eq(Message::getParentId, id);
        return this.remove(wrapper);
    }

    @Override
    public List<Message> getMessageTree() {
        // 获取所有留言
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Message::getCreateTime);
        List<Message> allMessages = this.list(wrapper);
        
        // 构建留言树
        // 1. 获取所有顶级留言
        List<Message> rootMessages = allMessages.stream()
                .filter(message -> message.getLevel() == 1)
                .collect(Collectors.toList());
        
        // 2. 获取所有回复留言
        List<Message> replyMessages = allMessages.stream()
                .filter(message -> message.getLevel() == 2)
                .collect(Collectors.toList());
        
        // 3. 将回复留言按照parentId分组
        for (Message rootMessage : rootMessages) {
            List<Message> children = new ArrayList<>();
            for (Message replyMessage : replyMessages) {
                if (replyMessage.getParentId().equals(rootMessage.getId())) {
                    children.add(replyMessage);
                }
            }
            // 这里可以将子留言设置到父留言中，如果需要的话可以在Message类中添加children字段
            // rootMessage.setChildren(children);
        }
        
        return rootMessages;
    }

    @Override
    public boolean batchDeleteMessages(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return false;
        }
        try {
            boolean result = this.removeBatchByIds(Arrays.asList(ids));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
