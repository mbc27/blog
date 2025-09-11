// 滚动位置重置工具
export default {
  install(Vue) {
    // 创建立即滚动重置方法
    Vue.prototype.$scrollToTop = function() {
      // 立即重置滚动位置，不使用任何动画或延迟
      window.scrollTo(0, 0)
      document.documentElement.scrollTop = 0
      document.body.scrollTop = 0
      
      // 重置可能的滚动容器
      const scrollContainers = document.querySelectorAll(
        '.scroll-container, .main-content, .el-scrollbar__wrap, .page-container'
      )
      scrollContainers.forEach(container => {
        if (container.scrollTop !== undefined) {
          container.scrollTop = 0
        }
      })
    }
    
    // 路由变化时立即重置滚动位置
    Vue.mixin({
      beforeRouteEnter(to, from, next) {
        // 在路由进入前立即重置
        window.scrollTo(0, 0)
        document.documentElement.scrollTop = 0
        document.body.scrollTop = 0
        next()
      },
      beforeRouteUpdate(to, from, next) {
        // 在路由更新前立即重置
        window.scrollTo(0, 0)
        document.documentElement.scrollTop = 0
        document.body.scrollTop = 0
        next()
      }
    })
  }
}