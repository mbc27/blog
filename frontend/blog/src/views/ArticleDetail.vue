<template>
  <div class="article-detail-page">
    <el-card class="article-card">
      <div class="article-header">
        <h1 class="article-title">{{ article.title }}</h1>
        <div class="article-meta">
          <span class="author">作者: {{ article.author || 'user' }}</span>
          <span class="date">发布时间: {{ article.createTime }}</span>
          <span class="views">阅读: {{ article.viewCount }}</span>
          <span class="comments">评论: {{ article.commentCount }}</span>
        </div>
      </div>
      
      <div class="article-content markdown-body" v-html="renderedContent"></div>
      
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
import MarkdownIt from 'markdown-it'
import CommentSection from '../components/CommentSection.vue'

const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true
})

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
        author: '',
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
    },
    // 渲染Markdown内容
    renderedContent() {
      if (!this.article.content) return '';
      return md.render(this.article.content);
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
          console.log('用户未登录，跳过获取用户信息')
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
        // 如果获取用户信息失败，清除本地状态，但不影响页面正常显示
        this.currentUserId = null
        this.isAdmin = false
      }
    },

    // 获取文章详情
    async getArticleDetail() {
      try {
        const articleId = this.$route.params.id
        const response = await api.article.getDetail(articleId)
        
        if (response.code === 200) {
          this.article = response.data
          console.log('获取文章详情成功:', this.article)
          
          // 更新评论数
          this.commentCount = this.article.commentCount || 0
          
          // 检查点赞状态
          if (this.currentUserId) {
            this.checkLikeStatus()
          }
        } else {
          this.$message({
            message: '获取文章详情失败',
            type: 'error',
            duration: 3000
          })
        }
      } catch (error) {
        console.error('获取文章详情失败:', error)
        this.$message({
          message: '获取文章详情失败',
          type: 'error',
          duration: 3000
        })
      }
    },

    // 检查点赞状态
    async checkLikeStatus() {
      if (!this.currentUserId || !this.article.id) {
        return
      }
      
      try {
        const response = await api.article.checkLike(this.article.id)
        if (response.code === 200) {
          this.isLiked = response.data
        }
      } catch (error) {
        console.error('检查点赞状态失败:', error)
      }
    },

    // 切换点赞状态
    async toggleLike() {
      if (!this.$store.getters.isAuthenticated) {
        this.$message({
          message: '请先登录',
          type: 'warning',
          duration: 3000
        })
        return
      }

      this.liking = true
      try {
        const response = await api.article.toggleLike(this.article.id)
        if (response.code === 200) {
          this.isLiked = !this.isLiked
          // 更新点赞数
          if (this.isLiked) {
            this.article.likeCount++
          } else {
            this.article.likeCount--
          }
          this.$message({
            message: this.isLiked ? '点赞成功' : '取消点赞成功',
            type: 'success',
            duration: 3000
          })
        } else {
          this.$message({
            message: response.message || '操作失败',
            type: 'error',
            duration: 3000
          })
        }
      } catch (error) {
        console.error('点赞操作失败:', error)
        this.$message({
          message: '操作失败',
          type: 'error',
          duration: 3000
        })
      } finally {
        this.liking = false
      }
    },

    // 更新评论数
    updateCommentCount(count) {
      this.commentCount = count
      this.article.commentCount = count
    }
  }
}
</script>

<style scoped>
.article-detail-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  min-height: calc(100vh - 70px);
}

.article-card {
  margin-bottom: 20px;
}

.article-header {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.article-title {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 15px 0;
  line-height: 1.3;
}

.article-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  color: #909399;
  font-size: 14px;
}

.article-meta span {
  display: flex;
  align-items: center;
}

.article-content {
  line-height: 1.8;
  font-size: 16px;
  color: #333;
  margin: 20px 0;
}

/* Markdown样式 */
.markdown-body {
  box-sizing: border-box;
  min-width: 200px;
  max-width: 980px;
  margin: 0 auto;
}

.markdown-body h1,
.markdown-body h2,
.markdown-body h3,
.markdown-body h4,
.markdown-body h5,
.markdown-body h6 {
  margin-top: 24px;
  margin-bottom: 16px;
  font-weight: 600;
  line-height: 1.25;
  color: #24292e;
}

.markdown-body h1 {
  font-size: 2em;
  border-bottom: 1px solid #eaecef;
  padding-bottom: 0.3em;
}

.markdown-body h2 {
  font-size: 1.5em;
  border-bottom: 1px solid #eaecef;
  padding-bottom: 0.3em;
}

.markdown-body h3 {
  font-size: 1.25em;
}

.markdown-body h4 {
  font-size: 1em;
}

.markdown-body h5 {
  font-size: 0.875em;
}

.markdown-body h6 {
  font-size: 0.85em;
  color: #6a737d;
}

.markdown-body p {
  margin-top: 0;
  margin-bottom: 16px;
}

.markdown-body blockquote {
  padding: 0 1em;
  color: #6a737d;
  border-left: 0.25em solid #dfe2e5;
  margin: 0 0 16px 0;
}

.markdown-body ul,
.markdown-body ol {
  padding-left: 2em;
  margin-top: 0;
  margin-bottom: 16px;
}

.markdown-body li {
  margin-bottom: 0.25em;
}

.markdown-body code {
  padding: 0.2em 0.4em;
  margin: 0;
  font-size: 85%;
  background-color: rgba(27, 31, 35, 0.05);
  border-radius: 3px;
  font-family: "SFMono-Regular", Consolas, "Liberation Mono", Menlo, Courier, monospace;
}

.markdown-body pre {
  padding: 16px;
  overflow: auto;
  font-size: 85%;
  line-height: 1.45;
  background-color: #f6f8fa;
  border-radius: 6px;
  margin-bottom: 16px;
}

.markdown-body pre code {
  display: inline;
  max-width: auto;
  padding: 0;
  margin: 0;
  overflow: visible;
  line-height: inherit;
  word-wrap: normal;
  background-color: transparent;
  border: 0;
}

.markdown-body strong {
  font-weight: 600;
}

.markdown-body em {
  font-style: italic;
}

.markdown-body del {
  text-decoration: line-through;
}

.markdown-body a {
  color: #0366d6;
  text-decoration: none;
}

.markdown-body a:hover {
  text-decoration: underline;
}

.markdown-body img {
  max-width: 100%;
  height: auto;
  border-radius: 6px;
  margin: 10px 0;
}

.markdown-body hr {
  height: 0.25em;
  padding: 0;
  margin: 24px 0;
  background-color: #e1e4e8;
  border: 0;
}

.markdown-body table {
  border-spacing: 0;
  border-collapse: collapse;
  margin-top: 0;
  margin-bottom: 16px;
  width: 100%;
}

.markdown-body table th,
.markdown-body table td {
  padding: 6px 13px;
  border: 1px solid #dfe2e5;
}

.markdown-body table th {
  font-weight: 600;
  background-color: #f6f8fa;
}

.markdown-body table tr:nth-child(2n) {
  background-color: #f6f8fa;
}

.article-content >>> img {
  max-width: 100%;
  height: auto;
  border-radius: 6px;
  margin: 10px 0;
}

.article-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.tags {
  display: flex;
  gap: 8px;
}

.comments-card {
  margin-top: 20px;
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.comments-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.comments-info {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #909399;
  font-size: 12px;
}

@media (max-width: 768px) {
  .article-detail-page {
    padding: 10px;
  }
  
  .article-title {
    font-size: 24px;
  }
  
  .article-meta {
    flex-direction: column;
    gap: 10px;
  }
  
  .article-footer {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
}
</style>