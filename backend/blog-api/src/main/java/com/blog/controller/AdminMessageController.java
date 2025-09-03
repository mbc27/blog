package com.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.entity.Message;
import com.blog.service.MessageService;
import com.blog.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员留言控制器
 */
@RestController
@RequestMapping("/api/admin/messages")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminMessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 分页查询留言列表（管理员）
     *
     * @param current 当前页
     * @param size    每页大小
     * @param keyword 搜索关键词
     * @return 留言列表
     */
    @GetMapping
    public Result listMessages(@RequestParam(defaultValue = "1") Integer current,
                              @RequestParam(defaultValue = "10") Integer size,
                              @RequestParam(required = false) String keyword) {
        System.out.println("AdminMessageController - 接收到获取留言列表请求: current=" + current + ", size=" + size + ", keyword=" + keyword);
        try {
            Page<Message> page = new Page<>(current, size);
            Page<Message> messagePage = messageService.listMessages(page, keyword);
            System.out.println("AdminMessageController - 查询结果: total=" + messagePage.getTotal() + ", records=" + messagePage.getRecords().size());
            return Result.success(messagePage);
        } catch (Exception e) {
            System.err.println("AdminMessageController - 查询留言列表出错: " + e.getMessage());
            e.printStackTrace();
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
     * 更新留言
     *
     * @param message 留言信息
     * @return 更新结果
     */
    @PutMapping
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
     * 删除留言
     *
     * @param id 留言ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
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

    /**
     * 批量删除留言
     *
     * @param ids 留言ID列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    public Result batchDeleteMessages(@RequestBody Long[] ids) {
        try {
            boolean result = messageService.batchDeleteMessages(ids);
            if (result) {
                return Result.success("批量删除成功");
            } else {
                return Result.error("批量删除失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}