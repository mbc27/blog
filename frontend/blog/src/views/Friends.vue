<template>
  <div class="friends-page page-container">
    <div class="container">
      <h1>友情链接</h1>
      <div class="friends-intro">
        <p>这里是我的朋友们，他们都是很棒的博主，欢迎访问他们的网站！</p>
        <el-button type="primary" @click="dialogVisible = true">申请友链</el-button>
      </div>
      
      <div class="friends-grid">
        <div class="friend-card" v-for="(friend, index) in friends" :key="index">
          <div class="friend-avatar">
            <div v-if="isValidImageUrl(friend.avatar)" class="avatar-image">
              <img :src="friend.avatar" :alt="friend.name" @error="handleImageError($event, friend)">
            </div>
            <div class="avatar-text" :style="{ display: isValidImageUrl(friend.avatar) ? 'none' : 'flex' }">
              {{ getAvatarText(friend.name) }}
            </div>
          </div>
          <div class="friend-info">
            <h3>{{ friend.name }}</h3>
            <p>{{ friend.description }}</p>
            <a :href="friend.url" target="_blank" class="friend-link">
              <i class="el-icon-link"></i> 访问
            </a>
          </div>
        </div>
      </div>
      
      <!-- 申请友链对话框 -->
      <el-dialog
        title="申请友链"
        :visible.sync="dialogVisible"
        width="500px"
        :close-on-click-modal="false"
        :before-close="handleDialogClose">
        <el-form :model="linkForm" :rules="rules" ref="linkForm" label-width="100px">
          <el-form-item label="网站名称" prop="name">
            <el-input v-model="linkForm.name"></el-input>
          </el-form-item>
          <el-form-item label="网站链接" prop="url">
            <el-input v-model="linkForm.url"></el-input>
          </el-form-item>
          <el-form-item label="网站描述" prop="description">
            <el-input v-model="linkForm.description"></el-input>
          </el-form-item>
          <el-form-item label="网站图标" prop="avatar">
            <el-input v-model="linkForm.avatar"></el-input>
          </el-form-item>
          <el-form-item label="联系方式" prop="contact">
            <el-input v-model="linkForm.contact" placeholder="邮箱或其他联系方式"></el-input>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitLinkApplication">提 交</el-button>
        </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import api from '../api'

export default {
  name: 'Friends',
  data() {
    return {
      dialogVisible: false,
      linkForm: {
        name: '',
        url: '',
        description: '',
        avatar: '',
        contact: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入网站名称', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        url: [
          { required: true, message: '请输入网站链接', trigger: 'blur' },
          { pattern: /^https?:\/\//, message: '请输入有效的URL', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '请输入网站描述', trigger: 'blur' },
          { max: 100, message: '长度不能超过100个字符', trigger: 'blur' }
        ],
        avatar: [
          { required: true, message: '请输入网站图标链接', trigger: 'blur' }
        ],
        contact: [
          { required: true, message: '请输入联系方式', trigger: 'blur' }
        ]
      },
      friends: [],
      loading: false
    }
  },
  created() {
    this.fetchFriendLinks()
  },
  mounted() {
    // 确保组件完全挂载
    this.$nextTick(() => {
      console.log('Friends component mounted')
    })
  },
  methods: {
    // 检查是否为有效的图片URL
    isValidImageUrl(url) {
      if (!url || url.trim() === '') {
        return false
      }
      
      // 只有以http://或https://开头的完整URL才被认为是有效的图片URL
      return url.startsWith('http://') || url.startsWith('https://')
    },
    
    // 获取文字头像显示内容
    getAvatarText(name) {
      if (!name || name.trim() === '') {
        return '?'
      }
      
      // 如果是中文，取前两个字符
      if (/[\u4e00-\u9fa5]/.test(name)) {
        return name.substring(0, 2)
      }
      
      // 如果是英文，取前两个字母并转大写
      return name.substring(0, 2).toUpperCase()
    },
    
    // 处理头像URL（保留原方法以防需要）
    processAvatarUrl(avatar) {
      return avatar
    },
    
    async fetchFriendLinks() {
      try {
        this.loading = true
        console.log('开始获取友链列表...')
        const response = await api.friendLink.getList()
        console.log('友链列表响应:', response)
        if (response.code === 200) {
          this.friends = response.data.map(link => ({
            id: link.id,
            name: link.name,
            description: link.description,
            avatar: this.processAvatarUrl(link.avatar),
            url: link.url
          }))
          console.log('友链列表处理完成:', this.friends)
        } else {
          console.error('获取友链列表失败，响应码:', response.code)
          this.$message({
            message: response.message || '获取友链列表失败',
            type: 'error',
            duration: 3000
          })
        }
      } catch (error) {
        console.error('获取友链列表失败:', error)
        this.$message({
          message: '获取友链列表失败: ' + (error.message || '网络错误'),
          type: 'error',
          duration: 3000
        })
        // 设置一些默认的友链数据以防止页面空白
        this.friends = []
      } finally {
        this.loading = false
      }
    },
    
    submitLinkApplication() {
      // 添加安全检查
      if (!this.$refs.linkForm) {
        this.$message({
          message: '表单未准备就绪，请稍后再试',
          type: 'error',
          duration: 3000
        })
        return
      }
      
      this.$refs.linkForm.validate(async (valid) => {
        if (valid) {
          try {
            const response = await api.friendLink.apply(this.linkForm)
            if (response.code === 200) {
              this.$message({
                message: '友链申请提交成功，请等待审核！',
                type: 'success',
                duration: 3000
              })
              this.dialogVisible = false
              this.linkForm = {
                name: '',
                url: '',
                description: '',
                avatar: '',
                contact: ''
              }
              // 重新获取友链列表
              this.fetchFriendLinks()
            }
          } catch (error) {
            console.error('提交友链申请失败:', error)
            this.$message({
              message: '提交友链申请失败',
              type: 'error',
              duration: 3000
            })
          }
        } else {
          return false
        }
      })
    },
    
    // 处理弹窗关闭
    handleDialogClose(done) {
      // 检查表单是否有内容
      const hasContent = this.linkForm.name || this.linkForm.url || this.linkForm.description || this.linkForm.avatar || this.linkForm.contact
      
      if (hasContent) {
        this.$confirm('表单中有未保存的内容，确定要关闭吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          // 清空表单
          this.linkForm = {
            name: '',
            url: '',
            description: '',
            avatar: '',
            contact: ''
          }
          done()
        }).catch(() => {
          // 取消关闭
        })
      } else {
        done()
      }
    },
    
    // 处理图片加载错误
    handleImageError(event, friend) {
      console.log('图片加载失败，切换到文字头像:', friend.name)
      // 隐藏图片，显示文字头像
      const avatarContainer = event.target.closest('.friend-avatar')
      const imageDiv = avatarContainer.querySelector('.avatar-image')
      const textDiv = avatarContainer.querySelector('.avatar-text')
      
      if (imageDiv && textDiv) {
        imageDiv.style.display = 'none'
        textDiv.style.display = 'flex'
        textDiv.textContent = this.getAvatarText(friend.name)
      }
    }
  }
}
</script>

