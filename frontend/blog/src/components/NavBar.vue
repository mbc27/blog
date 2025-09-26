<template>
  <header class="header" :class="{ 'sidebar-active': mobileMenuOpen }">
    <div class="nav-container">
      <div class="logo">{{ siteSettings.site_title || '博客系统' }}</div>
      
      <!-- 移动端汉堡菜单按钮 -->
      <div class="mobile-menu-toggle" @click="toggleMobileMenu">
        <div class="hamburger-icon" :class="{ 'hamburger-active': mobileMenuOpen }">
          <span></span>
          <span></span>
          <span></span>
        </div>
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
      
      <div class="user-section" :class="{ 'user-section-disabled': mobileMenuOpen }">
        <div v-if="isAuthenticated && user" class="user-avatar">
          <el-dropdown 
            trigger="click" 
            @command="handleCommand" 
            placement="bottom-start"
            :hide-on-click="true"
            :popper-class="'navbar-dropdown'"
            :disabled="mobileMenuOpen"
            :append-to-body="true"
            :offset="0"
            :popper-options="{
              modifiers: {
                preventOverflow: { enabled: false },
                flip: { enabled: false },
                hide: { enabled: false }
              }
            }"
          >
            <img :src="user.avatar" alt="用户头像" class="avatar" />
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
        <div v-else class="login-btn-container">
          <router-link to="/login" :class="{ 'disabled-link': mobileMenuOpen }" @click.native="handleLoginClick">
            <el-button type="primary" size="small" round :disabled="mobileMenuOpen">登录</el-button>
          </router-link>
        </div>
      </div>
    </div>
    
    <!-- 移动端侧边栏遮罩 -->
    <div v-if="mobileMenuOpen" class="sidebar-overlay" @click="closeMobileMenu"></div>
    
    <!-- 移动端侧边栏 -->
    <div class="sidebar" :class="{ 'sidebar-open': mobileMenuOpen }">
      <div class="sidebar-content">
        <router-link to="/" class="sidebar-item" exact active-class="custom-active" exact-active-class="custom-exact-active" @click.native="handleSidebarItemClick">
          <i class="el-icon-house"></i>
          <span>首页</span>
        </router-link>
        <router-link to="/article" class="sidebar-item" exact active-class="custom-active" exact-active-class="custom-exact-active" @click.native="handleSidebarItemClick">
          <i class="el-icon-document"></i>
          <span>文章</span>
        </router-link>
        <template v-if="isAuthenticated">
          <router-link to="/write" class="sidebar-item" exact active-class="custom-active" exact-active-class="custom-exact-active" @click.native="handleSidebarItemClick">
            <i class="el-icon-edit"></i>
            <span>写作</span>
          </router-link>
          <router-link to="/photos" class="sidebar-item" exact active-class="custom-active" exact-active-class="custom-exact-active" @click.native="handleSidebarItemClick">
            <i class="el-icon-picture"></i>
            <span>相册</span>
          </router-link>
          <router-link to="/message" class="sidebar-item" exact active-class="custom-active" exact-active-class="custom-exact-active" @click.native="handleSidebarItemClick">
            <i class="el-icon-chat-line-square"></i>
            <span>留言</span>
          </router-link>
        </template>
        <router-link to="/friends" class="sidebar-item" exact active-class="custom-active" exact-active-class="custom-exact-active" @click.native="handleSidebarItemClick">
          <i class="el-icon-user"></i>
          <span>友链</span>
        </router-link>
        <router-link to="/contact" class="sidebar-item" exact active-class="custom-active" exact-active-class="custom-exact-active" @click.native="handleSidebarItemClick">
          <i class="el-icon-phone"></i>
          <span>联系我</span>
        </router-link>
        <router-link to="/about" class="sidebar-item" exact active-class="custom-active" exact-active-class="custom-exact-active" @click.native="handleSidebarItemClick">
          <i class="el-icon-info"></i>
          <span>关于</span>
        </router-link>
        <router-link v-if="isAdmin" to="/admin" class="sidebar-item admin-item" exact active-class="custom-active" exact-active-class="custom-exact-active" @click.native="handleSidebarItemClick">
          <i class="el-icon-setting"></i>
          <span>管理</span>
        </router-link>
      </div>
    </div>
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
  mounted() {
    // 修复下拉菜单定位
    this.$nextTick(() => {
      this.fixDropdownPosition()
    })
  },
  methods: {
    ...mapActions(['logout']),
    
    // 修复下拉菜单定位 - 实时跟随头像位置
    fixDropdownPosition() {
      let resizeTimer = null
      
      // 实时更新下拉框位置的函数
      const updateDropdownPosition = () => {
        const dropdown = document.querySelector('.el-dropdown-menu.navbar-dropdown')
        if (dropdown) {
          const userAvatar = this.$el.querySelector('.user-avatar')
          if (userAvatar) {
            const avatarRect = userAvatar.getBoundingClientRect()
            const dropdownRect = dropdown.getBoundingClientRect()
            const viewportWidth = window.innerWidth
            const viewportHeight = window.innerHeight
            
            const avatarBottom = avatarRect.bottom
            
            // 计算下拉框应该的左侧位置（让下拉框右对齐头像右边缘）
            let dropdownLeft = avatarRect.right - dropdownRect.width
            
            // 确保下拉框不会超出屏幕左边界
            if (dropdownLeft < 10) {
              dropdownLeft = 10
            }
            
            // 确保下拉框不会超出屏幕右边界
            if (dropdownLeft + dropdownRect.width > viewportWidth - 10) {
              dropdownLeft = viewportWidth - dropdownRect.width - 10
            }
            
            // 计算垂直位置
            let dropdownTop = avatarBottom + 8
            
            // 如果下拉框会超出屏幕底部，则显示在头像上方
            if (dropdownTop + dropdownRect.height > viewportHeight - 10) {
              dropdownTop = avatarRect.top - dropdownRect.height - 8
            }
            
            // 应用位置
            dropdown.style.position = 'fixed'
            dropdown.style.left = dropdownLeft + 'px'
            dropdown.style.top = dropdownTop + 'px'
            dropdown.style.right = 'auto'
            dropdown.style.transform = 'none'
            dropdown.style.zIndex = '10001'
          }
        }
      }
      
      // 监听下拉框的出现
      const observer = new MutationObserver((mutations) => {
        mutations.forEach((mutation) => {
          mutation.addedNodes.forEach((node) => {
            if (node.nodeType === 1 && node.classList && node.classList.contains('navbar-dropdown')) {
              // 延迟一帧确保DOM完全渲染
              this.$nextTick(() => {
                setTimeout(updateDropdownPosition, 10)
              })
            }
          })
        })
      })
      
      observer.observe(document.body, {
        childList: true,
        subtree: true
      })
      
      // 监听窗口大小变化和滚动
      const handleResize = () => {
        clearTimeout(resizeTimer)
        resizeTimer = setTimeout(updateDropdownPosition, 100)
      }
      
      window.addEventListener('resize', handleResize)
      window.addEventListener('scroll', updateDropdownPosition)
      
      // 清理事件监听器
      this.$once('hook:beforeDestroy', () => {
        observer.disconnect()
        window.removeEventListener('resize', handleResize)
        window.removeEventListener('scroll', updateDropdownPosition)
        clearTimeout(resizeTimer)
      })
    },
    
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
        this.$message({
          message: '已退出登录',
          type: 'success',
          duration: 3000
        })
        this.$router.push('/').catch(() => {})
      }
    },
    
    // 切换移动端菜单
    toggleMobileMenu() {
      console.log('toggleMobileMenu 被调用，当前状态:', this.mobileMenuOpen)
      this.mobileMenuOpen = !this.mobileMenuOpen
      console.log('切换后状态:', this.mobileMenuOpen)
      if (this.mobileMenuOpen) {
        document.body.style.overflow = 'hidden'
        document.body.classList.add('sidebar-blur-active')
      } else {
        document.body.style.overflow = ''
        document.body.classList.remove('sidebar-blur-active')
      }
    },
    
    // 关闭移动端菜单
    closeMobileMenu() {
      console.log('关闭移动端菜单') // 调试日志
      this.mobileMenuOpen = false
      document.body.style.overflow = ''
      document.body.classList.remove('sidebar-blur-active')
    },
    
    // 处理侧边栏菜单项点击
    handleSidebarItemClick() {
      // 立即关闭侧边栏，不需要临时激活状态
      this.closeMobileMenu()
    },
    
    // 处理登录按钮点击
    handleLoginClick(event) {
      if (this.mobileMenuOpen) {
        event.preventDefault()
        return false
      }
    }
  }
}
</script>

