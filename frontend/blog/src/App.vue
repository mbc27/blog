<template>
  <div id="app">
    <nav-bar v-if="!isAdminRoute" />
    <div class="main-content" :class="{ 'admin-content': isAdminRoute }">
      <router-view/>
    </div>
    <Footer v-if="!isAdminRoute" />
    <!-- 回到顶部按钮 - 只在非管理端显示 -->
    <back-to-top v-if="!isAdminRoute" />
    <!-- AI咨询机器人 -->
    <ChatBot />
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import NavBar from './components/NavBar.vue'
import Footer from './components/Footer.vue'
import BackToTop from './components/BackToTop.vue'
import ChatBot from './components/ChatBot.vue'

export default {
  name: 'App',
  components: {
    NavBar,
    Footer,
    BackToTop,
    ChatBot
  },
  computed: {
    ...mapGetters(['isAuthenticated', 'isAdmin']),
    isAdminRoute() {
      return this.$route.path.startsWith('/admin')
    }
  },
  created() {
    // 检查用户登录状态
    this.checkAuthStatus()
    
    // 注意：这里不需要再添加路由守卫，因为已经在router/index.js中添加了全局前置守卫
  },
  watch: {
    // 监听路由变化，立即重置滚动位置到顶部
    '$route'() {
      // 立即重置滚动位置，不使用任何延迟或动画
      window.scrollTo(0, 0)
      document.documentElement.scrollTop = 0
      document.body.scrollTop = 0
    }
  },
  methods: {
    // 检查用户登录状态
    async checkAuthStatus() {
      const token = localStorage.getItem('token')
      // 如果有token，尝试获取用户信息
      if (token) {
        console.log('Token found in localStorage, attempting to get user info')
        try {
          // 先设置token到store，确保API请求能带上token
          this.$store.commit('SET_TOKEN', token)
          // 尝试获取用户信息
          const userData = await this.$store.dispatch('getUserInfo')
          if (!userData) {
            console.warn('Failed to get user info, but will continue with cached data if available')
            // 如果localStorage中有用户信息，使用它
            const cachedUser = JSON.parse(localStorage.getItem('user'))
            if (cachedUser) {
              this.$store.commit('SET_USER', cachedUser)
            }
          }
        } catch (error) {
          console.error('Error during auth check:', error)
          // 不要立即登出，尝试使用缓存的用户信息
        }
      } else {
        console.log('No token found, user is not authenticated')
      }
    }
  }
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  margin: 0;
  padding: 0;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  overflow-x: hidden;
}

.main-content {
  padding-top: 0; /* 移除顶部padding，让内容紧贴导航栏 */
  flex: 1; /* 让主内容区域占据剩余空间 */
}

.admin-content {
  padding-top: 0; /* 管理后台不需要导航栏空间 */
  padding: 0;
  margin: 0;
  height: 100vh;
  overflow: hidden;
  position: relative;
}
</style>
