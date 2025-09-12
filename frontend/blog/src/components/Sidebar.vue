<template>
  <div class="sidebar-container">
    <!-- 侧边栏触发按钮 -->
    <div class="sidebar-trigger" @click="toggleSidebar" :class="{ active: sidebarOpen }">
      <div class="hamburger-lines">
        <span class="line line1"></span>
        <span class="line line2"></span>
        <span class="line line3"></span>
      </div>
    </div>

    <!-- 模糊遮罩层 -->
    <div 
      class="sidebar-backdrop" 
      :class="{ active: sidebarOpen }" 
      @click="closeSidebar"
    ></div>

    <!-- 侧边栏主体 -->
    <div class="sidebar" :class="{ open: sidebarOpen }">
      <div class="sidebar-content">
        <!-- 侧边栏头部 -->
        <div class="sidebar-header">
          <h3 class="sidebar-title">{{ siteTitle }}</h3>
          <button class="close-btn" @click="closeSidebar">
            <i class="el-icon-close"></i>
          </button>
        </div>

        <!-- 导航菜单 -->
        <nav class="sidebar-nav">
          <router-link to="/" class="nav-item" exact @click.native="closeSidebar">
            <i class="el-icon-house"></i>
            <span>首页</span>
          </router-link>
          
          <router-link to="/article" class="nav-item" @click.native="closeSidebar">
            <i class="el-icon-document"></i>
            <span>文章</span>
          </router-link>
          
          <template v-if="isAuthenticated">
            <router-link to="/write" class="nav-item" @click.native="closeSidebar">
              <i class="el-icon-edit"></i>
              <span>写作</span>
            </router-link>
            
            <router-link to="/photos" class="nav-item" @click.native="closeSidebar">
              <i class="el-icon-picture"></i>
              <span>相册</span>
            </router-link>
            
            <router-link to="/message" class="nav-item" @click.native="closeSidebar">
              <i class="el-icon-chat-line-square"></i>
              <span>留言</span>
            </router-link>
          </template>
          
          <router-link to="/friends" class="nav-item" @click.native="closeSidebar">
            <i class="el-icon-user"></i>
            <span>友链</span>
          </router-link>
          
          <router-link to="/contact" class="nav-item" @click.native="closeSidebar">
            <i class="el-icon-message"></i>
            <span>联系我</span>
          </router-link>
          
          <router-link to="/about" class="nav-item" @click.native="closeSidebar">
            <i class="el-icon-info"></i>
            <span>关于</span>
          </router-link>
          
          <router-link v-if="isAdmin" to="/admin" class="nav-item admin-item" @click.native="closeSidebar">
            <i class="el-icon-setting"></i>
            <span>管理</span>
          </router-link>
        </nav>

        <!-- 用户信息区域 -->
        <div class="sidebar-footer">
          <div v-if="isAuthenticated && user" class="user-info">
            <img :src="user.avatar" alt="用户头像" class="user-avatar" />
            <div class="user-details">
              <p class="username">{{ user.username }}</p>
              <p class="user-role">{{ user.role === 0 ? '管理员' : '用户' }}</p>
            </div>
          </div>
          <div v-else class="login-section">
            <router-link to="/login" @click.native="closeSidebar">
              <el-button type="primary" size="small" round>登录</el-button>
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Sidebar',
  data() {
    return {
      sidebarOpen: false,
      siteTitle: 'XCSY的博客'
    }
  },
  computed: {
    ...mapGetters(['user', 'isAuthenticated']),
    isAdmin() {
      return this.user && this.user.role === 0;
    }
  },
  methods: {
    toggleSidebar() {
      this.sidebarOpen = !this.sidebarOpen
      this.updateBodyOverflow()
    },
    
    closeSidebar() {
      this.sidebarOpen = false
      this.updateBodyOverflow()
    },
    
    updateBodyOverflow() {
      if (this.sidebarOpen) {
        document.body.style.overflow = 'hidden'
      } else {
        document.body.style.overflow = ''
      }
    }
  },
  
  beforeDestroy() {
    // 清理样式
    document.body.style.overflow = ''
  }
}
</script>

<style scoped>
/* 侧边栏容器 */
.sidebar-container {
  position: relative;
  z-index: 10000;
}

/* 触发按钮 */
.sidebar-trigger {
  position: fixed;
  top: 20px;
  left: 20px;
  width: 50px;
  height: 50px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.3);
  z-index: 10001;
}

