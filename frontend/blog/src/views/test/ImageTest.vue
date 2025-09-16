<template>
  <div class="image-test">
    <h1>图片URL测试</h1>
    
    <div class="test-section">
      <h2>1. 服务器信息</h2>
      <el-button type="primary" @click="getServerInfo">获取服务器信息</el-button>
      <el-button type="primary" @click="getServerIp">获取服务器IP</el-button>
      
      <div v-if="serverInfo" class="result-box">
        <h3>服务器信息：</h3>
        <pre>{{ JSON.stringify(serverInfo, null, 2) }}</pre>
      </div>
      
      <div v-if="serverIp" class="result-box">
        <h3>服务器IP：</h3>
        <pre>{{ serverIp }}</pre>
      </div>
    </div>
    
    <div class="test-section">
      <h2>2. 图片URL测试</h2>
      <el-input v-model="imageUrl" placeholder="输入图片URL" style="width: 400px; margin-right: 10px;"></el-input>
      <el-button type="primary" @click="testImage">测试图片</el-button>
      <el-button type="success" @click="processImage">处理图片URL</el-button>
      <el-button type="warning" @click="tryMultipleUrls">尝试多种URL</el-button>
      
      <div class="image-preview">
        <h3>图片预览：</h3>
        <img v-if="imageUrl" :src="imageUrl" @error="handleImageError" class="test-image" />
        <div v-else class="no-image">请输入图片URL</div>
      </div>
      
      <div v-if="processedUrl" class="result-box">
        <h3>处理后的URL：</h3>
        <pre>{{ processedUrl }}</pre>
      </div>
      
      <div v-if="urlTestResults.length > 0" class="result-box">
        <h3>URL测试结果：</h3>
        <div v-for="(result, index) in urlTestResults" :key="index" class="url-test-result">
          <div :class="['status', result.success ? 'success' : 'error']">
            {{ result.success ? '✓' : '✗' }}
          </div>
          <div class="url">{{ result.url }}</div>
        </div>
      </div>
    </div>
    
    <div class="test-section">
      <h2>3. 图片上传测试</h2>
      <el-upload
        class="avatar-uploader"
        :action="uploadUrl"
        :show-file-list="false"
        :on-success="handleUploadSuccess"
        :before-upload="beforeUpload"
        :headers="uploadHeaders"
        :on-error="handleUploadError">
        <img v-if="uploadedImageUrl" :src="uploadedImageUrl" class="avatar" @error="handleUploadedImageError">
        <i v-else class="el-icon-plus avatar-uploader-icon"></i>
      </el-upload>
      
      <div v-if="uploadResponse" class="result-box">
        <h3>上传响应：</h3>
        <pre>{{ JSON.stringify(uploadResponse, null, 2) }}</pre>
      </div>
    </div>
  </div>
</template>

<script>
// 移除对已删除的ipUtils.js的引用

