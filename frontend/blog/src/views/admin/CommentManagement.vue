<template>
  <div class="comment-management">
    <el-card shadow="hover">
      <div slot="header" class="clearfix">
        <span>评论管理</span>
      </div>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="文章标题">
          <el-input v-model="searchForm.articleTitle" placeholder="文章标题" clearable></el-input>
        </el-form-item>
        <el-form-item label="评论内容">
          <el-input v-model="searchForm.content" placeholder="评论内容" clearable></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="评论状态" clearable>
            <el-option label="已审核" :value="1"></el-option>
            <el-option label="待审核" :value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchComments">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table
        :data="comments"
        style="width: 100%"
        v-loading="loading">
        <el-table-column
          type="index"
          label="序号"
          width="80"
          :index="indexMethod">
        </el-table-column>
        <el-table-column
          prop="articleTitle"
          label="文章标题"
          min-width="180">
          <template slot-scope="scope">
            <el-tooltip :content="scope.row.articleTitle" placement="top" :disabled="!scope.row.articleTitle || scope.row.articleTitle.length < 20">
              <span>{{ (scope.row.articleTitle || '未知文章') | ellipsis(20) }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column
          prop="content"
          label="评论内容"
          min-width="200">
          <template slot-scope="scope">
            <el-tooltip :content="scope.row.content" placement="top" :disabled="scope.row.content.length < 30">
              <span>{{ scope.row.content | ellipsis(30) }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column
          label="评论人"
          width="120">
          <template slot-scope="scope">
            <span>{{ scope.row.nickname || '匿名用户' }}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="状态"
          width="100">
          <template>
            <el-tag type="success">
              已发布
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="评论时间"
          width="180">
        </el-table-column>
        <el-table-column
          label="操作"
          width="250">
          <template slot-scope="scope">
            <el-button
              size="mini"
              @click="handleView(scope.row)">查看</el-button>
            <el-button
              size="mini"
              type="danger"
              @click="handleDelete(scope.row)">删除</el-button>
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

    <!-- 评论详情对话框 -->
    <el-dialog title="评论详情" :visible.sync="dialogVisible" width="600px">
      <div class="comment-detail">
        <div class="comment-item">
          <div class="comment-header">
            <span class="comment-author">{{ currentComment.nickname }}</span>
            <span class="comment-time">{{ currentComment.createTime }}</span>
          </div>
          <div class="comment-content">{{ currentComment.content }}</div>
          <div class="comment-article">
            <span>评论文章：</span>
            <el-link type="primary" :underline="false" @click="viewArticle(currentComment.articleId)">{{ currentComment.articleTitle }}</el-link>
          </div>
          <div class="comment-info">
            <span>IP地址：{{ currentComment.ipAddress || '未知' }}</span>
            <span>设备信息：{{ currentComment.device || '未知' }}</span>
          </div>
        </div>

        <div class="comment-reply" v-if="currentComment.replyCount > 0">
          <div class="reply-title">回复列表</div>
          <div class="reply-list">
            <div class="reply-item" v-for="(reply, index) in replies" :key="index">
              <div class="reply-header">
                <span class="reply-author">{{ reply.nickname }}</span>
                <span class="reply-time">{{ reply.createTime }}</span>
              </div>
              <div class="reply-content">{{ reply.content }}</div>
            </div>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button type="danger" @click="handleDelete(currentComment)">删除评论</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'CommentManagement',
  data() {
    return {
      searchForm: {
        articleTitle: '',
        content: '',
        status: ''
      },
      comments: [],
      loading: false,
      pagination: {
        current: 1,
        size: 10,
        total: 0
      },
      dialogVisible: false,
      currentComment: {},
      replies: []
    }
  },
  filters: {
    ellipsis(value, maxLength) {
      if (!value) return ''
      if (value.length <= maxLength) return value
      return value.slice(0, maxLength) + '...'
    }
  },
  created() {
    this.fetchComments()
  },
  methods: {
    indexMethod(index) {
      return index + 1;
    },
    fetchComments() {
      this.loading = true
      const params = {
        current: this.pagination.current,
        size: this.pagination.size,
        ...this.searchForm
      }
      
      axios.get('/api/comment', { 
        params,
        headers: {
          Authorization: this.$store.getters.token
        }
      })
        .then(response => {
          if (response.data && response.data.data) {
            // 检查返回的数据是否是分页对象
            if (response.data.data.records) {
              this.comments = response.data.data.records
              this.pagination.total = response.data.data.total
            } else {
              this.comments = response.data.data
              this.pagination.total = response.data.total || response.data.data.length
            }
          }
        })
        .catch(error => {
          console.error('获取评论列表失败', error)
          this.$message.error('获取评论列表失败')
        })
        .finally(() => {
          this.loading = false
        })
    },
    searchComments() {
      this.pagination.current = 1
      this.fetchComments()
    },
    resetSearch() {
      this.searchForm = {
        articleTitle: '',
        content: '',
        status: ''
      }
      this.searchComments()
    },
    handleSizeChange(val) {
      this.pagination.size = val
      this.fetchComments()
    },
    handleCurrentChange(val) {
      this.pagination.current = val
      this.fetchComments()
    },
    handleView(row) {
      this.currentComment = { ...row }
      this.dialogVisible = true
      
      // 获取评论回复
      if (row.replyCount > 0) {
        axios.get(`/api/comment/${row.id}/replies`, {
          headers: {
            Authorization: this.$store.getters.token
          }
        })
          .then(response => {
            this.replies = response.data.data || response.data
          })
          .catch(error => {
            console.error('获取评论回复失败', error)
            this.$message.error('获取评论回复失败')
          })
      } else {
        this.replies = []
      }
    },
    handleApprove(row) {
      this.$confirm('确认审核通过该评论吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        axios.put(`/api/comment/${row.id}/approve`, {}, {
          headers: {
            Authorization: this.$store.getters.token
          }
        })
          .then(() => {
            this.$message.success('审核通过成功')
            this.fetchComments()
            if (this.dialogVisible) {
              this.dialogVisible = false
            }
          })
          .catch(error => {
            console.error('审核评论失败', error)
            this.$message.error('审核评论失败')
          })
      }).catch(() => {})
    },
    handleDelete(row) {
      this.$confirm('确认删除该评论吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        axios.delete(`/api/comment/${row.id}`, {
          headers: {
            Authorization: this.$store.getters.token
          }
        })
          .then(() => {
            this.$message.success('删除成功')
            this.fetchComments()
            if (this.dialogVisible) {
              this.dialogVisible = false
            }
          })
          .catch(error => {
            console.error('删除评论失败', error)
            this.$message.error('删除评论失败')
          })
      }).catch(() => {})
    },
    viewArticle(articleId) {
      // 在新标签页中打开文章
      const routeUrl = this.$router.resolve({
        path: `/article/${articleId}`
      })
      window.open(routeUrl.href, '_blank')
    }
  }
}
</script>

<style scoped>
.comment-management {
  padding: 20px;
}
.search-form {
  margin-bottom: 20px;
}
.pagination-container {
  margin-top: 20px;
  text-align: right;
}
.comment-detail {
  padding: 10px;
}
.comment-item {
  border-bottom: 1px solid #eee;
  padding-bottom: 15px;
  margin-bottom: 15px;
}
.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}
.comment-author {
  font-weight: bold;
  color: #409EFF;
}
.comment-time {
  color: #999;
  font-size: 12px;
}
.comment-content {
  margin-bottom: 10px;
  line-height: 1.5;
  word-break: break-all;
}
.comment-article {
  margin-bottom: 10px;
  font-size: 13px;
}
.comment-info {
  display: flex;
  justify-content: space-between;
  color: #999;
  font-size: 12px;
}
.reply-title {
  font-weight: bold;
  margin-bottom: 10px;
}
.reply-item {
  background-color: #f9f9f9;
  padding: 10px;
  margin-bottom: 10px;
  border-radius: 4px;
}
.reply-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}
.reply-author {
  font-weight: bold;
}
.reply-time {
  color: #999;
  font-size: 12px;
}
.reply-content {
  line-height: 1.5;
  word-break: break-all;
}
</style>