<template>
  <div class="admin-home">
    <el-row :gutter="20">
      <!-- 统计卡片 -->
      <el-col :span="6" v-for="stat in stats" :key="stat.key">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: stat.color }">
              <i :class="stat.icon"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 最近文章 -->
      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span>最近文章</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="$router.push('/admin/article')">
              查看更多
            </el-button>
          </div>
          <el-table :data="recentArticles" style="width: 100%" size="small">
            <el-table-column prop="title" label="标题" show-overflow-tooltip></el-table-column>
            <el-table-column prop="viewCount" label="浏览量" width="80"></el-table-column>
            <el-table-column prop="createTime" label="发布时间" width="120">
              <template slot-scope="scope">
                {{ formatDate(scope.row.createTime) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 最近评论 -->
      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span>最近评论</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="$router.push('/admin/comment')">
              查看更多
            </el-button>
          </div>
          <el-table :data="recentComments" style="width: 100%" size="small">
            <el-table-column prop="nickname" label="用户" width="100"></el-table-column>
            <el-table-column prop="content" label="内容" show-overflow-tooltip></el-table-column>
            <el-table-column prop="createTime" label="时间" width="120">
              <template slot-scope="scope">
                {{ formatDate(scope.row.createTime) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 最近留言 -->
      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span>最近留言</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="$router.push('/admin/message')">
              查看更多
            </el-button>
          </div>
          <el-table :data="recentMessages" style="width: 100%" size="small">
            <el-table-column prop="nickname" label="用户" width="100"></el-table-column>
            <el-table-column prop="content" label="内容" show-overflow-tooltip></el-table-column>
            <el-table-column prop="createTime" label="时间" width="120">
              <template slot-scope="scope">
                {{ formatDate(scope.row.createTime) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 最近用户 -->
      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span>最近用户</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="$router.push('/admin/user')">
              查看更多
            </el-button>
          </div>
          <el-table :data="recentUsers" style="width: 100%" size="small">
            <el-table-column prop="nickname" label="昵称" width="100"></el-table-column>
            <el-table-column prop="email" label="邮箱" show-overflow-tooltip></el-table-column>
            <el-table-column prop="createTime" label="注册时间" width="120">
              <template slot-scope="scope">
                {{ formatDate(scope.row.createTime) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import api from '@/api'

export default {
  name: 'AdminHome',
  data() {
    return {
      stats: [
        {
          key: 'articles',
          label: '文章总数',
          value: 0,
          icon: 'el-icon-document',
          color: '#409EFF'
        },
        {
          key: 'comments',
          label: '评论总数',
          value: 0,
          icon: 'el-icon-chat-line-square',
          color: '#67C23A'
        },
        {
          key: 'messages',
          label: '留言总数',
          value: 0,
          icon: 'el-icon-message',
          color: '#E6A23C'
        },
        {
          key: 'users',
          label: '用户总数',
          value: 0,
          icon: 'el-icon-user',
          color: '#F56C6C'
        }
      ],
      recentArticles: [],
      recentComments: [],
      recentMessages: [],
      recentUsers: []
    }
  },
  created() {
    this.loadDashboardData()
  },
  methods: {
    async loadDashboardData() {
      try {
        // 获取统计数据
        await Promise.all([
          this.getArticleStats(),
          this.getCommentStats(),
          this.getMessageStats(),
          this.getUserStats(),
          this.getRecentArticles(),
          this.getRecentComments(),
          this.getRecentMessages(),
          this.getRecentUsers()
        ])
      } catch (error) {
        console.error('加载仪表板数据失败:', error)
      }
    },

    async getArticleStats() {
      try {
        const response = await api.article.getList({ current: 1, size: 1 })
        if (response.code === 200) {
          this.stats[0].value = response.data.total || 0
        }
      } catch (error) {
        console.error('获取文章统计失败:', error)
      }
    },

    async getCommentStats() {
      try {
        // 使用文章评论接口，传入一个存在的文章ID来获取评论数据
        const articleResponse = await api.article.getList({ current: 1, size: 1 })
        if (articleResponse.code === 200 && articleResponse.data.records.length > 0) {
          const articleId = articleResponse.data.records[0].id
          const response = await api.comment.getByArticle(articleId, { current: 1, size: 1 })
          if (response.code === 200) {
            this.stats[1].value = response.data.total || 0
          }
        } else {
          this.stats[1].value = 0
        }
      } catch (error) {
        console.error('获取评论统计失败:', error)
        this.stats[1].value = 0
      }
    },

    async getMessageStats() {
      try {
        const response = await api.message.getList({ current: 1, size: 1 })
        if (response.code === 200) {
          this.stats[2].value = response.data.total || 0
        }
      } catch (error) {
        console.error('获取留言统计失败:', error)
      }
    },

    async getUserStats() {
      try {
        // 尝试通过用户接口获取用户统计
        const response = await api.user.getList({ current: 1, size: 1 })
        if (response.code === 200) {
          this.stats[3].value = response.data.total || 0
        } else {
          this.stats[3].value = 0
        }
      } catch (error) {
        console.error('获取用户统计失败:', error)
        this.stats[3].value = 0
      }
    },

    async getRecentArticles() {
      try {
        const response = await api.article.getList({ current: 1, size: 5 })
        if (response.code === 200) {
          this.recentArticles = response.data.records || []
        }
      } catch (error) {
        console.error('获取最近文章失败:', error)
      }
    },

    async getRecentComments() {
      try {
        // 获取最新文章的评论作为最近评论
        const articleResponse = await api.article.getList({ current: 1, size: 3 })
        if (articleResponse.code === 200 && articleResponse.data.records.length > 0) {
          let allComments = []
          
          // 获取前几篇文章的评论
          for (const article of articleResponse.data.records) {
            try {
              const commentResponse = await api.comment.getByArticle(article.id, { current: 1, size: 3 })
              if (commentResponse.code === 200 && commentResponse.data.records) {
                const comments = commentResponse.data.records.map(comment => ({
                  ...comment,
                  nickname: comment.user ? comment.user.nickname : comment.nickname || '匿名用户',
                  articleTitle: article.title
                }))
                allComments = allComments.concat(comments)
              }
            } catch (err) {
              console.log('获取文章评论失败:', err)
            }
          }
          
          // 按时间排序并取前5条
          this.recentComments = allComments
            .sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
            .slice(0, 5)
        }
      } catch (error) {
        console.error('获取最近评论失败:', error)
        this.recentComments = []
      }
    },

    async getRecentMessages() {
      try {
        const response = await api.message.getRecentMessages({ current: 1, size: 5 })
        if (response.code === 200) {
          this.recentMessages = response.data.records || []
        }
      } catch (error) {
        console.error('获取最近留言失败:', error)
        this.recentMessages = []
      }
    },

    async getRecentUsers() {
      try {
        // 暂时显示空数据，因为没有公开的用户接口
        this.recentUsers = []
      } catch (error) {
        console.error('获取最近用户失败:', error)
        this.recentUsers = []
      }
    },

    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return `${date.getMonth() + 1}-${date.getDate()}`
    }
  }
}
</script>

<style scoped>
.admin-home {
  padding: 0;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.stat-icon i {
  font-size: 24px;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.el-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.el-table {
  font-size: 12px;
}
</style>