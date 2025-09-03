<template>
  <div class="article-review-page">
    <div class="container">
      <h1>文章审核</h1>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="待审核" name="pending">
          <el-table
            v-loading="loading"
            :data="pendingArticles"
            style="width: 100%">
            <el-table-column
              prop="title"
              label="标题"
              min-width="200">
              <template slot-scope="scope">
                <router-link :to="`/article/${scope.row.id}`">{{ scope.row.title }}</router-link>
              </template>
            </el-table-column>
            <el-table-column
              prop="authorName"
              label="作者"
              width="120">
            </el-table-column>
            <el-table-column
              prop="createTime"
              label="提交时间"
              width="180">
            </el-table-column>
            <el-table-column
              label="操作"
              width="200">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="primary"
                  @click="viewArticle(scope.row.id)">查看</el-button>
                <el-button
                  size="mini"
                  type="success"
                  @click="approveArticle(scope.row.id)">通过</el-button>
                <el-button
                  size="mini"
                  type="danger"
                  @click="rejectArticle(scope.row.id)">拒绝</el-button>
              </template>
            </el-table-column>
          </el-table>
          
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
        </el-tab-pane>
        
        <el-tab-pane label="已审核" name="reviewed">
          <el-table
            v-loading="loading"
            :data="reviewedArticles"
            style="width: 100%">
            <el-table-column
              prop="title"
              label="标题"
              min-width="200">
              <template slot-scope="scope">
                <router-link :to="`/article/${scope.row.id}`">{{ scope.row.title }}</router-link>
              </template>
            </el-table-column>
            <el-table-column
              prop="authorName"
              label="作者"
              width="120">
            </el-table-column>
            <el-table-column
              prop="createTime"
              label="提交时间"
              width="180">
            </el-table-column>
            <el-table-column
              prop="status"
              label="状态"
              width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                  {{ scope.row.status === 1 ? '已通过' : '已拒绝' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              label="操作"
              width="100">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="primary"
                  @click="viewArticle(scope.row.id)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <div class="pagination">
            <el-pagination
              background
              layout="prev, pager, next"
              :current-page.sync="reviewedCurrentPage"
              :page-size="pageSize"
              :total="reviewedTotal"
              @current-change="handleReviewedPageChange">
            </el-pagination>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
import api from '../api'

export default {
  name: 'ArticleReview',
  data() {
    return {
      activeTab: 'pending',
      pendingArticles: [],
      reviewedArticles: [],
      loading: false,
      currentPage: 1,
      reviewedCurrentPage: 1,
      pageSize: 10,
      total: 0,
      reviewedTotal: 0
    }
  },
  created() {
    this.checkAdminPermission()
    this.fetchPendingArticles()
  },
  watch: {
    activeTab(newVal) {
      if (newVal === 'pending') {
        this.fetchPendingArticles()
      } else if (newVal === 'reviewed') {
        this.fetchReviewedArticles()
      }
    }
  },
  methods: {
    // 检查是否有管理员权限
    async checkAdminPermission() {
      try {
        const response = await api.user.getInfo()
        if (response.code === 200) {
          const userInfo = response.data
          const isAdmin = userInfo.roles && userInfo.roles.some(role => role === 'ROLE_ADMIN')
          if (!isAdmin) {
            this.$message.error('您没有权限访问此页面')
            this.$router.push('/')
          }
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
        this.$message.error('获取用户信息失败')
        this.$router.push('/')
      }
    },
    
    // 获取待审核文章列表
    async fetchPendingArticles() {
      try {
        this.loading = true
        const response = await api.article.getPendingList({
          current: this.currentPage,
          size: this.pageSize
        })
        
        if (response.code === 200) {
          this.pendingArticles = response.data.records || []
          this.total = response.data.total || 0
        }
      } catch (error) {
        console.error('获取待审核文章失败:', error)
        this.$message.error('获取待审核文章失败')
      } finally {
        this.loading = false
      }
    },
    
    // 获取已审核文章列表
    async fetchReviewedArticles() {
      try {
        this.loading = true
        // 这里需要后端提供一个获取已审核文章的接口
        // 暂时使用普通文章列表接口代替
        const response = await api.article.getList({
          current: this.reviewedCurrentPage,
          size: this.pageSize
        })
        
        if (response.code === 200) {
          this.reviewedArticles = response.data.records || []
          this.reviewedTotal = response.data.total || 0
        }
      } catch (error) {
        console.error('获取已审核文章失败:', error)
        this.$message.error('获取已审核文章失败')
      } finally {
        this.loading = false
      }
    },
    
    // 查看文章详情
    viewArticle(id) {
      this.$router.push(`/article/${id}`)
    },
    
    // 审核通过文章
    async approveArticle(id) {
      try {
        const response = await api.article.approve(id)
        if (response.code === 200) {
          this.$message.success('审核通过成功')
          this.fetchPendingArticles()
        }
      } catch (error) {
        this.$message.error('审核操作失败: ' + (error.message || '未知错误'))
      }
    },
    
    // 审核拒绝文章
    async rejectArticle(id) {
      try {
        const response = await api.article.reject(id)
        if (response.code === 200) {
          this.$message.success('审核拒绝成功')
          this.fetchPendingArticles()
        }
      } catch (error) {
        this.$message.error('审核操作失败: ' + (error.message || '未知错误'))
      }
    },
    
    // 待审核文章分页
    handlePageChange(page) {
      this.currentPage = page
      this.fetchPendingArticles()
    },
    
    // 已审核文章分页
    handleReviewedPageChange(page) {
      this.reviewedCurrentPage = page
      this.fetchReviewedArticles()
    }
  }
}
</script>

<style scoped>
.article-review-page {
  padding: 100px 0;
}

h1 {
  color: #333;
  margin-bottom: 30px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>