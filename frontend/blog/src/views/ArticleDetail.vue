<template>
  <div class="article-detail-container">
    <el-card class="article-card">
      <div slot="header" class="header">
        <h1>{{ article.title }}</h1>
        <div class="article-meta">
          <span class="author">作者: {{ article.authorName || '未知' }}</span>
          <span class="time">发布时间: {{ formatDate(article.createTime) }}</span>
          <span class="views">阅读: {{ article.viewCount }}</span>
          <span class="likes">点赞: {{ article.likeCount }}</span>
          <span class="status" :class="getStatusClass(article.status)">
            {{ getStatusText(article.status) }}
          </span>
        </div>
        
        <!-- 管理员审核操作区 -->
        <div class="admin-actions" v-if="isAdmin && article.status === 2">
          <el-button type="success" size="small" @click="approveArticle">审核通过</el-button>
          <el-button type="danger" size="small" @click="rejectArticle">审核拒绝</el-button>
        </div>
        
        <!-- 作者编辑操作区 -->
        <div class="author-actions" v-if="isAuthor && (article.status === 2 || article.status === 3)">
          <el-button type="primary" size="small" @click="editArticle">编辑文章</el-button>
        </div>
      </div>
      
      <div class="article-content" v-html="article.content"></div>
      
      <div class="article-footer">
        <el-button 
          :type="isLiked ? 'success' : 'primary'" 
          :icon="isLiked ? 'el-icon-star-on' : 'el-icon-thumb'" 
          @click="toggleLike"
          :loading="liking">
          {{ isLiked ? '已点赞' : '点赞' }} {{ article.likeCount }}
        </el-button>
        
        <div class="tags" v-if="article.tags && article.tags.length">
          <el-tag 
            v-for="tag in article.tags" 
            :key="tag.id"
            :type="tag.color || 'info'"
            size="small">
            {{ tag.name }}
          </el-tag>
        </div>
      </div>
    </el-card>

    <!-- 评论区 -->
    <el-card class="comments-card" v-if="article.id">
      <div slot="header" class="comments-header">
        <div class="comments-title">
          <i class="el-icon-chat-dot-round"></i>
          <span>评论 ({{ commentCount }})</span>
        </div>
        <div class="comments-info" v-if="commentCount > 0">
          <el-tooltip content="评论规则" placement="top">
            <i class="el-icon-info"></i>
          </el-tooltip>
          <span>文明评论，理性发言</span>
        </div>
      </div>
      
      <comment-section 
        :article-id="article.id"
        @comment-count-updated="updateCommentCount"
      ></comment-section>
    </el-card>
  </div>
</template>

<script>
import CommentSection from '../components/CommentSection.vue'
import api from '../api'

