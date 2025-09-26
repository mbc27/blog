import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import axios from 'axios'
import api from './api'

// 配置axios
axios.defaults.baseURL = process.env.VUE_APP_BASE_API || 'http://localhost:8080/api'
axios.defaults.timeout = 10000

// 请求拦截器
axios.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
axios.interceptors.response.use(
  response => {
    return response
  },
  error => {
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      // 显示友好的错误提示
      ElementUI.Message({
        message: '登录已过期，请重新登录',
        type: 'warning',
        duration: 3 * 1000
      })
      // 清除token，但只在不是登录页面时才跳转
      localStorage.removeItem('token')
      store.dispatch('logout')
      if (router.currentRoute.path !== '/login') {
        router.push('/login').catch(() => {})
      }
    }
    return Promise.reject(error)
  }
)

Vue.prototype.$axios = axios
Vue.prototype.$api = api

Vue.use(ElementUI)

Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')