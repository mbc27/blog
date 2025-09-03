package com.blog.controller;

import com.blog.entity.FriendLink;
import com.blog.service.FriendLinkService;
import com.blog.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台友链控制器
 */
@RestController
@RequestMapping("/api/friend-links")
public class FriendLinkController {

    @Autowired
    private FriendLinkService friendLinkService;

    /**
     * 获取前台友链列表（仅显示已通过的）
     *
     * @return 友链列表
     */
    @GetMapping
    public Result getFrontFriendLinks() {
        try {
            List<FriendLink> friendLinks = friendLinkService.getFrontFriendLinks();
            return Result.success(friendLinks);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 申请友链
     *
     * @param friendLink 友链信息
     * @return 申请结果
     */
    @PostMapping("/apply")
    public Result applyFriendLink(@RequestBody FriendLink friendLink) {
        try {
            // 设置状态为待审核
            friendLink.setStatus(0);
            boolean result = friendLinkService.addFriendLink(friendLink);
            if (result) {
                return Result.success("友链申请提交成功，请等待审核");
            } else {
                return Result.error("友链申请提交失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}