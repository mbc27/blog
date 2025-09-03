<template>
  <div class="article-review">
    <el-card shadow="hover">
      <div slot="header" class="clearfix">
        <span>文章审核</span>
      </div>
      
      <el-table
        :data="articles"
        style="width: 100%"
        v-loading="loading">
        <el-table-column
          prop="title"
          label="标题"
          min-width="200">
          <template slot-scope="scope">
            <el-tooltip :content="scope.row.title" placement="top" :disabled="scope.row.title.length < 30">
              <span>{{ scope.row.title | ellipsis(30) }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column
          prop="authorName"
          label="作者"
          width="120">
        </el-table-column>
        <el-table-column
          prop="categoryName"
          label="分类"
          width="120">
        </el-table-column>
        <el-table-column
          prop="commentCount"
          label="评论数"
          width="100">
        </el-table-column>
        <el-table-column
          prop="status"
          label="状态"
          width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="创建时间"
          width="180">
        </el-table-column>
        <el-table-column
          label="操作"
          width="200">
          <template slot-scope="scope">
            <el-button-group>
              <el-button
                size="mini"
                @click="handlePreview(scope.row)">预览</el-button>
              <el-button
                size="mini"
                type="success"
                @click="handleApprove(scope.row)"
                v-if="scope.row.status === 2">通过</el-button>
              <el-button
                size="mini"
                type="danger"
                @click="handleReject(scope.row)"
                v-if="scope.row.status === 2">拒绝</el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pagination.current"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="pagination.size"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total">
        </el-pagination>
      </div>
    </el-card>

    <!-- 文章预览对话框 -->
    <el-dialog title="文章预览" :visible.sync="previewVisible" width="80%">
      <div v-if="previewArticle">
        <h2>{{ previewArticle.title }}</h2>
        <div class="article-meta">
          <span>作者：{{ previewArticle.authorName }}</span>
          <span>创建时间：{{ previewArticle.createTime }}</span>
        </div>
        <div class="article-summary" v-if="previewArticle.summary">
          <h4>摘要：</h4>
          <p>{{ previewArticle.summary }}</p>
        </div>
        <div class="article-content">
          <h4>内容：</h4>
          <div v-html="previewArticle.content"></div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'
import { mapGetters } from 'vuex'

export default {
  name: 'ArticleReview',
  data() {
    return {
      articles: [],
      loading: false,
      pagination: {
        current: 1,
        size: 10,
        total: 0
      },
      previewVisible: false,
      previewArticle: null
    }
  },
  computed: {
    ...mapGetters(['token'])
  },
  filters: {
    ellipsis(value, maxLength) {
      if (!value) return ''
      if (value.length <= maxLength) return value
      return value.slice(0, maxLength) + '...'
    }
  },
  created() {
    this.fetchPendingArticles()
  },
  methods: {
    async fetchPendingArticles() {
      this.loading = true
      const params = {
        current: this.pagination.current,
        size: this.pagination.size
      }
      
      try {
        const response = await axios.get('/api/article/pending', { 
          params,
          headers: {
            'Authorization': this.token
          }
        })
        
        console.log('待审核文章响应:', response)
        let articles = []
        let total = 0
        
        if (response.data && response.data.code === 200) {
          articles = response.data.data.records || []
          total = response.data.data.total || 0
        } else if (response.code === 200) {
          articles = response.data.records || []
          total = response.data.total || 0
        }
        
        // 为文章添加评论数
        if (articles.length > 0) {
          const articlesWithCommentCount = await this.addCommentCounts(articles)
          this.articles = articlesWithCommentCount
        } else {
          this.articles = articles
        }
        
        this.pagination.total = total
        
      } catch (error) {
        console.error('获取待审核文章失败', error)
        this.$message.error('获取待审核文章失败')
      } finally {
        this.loading = false
      }
    },
    
    // 为文章添加评论数
    async addCommentCounts(articles) {
      const commentCountMap = await this.getBatchCommentCounts(articles.map(a => a.id))
      
      return articles.map(article => ({
        ...article,
        commentCount: commentCountMap[article.id] || 0
      }))
    },
    
    // 批量获取文章的评论数
    async getBatchCommentCounts(articleIds) {
      const commentCountMap = {}
      
      // 限制并发请求数量，避免服务器压力过大
      const batchSize = 5
      for (let i = 0; i < articleIds.length; i += batchSize) {
        const batch = articleIds.slice(i, i + batchSize)
        const promises = batch.map(async (articleId) => {
          try {
            const response = await axios.get('/api/comment/list', {
              params: {
                articleId: articleId,
                current: 1,
                size: 1 // 只需要获取总数，不需要具体数据
              },
              headers: {
                'Authorization': this.token
              }
            })
            
            let total = 0
            if (response.data && response.data.code === 200) {
              total = response.data.data.total || 0
            } else if (response.code === 200) {
              total = response.data.total || 0
            }
            
            return { articleId, total }
          } catch (error) {
            console.error(`获取文章${articleId}的评论数失败:`, error)
            return { articleId, total: 0 }
          }
        })
        
        const results = await Promise.all(promises)
        results.forEach(({ articleId, total }) => {
          commentCountMap[articleId] = total
        })
      }
      
      return commentCountMap
    },
    handleSizeChange(val) {
      this.pagination.size = val
      this.fetchPendingArticles()
    },
    handleCurrentChange(val) {
      this.pagination.current = val
      this.fetchPendingArticles()
    },
    handlePreview(row) {
      this.previewArticle = row
      this.previewVisible = true
    },
    handleApprove(row) {
      this.$confirm('确认通过该文章的审核吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        axios.put(`/api/article/approve/${row.id}`, {}, {
          headers: {
            'Authorization': this.token
          }
        })
          .then(response => {
            if (response.data && response.data.code === 200) {
              this.$message.success('审核通过')
              this.fetchPendingArticles()
            } else if (response.code === 200) {
              this.$message.success('审核通过')
              this.fetchPendingArticles()
            } else {
              this.$message.error(response.message || '审核失败')
            }
          })
          .catch(error => {
            console.error('审核通过失败', error)
            this.$message.error('审核通过失败')
          })
      }).catch(() => {})
    },
    handleReject(row) {
      this.$confirm('确认拒绝该文章的审核吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        axios.put(`/api/article/reject/${row.id}`, {}, {
          headers: {
            'Authorization': this.token
          }
        })
          .then(response => {
            if (response.data && response.data.code === 200) {
              this.$message.success('审核拒绝')
              this.fetchPendingArticles()
            } else if (response.code === 200) {
              this.$message.success('审核拒绝')
              this.fetchPendingArticles()
            } else {
              this.$message.error(response.message || '审核失败')
            }
          })
          .catch(error => {
            console.error('审核拒绝失败', error)
            this.$message.error('审核拒绝失败')
          })
      }).catch(() => {})
    },
    getStatusType(status) {
      switch (status) {
        case 1: return 'success'
        case 2: return 'warning'
        case 3: return 'danger'
        default: return 'info'
      }
    },
    getStatusText(status) {
      switch (status) {
        case 0: return '草稿'
        case 1: return '已发布'
        case 2: return '待审核'
        case 3: return '审核不通过'
        default: return '未知'
      }
    }
  }
}
</script>

<style scoped>
.article-review {
  padding: 20px;
}
.pagination-container {
  margin-top: 20px;
  text-align: right;
}
.article-meta {
  margin: 10px 0;
  color: #666;
}
.article-meta span {
  margin-right: 20px;
}
.article-summary, .article-content {
  margin: 20px 0;
}
.article-content {
  max-height: 400px;
  overflow-y: auto;
}
</style>