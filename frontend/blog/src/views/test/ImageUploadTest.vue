<template>
  <div class="image-upload-test">
    <el-card class="test-card">
      <div slot="header" class="clearfix">
        <span>图片上传和显示测试</span>
      </div>
      
      <!-- 文章封面上传测试 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <h3>文章封面上传测试</h3>
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            :before-upload="beforeUpload"
            :headers="uploadHeaders">
            <img v-if="coverImage" :src="getImageUrl(coverImage)" class="avatar" @error="handleImageError">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
          <div class="upload-tip">点击上传文章封面</div>
          <div v-if="coverImage" class="image-info">
            <p><strong>原始URL:</strong> {{ coverImage }}</p>
            <p><strong>处理后URL:</strong> {{ getImageUrl(coverImage) }}</p>
          </div>
        </el-col>
        
        <el-col :span="12">
          <h3>头像上传测试</h3>
          <el-upload
            class="avatar-uploader"
            action="/upload/avatar"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeUpload"
            :headers="uploadHeaders">
            <img v-if="avatarImage" :src="getImageUrl(avatarImage)" class="avatar" @error="handleImageError">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
          <div class="upload-tip">点击上传头像</div>
          <div v-if="avatarImage" class="image-info">
            <p><strong>原始URL:</strong> {{ avatarImage }}</p>
            <p><strong>处理后URL:</strong> {{ getImageUrl(avatarImage) }}</p>
          </div>
        </el-col>
      </el-row>
      
      <!-- 图片显示测试 -->
      <el-divider>图片显示测试</el-divider>
      <el-row :gutter="20">
        <el-col :span="8" v-for="(testUrl, index) in testUrls" :key="index">
          <el-card class="image-test-card">
            <div slot="header">
              <span>测试 {{ index + 1 }}</span>
            </div>
            <div class="test-image-container">
              <img :src="getImageUrl(testUrl)" class="test-image" @error="handleImageError">
            </div>
            <div class="test-info">
              <p><strong>测试URL:</strong></p>
              <p class="url-text">{{ testUrl }}</p>
              <p><strong>处理后:</strong></p>
              <p class="url-text">{{ getImageUrl(testUrl) }}</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 上传日志 -->
      <el-divider>上传日志</el-divider>
      <el-card class="log-card">
        <div slot="header">
          <span>上传响应日志</span>
          <el-button style="float: right; padding: 3px 0" type="text" @click="clearLogs">清空日志</el-button>
        </div>
        <div class="log-content">
          <div v-for="(log, index) in uploadLogs" :key="index" class="log-item">
            <span class="log-time">{{ log.time }}</span>
            <span class="log-type" :class="log.type">{{ log.type }}</span>
            <pre class="log-message">{{ log.message }}</pre>
          </div>
          <div v-if="uploadLogs.length === 0" class="no-logs">暂无日志</div>
        </div>
      </el-card>
    </el-card>
  </div>
</template>

<script>
import { getImageUrl, handleImageError } from '@/utils/imageUtils'