.sidebar-trigger:hover {
  background: rgba(255, 255, 255, 0.95);
  transform: scale(1.05);
  box-shadow: 0 6px 25px rgba(0, 0, 0, 0.15);
}

.sidebar-trigger.active {
  background: rgba(102, 126, 234, 0.9);
  color: white;
}

/* 汉堡菜单线条 */
.hamburger-lines {
  width: 20px;
  height: 16px;
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.line {
  width: 100%;
  height: 2px;
  background: currentColor;
  border-radius: 2px;
  transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  transform-origin: center;
}

.sidebar-trigger.active .line1 {
  transform: rotate(45deg) translate(5px, 5px);
}

.sidebar-trigger.active .line2 {
  opacity: 0;
  transform: scaleX(0);
}

.sidebar-trigger.active .line3 {
  transform: rotate(-45deg) translate(7px, -6px);
}

/* 模糊遮罩层 */
.sidebar-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  background: rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(15px) saturate(150%) brightness(0.85);
  -webkit-backdrop-filter: blur(15px) saturate(150%) brightness(0.85);
  opacity: 0;
  visibility: hidden;
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  z-index: 9998;
}

.sidebar-backdrop.active {
  opacity: 1;
  visibility: visible;
}

/* 侧边栏主体 */
.sidebar {
  position: fixed;
  top: 0;
  left: -320px;
  width: 280px;
  height: 100vh;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(40px) saturate(200%) brightness(1.05);
  -webkit-backdrop-filter: blur(40px) saturate(200%) brightness(1.05);
  box-shadow: 
    4px 0 50px rgba(0, 0, 0, 0.12),
    inset -1px 0 0 rgba(255, 255, 255, 0.5);
  border-right: 1px solid rgba(255, 255, 255, 0.2);
  transition: left 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  z-index: 9999;
  overflow-y: auto;
}

.sidebar.open {
  left: 0;
}

/* 侧边栏内容 */
.sidebar-content {
  padding: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* 侧边栏头部 */
.sidebar-header {
  padding: 25px 20px 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255, 255, 255, 0.5);
}

.sidebar-title {
  font-size: 20px;
  font-weight: 700;
  margin: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.close-btn {
  background: none;
  border: none;
  font-size: 18px;
  color: #666;
  cursor: pointer;
  padding: 5px;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

/* 导航菜单 */
.sidebar-nav {
  flex: 1;
  padding: 10px 0;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 15px 25px;
  color: #2c3e50;
  text-decoration: none;
  font-weight: 500;
  font-size: 16px;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
}

.nav-item i {
  font-size: 18px;
  margin-right: 12px;
  width: 20px;
  text-align: center;
}

.nav-item:hover {
  background: rgba(102, 126, 234, 0.08);
  color: #667eea;
  border-left-color: rgba(102, 126, 234, 0.3);
}

.nav-item.router-link-active {
  background: rgba(102, 126, 234, 0.12);
  color: #667eea;
  border-left-color: #667eea;
}

.nav-item.router-link-exact-active {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.15) 100%);
  color: #667eea;
  border-left-color: #667eea;
  font-weight: 600;
}

.nav-item.admin-item {
  margin: 10px 15px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white !important;
  border-left: none;
}

.nav-item.admin-item:hover {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
  transform: translateX(2px);
}

/* 侧边栏底部 */
.sidebar-footer {
  padding: 20px;
  border-top: 1px solid rgba(0, 0, 0, 0.08);
  background: rgba(255, 255, 255, 0.3);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(102, 126, 234, 0.2);
}

.user-details {
  flex: 1;
}

.username {
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 4px 0;
  font-size: 14px;
}

.user-role {
  font-size: 12px;
  color: #666;
  margin: 0;
}

.login-section {
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    width: 260px;
    left: -280px;
  }
}

@media (max-width: 480px) {
  .sidebar {
    width: 240px;
    left: -260px;
  }
  
  .sidebar-trigger {
    top: 15px;
    left: 15px;
    width: 45px;
    height: 45px;
  }
}

/* 滚动条样式 */
.sidebar::-webkit-scrollbar {
  width: 4px;
}

.sidebar::-webkit-scrollbar-track {
  background: transparent;
}

.sidebar::-webkit-scrollbar-thumb {
  background: rgba(102, 126, 234, 0.3);
  border-radius: 2px;
}

.sidebar::-webkit-scrollbar-thumb:hover {
  background: rgba(102, 126, 234, 0.5);
}
</style>