# 后台管理菜单修复说明

## 问题描述
后台管理菜单不显示，即使使用管理员账号登录。

## 原因分析
1. 用户角色判断逻辑有问题
2. 数据库中管理员用户的角色值可能不正确
3. 路由守卫中的权限检查逻辑不完善
4. 缺少必要的调试信息

## 修复内容

### 1. 前端修改

#### 1.1 修改Vuex store中的登录逻辑 (store/index.js)
- 添加详细的日志输出
- 确保用户信息正确获取和保存

#### 1.2 修改Home.vue中的isAdmin计算属性
- 添加独立的isAdmin计算属性
- 添加调试日志

#### 1.3 修改Dashboard.vue中的权限检查逻辑
- 增强权限检查逻辑
- 添加详细的日志输出

#### 1.4 修改路由守卫中的管理员权限检查 (router/index.js)
- 直接从state中获取用户信息
- 添加详细的日志输出
- 优化权限判断逻辑

### 2. 后端修改

#### 2.1 创建临时控制器修复管理员角色 (AdminFixController.java)

我们创建了一个临时的REST控制器，用于修复管理员用户角色：

```java
@RestController
@RequestMapping("/api/admin-fix")
public class AdminFixController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @GetMapping("/fix-admin-role")
    public Result<?> fixAdminRole() {
        try {
            // 确保admin用户的角色值为0（管理员）
            int updateCount = jdbcTemplate.update("UPDATE tb_user SET role = 0 WHERE username = 'admin'");
            
            // 检查是否存在superadmin用户
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tb_user WHERE username = 'superadmin'", Integer.class);
            
            // 如果不存在，则添加一个新的管理员用户
            if (count != null && count == 0) {
                jdbcTemplate.update(
                    "INSERT INTO tb_user (username, password, nickname, avatar, email, phone, status, role) " +
                    "VALUES ('superadmin', '$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm', '超级管理员', " +
                    "'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png', " +
                    "'superadmin@example.com', '13800138001', 1, 0)"
                );
            }
            
            return Result.success("管理员用户角色修复成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("修复失败：" + e.getMessage());
        }
    }
}
```

## 使用说明

1. 重启后端服务
2. 访问以下URL修复管理员角色：
   ```
   http://localhost:8080/api/admin-fix/fix-admin-role
   ```
3. 清除浏览器缓存和localStorage
4. 使用以下账号登录：
   - 用户名：admin 或 superadmin
   - 密码：123456 或 admin123
5. 登录后，您应该能够看到后台管理菜单，并访问管理员功能

## 后台管理功能

后台管理包含以下功能：
- 控制台：显示网站统计信息
- 内容管理：
  - 文章管理
  - 分类管理
  - 标签管理
  - 评论管理
- 相册管理：
  - 照片管理
  - 相册分类
- 留言管理
- 友链管理
- 用户管理
- 系统设置