<style scoped>
.friends-page {
  padding: 20px 0;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: calc(100vh - 70px);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

h1 {
  color: #2c3e50;
  margin-bottom: 30px;
  font-size: 2.5rem;
  font-weight: 600;
  text-align: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.friends-intro {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  padding: 30px;
}

.friends-intro p {
  color: #666;
  font-size: 18px;
  margin: 0;
  line-height: 1.6;
  max-width: 70%;
}

.friends-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 30px;
  margin-bottom: 40px;
}

.friend-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  padding: 25px;
  display: flex;
  align-items: center;
  transition: all 0.3s ease;
}

.friend-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.friend-avatar {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 20px;
  border: 3px solid rgba(102, 126, 234, 0.2);
  position: relative;
}

.avatar-image {
  width: 100%;
  height: 100%;
}

.avatar-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.friend-card:hover .avatar-image img {
  transform: scale(1.1);
}

.avatar-text {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-size: 24px;
  font-weight: 600;
  text-transform: uppercase;
  transition: all 0.3s ease;
}

.friend-card:hover .avatar-text {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
  transform: scale(1.05);
}

.friend-info {
  flex: 1;
}

.friend-info h3 {
  margin: 0 0 12px 0;
  color: #2c3e50;
  font-size: 20px;
  font-weight: 600;
}

.friend-info p {
  color: #666;
  margin: 0 0 20px 0;
  font-size: 15px;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.friend-link {
  color: #667eea;
  text-decoration: none;
  font-size: 16px;
  display: inline-flex;
  align-items: center;
  padding: 8px 16px;
  background-color: rgba(102, 126, 234, 0.1);
  border-radius: 20px;
  transition: all 0.3s ease;
}

.friend-link:hover {
  background-color: rgba(102, 126, 234, 0.2);
  transform: translateY(-2px);
}

.friend-link i {
  margin-right: 8px;
  font-size: 18px;
}

/* 申请友链对话框样式 */
.el-dialog__header {
  text-align: center;
  padding: 20px 20px 10px;
}

.el-dialog__title {
  font-size: 22px;
  color: #2c3e50;
  font-weight: 600;
}

.el-dialog__body {
  padding: 20px 30px;
}

.el-form-item__label {
  font-weight: 500;
}

.el-button--primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: #667eea;
  transition: all 0.3s ease;
}

.el-button--primary:hover, .el-button--primary:focus {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
  border-color: #5a6fd8;
  transform: translateY(-2px);
}

@media (max-width: 768px) {
  .friends-intro {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
    padding: 25px;
  }
  
  .friends-intro p {
    max-width: 100%;
    margin-bottom: 15px;
  }
  
  .friends-grid {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  }
  
  .friend-card {
    flex-direction: column;
    text-align: center;
  }
  
  .friend-avatar {
    margin-right: 0;
    margin-bottom: 15px;
  }
  
  h1 {
    font-size: 2rem;
  }
}

@media (max-width: 480px) {
  .friends-grid {
    grid-template-columns: 1fr;
  }
}
</style>