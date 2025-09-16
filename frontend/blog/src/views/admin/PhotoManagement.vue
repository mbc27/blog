<template>
  <div class="photo-management">
    <el-card class="el-card is-hover-shadow">
      <div slot="header" class="clearfix">
        <span>照片管理</span>
        <el-button style="float: right" type="primary" size="small" @click="handleAddPhoto">上传照片</el-button>
      </div>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="相册分类">
          <el-select v-model="searchForm.categoryId" placeholder="选择相册分类" clearable>
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.categoryName"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchPhotos">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      
      <div class="photo-list" v-loading="loading">
        <el-empty description="暂无照片" v-if="photos.length === 0"></el-empty>
        <div class="photo-grid" v-else>
          <div class="photo-item" v-for="(photo, index) in photos" :key="photo.id">
            <el-card class="el-card is-hover-shadow" :body-style="{ padding: '0px' }">
              <div class="photo-img-container">
                <img :src="getFullImageUrl(photo.url)" class="photo-img" @click="previewPhoto(index)" @error="handleImageError">
              </div>
              <div class="photo-info">
                <div class="photo-title">{{ photo.description || '无描述' }}</div>
                <div class="photo-category">{{ getCategoryName(photo.categoryId) }}</div>
                <div class="photo-actions">
                  <el-button type="text" size="mini" @click="handleEdit(photo)">编辑</el-button>
                  <el-button type="text" size="mini" style="color: #F56C6C" @click="handleDelete(photo)">删除</el-button>
                </div>
              </div>
            </el-card>
          </div>
        </div>
      </div>
      
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pagination.current"
          :page-sizes="[12, 24, 36, 48]"
          :page-size="pagination.size"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total">
        </el-pagination>
      </div>
    </el-card>

    <!-- 照片上传/编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px" :close-on-click-modal="false" :before-close="handleDialogClose">
      <el-form :model="photoForm" :rules="photoRules" ref="photoForm" label-width="100px">
        <el-form-item label="相册分类" prop="categoryId">
          <el-select v-model="photoForm.categoryId" placeholder="请选择相册分类">
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.categoryName"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="照片描述" prop="description">
          <el-input type="textarea" v-model="photoForm.description" :rows="3" placeholder="请输入照片描述"></el-input>
        </el-form-item>
        <el-form-item label="照片" prop="url" v-if="!photoForm.id">
          <el-upload
            class="upload-container"
            :action="uploadActionUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforePhotoUpload">
            <img v-if="photoForm.url" :src="getFullImageUrl(photoForm.url)" class="upload-img" @error="handleImageError">
            <i v-else class="el-icon-plus upload-icon"></i>
            <div class="upload-tip" v-if="!photoForm.url">点击上传照片</div>
          </el-upload>
        </el-form-item>
        <el-form-item label="照片" v-else>
          <img :src="getFullImageUrl(photoForm.url)" class="preview-img" @error="handleImageError">
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitPhoto" :loading="submitLoading">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 照片预览 -->
    <el-dialog :visible.sync="previewVisible" append-to-body width="800px" :close-on-click-modal="true">
      <img :src="previewUrl" alt="Preview Image" style="width: 100%;">
    </el-dialog>
  </div>
</template>

<script>
import api from '@/api'
import { mapGetters } from 'vuex'
import { getImageUrl, handleImageError } from '@/utils/imageUtils'

