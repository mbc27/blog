<template>
  <div id="app">
    <nav-bar />
    <div class="main-content">
      <router-view/>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import NavBar from './components/NavBar.vue'

export default {
  name: 'App',
  components: {
    NavBar
  },
  computed: {
    ...mapGetters(['isAuthenticated', 'isAdmin'])
  },
  created() {
    // 检查用户登录状态
    this.checkAuthStatus()
    
    // 注意：这里不需要再添加路由守卫，因为已经在router/index.js中添加了全局前置守卫
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
  height: 100%;
}

html, body {
  margin: 0;
  padding: 0;
  height: 100%;
}

.main-content {
  padding-top: 70px; /* 为固定导航栏留出空间 */
}
</style>
