<template>
  <div class="article-page">
    <div class="container">
      <div class="filter-container">
        <div class="article-header-container">
          <h1>文章列表</h1>
          <div class="article-tabs">
            <el-radio-group v-model="viewMode" @change="handleViewModeChange" size="medium">
              <el-radio-button label="all">
                <i class="el-icon-document"></i> 全部文章
              </el-radio-button>
              <el-radio-button label="self">
                <i class="el-icon-user"></i> 我的文章
              </el-radio-button>
            </el-radio-group>
          </div>
        </div>
        
        <!-- 分类选择器 -->
        <div class="filter-options">
          <div class="category-filter">
            <el-select 
              v-model="selectedCategoryId" 
              placeholder="选择分类" 
              clearable 
              @change="handleCategoryChange"
              size="medium"
              popper-class="category-dropdown">
              <el-option label="全部分类" value=""></el-option>
              <el-option
                v-for="category in categories"
                :key="category.id"
                :label="category.name"
                :value="category.id">
                <span class="category-icon">
                  <i :class="getCategoryIcon(category.name)"></i>
                </span>
                <span class="category-name">{{ category.name }}</span>
              </el-option>
            </el-select>
          </div>
          
          <div class="article-count-container">
            <div class="article-count-box">
              <i class="el-icon-collection"></i>
              <span>共 {{ total }} 篇文章</span>
            </div>
          </div>
        </div>
      </div>
      <el-skeleton :loading="loading" animated :count="3" v-if="loading">
        <template slot="template">
          <div class="article-item">
            <div class="article-header">
              <el-skeleton-item variant="h3" style="width: 50%"></el-skeleton-item>
              <div class="article-meta">
                <el-skeleton-item variant="text" style="width: 30%"></el-skeleton-item>
              </div>
            </div>
            <div class="article-summary">
              <el-skeleton-item variant="p" style="width: 100%"></el-skeleton-item>
              <el-skeleton-item variant="p" style="width: 90%"></el-skeleton-item>
            </div>
            <div class="article-footer">
              <el-skeleton-item variant="button" style="width: 100px"></el-skeleton-item>
            </div>
          </div>
        </template>
      </el-skeleton>
      
      <div class="article-list" v-else>
        <div class="articles-masonry-grid">
          <div class="article-item" v-for="article in articles" :key="article.id">
              <div class="article-header">
                <h2 class="article-title">
                  <router-link :to="`/article/${article.id}`">{{ article.title }}</router-link>
                </h2>
                <div class="article-meta">
                  <span><i class="el-icon-user"></i> {{ article.author }}</span>
                  <span><i class="el-icon-date"></i> {{ article.date }}</span>
                  <span><i class="el-icon-view"></i> {{ article.views }}</span>
                  <span><i class="el-icon-collection-tag"></i> {{ article.categoryName }}</span>
                  <span v-if="viewMode === 'self'" :class="getStatusClass(article.status)">
                    <i class="el-icon-info"></i> {{ getStatusText(article.status) }}
                  </span>
                </div>
              </div>
              
              <div class="article-cover" v-if="article.cover">
                <img :src="article.cover" :alt="article.title">
              </div>
              
              <div class="article-summary">
                {{ article.summary }}
              </div>
              
              <div class="article-footer">
                <router-link :to="`/article/${article.id}`">
                  <el-button type="primary" size="small">阅读全文</el-button>
                </router-link>
                <div class="article-tags" v-if="article.tags && article.tags.length">
                  <el-tag size="small" v-for="(tag, i) in article.tags" :key="i">{{ tag }}</el-tag>
                </div>
              </div>
            </div>
          </div>
        
        <el-empty v-if="articles.length === 0" description="暂无文章"></el-empty>
      </div>
      
      <div class="pagination">
        <el-pagination
          background
          layout="prev, pager, next"
          :current-page.sync="currentPage"
          :page-size="pageSize"
          :total="total"
          @current-change="handlePageChange">
        </el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
import api from '../api'