export default {
  name: 'ImageTest',
  data() {
    return {
      serverInfo: null,
      serverIp: null,
      imageUrl: '',
      processedUrl: '',
      urlTestResults: [],
      uploadedImageUrl: '',
      uploadResponse: null,
      uploadUrl: (process.env.VUE_APP_BASE_API || '') + '/upload/image',
      uploadHeaders: {
        'Authorization': 'Bearer ' + this.$store.getters.token
      }
    };
  },
  methods: {
    // 获取服务器信息
    async getServerInfo() {
      try {
        const response = await fetch('/system/server-info');
        const data = await response.json();
        this.serverInfo = data;
        console.log('服务器信息:', data);
      } catch (error) {
        console.error('获取服务器信息失败:', error);
        this.$message.error('获取服务器信息失败');
      }
    },
    
    // 获取服务器IP
    async getServerIp() {
      try {
        const response = await fetch('/system/ip');
        const data = await response.json();
        this.serverIp = data.data;
        console.log('服务器IP:', data);
      } catch (error) {
        console.error('获取服务器IP失败:', error);
        this.$message.error('获取服务器IP失败');
      }
    },
    
    // 测试图片
    async testImage() {
      if (!this.imageUrl) {
        this.$message.warning('请输入图片URL');
        return;
      }
      
      // 使用Image对象测试图片是否可访问
      const success = await this.testImageUrl(this.imageUrl);
      if (success) {
        this.$message.success('图片可访问');
      } else {
        this.$message.error('图片不可访问');
      }
    },
    
    // 处理图片URL
    async processImage() {
      if (!this.imageUrl) {
        this.$message.warning('请输入图片URL');
        return;
      }
      
      try {
        // 处理图片URL，确保使用localhost
        let processed = this.imageUrl;
        
        // 如果是相对路径，添加localhost前缀
        if (processed.startsWith('/')) {
          processed = 'http://localhost:8080' + processed;
        }
        
        // 如果包含IP地址，替换为localhost
        const ipRegex = /https?:\/\/\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}(:\d+)?/;
        if (ipRegex.test(processed)) {
          processed = processed.replace(ipRegex, 'http://localhost:8080');
        }
        
        this.processedUrl = processed;
        this.imageUrl = processed;
        this.$message.success('URL处理成功');
      } catch (error) {
        console.error('处理图片URL失败:', error);
        this.$message.error('处理图片URL失败');
      }
    },
    
    // 尝试多种URL
    async tryMultipleUrls() {
      if (!this.imageUrl) {
        this.$message.warning('请输入图片URL');
        return;
      }
      
      this.urlTestResults = [];
      
      // 清除缓存参数
      let cleanUrl = this.imageUrl;
      if (cleanUrl.includes('?')) {
        cleanUrl = cleanUrl.split('?')[0];
      }
      
      // 准备多种URL格式
      const urlVariations = [];
      
      // 1. 原始URL
      urlVariations.push(cleanUrl);
      
      // 2. 使用localhost的URL
      const localhostUrl = cleanUrl.replace(/\b\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}(:\d+)?\b/g, 'localhost:8080');
      if (localhostUrl !== cleanUrl) {
        urlVariations.push(localhostUrl);
      }
      
      // 3. 相对路径
      if (cleanUrl.includes('/images/')) {
        const relativePath = '/images/' + cleanUrl.split('/images/')[1];
        urlVariations.push(relativePath);
      }
      
      // 4. 完整路径
      if (!cleanUrl.startsWith('http')) {
        const fullUrl = 'http://localhost:8080' + (cleanUrl.startsWith('/') ? '' : '/') + cleanUrl;
        urlVariations.push(fullUrl);
      }
      
      // 测试所有URL
      for (const url of urlVariations) {
        const success = await this.testImageUrl(url);
        this.urlTestResults.push({
          url,
          success
        });
        
        if (success) {
          this.imageUrl = url;
        }
      }
    },
    
    // 测试图片URL是否可访问
    testImageUrl(url) {
      return new Promise((resolve) => {
        if (!url) {
          resolve(false);
          return;
        }
        
        // 添加时间戳防止缓存
        const testUrl = url + (url.includes('?') ? '&' : '?') + 't=' + new Date().getTime();
        console.log('测试图片URL:', testUrl);
        
        const img = new Image();
        img.onload = () => {
          console.log('图片加载成功:', testUrl);
          resolve(true);
        };
        img.onerror = () => {
          console.log('图片加载失败:', testUrl);
          resolve(false);
        };
        img.src = testUrl;
        
        // 设置超时
        setTimeout(() => {
          console.log('图片加载超时:', testUrl);
          resolve(false);
        }, 3000);
      });
    },
    
    // 处理图片加载错误
    handleImageError(event) {
      console.error('图片加载失败:', event.target.src);
      
      // 设置默认样式
      event.target.style.backgroundColor = '#f5f5f5';
      event.target.style.display = 'flex';
      event.target.style.alignItems = 'center';
      event.target.style.justifyContent = 'center';
      event.target.style.color = '#999';
      event.target.innerHTML = '<span>图片加载失败</span>';
      
      // 尝试使用localhost URL
      const originalUrl = this.imageUrl;
      let fixedUrl = originalUrl;
      
      // 如果是相对路径，添加localhost前缀
      if (originalUrl.startsWith('/')) {
        fixedUrl = 'http://localhost:8080' + originalUrl;
      } 
      // 如果包含IP地址，替换为localhost
      else if (originalUrl.match(/\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}/)) {
        fixedUrl = originalUrl.replace(/https?:\/\/\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}(:\d+)?/g, 'http://localhost:8080');
      }
      
      if (fixedUrl !== originalUrl) {
        console.log('尝试修复后的URL:', fixedUrl);
        this.tryImageUrl(fixedUrl, (success) => {
          if (success) {
            this.imageUrl = fixedUrl;
            event.target.src = fixedUrl;
            event.target.style = '';
            event.target.innerHTML = '';
          }
        });
      }
    },
    
    // 尝试加载图片URL
    tryImageUrl(url, callback) {
      const img = new Image();
      img.onload = () => {
        console.log('修复后的URL可用:', url);
        callback(true);
      };
      img.onerror = () => {
        console.log('修复后的URL仍然不可用:', url);
        callback(false);
      };
      
      // 添加时间戳防止缓存
      const urlWithTimestamp = url + (url.includes('?') ? '&' : '?') + 't=' + new Date().getTime();
      img.src = urlWithTimestamp;
    },
    
    // 上传成功处理
    handleUploadSuccess(res) {
      console.log('上传响应:', res);
      this.uploadResponse = res;
      
      if (res.code === 200) {
        // 获取URL并确保使用localhost
        let imageUrl = res.data.url;
        
        // 如果是相对路径，添加localhost前缀
        if (imageUrl.startsWith('/')) {
          imageUrl = 'http://localhost:8080' + imageUrl;
        }
        
        // 如果包含IP地址，替换为localhost
        const ipRegex = /https?:\/\/\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}(:\d+)?/;
        if (ipRegex.test(imageUrl)) {
          imageUrl = imageUrl.replace(ipRegex, 'http://localhost:8080');
        }
        
        this.uploadedImageUrl = imageUrl;
      } else {
        this.$message.error(res.message || '上传失败');
      }
    },
    
    // 上传错误处理
    handleUploadError(err) {
      console.error('上传错误:', err);
      this.$message.error('图片上传失败');
    },
    
    // 上传前校验
    beforeUpload(file) {
      const isImage = file.type.startsWith('image/');
      const isLt2M = file.size / 1024 / 1024 < 2;
      
      if (!isImage) {
        this.$message.error('只能上传图片文件!');
      }
      if (!isLt2M) {
        this.$message.error('图片大小不能超过 2MB!');
      }
      return isImage && isLt2M;
    },
    
    // 处理上传图片加载错误
    handleUploadedImageError(event) {
      console.error('上传图片加载失败:', event.target.src);
      
      // 设置默认样式
      event.target.style.backgroundColor = '#f5f5f5';
      event.target.style.display = 'flex';
      event.target.style.alignItems = 'center';
      event.target.style.justifyContent = 'center';
      event.target.style.color = '#999';
      event.target.innerHTML = '<span>图片加载失败</span>';
      
      // 尝试使用localhost URL
      if (this.uploadResponse && this.uploadResponse.data && this.uploadResponse.data.url) {
        const originalUrl = this.uploadResponse.data.url;
        
        // 如果是相对路径，添加localhost前缀
        if (originalUrl.startsWith('/')) {
          const fixedUrl = 'http://localhost:8080' + originalUrl;
          console.log('尝试使用完整localhost URL:', fixedUrl);
          
          this.tryImageUrl(fixedUrl, (success) => {
            if (success) {
              this.uploadedImageUrl = fixedUrl;
              event.target.src = fixedUrl;
              event.target.style = '';
              event.target.innerHTML = '';
            }
          });
        } 
        // 如果包含IP地址，替换为localhost
        else if (originalUrl.match(/\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}/)) {
          const fixedUrl = originalUrl.replace(/https?:\/\/\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}(:\d+)?/g, 'http://localhost:8080');
          console.log('尝试将IP替换为localhost:', fixedUrl);
          
          this.tryImageUrl(fixedUrl, (success) => {
            if (success) {
              this.uploadedImageUrl = fixedUrl;
              event.target.src = fixedUrl;
              event.target.style = '';
              event.target.innerHTML = '';
            }
          });
        }
      }
    }
  }
};
</script>

<style scoped>
.image-test {
  padding: 20px;
}

.test-section {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.result-box {
  margin-top: 15px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.image-preview {
  margin-top: 15px;
}

.test-image {
  max-width: 300px;
  max-height: 300px;
  border: 1px solid #dcdfe6;
}

.no-image {
  width: 300px;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  border: 1px dashed #dcdfe6;
  color: #909399;
}

.url-test-result {
  display: flex;
  align-items: center;
  margin-bottom: 5px;
}

.status {
  width: 20px;
  margin-right: 10px;
  font-weight: bold;
}

.status.success {
  color: #67c23a;
}

.status.error {
  color: #f56c6c;
}

.url {
  word-break: break-all;
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