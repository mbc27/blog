<template>
  <div class="write-article-page">
    <div class="container">
      <div class="article-editor-card">
        <div class="editor-header">
          <h1>
            <i class="el-icon-edit-outline"></i>
            {{ isEditMode ? '编辑文章' : '发布文章' }}
          </h1>
          <div class="editor-tips">
            <el-alert
              title="支持 Markdown 格式，可以使用 # 表示标题，** 表示加粗，* 表示斜体等"
              type="info"
              :closable="false"
              show-icon>
            </el-alert>
          </div>
        </div>
        
        <el-form :model="articleForm" :rules="rules" ref="articleForm" label-width="100px" size="medium" class="article-form">
          <el-row :gutter="20">
            <el-col :span="16">
              <el-form-item label="文章标题" prop="title">
                <el-input 
                  v-model="articleForm.title" 
                  placeholder="请输入文章标题" 
                  prefix-icon="el-icon-reading"
                  maxlength="100"
                  show-word-limit>
                </el-input>
              </el-form-item>
            </el-col>
            
            <el-col :span="8">
              <el-form-item label="文章分类" prop="categoryId">
                <el-select 
                  v-model="articleForm.categoryId" 
                  placeholder="请选择文章分类"
                  style="width: 100%">
                  <el-option
                    v-for="category in categories"
                    :key="category.id"
                    :label="category.name"
                    :value="category.id">
                    <span class="category-icon">
                      <i :class="getCategoryIcon(category.name)"></i>
                    </span>
                    <span>{{ category.name }}</span>
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-form-item label="文章摘要" prop="summary">
            <el-input 
              type="textarea" 
              v-model="articleForm.summary" 
              rows="3" 
              placeholder="请输入文章摘要，将显示在文章列表中"
              maxlength="200"
              show-word-limit>
            </el-input>
          </el-form-item>
          
          <el-form-item label="文章内容" prop="content">
            <div class="content-editor-wrapper">
              <el-input 
                type="textarea" 
                v-model="articleForm.content" 
                rows="15" 
                placeholder="请输入文章内容，支持Markdown格式"
                class="content-editor">
              </el-input>
              <div class="markdown-tips">
                <el-popover
                  placement="right"
                  width="300"
                  trigger="hover">
                  <div class="markdown-cheatsheet">
                    <h4>Markdown 语法参考</h4>
                    <ul>
                      <li><code># 标题</code> - 一级标题</li>
                      <li><code>## 标题</code> - 二级标题</li>
                      <li><code>**粗体**</code> - <strong>粗体</strong></li>
                      <li><code>*斜体*</code> - <em>斜体</em></li>
                      <li><code>[链接](URL)</code> - <a href="#">链接</a></li>
                      <li><code>![图片](URL)</code> - 图片</li>
                      <li><code>- 列表项</code> - 无序列表</li>
                      <li><code>1. 列表项</code> - 有序列表</li>
                      <li><code>```代码块```</code> - 代码块</li>
                      <li><code>> 引用</code> - 引用文本</li>
                    </ul>
                  </div>
                  <el-button slot="reference" type="text" icon="el-icon-question" class="help-btn">
                    Markdown 帮助
                  </el-button>
                </el-popover>
              </div>
            </div>
          </el-form-item>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="封面图片">
                <div class="cover-upload-container">
                  <el-upload
                    class="cover-uploader"
                    :action="uploadUrl"
                    :headers="uploadHeaders"
                    :show-file-list="false"
                    :on-success="handleCoverSuccess"
                    :on-error="handleCoverError"
                    :before-upload="beforeCoverUpload"
                    accept="image/*">
                    <img v-if="articleForm.cover" :src="articleForm.cover" class="cover-image">
                    <div v-else class="cover-placeholder">
                      <i class="el-icon-picture-outline cover-uploader-icon"></i>
                      <div class="cover-uploader-text">点击上传封面图片</div>
                    </div>
                  </el-upload>
                  <div class="cover-tip">
                    <i class="el-icon-info"></i> 支持 JPG、PNG 格式，文件大小不超过 2MB
                    <el-button v-if="articleForm.cover" @click="removeCover" type="text" size="small" class="delete-cover-btn">
                      <i class="el-icon-delete"></i> 删除封面
                    </el-button>
                  </div>
                </div>
              </el-form-item>
            </el-col>
            
            <el-col :span="12">
              <el-form-item label="文章设置" class="article-settings">
                <div class="setting-item">
                  <span class="setting-label">
                    <i class="el-icon-top"></i> 置顶文章
                  </span>
                  <el-switch 
                    v-model="articleForm.isTop"
                    active-color="#13ce66"
                    inactive-color="#ff4949">
                  </el-switch>
                </div>
                <div class="setting-tip" v-if="articleForm.isTop">
                  <i class="el-icon-info"></i> 置顶文章将显示在文章列表顶部
                </div>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-form-item class="form-actions">
            <el-button 
              type="primary" 
              @click="submitForm" 
              :loading="loading"
              icon="el-icon-upload"
              size="medium"
              class="submit-btn">
              {{ isEditMode ? '更新文章' : (isAdmin ? '发布文章' : '提交审核') }}
            </el-button>
            <el-button 
              @click="resetForm"
              icon="el-icon-refresh"
              size="medium">
              重置
            </el-button>
            <el-button 
              @click="previewArticle"
              icon="el-icon-view"
              type="success"
              size="medium">
              预览
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
    
    <!-- 预览对话框 -->
    <el-dialog 
      title="文章预览" 
      :visible.sync="previewVisible" 
      width="70%" 
      class="preview-dialog"
      top="5vh">
      <div class="preview-content">
        <div class="preview-header">
          <h2>{{ articleForm.title }}</h2>
          <div class="preview-meta">
            <span class="meta-item"><i class="el-icon-user"></i> {{ user ? user.nickname : '未登录' }}</span>
            <span class="meta-item"><i class="el-icon-date"></i> {{ currentDate }}</span>
            <span class="meta-item"><i class="el-icon-collection-tag"></i> {{ getCategoryName(articleForm.categoryId) }}</span>
          </div>
        </div>
        
        <div class="preview-cover" v-if="articleForm.cover">
          <img :src="articleForm.cover" :alt="articleForm.title">
        </div>
        
        <div class="preview-summary">
          <i class="el-icon-info summary-icon"></i>
          {{ articleForm.summary }}
        </div>
        
        <div class="preview-divider"></div>
        
        <div class="preview-body markdown-body" v-html="renderedContent"></div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="previewVisible = false" icon="el-icon-close">关闭</el-button>
        <el-button 
          type="primary" 
          @click="submitForm" 
          icon="el-icon-upload"
          :loading="loading">
          {{ isEditMode ? '更新文章' : (isAdmin ? '发布文章' : '提交审核') }}
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import api from '../api'
import { mapGetters } from 'vuex'
import { marked } from 'marked'

