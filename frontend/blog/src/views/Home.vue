<template>
  <div class="home">
    <!-- 移除重复的导航栏，使用全局NavBar组件 -->
    <!-- <header class="header">
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
          <router-link to="/contact" class="nav-item">联系我</router-link>
          <router-link to="/about" class="nav-item">关于</router-link>
          <router-link v-if="isAdmin" to="/admin" class="nav-item admin-link">
            <i class="el-icon-setting"></i> 管理
          </router-link>
        </div>
        <div class="user-section">
          <div v-if="isAuthenticated && user" class="user-avatar">
            <el-dropdown 
              trigger="click" 
              @command="handleCommand" 
              placement="bottom-end"
              :hide-on-click="true"
              :popper-class="'home-navbar-dropdown'"
            >
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
    </header> -->

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
            <article v-for="article in articles" :key="article.id" class="article-card" :class="{ 'no-cover': !article.cover || article.cover.trim() === '' }">
              <!-- 分类标签统一放在卡片左上角 -->
              <div class="article-category-tag">{{ article.categoryName || '未分类' }}</div>
              
              <!-- 有封面图时显示图片 -->
              <div v-if="article.cover && article.cover.trim() !== ''" class="article-image">
                <img :src="article.cover" :alt="article.title" @error="handleImageError" />
              </div>
              <!-- 没有封面图时显示标题背景 -->
              <div v-else class="article-title-cover">
                <router-link :to="`/article/${article.id}`" class="title-overlay-link">
                  <div class="title-overlay">
                    <h3 class="cover-title">{{ article.title }}</h3>
                  </div>
                </router-link>
              </div>
              <div class="article-content">
                <h3 v-if="article.cover && article.cover.trim() !== ''" class="article-title">
                  <router-link :to="`/article/${article.id}`">{{ article.title }}</router-link>
                </h3>
                <h3 v-else class="article-title clickable-title">
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

        <!-- 精选项目区域 -->
        <div class="projects-section">
          <div class="section-header">
            <h2 class="section-title">
              <i class="el-icon-cpu"></i>
              精选项目
            </h2>
            <div class="project-filters">
              <el-button 
                :type="selectedProjectFilter === 'all' ? 'primary' : 'default'" 
                size="small" 
                @click="filterProjects('all')"
              >
                全部
              </el-button>
              <el-button 
                :type="selectedProjectFilter === 'completed' ? 'primary' : 'default'" 
                size="small" 
                @click="filterProjects('completed')"
              >
                已完成
              </el-button>
              <el-button 
                :type="selectedProjectFilter === 'developing' ? 'primary' : 'default'" 
                size="small" 
                @click="filterProjects('developing')"
              >
                开发中
              </el-button>
              <el-button 
                :type="selectedProjectFilter === 'planning' ? 'primary' : 'default'" 
                size="small" 
                @click="filterProjects('planning')"
              >
                规划中
              </el-button>
              <el-button 
                :type="selectedProjectFilter === 'maintenance' ? 'primary' : 'default'" 
                size="small" 
                @click="filterProjects('maintenance')"
              >
                维护中
              </el-button>
            </div>
          </div>
          
          <div v-if="projectsLoading" class="loading-container">
            <el-skeleton :rows="2" animated />
          </div>
          
          <div v-else-if="displayedProjects.length === 0" class="empty-state">
            <i class="el-icon-cpu"></i>
            <p>暂无项目</p>
          </div>
          
          <div v-else class="projects-waterfall" ref="projectsWaterfall">
            <div 
              v-for="project in displayedProjects" 
              :key="project.id" 
              class="project-card"
              @click="handleProjectCardClick(project)"
            >
              <div class="project-header">
                <div class="project-status">
                  <el-tag 
                    :type="getStatusType(project.status)" 
                    size="mini"
                  >
                    {{ getStatusText(project.status) }}
                  </el-tag>
                  <el-tag 
                    v-if="project.isFeatured" 
                    type="warning" 
                    size="mini"
                  >
                    精选
                  </el-tag>
                </div>
                <h3 class="project-title">{{ project.name }}</h3>
                <p class="project-subtitle">{{ project.title }}</p>
              </div>
              
              <div class="project-content">
                <p class="project-description">{{ project.description || project.summary }}</p>
                
                <div v-if="project.technologies" class="project-technologies">
                  <el-tag 
                    v-for="tech in getTechnologies(project.technologies)" 
                    :key="tech" 
                    size="mini" 
                    class="tech-tag"
                  >
                    {{ tech }}
                  </el-tag>
                </div>
              </div>
              
              <div class="project-timeline">
                <div class="timeline-item">
                  <span class="timeline-label">开始时间:</span>
                  <span class="timeline-value">{{ formatDate(project.startDate) || '未设置' }}</span>
                </div>
                <div class="timeline-item">
                  <span class="timeline-label">结束时间:</span>
                  <span class="timeline-value">{{ formatDate(project.endDate) || '进行中' }}</span>
                </div>
                <div class="timeline-item">
                  <span class="timeline-label">项目周期:</span>
                  <span class="timeline-value">{{ calculateProjectDuration(project.startDate, project.endDate) }}</span>
                </div>
              </div>
              
              <div class="project-footer">
                <div class="project-stats">
                  <div class="stat-item">
                    <i class="el-icon-view"></i>
                    <span>{{ project.viewCount || 0 }}</span>
                  </div>
                  <div class="stat-item like-item" :class="{ 'liked': project.isLiked }" @click.stop="handleProjectLike(project)">
                    <i class="el-icon-star-off"></i>
                    <span>{{ project.likeCount || 0 }}</span>
                  </div>
                </div>
                
                <div class="project-actions">
                  <el-button 
                    v-if="project.demoUrl" 
                    type="primary" 
                    size="mini" 
                    @click.stop="handleProjectPreview(project)"
                  >
                    <i class="el-icon-link"></i>
                    预览
                  </el-button>
                  <el-button 
                    v-if="project.githubUrl" 
                    type="default" 
                    size="mini" 
                    @click.stop="handleProjectSource(project)"
                  >
                    <i class="el-icon-document"></i>
                    源码
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
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
      // 项目相关数据
      projects: [],
      displayedProjects: [],
      projectsLoading: true,
      selectedProjectFilter: 'completed',
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
    await this.fetchProjects()
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
        
        // 先获取分类列表
        let categories = []
        try {
          const categoryResponse = await api.category.getAll()
          if (categoryResponse.code === 200) {
            categories = categoryResponse.data || []
            this.categoryCount = categories.length || 0
          }
        } catch (categoryError) {
          console.error('获取分类列表失败:', categoryError)
          this.categoryCount = 5
        }
        
        // 获取最新文章列表
        const articlesResponse = await api.article.getList({ current: 1, size: 6 })
        if (articlesResponse.code === 200) {
          this.articles = (articlesResponse.data.records || []).map(article => {
            // 根据categoryId查找分类名称
            let categoryName = '未分类'
            if (article.categoryId && categories.length > 0) {
              const category = categories.find(cat => cat.id === article.categoryId)
              if (category) {
                categoryName = category.name
              }
            } else if (article.categoryName) {
              categoryName = article.categoryName
            }
            
            // 处理封面图URL
            let coverUrl = ''
            if (article.cover && article.cover.trim() !== '') {
              coverUrl = article.cover.trim()
            }
            
            return {
              ...article,
              cover: coverUrl,
              categoryName: categoryName
            }
          })
          this.articleCount = articlesResponse.data.total || 0
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
    },
    
    handleImageError(event) {
      // 图片加载失败时的处理
      console.log('图片加载失败:', event.target.src)
      // 可以设置默认图片或隐藏图片
      event.target.style.display = 'none'
      // 或者设置默认图片
      // event.target.src = '/default-cover.jpg'
    },

    // 项目相关方法
    async fetchProjects() {
      try {
        this.projectsLoading = true
        const response = await api.project.getList()
        if (response.code === 200) {
          this.projects = response.data || []
          this.filterProjects(this.selectedProjectFilter)
        }
      } catch (error) {
        console.error('获取项目列表失败:', error)
        this.projects = []
        this.displayedProjects = []
      } finally {
        this.projectsLoading = false
      }
    },

    filterProjects(filter) {
      this.selectedProjectFilter = filter
      
      switch (filter) {
        case 'completed':
          this.displayedProjects = this.projects.filter(project => project.status === 'completed')
          break
        case 'developing':
          this.displayedProjects = this.projects.filter(project => project.status === 'developing')
          break
        case 'planning':
          this.displayedProjects = this.projects.filter(project => project.status === 'planning')
          break
        case 'maintenance':
          this.displayedProjects = this.projects.filter(project => project.status === 'maintenance')
          break
        case 'all':
        default:
          this.displayedProjects = [...this.projects]
          break
      }
      
      // 限制显示数量，避免页面过长
      if (this.displayedProjects.length > 8) {
        this.displayedProjects = this.displayedProjects.slice(0, 8)
      }
    },

    getTechnologies(techString) {
      if (!techString) return []
      return techString.split(',').map(tech => tech.trim()).filter(tech => tech)
    },

    viewProjectDetail(project) {
      // 可以跳转到项目详情页面或显示详情弹窗
      if (project.demoUrl) {
        this.openProjectUrl(project.demoUrl)
      } else if (project.githubUrl) {
        this.openProjectUrl(project.githubUrl)
      } else {
        this.$message.info('项目详情功能开发中...')
      }
    },

    openProjectUrl(url) {
      if (url) {
        window.open(url, '_blank')
      }
    },

    getStatusType(status) {
      const statusMap = {
        'planning': 'info',
        'developing': 'warning', 
        'completed': 'success',
        'maintenance': 'primary'
      }
      return statusMap[status] || 'info'
    },

    getStatusText(status) {
      const statusMap = {
        'planning': '规划中',
        'developing': '开发中',
        'completed': '已完成',
        'maintenance': '维护中'
      }
      return statusMap[status] || '未知'
    },

    // 计算项目周期
    calculateProjectDuration(startDate, endDate) {
      if (!startDate) return '未知';
      
      const start = new Date(startDate);
      const end = endDate ? new Date(endDate) : new Date();
      
      const diffTime = Math.abs(end - start);
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      
      if (diffDays < 30) {
        return `${diffDays}天`;
      } else if (diffDays < 365) {
        const months = Math.floor(diffDays / 30);
        const remainingDays = diffDays % 30;
        return remainingDays > 0 ? `${months}个月${remainingDays}天` : `${months}个月`;
      } else {
        const years = Math.floor(diffDays / 365);
        const remainingDays = diffDays % 365;
        const months = Math.floor(remainingDays / 30);
        return months > 0 ? `${years}年${months}个月` : `${years}年`;
      }
    },

    // 增加项目浏览量（静默增加，不显示消息）
    async incrementProjectView(project) {
      try {
        await this.$api.project.incrementView(project.id);
        project.viewCount = (project.viewCount || 0) + 1;
      } catch (error) {
        console.error('增加浏览量失败:', error);
      }
    },

    // 处理项目点赞切换
    async handleProjectLike(project) {
      try {
        const response = await this.$api.project.toggleLike(project.id);
        if (response.data && response.data.success) {
          project.likeCount = response.data.likeCount;
          project.isLiked = response.data.liked;
          
          if (response.data.liked) {
            this.$message.success('点赞成功');
          } else {
            this.$message.success('取消点赞');
          }
        }
      } catch (error) {
        console.error('点赞操作失败:', error);
        this.$message.error('点赞操作失败');
      }
    },

    // 处理项目卡片点击
    async handleProjectCardClick(project) {
      await this.incrementProjectView(project);
      // 如果有预览链接，优先打开预览，否则打开源码
      if (project.demoUrl) {
        this.openProjectUrl(project.demoUrl);
      } else if (project.githubUrl) {
        this.openProjectUrl(project.githubUrl);
      }
    },

    // 处理项目预览按钮点击
    async handleProjectPreview(project) {
      await this.incrementProjectView(project);
      this.openProjectUrl(project.demoUrl);
    },

    // 处理项目源码按钮点击
    async handleProjectSource(project) {
      await this.incrementProjectView(project);
      this.openProjectUrl(project.githubUrl);
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
  z-index: 10000;
  transition: all 0.3s ease;
}

