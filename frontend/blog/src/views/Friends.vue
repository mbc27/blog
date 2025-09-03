<template>
  <div class="friends-page">
    <div class="container">
      <h1>友情链接</h1>
      <div class="friends-intro">
        <p>这里是我的朋友们，他们都是很棒的博主，欢迎访问他们的网站！</p>
        <el-button type="primary" @click="dialogVisible = true">申请友链</el-button>
      </div>
      
      <div class="friends-grid">
        <div class="friend-card" v-for="(friend, index) in friends" :key="index">
          <div class="friend-avatar">
            <img :src="friend.avatar" :alt="friend.name">
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
        width="500px">
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
  methods: {
    async fetchFriendLinks() {
      try {
        this.loading = true
        const response = await api.friendLink.getList()
        if (response.code === 200) {
          this.friends = response.data.map(link => ({
            id: link.id,
            name: link.name,
            description: link.description,
            avatar: link.avatar || 'https://via.placeholder.com/80',
            url: link.url
          }))
        }
      } catch (error) {
        console.error('获取友链列表失败:', error)
        this.$message.error('获取友链列表失败')
      } finally {
        this.loading = false
      }
    },
    
    submitLinkApplication() {
      this.$refs.linkForm.validate(async (valid) => {
        if (valid) {
          try {
            const response = await api.friendLink.add(this.linkForm)
            if (response.code === 200) {
              this.$message.success('友链申请提交成功，请等待审核！')
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
            this.$message.error('提交友链申请失败')
          }
        } else {
          return false
        }
      })
    }
  }
}
</script>

<style scoped>
.friends-page {
  padding: 100px 0;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
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
}

.friend-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.friend-card:hover .friend-avatar img {
  transform: scale(1.1);
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