<style scoped>
/* 导航栏样式 */
.header {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(25px) saturate(180%);
  -webkit-backdrop-filter: blur(25px) saturate(180%);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  position: sticky;
  top: 0;
  width: 100%;
  z-index: 1000;
  transition: all 0.3s ease;
}

.nav-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 30px;
  max-width: 1400px;
  margin: 0 auto;
  height: 70px;
  box-sizing: border-box;
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
  transition: all 0.3s ease;
}

.user-section-disabled {
  pointer-events: none;
  filter: blur(2px);
  opacity: 0.6;
}

.disabled-link {
  pointer-events: none;
  cursor: not-allowed;
}

.user-avatar {
  width: 40px !important;
  height: 40px !important;
  min-width: 40px !important;
  min-height: 40px !important;
  max-width: 40px !important;
  max-height: 40px !important;
  border-radius: 50% !important;
  overflow: hidden !important;
  cursor: pointer;
  transition: transform 0.3s ease;
  border: 2px solid #e1e8ed;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 1001;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  flex-shrink: 0 !important;
}

.user-avatar:hover {
  transform: scale(1.05);
  border-color: #667eea;
}

.avatar {
  width: 100% !important;
  height: 100% !important;
  min-width: 100% !important;
  min-height: 100% !important;
  object-fit: cover !important;
  object-position: center center !important;
  display: block !important;
  border-radius: 0 !important;
  margin: 0 !important;
  padding: 0 !important;
  border: none !important;
  outline: none !important;
}

