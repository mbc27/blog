<template>
  <div class="article-management">
    <el-card shadow="hover">
      <div slot="header" class="clearfix">
        <span>文章管理</span>
        <el-button style="float: right" type="primary" size="small" @click="handleAddArticle">新增文章</el-button>
      </div>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="标题">
          <el-input v-model="searchForm.title" placeholder="文章标题" clearable></el-input>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.categoryId" placeholder="选择分类" clearable>
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.categoryName || item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="文章状态" clearable>
            <el-option label="已发布" :value="1"></el-option>
            <el-option label="草稿" :value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchArticles">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      
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
          prop="categoryName"
          label="分类"
          width="120">
        </el-table-column>
        <el-table-column
          prop="viewCount"
          label="浏览量"
          width="100">
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
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '已发布' : '草稿' }}
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
            <el-button
              size="mini"
              @click="handleEdit(scope.row)">编辑</el-button>
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

    <!-- 文章编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="80%" :before-close="handleDialogClose">
      <el-form :model="articleForm" :rules="articleRules" ref="articleForm" label-width="100px">
        <el-form-item label="文章标题" prop="title">
          <el-input v-model="articleForm.title" placeholder="请输入文章标题"></el-input>
        </el-form-item>
        <el-form-item label="文章分类" prop="categoryId">
          <el-select v-model="articleForm.categoryId" placeholder="请选择文章分类">
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.categoryName || item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <!-- 暂时移除标签选择功能 -->
        <el-form-item label="文章摘要" prop="summary">
          <el-input type="textarea" v-model="articleForm.summary" :rows="3" placeholder="请输入文章摘要"></el-input>
        </el-form-item>
        <el-form-item label="文章内容" prop="content">
          <div class="editor-container">
            <!-- 这里可以集成Markdown编辑器，如mavon-editor或其他编辑器 -->
            <el-input type="textarea" v-model="articleForm.content" :rows="15" placeholder="请输入文章内容（支持Markdown格式）"></el-input>
          </div>
        </el-form-item>
        <el-form-item label="文章封面">
          <el-upload
            class="avatar-uploader"
            action="/api/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            :before-upload="beforeCoverUpload">
            <img v-if="articleForm.cover" :src="articleForm.cover" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>
        <el-form-item label="是否置顶">
          <el-switch v-model="articleForm.isTop"></el-switch>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="articleForm.status">
            <el-radio :label="1">发布</el-radio>
            <el-radio :label="0">草稿</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitArticle" :loading="submitLoading">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'
import { mapGetters } from 'vuex'

