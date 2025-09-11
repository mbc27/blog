<template>
  <footer class="footer">
    <div class="container">
      <div class="footer-content">
        <div class="footer-info">
          <h3>{{ siteSettings.site_title || '博客系统' }}</h3>
          <p>{{ siteSettings.site_description || '记录生活，分享思考' }}</p>
        </div>
        <div class="footer-links">
          <router-link to="/about">关于我们</router-link>
          <router-link to="/friends">友情链接</router-link>
          <router-link v-if="isAuthenticated" to="/message">留言反馈</router-link>
        </div>
      </div>
      <div class="footer-bottom">
        <p>&copy; 2025 {{ siteSettings.site_title || '博客系统' }}. All rights reserved.</p>
      </div>
    </div>
  </footer>
</template>

<script>
import { mapGetters } from 'vuex'
import api from '../api'

export default {
  name: 'Footer',
  data() {
    return {
      siteSettings: {
        site_title: 'XCSY的博客',
        site_description: '一个基于Spring Boot和Vue的个人博客系统'
      }
    }
  },
  computed: {
    ...mapGetters(['isAuthenticated'])
  },
  async mounted() {
    await this.loadSiteSettings()
  },
  methods: {
    async loadSiteSettings() {
      try {
        const response = await api.system.getPublicSettings()
        if (response.code === 200 && response.data) {
          // 处理从后端获取的设置数据
          const settings = response.data
          this.siteSettings = {
            site_title: settings.site_title || 'XCSY的博客',
            site_description: settings.site_description || '一个基于Spring Boot和Vue的个人博客系统',
            site_author: settings.site_author || 'XCSY',
            site_keywords: settings.site_keywords || 'blog,博客,技术分享'
          }
        }
      } catch (error) {
        console.error('加载站点设置失败:', error)
        // 使用默认值
        this.siteSettings = {
          site_title: 'XCSY的博客',
          site_description: '一个基于Spring Boot和Vue的个人博客系统'
        }
      }
    }
  }
}
</script>

<style scoped>
/* 页脚样式 */
.footer {
  background: #2c3e50;
  color: white;
  padding: 60px 0 30px;
  margin-top: auto;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 40px;
}

.footer-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 60px;
  margin-bottom: 30px;
}

.footer-info h3 {
  font-size: 1.5rem;
  margin-bottom: 15px;
  color: white;
}

.footer-info p {
  color: #bdc3c7;
  line-height: 1.6;
}

.footer-links {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.footer-links a {
  color: #bdc3c7;
  text-decoration: none;
  transition: color 0.3s ease;
}

.footer-links a:hover {
  color: white;
}

.footer-bottom {
  text-align: center;
  padding-top: 30px;
  border-top: 1px solid #34495e;
  color: #95a5a6;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .container {
    padding: 0 20px;
  }
  
  .footer-content {
    grid-template-columns: 1fr;
    text-align: center;
    gap: 30px;
  }
  
  .footer {
    padding: 40px 0 20px;
  }
}
</style>