export default {
  name: 'ImageUploadTest',
  data() {
    return {
      coverImage: '',
      avatarImage: '',
      uploadUrl: '/upload/image',
      uploadHeaders: {
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      },
      uploadLogs: [],
      testUrls: [
        '/images/2025/09/15/test.jpg',
        'http://localhost:8080/images/2025/09/15/test.jpg',
        '/uploads/2025/09/15/test.jpg',
        '2025/09/15/test.jpg',
        '/api/images/2025/09/15/test.jpg'
      ]
    }
  },
  methods: {
    // 获取图片URL
    getImageUrl(url) {
      return getImageUrl(url)
    },
    
    // 处理图片错误
    handleImageError(event) {
      handleImageError(event)
    },
    
    // 上传前验证
    beforeUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isImage) {
        this.$message({
          message: '只能上传图片文件!',
          type: 'error',
          duration: 3000
        })
        return false
      }
      if (!isLt2M) {
        this.$message({
          message: '图片大小不能超过 2MB!',
          type: 'error',
          duration: 3000
        })
        return false
      }
      
      this.addLog('INFO', `开始上传文件: ${file.name}, 大小: ${(file.size / 1024).toFixed(2)}KB`)
      return true
    },
    
    // 封面上传成功
    handleCoverSuccess(res, file) {
      this.addLog('SUCCESS', `封面上传响应: ${JSON.stringify(res, null, 2)}`)
      
      if (res.code === 200) {
        this.coverImage = res.data.url
        this.$message({
          message: '封面上传成功!',
          type: 'success',
          duration: 3000
        })
        this.addLog('INFO', `封面URL设置为: ${this.coverImage}`)
      } else {
        this.$message({
          message: res.message || '封面上传失败',
          type: 'error',
          duration: 3000
        })
        this.addLog('ERROR', `封面上传失败: ${res.message}`)
      }
    },
    
    // 头像上传成功
    handleAvatarSuccess(res, file) {
      this.addLog('SUCCESS', `头像上传响应: ${JSON.stringify(res, null, 2)}`)
      
      if (res.code === 200) {
        this.avatarImage = res.data.url
        this.$message({
          message: '头像上传成功!',
          type: 'success',
          duration: 3000
        })
        this.addLog('INFO', `头像URL设置为: ${this.avatarImage}`)
      } else {
        this.$message({
          message: res.message || '头像上传失败',
          type: 'error',
          duration: 3000
        })
        this.addLog('ERROR', `头像上传失败: ${res.message}`)
      }
    },
    
    // 添加日志
    addLog(type, message) {
      const now = new Date()
      const time = now.toLocaleTimeString()
      this.uploadLogs.unshift({
        time,
        type,
        message
      })
      
      // 限制日志数量
      if (this.uploadLogs.length > 50) {
        this.uploadLogs = this.uploadLogs.slice(0, 50)
      }
    },
    
    // 清空日志
    clearLogs() {
      this.uploadLogs = []
    }
  },
  
  mounted() {
    this.addLog('INFO', '图片上传测试页面已加载')
    this.addLog('INFO', `当前环境: ${process.env.NODE_ENV}`)
    this.addLog('INFO', `API基础URL: ${process.env.VUE_APP_BASE_API}`)
  }
}
</script>

<style scoped>
.image-upload-test {
  padding: 20px;
}

.test-card {
  max-width: 1200px;
  margin: 0 auto;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 178px;
  height: 178px;
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
  object-fit: cover;
}

.upload-tip {
  margin-top: 10px;
  color: #666;
  font-size: 14px;
}

.image-info {
  margin-top: 15px;
  padding: 10px;
  background-color: #f5f5f5;
  border-radius: 4px;
  font-size: 12px;
}

.image-info p {
  margin: 5px 0;
  word-break: break-all;
}

.image-test-card {
  margin-bottom: 20px;
}

.test-image-container {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
}

.test-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.test-info {
  margin-top: 10px;
  font-size: 12px;
}

.url-text {
  word-break: break-all;
  background-color: #f0f0f0;
  padding: 5px;
  border-radius: 3px;
  margin: 5px 0;
}

.log-card {
  margin-top: 20px;
}

.log-content {
  max-height: 400px;
  overflow-y: auto;
}

.log-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 10px;
  padding: 8px;
  border-radius: 4px;
  background-color: #f9f9f9;
}

.log-time {
  color: #666;
  font-size: 12px;
  margin-right: 10px;
  min-width: 80px;
}

.log-type {
  font-weight: bold;
  margin-right: 10px;
  min-width: 60px;
  font-size: 12px;
}

.log-type.INFO {
  color: #409EFF;
}

.log-type.SUCCESS {
  color: #67C23A;
}

.log-type.ERROR {
  color: #F56C6C;
}

.log-message {
  flex: 1;
  margin: 0;
  font-size: 12px;
  white-space: pre-wrap;
  word-break: break-all;
}

.no-logs {
  text-align: center;
  color: #999;
  padding: 20px;
}
</style>