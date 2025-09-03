package com.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.entity.Message;
import com.blog.service.MessageService;
import com.blog.util.IpUtils;
import com.blog.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 留言控制器
 */
@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 分页查询留言列表
     *
     * @param current 当前页
     * @param size    每页大小
     * @return 留言列表
     */
    @GetMapping("/list")
    public Result listMessages(@RequestParam(defaultValue = "1") Integer current,
                              @RequestParam(defaultValue = "10") Integer size) {
        try {
            Page<Message> page = new Page<>(current, size);
            Page<Message> messagePage = messageService.listMessages(page);
            return Result.success(messagePage);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取留言树
     *
     * @return 留言树
     */
    @GetMapping("/tree")
    public Result getMessageTree() {
        try {
            List<Message> messageTree = messageService.getMessageTree();
            return Result.success(messageTree);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取留言详情
     *
     * @param id 留言ID
     * @return 留言详情
     */
    @GetMapping("/{id}")
    public Result getMessage(@PathVariable Long id) {
        try {
            Message message = messageService.getMessageById(id);
            if (message == null) {
                return Result.error("留言不存在");
            }
            return Result.success(message);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 新增留言
     *
     * @param message 留言信息
     * @return 新增结果
     */
    @PostMapping
    public Result addMessage(@RequestBody Message message, HttpServletRequest request) {
        try {
            // 获取客户端IP地址
            String clientIp = IpUtils.getClientIp(request);
            message.setIp(clientIp);
            
            boolean result = messageService.addMessage(message);
            if (result) {
                return Result.success("留言成功");
            } else {
                return Result.error("留言失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新留言（需要管理员权限）
     *
     * @param message 留言信息
     * @return 更新结果
     */
    @PutMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result updateMessage(@RequestBody Message message) {
        try {
            boolean result = messageService.updateMessage(message);
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
     * 删除留言（需要管理员权限）
     *
     * @param id 留言ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result deleteMessage(@PathVariable Long id) {
        try {
            boolean result = messageService.deleteMessage(id);
            if (result) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}