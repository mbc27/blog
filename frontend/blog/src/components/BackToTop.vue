<template>
  <transition name="fade">
    <div 
      v-show="visible" 
      class="back-to-top" 
      @click="scrollToTop"
      title="回到顶部"
    >
      <i class="el-icon-top"></i>
    </div>
  </transition>
</template>

<script>
export default {
  name: 'BackToTop',
  data() {
    return {
      visible: false,
      scrollThreshold: 200
    }
  },
  mounted() {
    // 监听多个滚动事件源，确保在不同布局下都能工作
    document.body.addEventListener('scroll', this.handleScroll, { passive: true })
    window.addEventListener('scroll', this.handleScroll, { passive: true })
    document.addEventListener('scroll', this.handleScroll, { passive: true })
    
    // 初始检查
    this.handleScroll()
  },
  beforeDestroy() {
    document.body.removeEventListener('scroll', this.handleScroll)
    window.removeEventListener('scroll', this.handleScroll)
    document.removeEventListener('scroll', this.handleScroll)
  },
  methods: {
    handleScroll() {
      // 获取滚动位置，兼容不同的滚动容器
      const bodyScroll = document.body.scrollTop || 0
      const windowScroll = window.pageYOffset || 0
      const documentScroll = document.documentElement.scrollTop || 0
      
      const currentScroll = Math.max(bodyScroll, windowScroll, documentScroll)
      this.visible = currentScroll > this.scrollThreshold
    },
    scrollToTop() {
      // 平滑滚动到顶部
      try {
        window.scrollTo({
          top: 0,
          behavior: 'smooth'
        })
        
        // 确保在所有情况下都能滚动到顶部
        document.body.scrollTop = 0
        document.documentElement.scrollTop = 0
      } catch (error) {
        // 备用方案：直接跳转
        window.scrollTo(0, 0)
        document.body.scrollTop = 0
        document.documentElement.scrollTop = 0
      }
    }
  }
}
</script>

<style scoped>
.back-to-top {
  position: fixed !important;
  right: 30px !important;
  bottom: 30px !important;
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex !important;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 20px 0 rgba(102, 126, 234, 0.3);
  z-index: 99999 !important;
  transition: all 0.3s ease;
  border: 2px solid rgba(255, 255, 255, 0.2);
  /* 确保按钮始终可见 */
  visibility: visible !important;
  opacity: 1 !important;
  pointer-events: auto !important;
}

.back-to-top:hover {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
  transform: translateY(-3px) scale(1.1);
  box-shadow: 0 6px 25px 0 rgba(102, 126, 234, 0.4);
  border-color: rgba(255, 255, 255, 0.3);
}

.back-to-top:active {
  transform: translateY(-1px) scale(1.05);
  background: linear-gradient(135deg, #4f5cd1 0%, #5d3a7e 100%);
}

.back-to-top i {
  color: white !important;
  font-size: 20px;
  font-weight: bold;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
  display: block !important;
}

/* 淡入淡出动画 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.4s ease, transform 0.4s ease;
}

.fade-enter, .fade-leave-to {
  opacity: 0;
  transform: translateY(20px) scale(0.8);
}

.fade-enter-to, .fade-leave {
  opacity: 1;
  transform: translateY(0) scale(1);
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .back-to-top {
    right: 20px !important;
    bottom: 20px !important;
    width: 45px;
    height: 45px;
  }
  
  .back-to-top i {
    font-size: 18px;
  }
}

/* 确保在所有布局下都能正确显示 */
@media screen and (min-width: 1px) {
  .back-to-top {
    position: fixed !important;
    z-index: 99999 !important;
  }
}

</style>