export default {
  name: 'Article',
  data() {
    return {
      articles: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      loading: false,
      viewMode: 'all', // 默认显示全部文章
      categories: [], // 分类列表
      selectedCategoryId: '', // 选中的分类ID
      siteSettings: {} // 系统设置
    }
  },
  created() {
    this.loadSiteSettings() // 获取系统设置
    this.fetchCategories() // 获取分类列表
  },
  mounted() {
    this.$nextTick(() => {
      this.optimizeArticleLayout()
    })
    // 监听窗口大小变化
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
  },
  updated() {
    this.$nextTick(() => {
      this.optimizeArticleLayout()
    })
  },
  methods: {
    // 获取系统设置
    async loadSiteSettings() {
      try {
        const response = await api.system.getPublicSettings()
        if (response.code === 200) {
          this.siteSettings = response.data
          console.log('获取到的系统设置:', this.siteSettings)
          
          // 设置每页文章数
          const articlesPerPage = parseInt(this.siteSettings.articles_per_page)
          this.pageSize = !isNaN(articlesPerPage) ? articlesPerPage : 10
          console.log('系统设置加载成功，每页文章数:', this.pageSize)
          console.log('每页文章数设置原始值:', this.siteSettings.articles_per_page)
          
          this.fetchArticles() // 加载文章列表
        }
      } catch (error) {
        console.error('获取系统设置失败:', error)
        this.fetchArticles() // 即使获取设置失败，也加载文章列表
      }
    },
    
    // 获取分类列表
    async fetchCategories() {
      try {
        const response = await api.category.getAll()
        if (response.code === 200) {
          this.categories = response.data
          console.log('获取分类列表成功:', this.categories)
        } else {
          console.error('获取分类列表失败:', response.message)
          this.$message.error('获取分类列表失败: ' + response.message)
        }
      } catch (error) {
        console.error('获取分类列表失败:', error)
        this.$message.error('获取分类列表失败: ' + (error.message || '未知错误'))
      }
    },
    
    async fetchArticles() {
      try {
        this.loading = true
        console.log('开始获取文章列表...')
        
        let axiosResponse;
        
        // 构建请求参数
        const params = {
          current: this.currentPage,
          size: this.pageSize
        }
        
        // 如果选择了分类，添加分类ID参数
        if (this.selectedCategoryId) {
          params.categoryId = this.selectedCategoryId
        }
        
        // 根据视图模式选择不同的API
        if (this.viewMode === 'self') {
          // 获取自己的文章
          console.log('获取自己的文章...')
          axiosResponse = await api.article.getMyList(params)
        } else {
          // 获取所有文章
          console.log('获取所有文章...')
          axiosResponse = await api.article.getList(params)
        }
        
        console.log('原始响应:', axiosResponse)
        
        // 检查响应是否有效
        if (!axiosResponse) {
          console.error('响应为空')
          this.$message.error('获取文章列表失败: 响应为空')
          this.articles = []
          return
        }
        
        const response = axiosResponse
        console.log('文章列表响应:', response)
        
        if (response.code === 200) {
          console.log('响应码正确')
          
          // 检查数据结构
          console.log('响应数据:', response.data)
          
          // 尝试处理不同的数据结构
          let records = []
          let total = 0
          
          if (response.data && Array.isArray(response.data)) {
            // 如果data直接是数组
            records = response.data
            total = records.length
            console.log('数据是数组格式')
          } else if (response.data && Array.isArray(response.data.records)) {
            // 如果data包含records数组
            records = response.data.records
            total = response.data.total || records.length
            console.log('数据是分页格式')
          } else if (response.data && typeof response.data === 'object') {
            // 尝试从对象中提取数据
            console.log('数据是对象格式，尝试提取')
            records = [response.data]
            total = 1
          } else {
            console.error('无法识别的数据格式:', response.data)
            this.$message.warning('数据格式不正确')
            this.articles = []
            this.total = 0
            return
          }
          
          console.log('提取的记录:', records)
          
          // 处理记录
          this.articles = records.map(article => {
            console.log('处理文章数据:', article)
            
            // 创建一个临时对象来存储处理后的文章数据
            const processedArticle = {
              id: article.id || 0,
              title: article.title || '无标题',
              date: this.formatDate(article.createTime || article.create_time || new Date()),
              views: article.viewCount || article.view_count || 0,
              comments: article.commentCount || article.comment_count || 0,
              summary: article.summary || '暂无摘要',
              author: '未知',
              tags: [],
              categoryId: article.categoryId || 0,
              categoryName: '未分类',
              status: article.status || 1, // 默认为已发布状态
              cover: article.cover || '' // 添加封面图片
            }
            
            // 尝试获取分类名称
            if (article.categoryId) {
              const category = this.categories.find(c => c.id === article.categoryId)
              if (category) {
                processedArticle.categoryName = category.name
              }
            }
            
            // 尝试获取作者信息
            if (article.author && article.author.nickname) {
              processedArticle.author = article.author.nickname
            } else if (article.user && article.user.nickname) {
              processedArticle.author = article.user.nickname
            } else if (article.nickname) {
              processedArticle.author = article.nickname
            } else if (article.userName) {
              processedArticle.author = article.userName
            } else if (article.authorName) {
              processedArticle.author = article.authorName
            }
            
            console.log('处理后的文章:', processedArticle)
            return processedArticle
          })
          
          // 如果是"我的文章"视图，按审核状态排序（未审核的排在前面）
          if (this.viewMode === 'self') {
            this.articles.sort((a, b) => {
              // 优先级：待审核(2) > 审核不通过(3) > 草稿(0) > 已发布(1)
              const priorityMap = { 2: 0, 3: 1, 0: 2, 1: 3 };
              const priorityA = priorityMap[a.status] !== undefined ? priorityMap[a.status] : 999;
              const priorityB = priorityMap[b.status] !== undefined ? priorityMap[b.status] : 999;
              return priorityA - priorityB;
            });
          }
          
          this.total = total
          console.log('最终文章列表:', this.articles)
          
          // 如果没有文章，显示提示
          if (this.articles.length === 0) {
            this.$message.info('暂无文章')
          }
        } else {
          console.error('响应码错误:', response.code)
          this.$message.error(response.message || '获取文章列表失败')
        }
      } catch (error) {
        console.error('获取文章列表失败:', error)
        this.$message.error('获取文章列表失败: ' + (error.message || '未知错误'))
      } finally {
        this.loading = false
        // 数据加载完成后，延迟执行布局优化
        this.$nextTick(() => {
          setTimeout(() => {
            this.optimizeArticleLayout()
            // 监听图片加载完成后重新布局
            this.watchImageLoad()
          }, 100)
        })
      }
    },
    
    handlePageChange(page) {
      this.currentPage = page
      this.fetchArticles()
    },
    
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    },
    
    handleViewModeChange() {
      // 切换视图模式时重置页码并重新获取文章
      this.currentPage = 1
      this.fetchArticles()
    },
    
    handleCategoryChange() {
      // 切换分类时重置页码并重新获取文章
      this.currentPage = 1
      this.fetchArticles()
    },
    
    // 获取文章状态文本
    getStatusText(status) {
      switch (status) {
        case 0: return '草稿';
        case 1: return '已发布';
        case 2: return '待审核';
        case 3: return '审核不通过';
        default: return '未知状态';
      }
    },
    
    // 获取文章状态样式类
    getStatusClass(status) {
      switch (status) {
        case 0: return 'status-draft';
        case 1: return 'status-published';
        case 2: return 'status-pending';
        case 3: return 'status-rejected';
        default: return '';
      }
    },
    
    // 根据分类名称获取对应的图标
    getCategoryIcon(categoryName) {
      const iconMap = {
        '技术': 'el-icon-s-opportunity',
        '生活': 'el-icon-coffee-cup',
        '资源': 'el-icon-folder-opened',
        '测试': 'el-icon-s-flag',
        '测试1': 'el-icon-s-flag'
      };
      
      return iconMap[categoryName] || 'el-icon-collection';
    },
    
    // 优化文章布局 - 真正的瀑布流实现
    optimizeArticleLayout() {
      const container = document.querySelector('.articles-masonry-grid')
      const items = document.querySelectorAll('.article-item')
      
      if (!container || !items.length) return
      
      // 只在有内容时才进行布局优化，避免影响骨架屏显示
      const hasRealContent = items.length > 0 && !items[0].classList.contains('el-skeleton')
      
      if (hasRealContent) {
        // 临时隐藏容器，避免布局计算时的闪烁
        container.style.opacity = '0'
        container.style.transition = 'none'
      }
      
      // 重置所有卡片的样式，确保能正确计算高度
      items.forEach(item => {
        item.style.position = 'static'
        item.style.width = 'auto'
        item.style.left = 'auto'
        item.style.top = 'auto'
        if (hasRealContent) {
          item.style.transition = 'none' // 暂时禁用过渡效果
        }
      })
      
      // 强制重新渲染
      container.offsetHeight
      
      // 动态计算列数和卡片宽度
      const containerWidth = container.offsetWidth
      const containerPadding = 20 // 只有左边距20px，右边距为0
      const availableWidth = containerWidth - containerPadding
      let columns = 1
      let itemWidth = availableWidth
      const gap = 20
      
      if (availableWidth >= 1200) {
        columns = 3
        // 重新计算，确保右侧紧贴
        itemWidth = Math.floor((availableWidth - gap * (columns - 1)) / columns)
      } else if (availableWidth >= 700) {
        columns = 2
        // 重新计算，确保右侧紧贴
        itemWidth = Math.floor((availableWidth - gap * (columns - 1)) / columns)
      }
      
      // 如果是多列布局，重新精确计算宽度以消除右侧空隙
      if (columns > 1) {
        const totalGapWidth = gap * (columns - 1)
        itemWidth = Math.floor((availableWidth - totalGapWidth) / columns)
      }
      
      // 如果只有一列，使用简单布局
      if (columns === 1) {
        items.forEach((item, index) => {
          item.style.width = '100%'
          item.style.marginBottom = `${gap}px`
          if (hasRealContent) {
            item.style.transition = 'none'
          }
          
          // 最后一个元素时显示容器
          if (index === items.length - 1) {
            setTimeout(() => {
              if (hasRealContent) {
                items.forEach(item => {
                  item.style.transition = 'all 0.3s ease'
                })
                container.style.transition = 'opacity 0.3s ease'
                container.style.opacity = '1'
              }
            }, 50)
          }
        })
        return
      }
      
      // 初始化每列的高度数组
      const columnHeights = new Array(columns).fill(0)
      
      // 为每个卡片设置宽度并计算位置
      items.forEach((item, index) => {
        // 设置卡片宽度
        item.style.width = `${itemWidth}px`
        item.style.position = 'absolute'
        item.style.boxSizing = 'border-box'
        
        // 等待DOM更新后再计算高度
        setTimeout(() => {
          const itemHeight = item.offsetHeight
          
          // 找到最短的列
          const shortestColumnIndex = columnHeights.indexOf(Math.min(...columnHeights))
          
          // 计算位置
          const left = shortestColumnIndex * (itemWidth + gap)
          const top = columnHeights[shortestColumnIndex]
          
          // 设置位置
          item.style.left = `${left}px`
          item.style.top = `${top}px`
          item.style.zIndex = '1'
          
          // 更新该列的高度
          columnHeights[shortestColumnIndex] = top + itemHeight + gap
          
          // 如果是最后一个元素，更新容器高度并显示
          if (index === items.length - 1) {
            setTimeout(() => {
              const maxHeight = Math.max(...columnHeights)
              container.style.height = `${maxHeight}px`
              
              // 布局完成后，恢复过渡效果和显示容器
              if (hasRealContent) {
                items.forEach(item => {
                  item.style.transition = 'all 0.3s ease'
                })
                container.style.transition = 'opacity 0.3s ease'
                container.style.opacity = '1'
              }
            }, 50)
          }
        }, index * 10) // 错开执行时间避免冲突
      })
      
      console.log('瀑布流布局开始:', {
        containerWidth,
        availableWidth,
        columns,
        itemWidth,
        gap
      })
    },
    
    // 处理窗口大小变化
    handleResize() {
      clearTimeout(this.resizeTimer)
      this.resizeTimer = setTimeout(() => {
        this.optimizeArticleLayout()
      }, 200)
    },
    
    // 监听图片加载完成
    watchImageLoad() {
      const images = document.querySelectorAll('.article-item img')
      let loadedCount = 0
      const totalImages = images.length
      
      if (totalImages === 0) {
        // 如果没有图片，直接重新布局
        setTimeout(() => this.optimizeArticleLayout(), 100)
        return
      }
      
      images.forEach(img => {
        if (img.complete) {
          loadedCount++
        } else {
          img.onload = () => {
            loadedCount++
            if (loadedCount === totalImages) {
              this.optimizeArticleLayout()
            }
          }
        }
      })
      
      // 如果所有图片都已加载完成
      if (loadedCount === totalImages) {
        setTimeout(() => this.optimizeArticleLayout(), 100)
      }
    }
  }
}
</script>

