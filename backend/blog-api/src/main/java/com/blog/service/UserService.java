package com.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.User;
import com.blog.vo.UserVo;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    UserVo login(String username, String password);

    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return 注册结果
     */
    boolean register(User user);

    /**
     * 分页查询用户列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param keyword 关键词
     * @return 用户列表
     */
    IPage<User> getUserList(int page, int size, String keyword);

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return 更新结果
     */
    boolean updateUserInfo(User user);

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 状态
     * @return 更新结果
     */
    boolean updateUserStatus(Long userId, Integer status);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 删除结果
     */
    boolean deleteUser(Long userId);

    /**
     * 更新用户角色
     *
     * @param userId 用户ID
     * @param role 角色
     * @return 更新结果
     */
    boolean updateUserRole(Long userId, Integer role);

    /**
     * 重置用户密码
     *
     * @param userId 用户ID
     * @return 重置结果
     */
    boolean resetPassword(Long userId);
    
    /**
     * 根据token获取用户信息
     *
     * @param token JWT令牌
     * @return 用户信息
     */
    UserVo getUserInfoByToken(String token);
    
    /**
     * 分页查询所有用户（支持多条件搜索）
     *
     * @param page 页码
     * @param size 每页大小
     * @param username 用户名
     * @param nickname 昵称
     * @param role 角色
     * @param status 状态
     * @return 用户列表
     */
    IPage<User> getAllUsers(int page, int size, String username, String nickname, Integer role, Integer status);
}
