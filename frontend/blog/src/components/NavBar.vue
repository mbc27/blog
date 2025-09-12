<template>
  <header class="header">
    <div class="nav-container">
      <div class="logo">{{ siteSettings.site_title || '博客系统' }}</div>
      
      <!-- 移动端汉堡菜单按钮 -->
      <div class="mobile-menu-toggle" @click="toggleMobileMenu">
        <i class="el-icon-s-unfold" v-if="!mobileMenuOpen"></i>
        <i class="el-icon-s-fold" v-else></i>
      </div>
      
      <!-- 桌面端导航链接 -->
      <div class="nav-links desktop-nav">
        <router-link to="/" class="nav-item" exact>首页</router-link>
        <router-link to="/article" class="nav-item">文章</router-link>
        <template v-if="isAuthenticated">
          <router-link to="/write" class="nav-item">写作</router-link>
          <router-link to="/photos" class="nav-item">相册</router-link>
          <router-link to="/message" class="nav-item">留言</router-link>
        </template>
        <router-link to="/friends" class="nav-item">友链</router-link>
        <router-link to="/contact" class="nav-item">联系我</router-link>
        <router-link to="/about" class="nav-item">关于</router-link>
        <router-link v-if="isAdmin" to="/admin" class="nav-item admin-link">
          <i class="el-icon-setting"></i> 管理
        </router-link>
      </div>
      
      <div class="user-section">
        <div v-if="isAuthenticated && user" class="user-avatar">
          <el-dropdown 
            trigger="click" 
            @command="handleCommand" 
            placement="bottom-end"
            :hide-on-click="true"
            :popper-class="'navbar-dropdown'"
          >
            <img :src="user.avatar" alt="用户头像" class="avatar" />
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
        <div v-else class="login-btn-container">
          <router-link to="/login">
            <el-button type="primary" size="small" round>登录</el-button>
          </router-link>
        </div>
      </div>
    </div>
    
    <!-- 移动端导航菜单 -->
    <div class="mobile-nav" :class="{ 'mobile-nav-open': mobileMenuOpen }">
      <div class="mobile-nav-content">
        <router-link to="/" class="mobile-nav-item" exact @click="closeMobileMenu">首页</router-link>
        <router-link to="/article" class="mobile-nav-item" @click="closeMobileMenu">文章</router-link>
        <template v-if="isAuthenticated">
          <router-link to="/write" class="mobile-nav-item" @click="closeMobileMenu">写作</router-link>
          <router-link to="/photos" class="mobile-nav-item" @click="closeMobileMenu">相册</router-link>
          <router-link to="/message" class="mobile-nav-item" @click="closeMobileMenu">留言</router-link>
        </template>
        <router-link to="/friends" class="mobile-nav-item" @click="closeMobileMenu">友链</router-link>
        <router-link to="/contact" class="mobile-nav-item" @click="closeMobileMenu">联系我</router-link>
        <router-link to="/about" class="mobile-nav-item" @click="closeMobileMenu">关于</router-link>
        <router-link v-if="isAdmin" to="/admin" class="mobile-nav-item admin-link" @click="closeMobileMenu">
          <i class="el-icon-setting"></i> 管理
        </router-link>
      </div>
    </div>
    
    <!-- 移动端遮罩层 -->
    <div class="mobile-nav-overlay" :class="{ 'mobile-nav-overlay-show': mobileMenuOpen }" @click="closeMobileMenu"></div>
  </header>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import api from '../api'

export default {
  name: 'NavBar',
  data() {
    return {
      siteSettings: {
        site_title: '博客系统',
        site_description: '记录生活，分享思考'
      },
      mobileMenuOpen: false
    }
  },
  computed: {
    ...mapGetters(['user', 'isAuthenticated']),
    isAdmin() {
      return this.user && this.user.role === 0;
    }
  },
  created() {
    this.loadSiteSettings()
    // 监听用户信息更新事件
    this.$root.$on('user-updated', () => {
      this.$forceUpdate();
    });
  },
  beforeDestroy() {
    // 清理事件监听器
    this.$root.$off('user-updated');
    // 清理移动端菜单状态
    if (this.mobileMenuOpen) {
      document.body.style.overflow = '';
    }
  },
  methods: {
    ...mapActions(['logout']),
    
    async loadSiteSettings() {
      try {
        const response = await api.system.getPublicSettings()
        if (response.code === 200) {
          this.siteSettings = response.data
        }
      } catch (error) {
        console.log('加载系统设置失败，使用默认设置:', error)
      }
    },
    
    handleCommand(command) {
      if (command === 'profile') {
        this.$router.push('/user/profile')
      } else if (command === 'logout') {
        this.logout()
        this.$message.success('已退出登录')
        this.$router.push('/').catch(() => {})
      }
    },
    
    // 切换移动端菜单
    toggleMobileMenu() {
      this.mobileMenuOpen = !this.mobileMenuOpen
      // 防止背景滚动
      if (this.mobileMenuOpen) {
        document.body.style.overflow = 'hidden'
      } else {
        document.body.style.overflow = ''
      }
    },
    
    // 关闭移动端菜单
    closeMobileMenu() {
      this.mobileMenuOpen = false
      document.body.style.overflow = ''
    }
  }
}
</script>