<style scoped>
.article-page {
  padding: 100px 0;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
}

.filter-container {
  background: white;
  border-radius: 15px;
  padding: 25px;
  margin-bottom: 30px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.05);
}

.article-header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 20px;
}

h1 {
  color: #2c3e50;
  margin-bottom: 0;
  font-size: 2.2rem;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  display: flex;
  align-items: center;
}

h1:before {
  content: '';
  display: inline-block;
  width: 5px;
  height: 25px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  margin-right: 15px;
  border-radius: 3px;
}

.article-tabs {
  margin-bottom: 0;
}

.article-tabs .el-radio-button__inner {
  padding: 10px 20px;
  transition: all 0.3s ease;
}

.filter-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.category-filter {
  width: 200px;
}

.category-icon {
  margin-right: 8px;
  color: #667eea;
}

.category-name {
  font-weight: 500;
}

.article-count-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.article-count-box {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 12px 20px;
  border-radius: 25px;
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
  min-width: 150px;
  justify-content: center;
}

.article-count-box:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.article-count-box i {
  font-size: 16px;
}

.article-list {
  margin-bottom: 40px;
}

.articles-masonry-grid {
  position: relative;
  width: 100%;
  padding: 20px 0 20px 20px;
  box-sizing: border-box;
}

.article-item {
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  padding: 30px;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  position: absolute;
  overflow: hidden;
  break-inside: avoid;
}