/* 下拉菜单样式 */
.home-navbar-dropdown {
  z-index: 10001 !important;
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
  height: calc(100vh - 70px); /* 减去导航栏高度 */
  margin-top: 0; /* 移除固定导航栏的空间 */
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

/* 统一的分类标签样式 - 放在卡片左上角 */
.article-category-tag {
  position: absolute;
  top: 15px;
  left: 15px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  z-index: 10;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.article-category-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

/* 没有封面图时的标题背景样式 */
.article-title-cover {
  position: relative;
  height: 200px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.article-title-cover::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  z-index: 1;
}

.title-overlay-link {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  text-decoration: none;
  color: inherit;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.title-overlay-link:hover {
  background: rgba(0, 0, 0, 0.1);
}

.title-overlay {
  position: relative;
  z-index: 2;
  text-align: center;
  color: white;
  padding: 20px;
}

.cover-title {
  font-size: 1.4rem;
  font-weight: 600;
  line-height: 1.4;
  margin: 0;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.5);
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: all 0.3s ease;
}

.title-overlay-link:hover .cover-title {
  transform: scale(1.05);
}

/* 没有封面图的文章卡片调整 */
.article-card.no-cover .article-content {
  padding-top: 20px;
}

.article-card.no-cover .article-summary {
  margin-top: 15px;
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

/* 项目区域样式 */
.projects-section {
  margin-bottom: 80px;
}

.project-filters {
  display: flex;
  gap: 10px;
}

.projects-waterfall {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 25px;
  margin-top: 30px;
}

.project-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid rgba(0, 0, 0, 0.05);
  position: relative;
  overflow: hidden;
}

.project-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.project-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.12);
}

