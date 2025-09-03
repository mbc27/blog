<template>
  <div class="write-container">
    <div class="write-header">
      <h1>{{ isEdit ? '编辑文章' : '写文章' }}</h1>
      <div class="header-actions">
        <el-button @click="saveDraft" :loading="saving">保存草稿</el-button>
        <el-button type="primary" @click="publishArticle" :loading="publishing">
          {{ isEdit ? '更新文章' : '发布文章' }}
        </el-button>
      </div>
    </div>

    <div class="write-content">
      <el-form :model="article" :rules="rules" ref="articleForm" label-width="100px">
        <!-- 文章标题 -->
        <el-form-item label="文章标题" prop="title">
          <el-input
            v-model="article.title"
            placeholder="请输入文章标题"
            maxlength="100"
            show-word-limit
            size="large"
          />
        </el-form-item>

        <!-- 文章摘要 -->
        <el-form-item label="文章摘要" prop="summary">
          <el-input
            v-model="article.summary"
            type="textarea"
            placeholder="请输入文章摘要（可选）"
            :rows="3"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <!-- 封面图片 -->
        <el-form-item label="封面图片">
          <div class="cover-upload">
            <el-upload
              class="cover-uploader"
              :action="uploadUrl"
              :headers="uploadHeaders"
              :show-file-list="false"
              :on-success="handleCoverSuccess"
              :on-error="handleCoverError"
              :before-upload="beforeCoverUpload"
              accept="image/*"
            >
              <img v-if="article.coverImage" :src="article.coverImage" class="cover-image" />
              <div v-else class="cover-placeholder">
                <i class="el-icon-plus cover-uploader-icon"></i>
                <div class="cover-uploader-text">点击上传封面图片</div>
              </div>
            </el-upload>
            <div class="cover-tips">
              <p>建议尺寸：800x450px，支持 JPG、PNG 格式，文件大小不超过 2MB</p>
              <el-button v-if="article.coverImage" @click="removeCover" type="text" size="small">
                <i class="el-icon-delete"></i> 删除封面
              </el-button>
            </div>
          </div>
        </el-form-item>

        <!-- 文章分类 -->
        <el-form-item label="文章分类" prop="categoryId">
          <el-select v-model="article.categoryId" placeholder="请选择分类" style="width: 200px;">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
          <el-button @click="showCategoryDialog = true" type="text" style="margin-left: 10px;">
            <i class="el-icon-plus"></i> 新建分类
          </el-button>
        </el-form-item>

        <!-- 文章标签 -->
        <el-form-item label="文章标签">
          <el-tag
            v-for="tag in article.tags"
            :key="tag"
            closable
            @close="removeTag(tag)"
            style="margin-right: 10px;"
          >
            {{ tag }}
          </el-tag>
          <el-input
            v-if="inputVisible"
            v-model="inputValue"
            ref="saveTagInput"
            size="small"
            @keyup.enter.native="handleInputConfirm"
            @blur="handleInputConfirm"
            style="width: 90px;"
          />
          <el-button v-else @click="showInput" size="small">
            <i class="el-icon-plus"></i> 添加标签
          </el-button>
        </el-form-item>

        <!-- 文章内容 -->
        <el-form-item label="文章内容" prop="content">
          <div class="editor-container">
            <div class="editor-toolbar">
              <el-button-group>
                <el-button @click="insertImage" size="small">
                  <i class="el-icon-picture"></i> 插入图片
                </el-button>
                <el-button @click="insertLink" size="small">
                  <i class="el-icon-link"></i> 插入链接
                </el-button>
                <el-button @click="insertCode" size="small">
                  <i class="el-icon-document"></i> 插入代码
                </el-button>
              </el-button-group>
            </div>
            <el-input
              v-model="article.content"
              type="textarea"
              placeholder="请输入文章内容，支持 Markdown 语法"
              :rows="20"
              class="content-editor"
            />
          </div>
        </el-form-item>

        <!-- 发布设置 -->
        <el-form-item label="发布设置">
          <el-checkbox v-model="article.isTop">置顶文章</el-checkbox>
          <el-checkbox v-model="article.allowComment" style="margin-left: 20px;">允许评论</el-checkbox>
        </el-form-item>
      </el-form>
    </div>

    <!-- 新建分类对话框 -->
    <el-dialog title="新建分类" :visible.sync="showCategoryDialog" width="400px">
      <el-form :model="newCategory" :rules="categoryRules" ref="categoryForm">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="newCategory.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类描述">
          <el-input v-model="newCategory.description" placeholder="请输入分类描述（可选）" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="showCategoryDialog = false">取消</el-button>
        <el-button type="primary" @click="createCategory" :loading="creatingCategory">创建</el-button>
      </div>
    </el-dialog>

    <!-- 图片上传对话框 -->
    <el-dialog title="上传图片" :visible.sync="showImageDialog" width="500px">
      <el-upload
        class="image-uploader"
        :action="uploadUrl"
        :headers="uploadHeaders"
        :on-success="handleImageSuccess"
        :on-error="handleImageError"
        :before-upload="beforeImageUpload"
        drag
        accept="image/*"
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">只能上传jpg/png文件，且不超过2MB</div>
      </el-upload>
    </el-dialog>
  </div>