export default {
  name: 'PhotoManagement',
  data() {
    return {
      searchForm: {
        categoryId: ''
      },
      photos: [],
      categories: [],
      loading: false,
      pagination: {
        current: 1,
        size: 12,
        total: 0
      },
      dialogVisible: false,
      dialogTitle: '上传照片',
      photoForm: {
        id: null,
        categoryId: '',
        description: '',
        url: ''
      },
      photoRules: {
        categoryId: [
          { required: true, message: '请选择相册分类', trigger: 'change' }
        ],
        url: [
          { required: true, message: '请上传照片', trigger: 'change' }
        ]
      },
      submitLoading: false,
      previewVisible: false,
      previewUrl: '',
      uploadActionUrl: ''
    }
  },
  computed: {
    ...mapGetters(['token']),
    uploadHeaders() {
      return {
        'Authorization': `Bearer ${this.token}`
      }
    }
  },
  created() {
    this.fetchCategories()
    this.fetchPhotos()
  },
  mounted() {
    this.uploadActionUrl = process.env.VUE_APP_BASE_API + '/upload/image'
    this.fetchCategories()
    this.fetchPhotos()
  },
  methods: {
    // 获取完整的图片URL
    getFullImageUrl(url) {
      if (!url) return ''
      
      // 使用统一的图片URL处理函数
      const processedUrl = getImageUrl(url)
      
      // 添加随机参数防止缓存
      const timestamp = new Date().getTime()
      const separator = processedUrl.includes('?') ? '&' : '?'
      
      return processedUrl + separator + 't=' + timestamp
    },
    fetchCategories() {
      api.photo.getCategories()
        .then(response => {
          console.log('Categories response:', response)
          const data = response.data || []
          this.categories = Array.isArray(data) ? data.map(item => ({
            ...item,
            categoryName: item.categoryName || item.name
          })) : []
        })
        .catch(error => {
          console.error('获取相册分类失败', error)
          this.$message.error('获取相册分类失败')
        })
    },
    fetchPhotos() {
      this.loading = true
      const params = {
        current: this.pagination.current,
        size: this.pagination.size,
        ...this.searchForm
      }
      
      console.log('获取管理员照片列表，参数:', params)
      
      api.photo.getAdminList(params)
        .then(response => {
          console.log('管理员照片列表响应:', response)
          
          let photoData = []
          let totalCount = 0
          
          if (response.data) {
            if (Array.isArray(response.data.records)) {
              // 分页数据格式
              photoData = response.data.records
              totalCount = response.data.total || 0
            } else if (Array.isArray(response.data)) {
              // 直接数组格式
              photoData = response.data
              totalCount = photoData.length
            }
          }
          
          // 确保照片URL指向后端服务器
          this.photos = photoData.map(photo => ({
            ...photo,
            url: photo.url // URL会在模板中通过getFullImageUrl处理
          }))
          this.pagination.total = totalCount
          
          console.log('处理后的管理员照片数据:', this.photos)
        })
        .catch(error => {
          console.error('获取照片列表失败', error)
          this.$message.error('获取照片列表失败: ' + (error.message || '网络错误'))
          this.photos = []
          this.pagination.total = 0
        })
        .finally(() => {
          this.loading = false
        })
    },
    getCategoryName(categoryId) {
      const category = this.categories.find(item => item.id === categoryId)
      return category ? (category.categoryName || category.name) : '未分类'
    },
    searchPhotos() {
      this.pagination.current = 1
      this.fetchPhotos()
    },
    resetSearch() {
      this.searchForm = {
        categoryId: ''
      }
      this.searchPhotos()
    },
    handleSizeChange(val) {
      this.pagination.size = val
      this.fetchPhotos()
    },
    handleCurrentChange(val) {
      this.pagination.current = val
      this.fetchPhotos()
    },
    handleAddPhoto() {
      this.dialogTitle = '上传照片'
      this.photoForm = {
        id: null,
        categoryId: '',
        description: '',
        url: ''
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.photoForm && this.$refs.photoForm.clearValidate()
      })
    },
    handleEdit(photo) {
      this.dialogTitle = '编辑照片'
      this.photoForm = {
        id: photo.id,
        categoryId: photo.categoryId,
        description: photo.description,
        url: photo.url
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.photoForm && this.$refs.photoForm.clearValidate()
      })
    },
    handleDelete(photo) {
      this.$confirm('确认删除该照片吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        api.photo.delete(photo.id)
          .then(() => {
            this.$message.success('删除成功')
            this.fetchPhotos()
          })
          .catch(error => {
            console.error('删除照片失败', error)
            this.$message.error('删除照片失败')
          })
      }).catch(() => {})
    },
    handleUploadSuccess(res) {
      console.log('上传响应:', res);
      if (res.code === 200) {
        // 使用统一的图片URL处理函数
        let imageUrl = res.data?.url || res.data || '';
        this.photoForm.url = getImageUrl(imageUrl);
        this.$message.success('上传成功');
      } else {
        this.$message.error('上传失败: ' + (res.message || '未知错误'));
      }
    },
    beforePhotoUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/gif'
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG/PNG/GIF 格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!')
      }
      return isJPG && isLt2M
    },
    submitPhoto() {
      this.$refs.photoForm.validate((valid) => {
        if (valid) {
          this.submitLoading = true
          
          console.log('提交照片数据:', this.photoForm)
          
          const promise = this.photoForm.id 
            ? api.photo.update(this.photoForm.id, this.photoForm)
            : api.photo.add(this.photoForm)
            
          promise
            .then(response => {
              console.log('提交照片响应:', response)
              this.$message.success(this.photoForm.id ? '更新成功' : '添加成功')
              this.dialogVisible = false
              // 重置分页到第一页以确保能看到新添加的照片
              this.pagination.current = 1
              // 延迟刷新以确保后端数据已保存
              setTimeout(() => {
                this.fetchPhotos()
              }, 500)
            })
            .catch(error => {
              console.error('提交照片失败', error)
              const errorMsg = error.response?.data?.message || error.message || '未知错误'
              this.$message.error(this.photoForm.id ? '更新照片失败: ' + errorMsg : '添加照片失败: ' + errorMsg)
            })
            .finally(() => {
              this.submitLoading = false
            })
        }
      })
    },
    previewPhoto(index) {
      this.previewUrl = this.getFullImageUrl(this.photos[index].url)
      this.previewVisible = true
    },
    
    // 图片加载错误处理
    handleImageError(event) {
      handleImageError(event);
    },
    handleDialogClose() {
      this.dialogVisible = false
    }
  }
}
</script>

<style scoped>
.photo-management {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.photo-item {
  border-radius: 8px;
  overflow: hidden;
}

.photo-img-container {
  height: 150px;
  overflow: hidden;
}

.photo-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  cursor: pointer;
  transition: transform 0.3s;
}

.photo-img:hover {
  transform: scale(1.05);
}

.photo-info {
  padding: 10px;
}

.photo-title {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.photo-category {
  font-size: 12px;
  color: #999;
  margin-bottom: 10px;
}

.photo-actions {
  display: flex;
  justify-content: space-between;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.upload-container {
  width: 100px;
  height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.upload-container:hover {
  border-color: #409EFF;
}

.upload-icon {
  font-size: 28px;
  color: #8c939d;
  margin-bottom: 10px;
}

.upload-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-tip {
  color: #8c939d;
  font-size: 14px;
}

.preview-img {
  max-width: 200px;
  max-height: 200px;
}
</style>