/* 下拉菜单样式 - 强制定位修复 */
.navbar-dropdown {
  z-index: 10001 !important;
  margin-top: 8px !important;
  border-radius: 8px !important;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  backdrop-filter: blur(20px) saturate(180%) !important;
  -webkit-backdrop-filter: blur(20px) saturate(180%) !important;
  background: rgba(255, 255, 255, 0.95) !important;
  position: fixed !important;
  transform: none !important;
}

.navbar-dropdown .el-dropdown-menu__item {
  padding: 12px 20px !important;
  font-size: 14px !important;
  color: #2c3e50 !important;
  transition: all 0.3s ease !important;
}

.navbar-dropdown .el-dropdown-menu__item:hover {
  background: rgba(102, 126, 234, 0.1) !important;
  color: #667eea !important;
}

.navbar-dropdown .el-dropdown-menu__item:not(:last-child) {
  border-bottom: 1px solid rgba(0, 0, 0, 0.05) !important;
}

/* 下拉菜单箭头样式 */
.navbar-dropdown .popper__arrow {
  border-bottom-color: rgba(255, 255, 255, 0.95) !important;
}

.navbar-dropdown .popper__arrow::after {
  border-bottom-color: rgba(255, 255, 255, 0.95) !important;
}

/* 移动端汉堡菜单按钮 */
.mobile-menu-toggle {
  display: none;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
  width: 40px;
  height: 40px;
  align-items: center;
  justify-content: center;
}

.mobile-menu-toggle:hover {
  background: rgba(102, 126, 234, 0.1);
}

/* 汉堡图标动画 */
.hamburger-icon {
  width: 24px;
  height: 18px;
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
}

.hamburger-icon span {
  display: block;
  height: 2px;
  width: 100%;
  background: #2c3e50;
  border-radius: 1px;
  transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  transform-origin: center;
  margin: 0;
}

.hamburger-icon span:not(:last-child) {
  margin-bottom: 6px;
}

.hamburger-icon:hover span {
  background: #667eea;
}

/* 汉堡图标激活状态 - 变成X */
.hamburger-active span:nth-child(1) {
  transform: rotate(45deg) translate(6px, 6px);
}

.hamburger-active span:nth-child(2) {
  opacity: 0;
  transform: scale(0);
}

.hamburger-active span:nth-child(3) {
  transform: rotate(-45deg) translate(6px, -6px);
}

/* 移动端侧边栏遮罩 - 优化玻璃质感 */
.sidebar-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(12px) saturate(150%);
  -webkit-backdrop-filter: blur(12px) saturate(150%);
  z-index: 9998;
  animation: fadeIn 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

/* 当侧边栏打开时，模糊主要内容 */
.header.sidebar-active .nav-container > *:not(.mobile-menu-toggle) {
  filter: blur(2px);
  pointer-events: none;
  transition: filter 0.3s ease;
}

.header.sidebar-active .mobile-menu-toggle {
  filter: none;
  pointer-events: auto;
}

@keyframes fadeIn {
  from { 
    opacity: 0;
    backdrop-filter: blur(0px);
    -webkit-backdrop-filter: blur(0px);
  }
  to { 
    opacity: 1;
    backdrop-filter: blur(8px) saturate(150%);
    -webkit-backdrop-filter: blur(8px) saturate(150%);
  }
}

