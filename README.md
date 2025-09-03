# 博客系统

一个功能完善的前后端分离博客系统，包含博客展示和后台管理功能。

## 🛠️ 技术栈

| 模块   | 技术栈            | 组件                                  |
|------|----------------|-------------------------------------|
| 后端   | SpringBoot 2.7 | Nginx + MySQL 8.0.26 + Mybatis-Plus + T-io |
| 博客   | Vue2           | Element UI                          |
| 聊天室  | Vue3           | Element-Plus UI + Naive UI          |
| 移动适配 | CSS            | 完美自适应                               |

## 📋 功能介绍

网站分两个模块：

- 博客系统：具有文章，表白墙，图片墙，收藏夹，音乐播放，视频播放，留言，友链，后台管理等功能。
- 聊天室系统：具有朋友圈，好友，群等功能。

本网站采用前后端分离进行实现，两个前端项目通过Nginx代理，后端使用Java，数据存储使用Mysql。

文件服务可以使用七牛云，也可以使用服务器。默认使用服务器。

Vue3（IM 聊天室系统）是非必须的。如果部署，则需要依赖博客，然后从博客的"联系我"进入，因为登录模块在博客。

## 🔧 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+
- Node.js 14+
- Nginx (生产环境部署需要)

## 📦 项目结构

```
├── backend                 # 后端项目
│   └── blog-api            # 后端API服务
├── frontend                # 前端项目
│   └── blog                # 博客前端
└── blog.sql                # 数据库初始化脚本
```

## 🚀 增强功能（待实现）

1. 多维度门户系统：
    - 经典博客首页
    - 文章首页
    - 专栏首页
    - 极简个人主页

2. 支付模块：
    - 支付宝/微信/易支付/码支付对接
    - 商品系统
    - 会员体系

3. 智能增强：
    - AI文章生成
    - AI文章摘要生成

4. 全方位登录方案：
    - 国内主流社交平台登录
    - 微信公众号验证码登录
    - 手机邮箱验证码登录

5. 存储解决方案：
    - 支持7大云存储厂商
    - 自建存储方案
    - 混合存储模式

## 🚀 快速开始

### 数据库配置

1. 创建数据库

```sql
CREATE DATABASE blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

2. 导入数据库脚本

```bash
mysql -u用户名 -p密码 blog < blog.sql
```

sql地址：backend/blog-api/src/main/resources/sql

### 后端启动

1. 修改数据库配置

编辑 `backend/blog-api/src/main/resources/application.yml` 文件，配置数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/blog?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: 你的数据库用户名
    password: 你的数据库密码
```

2. 编译运行

```bash
cd backend/blog-api
mvn clean package
java -jar target/blog-api-0.0.1-SNAPSHOT.jar
```

或者使用Maven插件运行：

```bash
cd backend/blog-api
mvn spring-boot:run
```

### 前端启动

1. 安装依赖

```bash
cd frontend/blog
npm install
```

2. 开发环境运行

```bash
npm run serve
```

3. 生产环境构建

```bash
npm run build
```

## 🔐 默认账号

- 普通账号：user
- 普通密码：123456
- 管理员账号：admin
- 管理员密码：123456
