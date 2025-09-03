import Vue from 'vue'
import Vuex from 'vuex'
import api from '../api'

Vue.use(Vuex)

const store = new Vuex.Store({
  state: {
    token: localStorage.getItem('token') || '',
    user: JSON.parse(localStorage.getItem('user')) || null
  },
  getters: {
    token: state => state.token,
    user: state => state.user,
    isAuthenticated: state => !!state.token,
    isAdmin: state => {
      console.log('User role:', state.user ? state.user.role : 'No user');
      return state.user && state.user.role === 0;
    }
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token
      localStorage.setItem('token', token)
    },
    SET_USER(state, user) {
      state.user = user
      localStorage.setItem('user', JSON.stringify(user))
    },
    CLEAR_AUTH(state) {
      state.token = ''
      state.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  },
  actions: {
    // 登录
    async login({ commit, dispatch }, loginData) {
      try {
        const response = await api.user.login(loginData)
        if (response.code === 200) {
          const token = response.data.token
          commit('SET_TOKEN', token)
          
          try {
            // 尝试获取用户信息，但即使失败也不影响登录
            const userInfo = await dispatch('getUserInfo')
            console.log('Login successful, user info:', userInfo)
          } catch (userInfoError) {
            console.error('Failed to get user info, but login successful:', userInfoError)
            // 创建一个基本的用户对象，确保应用可以继续运行
            commit('SET_USER', {
              id: null,
              username: loginData.username,
              nickname: loginData.username,
              avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
              role: 1 // 默认为普通用户
            })
          }
          
          return true
        } else {
          throw new Error(response.message || '登录失败')
        }
      } catch (error) {
        console.error('Login error:', error)
        throw error
      }
    },
    
    // 注册
    async register(_, registerData) {
      const response = await api.user.register(registerData)
      if (response.code === 200) {
        return true
      } else {
        throw new Error(response.message || '注册失败')
      }
    },
    
    // 获取用户信息
    async getUserInfo({ commit, state }) {
      try {
        // 优先从localStorage获取token，确保页面刷新后仍能获取到token
        const token = localStorage.getItem('token') || state.token
        if (!token) {
          console.warn('No token available, cannot get user info')
          return null
        }
        
        // 确保token已设置到store中
        if (!state.token) {
          commit('SET_TOKEN', token)
        }
        
        console.log('Getting user info with token:', token)
        const response = await api.user.getInfo()
        console.log('getUserInfo response:', response)
        
        if (response && response.code === 200) {
          const userData = response.data
          console.log('User info received:', userData)
          console.log('User role:', userData.role)
          commit('SET_USER', userData)
          return userData
        } else {
          console.warn('Failed to get user info:', response ? response.message : 'No response')
          // 检查是否有缓存的用户信息
          const cachedUser = localStorage.getItem('user')
          if (cachedUser) {
            try {
              const parsedUser = JSON.parse(cachedUser)
              console.log('Using cached user info:', parsedUser)
              commit('SET_USER', parsedUser)
              return parsedUser
            } catch (parseError) {
              console.error('Error parsing cached user:', parseError)
            }
          }
          return null
        }
      } catch (error) {
        console.error('Error getting user info:', error)
        
        // 检查是否有缓存的用户信息
        const cachedUser = localStorage.getItem('user')
        if (cachedUser) {
          try {
            const parsedUser = JSON.parse(cachedUser)
            console.log('Using cached user info after error:', parsedUser)
            commit('SET_USER', parsedUser)
            return parsedUser
          } catch (parseError) {
            console.error('Error parsing cached user after error:', parseError)
          }
        }
        
        // 如果是401或403错误，清除token
        if (error.response && (error.response.status === 401 || error.response.status === 403)) {
          console.warn('Authentication error, clearing token')
          commit('CLEAR_AUTH')
        }
        
        // 不要抛出错误，返回null让应用继续运行
        return null
      }
    },
    
    // 更新用户信息
    async updateUserInfo({ commit }, userData) {
      const response = await api.user.updateInfo(userData)
      if (response.code === 200) {
        commit('SET_USER', response.data)
        return true
      } else {
        throw new Error(response.message || '更新用户信息失败')
      }
    },
    
    // 修改密码
    async changePassword(_, passwordData) {
      const response = await api.user.changePassword(passwordData)
      if (response.code === 200) {
        return true
      } else {
        throw new Error(response.message || '修改密码失败')
      }
    },
    
    // 登出
    // 登出
    logout({ commit }) {
      commit('CLEAR_AUTH')
      // 不在这里进行路由跳转，让调用方决定是否跳转
      return Promise.resolve()
    }
  }
})

export default store