.project-card:hover::before {
  transform: scaleX(1);
}

.project-header {
  margin-bottom: 16px;
}

.project-status {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.project-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 8px 0;
  line-height: 1.3;
}

.project-subtitle {
  color: #667eea;
  font-size: 0.95rem;
  margin: 0;
  font-weight: 500;
}

.project-content {
  margin-bottom: 20px;
}

.project-description {
  color: #5a6c7d;
  line-height: 1.6;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.project-technologies {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.tech-tag {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
  border: none;
  font-size: 11px;
  padding: 4px 8px;
  border-radius: 12px;
}

.project-timeline {
  margin-bottom: 20px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
  border-left: 4px solid #667eea;
}

.timeline-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 13px;
}

.timeline-item:last-child {
  margin-bottom: 0;
}

.timeline-label {
  color: #6c757d;
  font-weight: 500;
}

.timeline-value {
  color: #2c3e50;
  font-weight: 600;
}

.project-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.project-stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #6c757d;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 4px 8px;
  border-radius: 8px;
}

.stat-item:hover {
  background: #f8f9fa;
  color: #495057;
}

.like-item {
  color: #dc3545;
}

.like-item.liked {
  color: #e74c3c;
  background: rgba(231, 76, 60, 0.1);
}

.like-item.liked i {
  color: #e74c3c;
}

.project-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #8492a6;
}

.project-actions {
  display: flex;
  gap: 8px;
}

.project-actions .el-button {
  padding: 6px 12px;
  font-size: 12px;
}

.project-actions .el-button--primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.project-actions .el-button--default {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  color: #6c757d;
}

.project-actions .el-button--default:hover {
  background: #e9ecef;
  border-color: #dee2e6;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .projects-waterfall {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .projects-waterfall {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .project-card {
    padding: 20px;
  }
  
  .project-footer {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .project-actions {
    width: 100%;
    justify-content: flex-end;
  }
  
  .project-filters {
    flex-wrap: wrap;
    gap: 8px;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-section {
    height: 100vh;
    margin-top: 0;
  }
  
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
  

}

@media (max-width: 480px) {
  .hero-section {
    height: 100vh;
    margin-top: 0;
  }
  
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