</template>

<script>
import api from '../api'

export default {
  name: 'Write',
  data() {
    return {
      isEdit: false,
      article: {
        id: null,
        title: '',
        summary: '',
        content: '',
        coverImage: '',
        categoryId: null,
        tags: [],
        isTop: false,
        allowComment: true,
        status: 1 // 1: 已发布, 0: 草稿
      },
      categories: [],
      saving: false,
      publishing: false,
      inputVisible: false,
      inputValue: '',
      showCategoryDialog: false,
      showImageDialog: false,
      newCategory: {
        name: '',
        description: ''
      },
      creatingCategory: false,
      uploadUrl: process.env.VUE_APP_BASE_API + '/upload/image',
      uploadHeaders: {
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      },
      rules: {
        title: [
          { required: true, message: '请输入文章标题', trigger: 'blur' },
          { min: 1, max: 100, message: '标题长度在 1 到 100 个字符', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入文章内容', trigger: 'blur' }
        ],
        categoryId: [
          { required: true, message: '请选择文章分类', trigger: 'change' }
        ]
      },
      categoryRules: {
        name: [
          { required: true, message: '请输入分类名称', trigger: 'blur' }
        ]
      }
    }
  },
  async created() {
    // 检查是否是编辑模式
    if (this.$route.params.id) {
      this.isEdit = true
      await this.loadArticle(this.$route.params.id)
    }
    
    await this.loadCategories()
    
    // 更新上传头部的token
    this.updateUploadHeaders()
  },
  methods: {
    updateUploadHeaders() {
      const token = localStorage.getItem('token')
      if (token) {
        this.uploadHeaders = {
          'Authorization': 'Bearer ' + token
        }
      }
    },

    async loadCategories() {
      try {
        const response = await api.category.getAll()
        if (response.code === 200) {
          this.categories = response.data || []
        }
      } catch (error) {
        console.error('加载分类失败:', error)
        this.$message.error('加载分类失败')
      }
    },

    async loadArticle(id) {
      try {
        const response = await api.article.getById(id)
        if (response.code === 200) {
          this.article = { ...response.data }
          this.article.tags = this.article.tags || []
        }
      } catch (error) {
        console.error('加载文章失败:', error)
        this.$message.error('加载文章失败')
        this.$router.push('/write')
      }
    },

    async saveDraft() {
      this.saving = true
      try {
        this.article.status = 0 // 草稿状态
        await this.saveArticle()
        this.$message.success('草稿保存成功')
      } catch (error) {
        this.$message.error('保存草稿失败')
      } finally {
        this.saving = false
      }
    },

    async publishArticle() {
      // 表单验证
      const valid = await this.$refs.articleForm.validate().catch(() => false)
      if (!valid) {
        return
      }

      this.publishing = true
      try {
        this.article.status = 1 // 发布状态
        await this.saveArticle()
        this.$message.success(this.isEdit ? '文章更新成功' : '文章发布成功')
        this.$router.push('/article')
      } catch (error) {
        this.$message.error(this.isEdit ? '更新文章失败' : '发布文章失败')
      } finally {
        this.publishing = false
      }
    },

    async saveArticle() {
      const articleData = {
        ...this.article,
        tags: this.article.tags.join(',')
      }

      if (this.isEdit) {
        await api.article.update(articleData.id, articleData)
      } else {
        const response = await api.article.create(articleData)
        if (response.code === 200) {
          this.article.id = response.data.id
          this.isEdit = true
        }
      }
    },

    // 封面图片上传
    beforeCoverUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isImage) {
        this.$message.error('只能上传图片文件!')
        return false
      }
      if (!isLt2M) {
        this.$message.error('图片大小不能超过 2MB!')
        return false
      }
      
      this.updateUploadHeaders()
      return true
    },

    handleCoverSuccess(response) {
      if (response.code === 200) {
        this.article.coverImage = response.data.url
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
      this.article.coverImage = ''
    },

    // 内容图片上传
    insertImage() {
      this.showImageDialog = true
    },

    beforeImageUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isImage) {
        this.$message.error('只能上传图片文件!')
        return false
      }
      if (!isLt2M) {
        this.$message.error('图片大小不能超过 2MB!')
        return false
      }
      
      this.updateUploadHeaders()
      return true
    },

    handleImageSuccess(response) {
      if (response.code === 200) {
        const imageUrl = response.data.url
        const markdownImage = `![图片](${imageUrl})`
        this.insertTextAtCursor(markdownImage)
        this.showImageDialog = false
        this.$message.success('图片上传成功')
      } else {
        this.$message.error(response.message || '上传失败')
      }
    },

    handleImageError(error) {
      console.error('图片上传失败:', error)
      this.$message.error('图片上传失败')
    },

    insertTextAtCursor(text) {
      const textarea = this.$el.querySelector('.content-editor textarea')
      const start = textarea.selectionStart
      const end = textarea.selectionEnd
      const content = this.article.content
      
      this.article.content = content.substring(0, start) + text + content.substring(end)
      
      this.$nextTick(() => {
        textarea.focus()
        textarea.setSelectionRange(start + text.length, start + text.length)
      })
    },

    insertLink() {
      this.$prompt('请输入链接地址', '插入链接', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(({ value }) => {
        if (value) {
          const linkText = `[链接文字](${value})`
          this.insertTextAtCursor(linkText)
        }
      }).catch(() => {})
    },

    insertCode() {
      const codeBlock = '```\n代码内容\n```'
      this.insertTextAtCursor(codeBlock)
    },

    // 标签管理
    removeTag(tag) {
      this.article.tags.splice(this.article.tags.indexOf(tag), 1)
    },

    showInput() {
      this.inputVisible = true
      this.$nextTick(() => {
        this.$refs.saveTagInput.$refs.input.focus()
      })
    },

    handleInputConfirm() {
      const inputValue = this.inputValue
      if (inputValue && !this.article.tags.includes(inputValue)) {
        this.article.tags.push(inputValue)
      }
      this.inputVisible = false
      this.inputValue = ''
    },

    // 分类管理
    async createCategory() {
      const valid = await this.$refs.categoryForm.validate().catch(() => false)
      if (!valid) {
        return
      }

      this.creatingCategory = true
      try {
        const response = await api.category.create(this.newCategory)
        if (response.code === 200) {
          this.$message.success('分类创建成功')
          this.showCategoryDialog = false
          this.newCategory = { name: '', description: '' }
          await this.loadCategories()
          this.article.categoryId = response.data.id
        }
      } catch (error) {
        this.$message.error('创建分类失败')
      } finally {
        this.creatingCategory = false
      }
    }
  }
}
</script>

