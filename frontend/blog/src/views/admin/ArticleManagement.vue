<template>
  <div class="article-management">
    <!-- 搜索和操作栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="请输入文章标题搜索"
        style="width: 300px; margin-right: 10px;"
        @keyup.enter="searchArticles"
      >
        <el-button slot="append" icon="el-icon-search" @click="searchArticles"></el-button>
      </el-input>
      <el-button type="primary" @click="openAddDialog">新增文章</el-button>
    </div>

    <!-- 文章列表 -->
    <el-table :data="articleList" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="title" label="标题" min-width="200"></el-table-column>
      <el-table-column prop="categoryName" label="分类" width="120"></el-table-column>
      <el-table-column prop="tags" label="标签" width="150">
        <template slot-scope="scope">
          <el-tag v-for="(tag, index) in scope.row.tags" :key="index" size="mini" style="margin-right: 5px;">
            {{ tag }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="viewCount" label="浏览量" width="100"></el-table-column>
      <el-table-column prop="isTop" label="置顶" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isTop ? 'success' : 'info'" size="mini">
            {{ scope.row.isTop ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'warning'" size="mini">
            {{ scope.row.status === 1 ? '发布' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" @click="editArticle(scope.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="deleteArticle(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>
    </div>

    <!-- 文章编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="80%" :before-close="handleDialogClose" :close-on-click-modal="false">
      <el-form :model="articleForm" :rules="articleRules" ref="articleForm" label-width="100px">
        <el-form-item label="文章标题" prop="title">
          <el-input v-model="articleForm.title" placeholder="请输入文章标题"></el-input>
        </el-form-item>
        <el-form-item label="文章分类" prop="categoryId">
          <el-select v-model="articleForm.categoryId" placeholder="请选择分类" style="width: 100%;">
            <el-option
              v-for="category in categoryList"
              :key="category.id"
              :label="category.name"
              :value="category.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="文章标签">
          <el-select
            v-model="articleForm.tags"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="请选择或输入标签"
            style="width: 100%;">
            <el-option
              v-for="(tag, index) in tagList"
              :key="index"
              :label="tag.name || tag"
              :value="tag.name || tag">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="文章摘要">
          <el-input
            type="textarea"
            :rows="3"
            placeholder="请输入文章摘要"
            v-model="articleForm.summary">
          </el-input>
        </el-form-item>
        <el-form-item label="文章内容" prop="content">
          <el-input
            type="textarea"
            v-model="articleForm.content"
            :rows="15"
            placeholder="请输入文章内容（支持Markdown格式）">
          </el-input>
        </el-form-item>
        <el-form-item label="文章封面">
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            :before-upload="beforeCoverUpload"
            :headers="uploadHeaders"
            :on-error="handleUploadError"
            :http-request="customUpload">
            <img v-if="articleForm.coverImage" :src="getImageUrl(articleForm.coverImage)" class="avatar" @error="handleImageError" ref="coverImage">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
          <div v-if="imageError" class="image-error-tip">图片加载失败，请检查图片链接是否有效</div>
        </el-form-item>
        <el-form-item label="是否置顶">
          <el-switch v-model="articleForm.isTop" :active-value="1" :inactive-value="0"></el-switch>
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
import api from '@/api'
import { getImageUrl, handleImageError } from '@/utils/imageUtils'

export default {
  name: 'ArticleManagement',
  data() {
    return {
      // 搜索关键词
      searchKeyword: '',
      // 文章列表
      articleList: [],
      // 分页信息
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      // 对话框
      dialogVisible: false,
      dialogTitle: '新增文章',
      isEdit: false,
      // 图片错误状态
      imageError: false,
      // 保存最后一次上传响应
      lastUploadResponse: null,
      // 文章表单
      articleForm: {
        id: null,
        title: '',
        categoryId: null,
        tags: [],
        summary: '',
        content: '',
        coverImage: '',
        isTop: 0,
        status: 1
      },
      // 表单验证规则
      articleRules: {
        title: [
          { required: true, message: '请输入文章标题', trigger: 'blur' }
        ],
        categoryId: [
          { required: true, message: '请选择文章分类', trigger: 'change' }
        ],
        content: [
          { required: true, message: '请输入文章内容', trigger: 'blur' }
        ]
      },
      // 分类列表
      categoryList: [],
      // 标签列表
      tagList: [],
      // 提交loading
      submitLoading: false,
      // 上传相关
      uploadUrl: '/upload/image',
      uploadHeaders: {
        'Authorization': 'Bearer ' + this.$store.getters.token
      }
    }
  },
  created() {
    this.getArticleList()
    this.getCategoryList()
    this.getTagList()
  },
  methods: {
    // 自定义上传方法
    customUpload(options) {
      const formData = new FormData()
      formData.append('file', options.file)
      
      // 使用axios实例发送请求
      this.$axios.post('/upload/image', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
          'Authorization': 'Bearer ' + this.$store.getters.token
        }
      }).then(response => {
        if (response.data && response.data.code === 200) {
          options.onSuccess(response.data, options.file)
        } else {
          options.onError(new Error(response.data?.message || '上传失败'))
        }
      }).catch(error => {
        console.error('上传错误:', error)
        options.onError(error)
      })
    },
    
    // 获取文章列表
    async getArticleList() {
      this.loading = true
      try {
        const params = {
          page: this.currentPage,
          size: this.pageSize,
          keyword: this.searchKeyword
        }
        const response = await api.article.getList(params)
        this.articleList = response.data.records
        this.total = response.data.total
      } catch (error) {
        this.$message.error('获取文章列表失败')
      } finally {
        this.loading = false
      }
    },
    
    // 获取分类列表
    async getCategoryList() {
      try {
        const response = await api.category.getAll()
        this.categoryList = response.data
      } catch (error) {
        console.error('获取分类列表失败:', error)
      }
    },
    
    // 获取标签列表
    async getTagList() {
      try {
        const response = await api.tag.getAll()
        this.tagList = response.data
      } catch (error) {
        console.error('获取标签列表失败:', error)
      }
    },
    
    // 搜索文章
    searchArticles() {
      this.currentPage = 1
      this.getArticleList()
    },
    
    // 分页大小改变
    handleSizeChange(val) {
      this.pageSize = val
      this.getArticleList()
    },
    
    // 当前页改变
    handleCurrentChange(val) {
      this.currentPage = val
      this.getArticleList()
    },
    
    // 打开新增对话框
    openAddDialog() {
      this.dialogTitle = '新增文章'
      this.isEdit = false
      this.resetForm()
      this.dialogVisible = true
    },
    
    // 编辑文章
    editArticle(row) {
      this.dialogTitle = '编辑文章'
      this.isEdit = true
      this.articleForm = { ...row }
      this.dialogVisible = true
    },
    
    // 删除文章
    async deleteArticle(id) {
      try {
        await this.$confirm('此操作将永久删除该文章, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await api.article.delete(id)
        this.$message.success('删除成功')
        this.getArticleList()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    },
    
    // 提交文章
    async submitArticle() {
      try {
        // 检查分类是否已选择
        if (!this.articleForm.categoryId) {
          this.$message.error('请选择文章分类');
          return;
        }
        
        // 表单验证
        await this.$refs.articleForm.validate();
        
        if (!this.articleForm.content) {
          this.$message.error('请输入文章内容');
          return;
        }
        
        this.submitLoading = true;
        console.log('提交文章数据:', JSON.stringify(this.articleForm));
        
        if (this.isEdit) {
          await api.article.update(this.articleForm.id, this.articleForm);
          this.$message.success('更新成功');
        } else {
          await api.article.add(this.articleForm);
          this.$message.success('添加成功');
        }
        
        this.dialogVisible = false;
        this.getArticleList();
      } catch (error) {
        console.error('提交文章失败:', error);
        if (error.message) {
          this.$message.error(error.message);
        } else {
          this.$message.error('提交失败，请检查表单');
        }
      } finally {
        this.submitLoading = false;
      }
    },
    
    // 重置表单
    resetForm() {
      this.articleForm = {
        id: null,
        title: '',
        categoryId: null,
        tags: [],
        summary: '',
        content: '',
        coverImage: '',
        isTop: 0,
        status: 1
      }
      if (this.$refs.articleForm) {
        this.$refs.articleForm.resetFields()
      }
    },
    
    // 对话框关闭前的回调
    handleDialogClose(done) {
      this.$confirm('确认关闭？')
        .then(() => {
          done()
        })
        .catch(() => {})
    },
    
    // 封面上传成功
    handleCoverSuccess(res) {
      console.log('上传响应:', JSON.stringify(res));
      
      if (res.code === 200) {
        // 重置图片错误状态
        this.imageError = false;
        
        // 获取URL并使用工具函数处理
        let imageUrl = res.data.url;
        console.log('原始图片URL:', imageUrl);
        
        // 使用统一的图片URL处理函数
        this.articleForm.coverImage = this.getImageUrl(imageUrl);
        console.log('处理后的图片URL:', this.articleForm.coverImage);
        
        this.$message.success('封面上传成功');
      } else {
        this.$message.error(res.message || '上传失败');
      }
    },
    
    // 上传错误处理
    handleUploadError(err) {
      console.error('上传错误:', err)
      this.$message.error('图片上传失败，请重试')
      this.imageError = true
    },
    
    // 封面上传前的校验
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
    },
    
    // 图片加载错误处理
    handleImageError(event) {
      this.imageError = true;
      handleImageError(event);
    },
    
    // 获取图片URL
    getImageUrl(url) {
      return getImageUrl(url);
    }
  }
}
</script>

<style scoped>
.article-management {
  padding: 20px;
}

.search-bar {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
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

.dialog-footer {
  text-align: right;
}

.image-error-tip {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 5px;
}
</style>