export default {
  name: 'ArticleDetail',
  components: {
    CommentSection
  },
  data() {
    return {
      article: {
        id: null,
        title: '',
        content: '',
        authorName: '',
        createTime: '',
        viewCount: 0,
        likeCount: 0,
        commentCount: 0,
        tags: [],
        status: 1, // 默认已发布状态
        userId: null // 作者ID
      },
      commentCount: 0, // 实时评论数
      liking: false,
      isAdmin: false, // 是否为管理员
      currentUserId: null, // 当前登录用户ID
      isLiked: false // 是否已点赞
    }
  },
  computed: {
    // 判断当前用户是否为文章作者
    isAuthor() {
      return this.currentUserId && this.article.userId && this.currentUserId === this.article.userId;
    }
  },
  created() {
    this.getUserInfo()
    this.getArticleDetail()
  },
  
  watch: {
    // 监听文章ID变化，重新检查点赞状态
    'article.id': function(newVal) {
      if (newVal) {
        this.checkLikeStatus()
      }
    }
  },
  methods: {
    // 获取当前用户信息
    async getUserInfo() {
      try {
        // 检查是否已登录
        if (!this.$store.getters.isAuthenticated) {
          console.log('用户未登录，无法获取用户信息')
          return
        }
        
        const response = await api.user.getInfo()
        if (response.code === 200) {
          const userInfo = response.data
          this.currentUserId = userInfo.id
          // 检查用户是否为管理员
          this.isAdmin = userInfo.role === 0
          console.log('获取用户信息成功:', userInfo)
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
        // 从store中获取基本用户信息
        const storeUser = this.$store.getters.user
        if (storeUser) {
          this.currentUserId = storeUser.id
          this.isAdmin = storeUser.role === 0
          console.log('从store获取用户信息:', storeUser)
        }
      }
    },
    
    async getArticleDetail() {
      try {
        const articleId = this.$route.params.id
        const response = await api.article.getDetail(articleId)
        if (response.code === 200) {
          this.article = response.data
          // 初始化评论计数
          this.commentCount = this.article.commentCount || 0
          
          // 检查点赞状态
          this.checkLikeStatus()
          
          // 增加文章浏览量
          this.incrementViewCount()
        } else {
          // 处理业务错误
          this.$message.error(response.message || '获取文章详情失败')
          if (response.message === '您无权查看该文章') {
            setTimeout(() => {
              this.$router.push('/article')
            }, 1500)
          }
        }
      } catch (error) {
        console.error('获取文章详情失败:', error)
        let errorMsg = '获取文章详情失败'
        
        if (error.response) {
          switch (error.response.status) {
            case 403:
              errorMsg = '您无权查看该文章'
              break
            case 404:
              errorMsg = '请求的资源不存在'
              break
            default:
              errorMsg = `获取文章详情失败: ${error.message || '未知错误'}`
          }
        } else if (error.message) {
          errorMsg = `获取文章详情失败: ${error.message}`
        }
        
        this.$message.error(errorMsg)
        setTimeout(() => {
          this.$router.push('/article')
        }, 1500)
      }
    },
    
    // 增加文章浏览量
    async incrementViewCount() {
      try {
        // 这里可以添加增加浏览量的API调用
        // 暂时不实现，因为后端可能已经在获取文章详情时自动增加了浏览量
      } catch (error) {
        console.error('增加浏览量失败:', error)
      }
    },
    
    // 检查当前用户是否已点赞该文章
    async checkLikeStatus() {
      if (!this.article.id) {
        return
      }
      
      // 检查是否有token
      const token = localStorage.getItem('token')
      if (!token) {
        console.log('用户未登录，无法检查点赞状态')
        this.isLiked = false
        return
      }
      
      try {
        const response = await api.article.checkLikeStatus(this.article.id)
        if (response.code === 200) {
          this.isLiked = response.data
          console.log('点赞状态:', this.isLiked ? '已点赞' : '未点赞')
        }
      } catch (error) {
        console.error('检查点赞状态失败:', error)
        // 错误不影响用户体验，只记录日志
        this.isLiked = false
      }
    },
    
    // 点赞或取消点赞
    async toggleLike() {
      // 检查是否有token
      const token = localStorage.getItem('token')
      if (!token) {
        this.$message.warning('请先登录')
        // 保存当前页面URL，登录后可以返回
        localStorage.setItem('redirectUrl', this.$route.fullPath)
        // 跳转到登录页
        this.$router.push('/login')
        return
      }
      
      try {
        this.liking = true
        
        // 确保token已设置到store中
        if (!this.$store.getters.token) {
          this.$store.commit('SET_TOKEN', token)
        }
        
        // 尝试获取用户信息（如果还没有）
        if (!this.currentUserId) {
          // 尝试从store获取用户信息
          const storeUser = this.$store.getters.user
          if (storeUser && storeUser.id) {
            this.currentUserId = storeUser.id
            this.isAdmin = storeUser.role === 0
          } else {
            // 尝试从API获取用户信息
            try {
              await this.$store.dispatch('getUserInfo')
              const updatedUser = this.$store.getters.user
              if (updatedUser) {
                this.currentUserId = updatedUser.id
                this.isAdmin = updatedUser.role === 0
              } else {
                throw new Error('无法获取用户信息')
              }
            } catch (userError) {
              console.error('获取用户信息失败:', userError)
              this.$message.error('登录状态异常，请重新登录')
              this.$store.dispatch('logout')
              localStorage.setItem('redirectUrl', this.$route.fullPath)
              this.$router.push('/login')
              return
            }
          }
        }
        
        // 执行点赞操作
        const response = await api.article.like(this.article.id)
        
        if (response.code === 200) {
          // 切换点赞状态
          this.isLiked = !this.isLiked
          
          // 更新点赞数
          if (this.isLiked) {
            this.article.likeCount++
            this.$message.success('点赞成功')
          } else {
            if (this.article.likeCount > 0) {
              this.article.likeCount--
            }
            this.$message.success('已取消点赞')
          }
        }
      } catch (error) {
        console.error('点赞操作失败:', error)
        if (error.response) {
          if (error.response.status === 401 || error.response.status === 403) {
            this.$message.error('登录已过期，请重新登录')
            // 清除token并重定向到登录页
            this.$store.dispatch('logout')
            localStorage.setItem('redirectUrl', this.$route.fullPath)
            this.$router.push('/login')
          } else {
            this.$message.error('操作失败: ' + (error.message || '未知错误'))
          }
        } else {
          this.$message.error('操作失败: ' + (error.message || '网络错误'))
        }
      } finally {
        this.liking = false
      }
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
    
    // 审核通过文章
    async approveArticle() {
      try {
        const response = await api.article.approve(this.article.id)
        if (response.code === 200) {
          this.$message.success('审核通过成功')
          this.article.status = 1 // 更新状态为已发布
        }
      } catch (error) {
        this.$message.error('审核操作失败: ' + (error.message || '未知错误'))
      }
    },
    
    // 审核拒绝文章
    async rejectArticle() {
      try {
        const response = await api.article.reject(this.article.id)
        if (response.code === 200) {
          this.$message.success('审核拒绝成功')
          this.article.status = 3 // 更新状态为审核不通过
        }
      } catch (error) {
        this.$message.error('审核操作失败: ' + (error.message || '未知错误'))
      }
    },
    
    // 编辑文章
    editArticle() {
      this.$router.push(`/write?id=${this.article.id}`)
    },
    
    // 更新评论计数
    updateCommentCount(count) {
      this.commentCount = count
      // 同时更新文章对象中的评论数
      this.article.commentCount = count
    },
    
    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
    }
  }
}
</script>

<style scoped>
.article-detail-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.article-card {
  margin-bottom: 20px;
}

.header h1 {
  margin: 0 0 10px 0;
  color: #333;
}

.article-meta {
  display: flex;
  gap: 20px;
  color: #666;
  font-size: 14px;
  flex-wrap: wrap;
}

.status {
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: bold;
}

.status-draft {
  background-color: #e6e6e6;
  color: #666;
}

.status-published {
  background-color: #67C23A;
  color: white;
}

.status-pending {
  background-color: #E6A23C;
  color: white;
}

.status-rejected {
  background-color: #F56C6C;
  color: white;
}

.admin-actions, .author-actions {
  margin-top: 10px;
  display: flex;
  gap: 10px;
}

.article-content {
  line-height: 1.8;
  font-size: 16px;
}

.article-content >>> img {
  max-width: 100%;
  height: auto;
}

.article-footer {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tags {
  display: flex;
  gap: 8px;
}

.comments-card {
  margin-top: 30px;
  border-radius: 12px;
  overflow: hidden;
}

.comments-card >>> .el-card__header {
  background: linear-gradient(135deg, #f6f9fc 0%, #eef1f5 100%);
  padding: 15px 20px;
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.comments-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.comments-title i {
  color: #409EFF;
  font-size: 18px;
}

.comments-info {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #909399;
  font-size: 13px;
}

.comments-info i {
  color: #E6A23C;
  cursor: pointer;
}

.comments-card >>> .el-card__body {
  padding: 0;
}
</style>