<style scoped>
.write-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background: #f5f5f5;
  min-height: 100vh;
}

.write-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 20px 30px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.write-header h1 {
  margin: 0;
  color: #2c3e50;
  font-size: 24px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.write-content {
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 封面上传样式 */
.cover-upload {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.cover-uploader {
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  width: 300px;
  height: 180px;
  overflow: hidden;
  position: relative;
  cursor: pointer;
  transition: border-color 0.3s;
}

.cover-uploader:hover {
  border-color: #409eff;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
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
  font-size: 28px;
  margin-bottom: 10px;
}

.cover-uploader-text {
  font-size: 14px;
}

.cover-tips {
  font-size: 12px;
  color: #999;
  line-height: 1.5;
}

/* 编辑器样式 */
.editor-container {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.editor-toolbar {
  background: #f5f7fa;
  padding: 10px;
  border-bottom: 1px solid #dcdfe6;
}

.content-editor {
  border: none;
}

.content-editor >>> .el-textarea__inner {
  border: none;
  border-radius: 0;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 14px;
  line-height: 1.6;
}

/* 图片上传对话框样式 */
.image-uploader {
  text-align: center;
}

.image-uploader >>> .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 100%;
}

.image-uploader >>> .el-upload:hover {
  border-color: #409eff;
}

/* 标签样式 */
.el-tag {
  margin-right: 10px;
  margin-bottom: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .write-container {
    padding: 10px;
  }
  
  .write-header {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .write-content {
    padding: 20px;
  }
  
  .cover-uploader {
    width: 100%;
    max-width: 300px;
  }
}
</style>