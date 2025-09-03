package com.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.entity.FriendLink;
import com.blog.service.FriendLinkService;
import com.blog.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员友链控制器
 */
@RestController
@RequestMapping("/api/admin/friend-links")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminFriendLinkController {

    @Autowired
    private FriendLinkService friendLinkService;

    /**
     * 分页查询友链列表（管理员）
     *
     * @param current 当前页
     * @param size    每页大小
     * @param keyword 搜索关键词
     * @return 友链列表
     */
    @GetMapping
    public Result listFriendLinks(@RequestParam(defaultValue = "1") Integer current,
                                 @RequestParam(defaultValue = "10") Integer size,
                                 @RequestParam(required = false) String keyword) {
        System.out.println("AdminFriendLinkController - 接收到获取友链列表请求: current=" + current + ", size=" + size + ", keyword=" + keyword);
        try {
            Page<FriendLink> page = new Page<>(current, size);
            Page<FriendLink> friendLinkPage = friendLinkService.listFriendLinks(page, keyword);
            System.out.println("AdminFriendLinkController - 查询结果: total=" + friendLinkPage.getTotal() + ", records=" + friendLinkPage.getRecords().size());
            return Result.success(friendLinkPage);
        } catch (Exception e) {
            System.err.println("AdminFriendLinkController - 查询友链列表出错: " + e.getMessage());
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取友链详情
     *
     * @param id 友链ID
     * @return 友链详情
     */
    @GetMapping("/{id}")
    public Result getFriendLink(@PathVariable Long id) {
        try {
            FriendLink friendLink = friendLinkService.getById(id);
            if (friendLink == null) {
                return Result.error("友链不存在");
            }
            return Result.success(friendLink);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 新增友链
     *
     * @param friendLink 友链信息
     * @return 新增结果
     */
    @PostMapping
    public Result addFriendLink(@RequestBody FriendLink friendLink) {
        try {
            boolean result = friendLinkService.addFriendLink(friendLink);
            if (result) {
                return Result.success("新增成功");
            } else {
                return Result.error("新增失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新友链
     *
     * @param friendLink 友链信息
     * @return 更新结果
     */
    @PutMapping
    public Result updateFriendLink(@RequestBody FriendLink friendLink) {
        try {
            boolean result = friendLinkService.updateFriendLink(friendLink);
            if (result) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除友链
     *
     * @param id 友链ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result deleteFriendLink(@PathVariable Long id) {
        try {
            boolean result = friendLinkService.deleteFriendLink(id);
            if (result) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量删除友链
     *
     * @param ids 友链ID列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    public Result batchDeleteFriendLinks(@RequestBody Long[] ids) {
        try {
            boolean result = friendLinkService.batchDeleteFriendLinks(ids);
            if (result) {
                return Result.success("批量删除成功");
            } else {
                return Result.error("批量删除失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 审核友链
     *
     * @param id 友链ID
     * @param status 审核状态 1-通过 2-拒绝
     * @return 审核结果
     */
    @PutMapping("/{id}/audit")
    public Result auditFriendLink(@PathVariable Long id, @RequestParam Integer status) {
        try {
            if (status != 1 && status != 2) {
                return Result.error("审核状态参数错误");
            }
            boolean result = friendLinkService.auditFriendLink(id, status);
            if (result) {
                String message = status == 1 ? "审核通过" : "审核拒绝";
                return Result.success(message);
            } else {
                return Result.error("审核失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}