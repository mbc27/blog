<template>
  <div class="home">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="nav-container">
        <div class="logo">{{ siteSettings.site_title || '博客系统' }}</div>
        <div class="nav-links">
          <router-link to="/" class="nav-item">首页</router-link>
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

    <!-- 主横幅区域 -->
    <section class="hero-section">
      <div class="hero-background">
        <div class="hero-overlay"></div>
        <div class="hero-content">
          <h1 class="hero-title">{{ siteSettings.site_title || '博客系统' }}</h1>
          <p class="hero-subtitle">{{ siteSettings.site_description || '记录生活，分享思考' }}</p>
          <div class="hero-actions">
            <router-link to="/article">
              <el-button type="primary" size="large" round>开始阅读</el-button>
            </router-link>
            <router-link v-if="isAuthenticated" to="/write">
              <el-button type="success" size="large" round>开始创作</el-button>
            </router-link>
          </div>
        </div>
        <div class="scroll-indicator">
          <i class="el-icon-arrow-down"></i>
        </div>
      </div>
    </section>

    <!-- 主要内容区域 -->
    <main class="main-content">
      <div class="container">
        <!-- 统计信息卡片 -->
        <div class="stats-section">
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-icon articles">
                <i class="el-icon-document"></i>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ articleCount }}</div>
                <div class="stat-label">文章</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon categories">
                <i class="el-icon-folder"></i>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ categoryCount }}</div>
                <div class="stat-label">分类</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon views">
                <i class="el-icon-view"></i>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{ formatNumber(viewCount) }}</div>
                <div class="stat-label">访问</div>
              </div>
            </div>
          </div>
        </div>



        <!-- 最新文章区域 -->
        <div class="articles-section">
          <div class="section-header">
            <h2 class="section-title">
              <i class="el-icon-document"></i>
              最新文章
            </h2>
            <router-link to="/article" class="view-all">
              查看全部 <i class="el-icon-arrow-right"></i>
            </router-link>
          </div>
          
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="3" animated />
          </div>
          
          <div v-else-if="articles.length === 0" class="empty-state">
            <i class="el-icon-document"></i>
            <p>暂无文章</p>
            <router-link v-if="isAuthenticated" to="/write">
              <el-button type="primary" round>写第一篇文章</el-button>
            </router-link>
          </div>
          
          <div v-else class="articles-grid">
            <article v-for="article in articles" :key="article.id" class="article-card">
              <div class="article-image">
                <img :src="article.coverImage || '/default-cover.jpg'" :alt="article.title" />
                <div class="article-category">{{ article.categoryName || '未分类' }}</div>
              </div>
              <div class="article-content">
                <h3 class="article-title">
                  <router-link :to="`/article/${article.id}`">{{ article.title }}</router-link>
                </h3>
                <p class="article-summary">{{ article.summary || '暂无摘要...' }}</p>
                <div class="article-meta">
                  <span class="meta-item">
                    <i class="el-icon-time"></i>
                    {{ formatDate(article.createTime) }}
                  </span>
                  <span class="meta-item">
                    <i class="el-icon-view"></i>
                    {{ article.viewCount || 0 }}
                  </span>
                  <span class="meta-item">
                    <i class="el-icon-chat-line-round"></i>
                    {{ article.commentCount || 0 }}
                  </span>
                </div>
              </div>
            </article>
          </div>
        </div>
      </div>
    </main>

    <!-- 页脚 -->
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
  </div>
</template>

<script>
import api from '../api'
import { mapGetters, mapActions } from 'vuex'