/* 移动端侧边栏 - 匹配系统玻璃质感 */
.sidebar {
  position: fixed;
  top: 70px;
  left: 0;
  width: 260px;
  height: calc(100vh - 70px);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(25px) saturate(180%);
  -webkit-backdrop-filter: blur(25px) saturate(180%);
  box-shadow: 
    2px 0 30px rgba(0, 0, 0, 0.15),
    inset -1px 0 0 rgba(255, 255, 255, 0.3);
  border-right: 1px solid rgba(255, 255, 255, 0.2);
  z-index: 9999;
  transform: translateX(-100%);
  transition: transform 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  overflow-y: auto;
}

.sidebar-open {
  transform: translateX(0);
}

.sidebar-content {
  padding: 15px 0;
}

.sidebar-item {
  display: flex;
  align-items: center;
  padding: 18px 25px;
  color: #2c3e50;
  text-decoration: none;
  transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  border-left: 4px solid transparent;
  margin: 0;
  position: relative;
}

.sidebar-item:first-child {
  margin-top: 0;
}

.sidebar-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  transition: width 0.3s ease;
  z-index: -1;
}

.sidebar-item i {
  margin-right: 15px;
  font-size: 18px;
  width: 24px;
  text-align: center;
  transition: all 0.3s ease;
}

.sidebar-item span {
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.sidebar-item:hover {
  background: rgba(102, 126, 234, 0.08);
  color: #667eea;
  border-left-color: #667eea;
  transform: translateX(5px);
}

.sidebar-item:hover::before {
  width: 4px;
}

.sidebar-item:hover i {
  transform: scale(1.1);
  color: #667eea;
}

/* 移除非精确匹配的激活状态，只保留精确匹配 */

.sidebar-item.custom-exact-active {
  background: rgba(102, 126, 234, 0.12);
  color: #667eea;
  border-left-color: #667eea;
  font-weight: 600;
}

.sidebar-item.custom-exact-active::before {
  width: 4px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.sidebar-item.admin-item {
  margin: 15px 20px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-left: none;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
  position: relative;
  overflow: hidden;
}

.sidebar-item.admin-item::before {
  display: none;
}

.sidebar-item.admin-item::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.sidebar-item.admin-item:hover {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
  color: white;
  border-left: none;
  transform: translateX(0) scale(1.02);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.sidebar-item.admin-item:hover::after {
  left: 100%;
}

.sidebar-item.admin-item i {
  color: white;
}

.sidebar-item.admin-item:hover i {
  color: white;
  transform: scale(1.1) rotate(90deg);
}

@media (max-width: 768px) {
  .nav-container {
    padding: 15px 20px;
    position: relative;
    height: 70px;
    box-sizing: border-box;
  }
  
  .desktop-nav {
    display: none;
  }
  
  .mobile-menu-toggle {
    display: flex !important;
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
    width: 36px !important;
    height: 36px !important;
    min-width: 36px !important;
    min-height: 36px !important;
    max-width: 36px !important;
    max-height: 36px !important;
  }
}

@media (max-width: 480px) {
  .nav-container {
    padding: 12px 15px;
    height: 60px;
    box-sizing: border-box;
  }
  
  .logo {
    font-size: 20px;
  }
  
  .sidebar {
    width: 240px;
    top: 60px;
    height: calc(100vh - 60px);
  }
  
  .user-avatar {
    width: 32px !important;
    height: 32px !important;
    min-width: 32px !important;
    min-height: 32px !important;
    max-width: 32px !important;
    max-height: 32px !important;
  }
}

@media (max-width: 360px) {
  .sidebar {
    width: 220px;
  }
}
</style>

<style>
/* 全局样式 - 当侧边栏激活时模糊页面内容 */
body.sidebar-blur-active > #app > *:not(header) {
  filter: blur(3px);
  pointer-events: none;
  transition: filter 0.3s ease;
}

body.sidebar-blur-active {
  overflow: hidden;
}

/* 全局下拉菜单定位修复 - 完全由JavaScript控制位置 */
.el-dropdown-menu.navbar-dropdown {
  position: fixed !important;
  z-index: 10001 !important;
  transform: none !important;
  margin: 0 !important;
  min-width: 120px !important;
  max-width: 180px !important;
  border-radius: 8px !important;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  backdrop-filter: blur(20px) saturate(180%) !important;
  -webkit-backdrop-filter: blur(20px) saturate(180%) !important;
  background: rgba(255, 255, 255, 0.95) !important;
  animation: dropdownFadeIn 0.2s ease-out !important;
}

@keyframes dropdownFadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 移动端优化 */
@media (max-width: 768px) {
  .el-dropdown-menu.navbar-dropdown {
    min-width: 110px !important;
    max-width: 160px !important;
  }
}

@media (max-width: 480px) {
  .el-dropdown-menu.navbar-dropdown {
    min-width: 100px !important;
    max-width: 140px !important;
  }
}
</style>