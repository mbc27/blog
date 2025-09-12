import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import './assets/css/global.css'
import './assets/css/mobile-fix.css'
import './assets/css/admin-layout.css'
import './assets/css/emoji-popover.css'
import './assets/css/dialog-fix.css'
import ScrollReset from './utils/scrollReset'

Vue.use(ElementUI)
Vue.use(ScrollReset)
Vue.config.productionTip = false

// 全局错误处理器
Vue.config.errorHandler = (err, vm, info) => {
  console.error('Vue Error:', err)
  console.error('Component:', vm)
  console.error('Info:', info)
  
  // 特别处理 getBoundingClientRect 错误
  if (err.message && err.message.includes('getBoundingClientRect')) {
    console.warn('DOM element access error caught and handled')
    return
  }
  
  // 其他错误可以选择性地显示给用户
  if (process.env.NODE_ENV === 'development') {
    console.error('Unhandled Vue error:', err)
  }
}

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')