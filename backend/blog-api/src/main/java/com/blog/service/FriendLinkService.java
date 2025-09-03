package com.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.FriendLink;

import java.util.List;

/**
 * 友链服务接口
 */
public interface FriendLinkService extends IService<FriendLink> {

    /**
     * 分页查询友链列表（管理员）
     *
     * @param page 分页参数
     * @param keyword 搜索关键词
     * @return 友链分页列表
     */
    Page<FriendLink> listFriendLinks(Page<FriendLink> page, String keyword);

    /**
     * 获取前台友链列表（仅显示已通过的）
     *
     * @return 友链列表
     */
    List<FriendLink> getFrontFriendLinks();

    /**
     * 新增友链
     *
     * @param friendLink 友链信息
     * @return 是否成功
     */
    boolean addFriendLink(FriendLink friendLink);

    /**
     * 更新友链
     *
     * @param friendLink 友链信息
     * @return 是否成功
     */
    boolean updateFriendLink(FriendLink friendLink);

    /**
     * 删除友链
     *
     * @param id 友链ID
     * @return 是否成功
     */
    boolean deleteFriendLink(Long id);

    /**
     * 批量删除友链
     *
     * @param ids 友链ID数组
     * @return 是否成功
     */
    boolean batchDeleteFriendLinks(Long[] ids);

    /**
     * 审核友链
     *
     * @param id 友链ID
     * @param status 审核状态 1-通过 2-拒绝
     * @return 是否成功
     */
    boolean auditFriendLink(Long id, Integer status);
}