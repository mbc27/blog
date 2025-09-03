package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.mapper.FriendLinkMapper;
import com.blog.entity.FriendLink;
import com.blog.service.FriendLinkService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 友链服务实现类
 */
@Service
public class FriendLinkServiceImpl extends ServiceImpl<FriendLinkMapper, FriendLink> implements FriendLinkService {

    @Override
    public Page<FriendLink> listFriendLinks(Page<FriendLink> page, String keyword) {
        System.out.println("开始查询友链列表 - 当前页: " + page.getCurrent() + ", 每页大小: " + page.getSize() + ", 关键词: " + keyword);
        LambdaQueryWrapper<FriendLink> wrapper = new LambdaQueryWrapper<>();
        
        // 如果有关键词，添加搜索条件
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(FriendLink::getName, keyword)
                    .or().like(FriendLink::getDescription, keyword)
                    .or().like(FriendLink::getUrl, keyword));
        }
        
        wrapper.orderByAsc(FriendLink::getSortOrder)
               .orderByDesc(FriendLink::getCreateTime);
        
        Page<FriendLink> result = this.page(page, wrapper);
        System.out.println("查询友链列表完成 - 总数: " + result.getTotal() + ", 当前页数据: " + result.getRecords().size());
        return result;
    }

    @Override
    public List<FriendLink> getFrontFriendLinks() {
        LambdaQueryWrapper<FriendLink> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FriendLink::getStatus, 1) // 只显示已通过的友链
               .orderByAsc(FriendLink::getSortOrder)
               .orderByDesc(FriendLink::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public boolean addFriendLink(FriendLink friendLink) {
        // 设置创建时间和更新时间
        Date now = new Date();
        friendLink.setCreateTime(now);
        friendLink.setUpdateTime(now);
        
        // 设置默认状态为待审核
        if (friendLink.getStatus() == null) {
            friendLink.setStatus(0);
        }
        
        // 设置默认排序
        if (friendLink.getSortOrder() == null) {
            friendLink.setSortOrder(0);
        }
        
        return this.save(friendLink);
    }

    @Override
    public boolean updateFriendLink(FriendLink friendLink) {
        // 设置更新时间
        friendLink.setUpdateTime(new Date());
        return this.updateById(friendLink);
    }

    @Override
    public boolean deleteFriendLink(Long id) {
        return this.removeById(id);
    }

    @Override
    public boolean batchDeleteFriendLinks(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return false;
        }
        try {
            return this.removeBatchByIds(Arrays.asList(ids));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean auditFriendLink(Long id, Integer status) {
        FriendLink friendLink = new FriendLink();
        friendLink.setId(id);
        friendLink.setStatus(status);
        friendLink.setUpdateTime(new Date());
        return this.updateById(friendLink);
    }
}