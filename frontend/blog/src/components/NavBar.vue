<template>
  <header class="header">
    <div class="nav-container">
      <div class="logo">{{ siteSettings.site_title || '博客系统' }}</div>
      <div class="nav-links">
        <router-link to="/" class="nav-item" exact>首页</router-link>
        <router-link to="/article" class="nav-item">文章</router-link>
        <template v-if="isAuthenticated">
          <router-link to="/write" class="nav-item">写作</router-link>
          <router-link to="/photos" class="nav-item">相册</router-link>
          <router-link to="/message" class="nav-item">留言</router-link>
        </template>
        <router-link to="/friends" class="nav-item">友链</router-link>
        <router-link to="/about" class="nav-item">关于</router-link>
        <router-link v-if="isAdmin" to="/admin" class="nav-item admin-link">
          <i class="el-icon-setting"></i> 管理
        </router-link>
      </div>
      <div class="user-section">
        <div v-if="isAuthenticated && user" class="user-avatar">
          <el-dropdown trigger="click" @command="handleCommand">
            <img :src="user.avatar" alt="用户头像" class="avatar" />
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
        <div v-else>
          <router-link to="/login">
            <el-button type="primary" size="small" round>登录</el-button>
          </router-link>
        </div>
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
      }
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
}

.user-avatar:hover {
  transform: scale(1.1);
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

@media (max-width: 768px) {
  .nav-container {
    padding: 15px 20px;
    flex-wrap: wrap;
  }
  
  .nav-links {
    order: 3;
    width: 100%;
    overflow-x: auto;
    padding: 10px 0;
    justify-content: flex-start;
    gap: 15px;
  }
  
  .nav-item {
    font-size: 14px;
    padding: 6px 12px;
    white-space: nowrap;
  }
  
  .logo {
    font-size: 22px;
  }
}
</style>