<style scoped>
/* 导航栏样式 */
.header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
  position: fixed;
  width: 100%;
  top: 0;
  z-index: 10000;
  transition: all 0.3s ease;
}

.nav-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 30px;
  max-width: 1400px;
  margin: 0 auto;
}

.logo {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 30px;
}

.nav-item {
  color: #2c3e50;
  text-decoration: none;
  font-weight: 500;
  font-size: 16px;
  padding: 8px 16px;
  border-radius: 25px;
  transition: all 0.3s ease;
  position: relative;
}

.nav-item:hover {
  color: #667eea;
  background: rgba(102, 126, 234, 0.1);
}

.nav-item.router-link-active {
  color: #667eea;
  background: rgba(102, 126, 234, 0.15);
}

.nav-item.router-link-exact-active {
  color: white;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.admin-link {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white !important;
  padding: 10px 20px;
  border-radius: 25px;
  font-size: 14px;
}

.admin-link:hover {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
  color: white !important;
}

.user-section {
  display: flex;
  align-items: center;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s ease;
  border: 2px solid #e1e8ed;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-avatar:hover {
  transform: scale(1.1);
  border-color: #667eea;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

/* 下拉菜单样式 */
.navbar-dropdown {
  z-index: 10001 !important;
}

/* 移动端汉堡菜单按钮 */
.mobile-menu-toggle {
  display: none;
  font-size: 24px;
  cursor: pointer;
  color: #2c3e50;
  padding: 8px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.mobile-menu-toggle:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

/* 移动端导航菜单 */
.mobile-nav {
  position: fixed;
  top: 70px;
  left: -100%;
  width: 280px;
  height: calc(100vh - 70px);
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  box-shadow: 2px 0 20px rgba(0, 0, 0, 0.1);
  transition: left 0.3s ease;
  z-index: 9999;
  overflow-y: auto;
}

.mobile-nav-open {
  left: 0;
}

.mobile-nav-content {
  padding: 20px 0;
}

.mobile-nav-item {
  display: block;
  color: #2c3e50;
  text-decoration: none;
  font-weight: 500;
  font-size: 16px;
  padding: 15px 30px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.mobile-nav-item:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.mobile-nav-item.router-link-active {
  color: #667eea;
  background: rgba(102, 126, 234, 0.15);
}

.mobile-nav-item.router-link-exact-active {
  color: white;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.mobile-nav-item.admin-link {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white !important;
  margin: 10px 20px;
  border-radius: 25px;
  border-bottom: none;
}

/* 移动端遮罩层 */
.mobile-nav-overlay {
  position: fixed;
  top: 70px;
  left: 0;
  width: 100%;
  height: calc(100vh - 70px);
  background: rgba(0, 0, 0, 0.5);
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  z-index: 9998;
}

.mobile-nav-overlay-show {
  opacity: 1;
  visibility: visible;
}

@media (max-width: 768px) {
  .nav-container {
    padding: 15px 20px;
    position: relative;
  }
  
  .desktop-nav {
    display: none;
  }
  
  .mobile-menu-toggle {
    display: block;
  }
  
  .logo {
    font-size: 22px;
  }
  
  .user-section {
    position: relative;
    z-index: 10001;
  }
  
  .login-btn-container .el-button {
    font-size: 12px;
    padding: 8px 16px;
  }
  
  .user-avatar {
    width: 35px;
    height: 35px;
  }
}

@media (max-width: 480px) {
  .nav-container {
    padding: 12px 15px;
  }
  
  .logo {
    font-size: 20px;
  }
  
  .mobile-nav {
    width: 100%;
    left: -100%;
  }
  
  .mobile-nav-open {
    left: 0;
  }
}
</style>