export default {
  name: 'Home',
  data() {
    return {
      articleCount: 0,
      categoryCount: 0,
      totalViews: 0,
      viewCount: 0,
      articles: [],
      loading: true,
      siteSettings: {
        site_title: '博客系统',
        site_description: '记录生活，分享思考',
        site_author: '博主',
        site_keywords: 'blog,博客,技术分享'
      }
    }
  },
  computed: {
    ...mapGetters(['user', 'isAuthenticated']),
    isAdmin() {
      return this.user && this.user.role === 0;
    }
  },
  async mounted() {
    // 检查是否有token，如果有则尝试获取用户信息
    const token = localStorage.getItem('token')
    if (token && !this.user) {
      try {
        await this.$store.dispatch('getUserInfo')
      } catch (error) {
        console.error('Failed to get user info:', error)
      }
    }
    
    await this.loadSiteSettings()
    await this.fetchData()
  },
  methods: {
    ...mapActions(['logout']),
    
    async loadSiteSettings() {
      try {
        const response = await api.system.getPublicSettings()
        if (response.code === 200) {
          this.siteSettings = response.data
          if (this.siteSettings.site_title) {
            document.title = this.siteSettings.site_title
          }
          this.updateMetaTags()
        }
      } catch (error) {
        console.log('加载系统设置失败，使用默认设置:', error)
      }
    },
    
    updateMetaTags() {
      // 更新meta标签
      const updateMeta = (name, content) => {
        let meta = document.querySelector(`meta[name="${name}"]`)
        if (!meta) {
          meta = document.createElement('meta')
          meta.name = name
          document.head.appendChild(meta)
        }
        meta.content = content
      }
      
      if (this.siteSettings.site_description) {
        updateMeta('description', this.siteSettings.site_description)
      }
      if (this.siteSettings.site_keywords) {
        updateMeta('keywords', this.siteSettings.site_keywords)
      }
      if (this.siteSettings.site_author) {
        updateMeta('author', this.siteSettings.site_author)
      }
    },
    
    async fetchData() {
      try {
        this.loading = true
        
        // 获取最新文章列表
        const articlesResponse = await api.article.getList({ current: 1, size: 6 })
        if (articlesResponse.code === 200) {
          this.articles = articlesResponse.data.records || []
          this.articleCount = articlesResponse.data.total || 0
        }
        
        // 获取分类数量
        try {
          const categoryResponse = await api.category.getAll()
          if (categoryResponse.code === 200) {
            this.categoryCount = categoryResponse.data.length || 0
          }
        } catch (categoryError) {
          this.categoryCount = 5
        }
        
        // 获取所有文章的访问量总和
        await this.fetchTotalViewCount()
        
      } catch (error) {
        console.error('获取数据失败:', error)
      } finally {
        this.loading = false
      }
    },
    
    async fetchTotalViewCount() {
      try {
        // 获取所有文章来计算总访问量
        const allArticlesResponse = await api.article.getList({ current: 1, size: 1000 })
        if (allArticlesResponse.code === 200) {
          const allArticles = allArticlesResponse.data.records || []
          this.viewCount = allArticles.reduce((total, article) => {
            return total + (article.viewCount || 0)
          }, 0)
        }
      } catch (error) {
        console.error('获取访问量统计失败:', error)
        this.viewCount = 0
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
    },
    
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    },
    
    formatNumber(num) {
      if (num >= 10000) {
        return (num / 10000).toFixed(1) + 'w'
      } else if (num >= 1000) {
        return (num / 1000).toFixed(1) + 'k'
      }
      return num.toString()
    }
  }
}
</script>

<style scoped>
/* 全局样式 */
.home {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

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

/* 主横幅样式 */
.hero-section {
  height: 100vh;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.2);
}

.hero-content {
  position: relative;
  z-index: 2;
  text-align: center;
  color: white;
  max-width: 800px;
  padding: 0 20px;
}

.hero-title {
  font-size: 4rem;
  font-weight: 300;
  margin-bottom: 1rem;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.hero-subtitle {
  font-size: 1.5rem;
  margin-bottom: 3rem;
  opacity: 0.9;
  line-height: 1.6;
}

.hero-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
  flex-wrap: wrap;
}

.scroll-indicator {
  position: absolute;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  color: white;
  font-size: 24px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateX(-50%) translateY(0);
  }
  40% {
    transform: translateX(-50%) translateY(-10px);
  }
  60% {
    transform: translateX(-50%) translateY(-5px);
  }
}

/* 主要内容样式 */
.main-content {
  padding: 80px 0;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 30px;
}

/* 统计信息样式 */
.stats-section {
  margin-bottom: 80px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
  max-width: 900px;
  margin: 0 auto;
}

.stat-card {
  background: white;
  padding: 40px 30px;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 20px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 28px;
}

.stat-icon.articles {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.categories {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.views {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-number {
  font-size: 2.5rem;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 5px;
}

.stat-label {
  color: #7f8c8d;
  font-size: 16px;
  font-weight: 500;
}



/* 文章区域样式 */
.articles-section {
  margin-bottom: 80px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
}

.view-all {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: all 0.3s ease;
}

.view-all:hover {
  color: #5a6fd8;
}

.loading-container {
  background: white;
  padding: 40px;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #7f8c8d;
}

.empty-state i {
  font-size: 64px;
  margin-bottom: 20px;
  display: block;
  color: #bdc3c7;
}

.articles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 30px;
}

.article-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.article-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.article-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.article-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.article-card:hover .article-image img {
  transform: scale(1.05);
}

.article-category {
  position: absolute;
  top: 15px;
  left: 15px;
  background: rgba(102, 126, 234, 0.9);
  color: white;
  padding: 5px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 500;
}

.article-content {
  padding: 25px;
}

.article-title {
  margin-bottom: 15px;
}

.article-title a {
  color: #2c3e50;
  text-decoration: none;
  font-size: 1.2rem;
  font-weight: 600;
  line-height: 1.4;
  transition: color 0.3s ease;
}

.article-title a:hover {
  color: #667eea;
}

.article-summary {
  color: #7f8c8d;
  line-height: 1.6;
  margin-bottom: 20px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-meta {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #95a5a6;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

/* 页脚样式 */
.footer {
  background: #2c3e50;
  color: white;
  padding: 60px 0 30px;
}

.footer-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 40px;
  margin-bottom: 40px;
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
  .nav-container {
    padding: 15px 20px;
  }
  
  .nav-links {
    display: none;
  }
  
  .hero-title {
    font-size: 2.5rem;
  }
  
  .hero-subtitle {
    font-size: 1.2rem;
  }
  
  .hero-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .container {
    padding: 0 20px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  

  
  .articles-grid {
    grid-template-columns: 1fr;
  }
  
  .section-header {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
  
  .footer-content {
    grid-template-columns: 1fr;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .hero-title {
    font-size: 2rem;
  }
  
  .stat-card {
    padding: 25px 20px;
  }
  
  .feature-link {
    padding: 20px;
  }
  
  .article-content {
    padding: 20px;
  }
}
</style>