.article-item:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.article-item:before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 5px;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.article-header {
  margin-bottom: 20px;
}

.article-title {
  margin: 0 0 15px 0;
  font-size: 22px;
  /* 标题文字溢出处理 */
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
  max-height: 62px;
}

.article-title a {
  color: #2c3e50;
  text-decoration: none;
  transition: color 0.3s ease;
}

.article-title a:hover {
  color: #667eea;
}

.article-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  color: #7f8c8d;
  font-size: 13px;
}

.article-meta span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.article-cover {
  margin-bottom: 20px;
  border-radius: 10px;
  overflow: hidden;
  height: 180px;
  position: relative;
}

.article-cover:after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.1);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.article-cover:hover:after {
  opacity: 1;
}

.article-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.article-cover img:hover {
  transform: scale(1.05);
}

.article-summary {
  color: #666;
  line-height: 1.8;
  margin-bottom: 25px;
  /* 摘要文字溢出处理 */
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  flex-grow: 1;
}

.article-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.article-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.el-tag {
  background-color: rgba(102, 126, 234, 0.1);
  color: #667eea;
  border-color: rgba(102, 126, 234, 0.2);
  transition: all 0.3s ease;
}

.el-tag:hover {
  background-color: rgba(102, 126, 234, 0.2);
  transform: translateY(-2px);
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

/* 文章状态样式 */
.status-draft {
  background-color: #e6e6e6;
  color: #666;
  padding: 4px 10px;
  border-radius: 15px;
  font-weight: bold;
  font-size: 12px;
}

.status-published {
  background-color: #67C23A;
  color: white;
  padding: 4px 10px;
  border-radius: 15px;
  font-weight: bold;
  font-size: 12px;
}

.status-pending {
  background-color: #E6A23C;
  color: white;
  padding: 4px 10px;
  border-radius: 15px;
  font-weight: bold;
  font-size: 12px;
}

.status-rejected {
  background-color: #F56C6C;
  color: white;
  padding: 4px 10px;
  border-radius: 15px;
  font-weight: bold;
  font-size: 12px;
}

/* 瀑布流布局优化 */
@supports (grid-template-rows: masonry) {
  .articles-masonry-grid {
    grid-template-rows: masonry;
  }
}

/* 响应式设计 */
@media (max-width: 1400px) {
  .articles-masonry-grid {
    grid-template-columns: repeat(2, 1fr) !important;
  }
}

@media (max-width: 900px) {
  .articles-masonry-grid {
    grid-template-columns: 1fr !important;
    gap: 20px;
  }
  
  .article-item {
    padding: 20px;
  }
}

@media (max-width: 768px) {
  .article-header-container {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .filter-options {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .category-filter {
    width: 100%;
  }
  
  .article-footer {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .article-meta {
    flex-wrap: wrap;
  }
  
  .article-cover {
    height: 150px;
  }
  
  .articles-masonry-grid {
    gap: 15px;
  }
  
  .article-item {
    padding: 15px;
  }
}

@media (max-width: 480px) {
  .article-page {
    padding: 80px 0 20px;
  }
  
  .filter-container {
    margin: 0 15px 20px;
    padding: 20px;
  }
  
  .article-list {
    margin: 0 15px;
  }
  
  h1 {
    font-size: 1.8rem;
  }
  
  .article-title {
    font-size: 18px;
  }
  
  .article-item {
    padding: 15px;
  }
}
</style>