export default {
  name: 'ArticleManagement',
  data() {
    return {
      searchForm: {
        title: '',
        categoryId: '',
        status: ''
      },
      articles: [],
      categories: [],
      tags: [],
      loading: false,
      pagination: {
        current: 1,
        size: 10,
        total: 0
      },
      dialogVisible: false,
      dialogTitle: '新增文章',
      articleForm: {
        id: null,
        title: '',
        categoryId: '',
        tagIds: [],
        summary: '',
        content: '',
        cover: '',
        isTop: false,
        isRecommend: false,
        status: 1
      },
      articleRules: {
        title: [
          { required: true, message: '请输入文章标题', trigger: 'blur' },
          { max: 100, message: '长度不能超过100个字符', trigger: 'blur' }
        ],
        categoryId: [
          { required: true, message: '请选择文章分类', trigger: 'change' }
        ],
        content: [
          { required: true, message: '请输入文章内容', trigger: 'blur' }
        ]
      },
      submitLoading: false
    }
  },
  computed: {
    ...mapGetters(['token']),
    uploadHeaders() {
      return {
        'Authorization': this.token
      }
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
    this.fetchCategories()
    this.fetchTags()
    this.fetchArticles()
  },
  methods: {
    fetchCategories() {
      axios.get('/api/category/list', {
        headers: {
          'Authorization': this.token
        }
      })
        .then(response => {
          console.log('分类列表响应:', response)
          if (response.data && response.data.code === 200) {
            this.categories = response.data.data || []
          } else if (response.code === 200) {
            this.categories = response.data || []
          }
        })
        .catch(error => {
          console.error('获取分类列表失败', error)
          this.$message.error('获取分类列表失败')
        })
    },
    fetchTags() {
      // 根据需求暂时不获取标签
      this.tags = []
      /*
      axios.get('/api/tag/list', {
        headers: {
          'Authorization': this.token
        }
      })
        .then(response => {
          console.log('标签列表响应:', response)
          if (response.data && response.data.code === 200) {
            this.tags = response.data.data || []
          } else if (response.code === 200) {
            this.tags = response.data || []
          }
        })
        .catch(error => {
          console.error('获取标签列表失败', error)
          this.$message.error('获取标签列表失败')
        })
      */
    },
    async fetchArticles() {
      this.loading = true
      
      try {
        // 先获取已发布的文章
        const publishedArticles = await this.fetchPublishedArticles()
        
        // 再获取待审核的文章
        const pendingArticles = await this.fetchPendingArticles()
        
        // 合并文章列表
        const allArticles = [...publishedArticles, ...pendingArticles]
        
        // 为文章添加分类名称
        const articlesWithCategory = await this.addCategoryNames(allArticles)
        
        // 根据搜索条件过滤
        let filteredArticles = articlesWithCategory
        if (this.searchForm.title) {
          filteredArticles = articlesWithCategory.filter(article => 
            article.title && article.title.includes(this.searchForm.title)
          )
        }
        if (this.searchForm.categoryId) {
          filteredArticles = filteredArticles.filter(article => 
            article.categoryId === this.searchForm.categoryId
          )
        }
        if (this.searchForm.status !== '') {
          filteredArticles = filteredArticles.filter(article => 
            article.status === this.searchForm.status
          )
        }
        
        // 分页处理
        const startIndex = (this.pagination.current - 1) * this.pagination.size
        const endIndex = startIndex + this.pagination.size
        this.articles = filteredArticles.slice(startIndex, endIndex)
        this.pagination.total = filteredArticles.length
        
      } catch (error) {
        console.error('获取文章列表失败', error)
        this.$message.error('获取文章列表失败')
      } finally {
        this.loading = false
      }
    },
    
    // 为文章添加分类名称和评论数
    async addCategoryNames(articles) {
      // 创建分类ID到分类名称的映射
      const categoryMap = {}
      this.categories.forEach(category => {
        categoryMap[category.id] = category.categoryName || category.name
      })
      
      // 批量获取所有文章的评论数
      const commentCountMap = await this.getBatchCommentCounts(articles.map(a => a.id))
      
      // 为每篇文章添加分类名称和评论数
      return articles.map(article => ({
        ...article,
        categoryName: categoryMap[article.categoryId] || '未分类',
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
    
    // 获取已发布的文章
    async fetchPublishedArticles() {
      try {
        const response = await axios.get('/api/article/list', {
          params: { current: 1, size: 1000 }, // 获取大量数据
          headers: { 'Authorization': this.token }
        })
        
        console.log('已发布文章响应:', response)
        
        if (response.data && response.data.code === 200) {
          return response.data.data.records || []
        } else if (response.code === 200) {
          return response.data.records || []
        }
        return []
      } catch (error) {
        console.error('获取已发布文章失败', error)
        return []
      }
    },
    
    // 获取待审核的文章
    async fetchPendingArticles() {
      try {
        const response = await axios.get('/api/article/pending', {
          params: { current: 1, size: 1000 }, // 获取大量数据
          headers: { 'Authorization': this.token }
        })
        
        console.log('待审核文章响应:', response)
        
        if (response.data && response.data.code === 200) {
          return response.data.data.records || []
        } else if (response.code === 200) {
          return response.data.records || []
        }
        return []
      } catch (error) {
        console.error('获取待审核文章失败', error)
        return []
      }
    },
    searchArticles() {
      this.pagination.current = 1
      this.fetchArticles()
    },
    resetSearch() {
      this.searchForm = {
        title: '',
        categoryId: '',
        status: ''
      }
      this.searchArticles()
    },
    handleSizeChange(val) {
      this.pagination.size = val
      this.fetchArticles()
    },
    handleCurrentChange(val) {
      this.pagination.current = val
      this.fetchArticles()
    },
    handleAddArticle() {
      this.dialogTitle = '新增文章'
      this.articleForm = {
        id: null,
        title: '',
        categoryId: '',
        tagIds: [], // 保留字段但不使用
        summary: '',
        content: '',
        cover: '',
        isTop: false,
        status: 1,
        userId: null // 新增时不设置用户ID，让后端自动设置
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.articleForm && this.$refs.articleForm.clearValidate()
      })
    },
    handleEdit(row) {
      this.dialogTitle = '编辑文章'
      this.loading = true
      
      axios.get(`/api/article/${row.id}`, {
        headers: {
          'Authorization': this.token
        }
      })
        .then(response => {
          console.log('文章详情响应:', response)
          let article
          if (response.data && response.data.code === 200) {
            article = response.data.data
          } else if (response.code === 200) {
            article = response.data
          }
          
          if (article) {
            this.articleForm = {
              id: article.id,
              title: article.title,
              categoryId: article.categoryId,
              tagIds: [], // 暂时不使用标签
              summary: article.summary,
              content: article.content,
              cover: article.cover,
              isTop: article.isTop === 1,
              status: article.status,
              userId: article.userId // 保存原始用户ID
            }
            this.dialogVisible = true
            this.$nextTick(() => {
              this.$refs.articleForm && this.$refs.articleForm.clearValidate()
            })
          }
        })
        .catch(error => {
          console.error('获取文章详情失败', error)
          this.$message.error('获取文章详情失败')
        })
        .finally(() => {
          this.loading = false
        })
    },
    handleDelete(row) {
      this.$confirm('确认删除该文章吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        axios.delete(`/api/article/${row.id}`, {
          headers: {
            'Authorization': this.token
          }
        })
          .then(response => {
            console.log('删除文章响应:', response)
            // 检查不同的响应格式
            if (response.data && response.data.code === 200) {
              this.$message.success('删除成功')
              this.fetchArticles()
            } else if (response.code === 200 || response.status === 200) {
              this.$message.success('删除成功')
              this.fetchArticles()
            } else {
              const errorMsg = (response.data && response.data.message) || response.message || '删除文章失败'
              this.$message.error(errorMsg)
            }
          })
          .catch(error => {
            console.error('删除文章失败', error)
            
            // 检查是否实际删除成功但返回了错误状态
            if (error.response && error.response.status === 200) {
              this.$message.success('删除成功')
              this.fetchArticles()
            } else {
              let errorMsg = '删除文章失败'
              if (error.response && error.response.data && error.response.data.message) {
                errorMsg = error.response.data.message
              } else if (error.message) {
                errorMsg = error.message
              }
              this.$message.error(errorMsg)
              
              // 即使出错也刷新列表，因为可能实际已经删除成功
              setTimeout(() => {
                this.fetchArticles()
              }, 1000)
            }
          })
      }).catch(() => {})
    },
    submitArticle() {
      this.$refs.articleForm.validate(valid => {
        if (valid) {
          this.submitLoading = true
          
          // 转换布尔值为数字，移除不需要的字段
          const formData = {
            ...this.articleForm,
            isTop: this.articleForm.isTop ? 1 : 0
          }
          
          // 移除前端特有的字段
          delete formData.tagIds
          
          let promise
          if (this.articleForm.id) {
            // 更新文章
            promise = axios.put(`/api/article/${this.articleForm.id}`, formData, {
              headers: {
                'Authorization': this.token
              }
            })
          } else {
            // 新增文章
            promise = axios.post('/api/article', formData, {
              headers: {
                'Authorization': this.token
              }
            })
          }
          
          promise
            .then(response => {
              console.log('文章提交响应:', response)
              if (response.data && response.data.code === 200) {
                this.$message.success(`${this.articleForm.id ? '更新' : '添加'}文章成功`)
                this.dialogVisible = false
                this.fetchArticles()
              } else if (response.code === 200) {
                this.$message.success(`${this.articleForm.id ? '更新' : '添加'}文章成功`)
                this.dialogVisible = false
                this.fetchArticles()
              } else {
                const errorMsg = (response.data && response.data.message) || response.message || `${this.articleForm.id ? '更新' : '添加'}文章失败`
                this.$message.error(errorMsg)
              }
            })
            .catch(error => {
              console.error(`${this.articleForm.id ? '更新' : '添加'}文章失败`, error)
              let errorMsg = `${this.articleForm.id ? '更新' : '添加'}文章失败`
              if (error.response && error.response.data && error.response.data.message) {
                errorMsg = error.response.data.message
              } else if (error.message) {
                errorMsg = error.message
              }
              this.$message.error(errorMsg)
            })
            .finally(() => {
              this.submitLoading = false
            })
        } else {
          return false
        }
      })
    },
    handleDialogClose(done) {
      if (this.submitLoading) {
        this.$message.warning('正在提交，请稍候...')
        return
      }
      done()
    },
    handleCoverSuccess(res) {
      this.articleForm.cover = res.data
      this.$message.success('封面上传成功')
    },
    beforeCoverUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isImage) {
        this.$message.error('上传封面只能是图片格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传封面大小不能超过 2MB!')
      }
      return isImage && isLt2M
    }
  }
}
</script>

<style scoped>
.article-management {
  padding: 20px;
}
.search-form {
  margin-bottom: 20px;
}
.pagination-container {
  margin-top: 20px;
  text-align: right;
}
.editor-container {
  min-height: 300px;
}
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>