export default {
  name: 'WriteArticle',
  data() {
    return {
      articleForm: {
        title: '',
        categoryId: '',
        summary: '',
        content: '',
        cover: '',
        isTop: false
      },
      uploadUrl: process.env.VUE_APP_BASE_API + '/upload/image',
      uploadHeaders: {
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      },
      rules: {
        title: [
          { required: true, message: '请输入文章标题', trigger: 'blur' },
          { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
        ],
        categoryId: [
          { required: true, message: '请选择文章分类', trigger: 'change' }
        ],
        summary: [
          { required: true, message: '请输入文章摘要', trigger: 'blur' },
          { min: 10, max: 200, message: '长度在 10 到 200 个字符', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入文章内容', trigger: 'blur' },
          { min: 20, message: '内容不能少于20个字符', trigger: 'blur' }
        ]
      },
      categories: [],
      loading: false,
      previewVisible: false,
      isEditMode: false,
      editingArticleId: null
    }
  },
  computed: {
    ...mapGetters(['user', 'isAuthenticated']),
    currentDate() {
      const now = new Date()
      return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`
    },
    renderedContent() {
      return this.articleForm.content ? marked(this.articleForm.content) : ''
    },
    // 判断当前用户是否为管理员
    isAdmin() {
      return this.user && this.user.roles && this.user.roles.includes('ROLE_ADMIN')
    }
  },
  created() {
    this.fetchCategories()
    
    // 检查用户是否已登录
    if (!this.isAuthenticated) {
      this.$message.warning('请先登录')
      this.$router.push('/login')
      return
    }
    
    // 更新上传头部
    this.updateUploadHeaders()
    
    // 检查是否是编辑模式
    const articleId = this.$route.query.id
    if (articleId) {
      this.loadArticleForEdit(articleId)
    }
  },
  methods: {
    async fetchCategories() {
      try {
        const response = await api.category.getAll()
        if (response.code === 200) {
          this.categories = response.data
        }
      } catch (error) {
        console.error('获取分类失败:', error)
        this.$message.error('获取分类失败')
      }
    },
    
    // 加载文章进行编辑
    async loadArticleForEdit(articleId) {
      try {
        this.loading = true
        const response = await api.article.getDetail(articleId)
        if (response.code === 200) {
          const article = response.data
          // 检查是否是当前用户的文章或者用户是管理员
          if (article.userId !== this.user.id && !this.isAdmin) {
            this.$message.error('您没有权限编辑此文章')
            this.$router.push('/article')
            return
          }
          
          // 填充表单数据
          this.articleForm = {
            title: article.title,
            categoryId: article.categoryId,
            summary: article.summary,
            content: article.content,
            cover: article.cover || '',
            isTop: article.isTop === 1
          }
          
          this.isEditMode = true
          this.editingArticleId = articleId
        } else {
          this.$message.error('文章不存在或已被删除')
          this.$router.push('/article')
        }
      } catch (error) {
        console.error('加载文章失败:', error)
        this.$message.error('加载文章失败')
        this.$router.push('/article')
      } finally {
        this.loading = false
      }
    },
    
    updateUploadHeaders() {
      const token = localStorage.getItem('token')
      if (token) {
        this.uploadHeaders = {
          'Authorization': 'Bearer ' + token
        }
      }
    },

    handleCoverSuccess(response) {
      if (response.code === 200) {
        this.articleForm.cover = response.data.url
        this.$message.success('封面上传成功')
      } else {
        this.$message.error(response.message || '上传失败')
      }
    },

    handleCoverError(error) {
      console.error('封面上传失败:', error)
      this.$message.error('封面上传失败')
    },

    removeCover() {
      this.articleForm.cover = ''
      this.$message.success('封面已删除')
    },
    
    beforeCoverUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2
      
      if (!isImage) {
        this.$message.error('只能上传图片文件!')
      }
      if (!isLt2M) {
        this.$message.error('图片大小不能超过 2MB!')
      }
      
      return isImage && isLt2M
    },
    
    submitForm() {
      this.$refs.articleForm.validate(async valid => {
        if (valid) {
          this.loading = true
          try {
            let response
            if (this.isEditMode) {
              // 编辑模式：更新文章
              response = await api.article.update(this.editingArticleId, {
                ...this.articleForm,
                isTop: this.articleForm.isTop ? 1 : 0,
                userId: this.user.id
              })
            } else {
              // 新建模式：添加文章
              response = await api.article.add({
                ...this.articleForm,
                isTop: this.articleForm.isTop ? 1 : 0,
                userId: this.user.id
              })
            }
            
            if (response.code === 200) {
              if (this.isEditMode) {
                this.$message.success('文章更新成功')
                // 跳转到文章详情页
                this.$router.push(`/article/${this.editingArticleId}`)
              } else {
                if (this.isAdmin) {
                  this.$message.success('文章发布成功')
                } else {
                  this.$message.success('文章提交成功，等待管理员审核')
                }
                this.previewVisible = false
                // 跳转到文章主页
                this.$router.push('/article')
              }
            } else {
              this.$message.error(response.message || (this.isEditMode ? '更新失败' : '发布失败'))
            }
          } catch (error) {
            console.error(this.isEditMode ? '更新文章失败:' : '发布文章失败:', error)
            this.$message.error(this.isEditMode ? '更新文章失败' : '发布文章失败')
          } finally {
            this.loading = false
          }
        } else {
          return false
        }
      })
    },
    
    resetForm() {
      this.$refs.articleForm.resetFields()
      this.articleForm.cover = ''
      this.articleForm.isTop = false
    },
    
    previewArticle() {
      this.$refs.articleForm.validate(valid => {
        if (valid) {
          this.previewVisible = true
        } else {
          return false
        }
      })
    },
    
    getCategoryName(categoryId) {
      const category = this.categories.find(item => item.id === categoryId)
      return category ? category.name : '未分类'
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
    }
  }
}
</script>

<style scoped>
.write-article-page {
  padding: 100px 0;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.article-editor-card {
  background: white;
  border-radius: 15px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  padding: 40px;
  margin-bottom: 40px;
}

.editor-header {
  margin-bottom: 30px;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 20px;
}

h1 {
  color: #2c3e50;
  margin-bottom: 20px;
  font-size: 2.2rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

h1 i {
  margin-right: 15px;
  font-size: 1.8rem;
}

.editor-tips {
  margin-bottom: 20px;
}

.article-form {
  margin-top: 30px;
}

.content-editor-wrapper {
  position: relative;
}

.content-editor {
  font-family: 'Courier New', monospace;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.markdown-tips {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 10;
}

.help-btn {
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 4px;
  padding: 5px 10px;
  font-size: 12px;
}

.markdown-cheatsheet {
  font-size: 13px;
}

.markdown-cheatsheet h4 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #409EFF;
}

.markdown-cheatsheet ul {
  padding-left: 15px;
  margin: 0;
}

.markdown-cheatsheet li {
  margin-bottom: 5px;
}

.markdown-cheatsheet code {
  background-color: #f0f0f0;
  padding: 2px 4px;
  border-radius: 3px;
  font-family: 'Courier New', monospace;
}

.cover-upload-container {
  display: flex;
  flex-direction: column;
}

.cover-uploader {
  border: 2px dashed #d9d9d9;
  border-radius: 12px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 280px;
  height: 160px;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: all 0.3s ease;
  background-color: #f9f9f9;
}

.cover-uploader:hover {
  border-color: #409EFF;
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.cover-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #8c939d;
}

.cover-uploader-icon {
  font-size: 36px;
  margin-bottom: 10px;
  color: #409EFF;
}

.cover-uploader-text {
  font-size: 14px;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 10px;
}

.cover-tip {
  font-size: 13px;
  color: #909399;
  margin-top: 10px;
  display: flex;
  align-items: center;
}

.cover-tip i {
  margin-right: 5px;
  color: #409EFF;
}

.delete-cover-btn {
  margin-left: 10px;
  color: #F56C6C;
}

.article-settings {
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 12px;
  height: 160px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.setting-label {
  font-size: 15px;
  display: flex;
  align-items: center;
}

.setting-label i {
  margin-right: 8px;
  color: #409EFF;
}

.setting-tip {
  font-size: 13px;
  color: #909399;
  margin-top: 10px;
}

.setting-tip i {
  color: #E6A23C;
  margin-right: 5px;
}

.form-actions {
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: center;
}

.submit-btn {
  padding-left: 25px;
  padding-right: 25px;
}

/* 预览对话框样式 */
.preview-dialog >>> .el-dialog {
  border-radius: 15px;
  overflow: hidden;
}

.preview-dialog >>> .el-dialog__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 15px 20px;
}

.preview-dialog >>> .el-dialog__title {
  color: white;
  font-weight: 600;
}

.preview-dialog >>> .el-dialog__headerbtn .el-dialog__close {
  color: white;
}

.preview-content {
  padding: 30px;
  max-height: 70vh;
  overflow-y: auto;
}

.preview-header {
  margin-bottom: 25px;
}

.preview-header h2 {
  font-size: 28px;
  color: #2c3e50;
  margin-bottom: 15px;
  font-weight: 600;
}

.preview-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  color: #909399;
  font-size: 14px;
  margin-bottom: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
}

.meta-item i {
  margin-right: 5px;
}

.preview-cover {
  margin-bottom: 25px;
  border-radius: 12px;
  overflow: hidden;
  max-height: 300px;
}

.preview-cover img {
  width: 100%;
  object-fit: cover;
}

.preview-summary {
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 25px;
  color: #606266;
  position: relative;
  padding-left: 40px;
}

.summary-icon {
  position: absolute;
  left: 15px;
  top: 20px;
  color: #409EFF;
  font-size: 18px;
}

.preview-divider {
  height: 1px;
  background: linear-gradient(to right, transparent, #dcdfe6, transparent);
  margin: 30px 0;
}

.preview-body {
  line-height: 1.8;
  color: #303133;
}

/* Markdown 样式 */
.markdown-body h1,
.markdown-body h2,
.markdown-body h3,
.markdown-body h4,
.markdown-body h5,
.markdown-body h6 {
  color: #2c3e50;
  margin-top: 24px;
  margin-bottom: 16px;
  font-weight: 600;
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
  margin-bottom: 16px;
}

.markdown-body img {
  max-width: 100%;
  box-sizing: content-box;
  background-color: #fff;
  border-radius: 3px;
}

.markdown-body code {
  padding: 0.2em 0.4em;
  margin: 0;
  font-size: 85%;
  background-color: rgba(27, 31, 35, 0.05);
  border-radius: 3px;
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
}

.markdown-body pre {
  padding: 16px;
  overflow: auto;
  font-size: 85%;
  line-height: 1.45;
  background-color: #f6f8fa;
  border-radius: 3px;
  margin-bottom: 16px;
}

.markdown-body pre code {
  padding: 0;
  margin: 0;
  font-size: 100%;
  background-color: transparent;
  border: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .article-editor-card {
    padding: 20px;
  }
  
  .preview-meta {
    flex-direction: column;
    gap: 10px;
  }
  
  .form-actions {
    flex-direction: column;
    gap: 10px;
    align-items: stretch;
  }
  
  .form-actions .el-button {
    margin-left: 0 !important;
    margin-bottom: 10px;
  }
  
  .cover-uploader {
    width: 100%;
  }
}
</style>