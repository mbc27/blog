<template>
  <div class="message-page">
    <div class="container">
      <h1>留言板</h1>
      
      <div class="message-form">
        <h2>发表留言</h2>
        <el-form :model="messageForm" :rules="currentRules" ref="messageForm">
          <el-form-item prop="nickname" v-if="!isAuthenticated">
            <el-input v-model="messageForm.nickname" placeholder="昵称"></el-input>
          </el-form-item>
          <el-form-item v-else>
            <el-input v-model="messageForm.nickname" placeholder="昵称" :disabled="true">
              <template slot="prepend">登录用户</template>
            </el-input>
          </el-form-item>
          <el-form-item prop="email" v-if="!isAuthenticated">
            <el-input v-model="messageForm.email" placeholder="邮箱（不会公开）"></el-input>
          </el-form-item>
          <el-form-item v-else>
            <el-input v-model="messageForm.email" placeholder="邮箱（不会公开）" :disabled="true">
              <template slot="prepend">邮箱</template>
            </el-input>
          </el-form-item>
          <el-form-item prop="content">
            <el-input type="textarea" v-model="messageForm.content" rows="4" placeholder="留言内容"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitMessage">提交留言</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 留言列表 -->
      <el-skeleton :loading="loading" animated :count="3" v-if="loading">
        <template slot="template">
          <div class="message-list">
            <div class="message-item" v-for="i in 3" :key="i">
              <div class="message-avatar">
                <el-skeleton-item variant="circle" style="width: 50px; height: 50px;" />
              </div>
              <div class="message-content">
                <div class="message-header">
                  <el-skeleton-item variant="text" style="width: 30%;" />
                  <el-skeleton-item variant="text" style="width: 20%;" />
                </div>
                <el-skeleton-item variant="p" style="width: 100%;" />
                <el-skeleton-item variant="p" style="width: 100%;" />
              </div>
            </div>
          </div>
        </template>
      </el-skeleton>
      
      <template v-else>
        <div class="message-list" v-if="messages.length > 0">
          <div class="message-item" v-for="message in messages" :key="message.id">
            <div class="message-avatar">
              <img :src="message.avatar" :alt="message.nickname">
            </div>
            <div class="message-content">
              <div class="message-header">
                <span class="message-nickname">{{ message.nickname }}</span>
                <span class="message-time">{{ message.time }}</span>
              </div>
              <div class="message-text">{{ message.content }}</div>
              <div class="message-actions">
                <span class="message-like">
                  <i class="el-icon-thumb"></i>
                  {{ message.likes || 0 }}
                </span>
                <el-button 
                  type="text" 
                  size="mini" 
                  @click="toggleReplyInput(message.id)"
                  icon="el-icon-chat-dot-round"
                >
                  回复 ({{ message.replies ? message.replies.length : 0 }})
                </el-button>
              </div>
              
              <!-- 回复输入框 -->
              <div v-if="replyingTo === message.id" class="reply-input-section">
                <el-form :model="replyForm" :rules="replyRules" ref="replyForm">
                  <el-form-item prop="nickname" v-if="!isAuthenticated">
                    <el-input v-model="replyForm.nickname" placeholder="昵称"></el-input>
                  </el-form-item>
                  <el-form-item v-else>
                    <el-input v-model="replyForm.nickname" placeholder="昵称" :disabled="true">
                      <template slot="prepend">登录用户</template>
                    </el-input>
                  </el-form-item>
                  <el-form-item prop="email" v-if="!isAuthenticated">
                    <el-input v-model="replyForm.email" placeholder="邮箱（不会公开）"></el-input>
                  </el-form-item>
                  <el-form-item v-else>
                    <el-input v-model="replyForm.email" placeholder="邮箱（不会公开）" :disabled="true">
                      <template slot="prepend">邮箱</template>
                    </el-input>
                  </el-form-item>
                  <el-form-item prop="content">
                    <el-input 
                      type="textarea" 
                      v-model="replyForm.content" 
                      rows="3" 
                      :placeholder="`回复 @${message.nickname}`"
                    ></el-input>
                  </el-form-item>
                  <el-form-item>
                    <el-button size="small" @click="cancelReply">取消</el-button>
                    <el-button type="primary" size="small" @click="submitReply(message)" :loading="replySubmitting">
                      提交回复
                    </el-button>
                  </el-form-item>
                </el-form>
              </div>
              
              <div class="reply-list" v-if="message.replies && message.replies.length > 0">
                <div class="reply-item" v-for="reply in message.replies" :key="reply.id">
                  <div class="reply-avatar">
                    <img :src="reply.avatar" :alt="reply.nickname">
                  </div>
                  <div class="reply-content">
                    <div class="reply-header">
                      <span class="reply-nickname">{{ reply.nickname }}</span>
                      <span class="reply-time">{{ reply.time }}</span>
                    </div>
                    <div class="reply-text">{{ reply.content }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <el-empty v-else description="暂无留言"></el-empty>
        
        <div class="pagination" v-if="total > pageSize">
          <el-pagination
            background
            layout="prev, pager, next"
            :current-page.sync="currentPage"
            :page-size="pageSize"
            :total="total"
            @current-change="handlePageChange">
          </el-pagination>
        </div>
      </template>
    </div>
  </div>
</template>

<script>
import api from '../api'
import { mapGetters } from 'vuex'

export default {
  name: 'Message',
  data() {
    return {
      messageForm: {
        nickname: '',
        email: '',
        content: ''
      },
      replyForm: {
        nickname: '',
        email: '',
        content: ''
      },
      rules: {
        content: [
          { required: true, message: '请输入留言内容', trigger: 'blur' },
          { min: 5, max: 500, message: '长度在 5 到 500 个字符', trigger: 'blur' }
        ]
      },
      // 未登录用户的验证规则
      guestRules: {
        nickname: [
          { required: true, message: '请输入昵称', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入留言内容', trigger: 'blur' },
          { min: 5, max: 500, message: '长度在 5 到 500 个字符', trigger: 'blur' }
        ]
      },
      replyRules: {
        nickname: [
          { required: true, message: '请输入昵称', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入回复内容', trigger: 'blur' },
          { min: 5, max: 500, message: '长度在 5 到 500 个字符', trigger: 'blur' }
        ]
      },
      messages: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      loading: false,
      replyingTo: null,
      replySubmitting: false
    }
  },
  computed: {
    ...mapGetters(['user', 'isAuthenticated']),
    currentRules() {
      return this.isAuthenticated ? this.rules : this.guestRules
    }
  },
  created() {
    this.fetchMessages()
    this.initUserInfo()
  },
  methods: {
    // 初始化用户信息
    initUserInfo() {
      if (this.isAuthenticated && this.user) {
        this.messageForm.nickname = this.user.nickname || this.user.username || ''
        this.messageForm.email = this.user.email || ''
        this.replyForm.nickname = this.user.nickname || this.user.username || ''
        this.replyForm.email = this.user.email || ''
      }
    },
    
    async fetchMessages() {
      try {
        this.loading = true
        const response = await api.message.getList({
          current: this.currentPage,
          size: this.pageSize
        })
        
        if (response.code === 200) {
          // 确保response.data和response.data.records存在
          if (response.data && Array.isArray(response.data.records)) {
            // 只显示顶级留言，回复留言应该嵌套在父留言下
            const allMessages = response.data.records || []
            
            // 分离顶级留言和回复留言
            const topLevelMessages = allMessages.filter(msg => !msg.parentId)
            const replyMessages = allMessages.filter(msg => msg.parentId)
            
            // 为每个顶级留言添加其回复
            this.messages = topLevelMessages.map(message => {
              const messageReplies = replyMessages.filter(reply => reply.parentId === message.id)
              
              return {
                id: message.id,
                nickname: message.nickname,
                avatar: message.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
                time: this.formatDate(message.createTime),
                content: message.content,
                likes: message.likes || 0,
                isLiked: message.isLiked || false,
                replies: messageReplies.map(reply => ({
                  id: reply.id,
                  nickname: reply.nickname,
                  avatar: reply.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
                  time: this.formatDate(reply.createTime),
                  content: reply.content,
                  replyToNickname: reply.replyToNickname || message.nickname
                }))
              }
            })
            
            // 总数应该只计算顶级留言
            this.total = topLevelMessages.length
          } else {
            // 如果数据格式不正确，设置为空数组
            this.messages = []
            this.total = 0
            console.warn('留言数据格式不正确:', response.data)
          }
        }
      } catch (error) {
        console.error('获取留言列表失败:', error)
        // 不显示错误提示，静默失败
        this.messages = []
        this.total = 0
      } finally {
        this.loading = false
      }
    },
    
    async submitMessage() {
      this.$refs.messageForm.validate(async (valid) => {
        if (valid) {
          try {
            const response = await api.message.add(this.messageForm)
            if (response.code === 200) {
              this.$message.success('留言提交成功！')
              // 只清空内容，保留用户信息
              this.messageForm.content = ''
              if (!this.isAuthenticated) {
                this.messageForm.nickname = ''
                this.messageForm.email = ''
              }
              // 重新获取留言列表
              this.fetchMessages()
            }
          } catch (error) {
            console.error('提交留言失败:', error)
            this.$message.error('提交留言失败')
          }
        } else {
          return false
        }
      })
    },
    
    handlePageChange(page) {
      this.currentPage = page
      this.fetchMessages()
    },
    
    toggleReplyInput(messageId) {
      if (this.replyingTo === messageId) {
        this.cancelReply()
      } else {
        this.replyingTo = messageId
        this.replyForm.content = ''
        // 重新初始化用户信息
        this.initUserInfo()
      }
    },

    cancelReply() {
      this.replyingTo = null
      this.replyForm = {
        nickname: '',
        email: '',
        content: ''
      }
      // 重新初始化用户信息
      this.initUserInfo()
    },

    async submitReply(parentMessage) {
      // 手动验证表单
      let isValid = true
      if (!this.replyForm.content.trim()) {
        this.$message.error('请输入回复内容')
        isValid = false
      }
      if (!this.isAuthenticated) {
        if (!this.replyForm.nickname.trim()) {
          this.$message.error('请输入昵称')
          isValid = false
        }
        if (!this.replyForm.email.trim()) {
          this.$message.error('请输入邮箱')
          isValid = false
        }
      }

      if (!isValid) return

      this.replySubmitting = true
      try {
        const replyData = {
          parentId: parentMessage.id,
          nickname: this.replyForm.nickname,
          email: this.replyForm.email,
          content: this.replyForm.content
        }
        
        const response = await api.message.add(replyData)
        if (response.code === 200) {
          this.$message.success('回复成功！')
          this.cancelReply()
          // 重新获取留言列表
          this.fetchMessages()
        }
      } catch (error) {
        console.error('回复失败:', error)
        this.$message.error('回复失败')
      } finally {
        this.replySubmitting = false
      }
    },


    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
    }
  }
}
</script>

<style scoped>
.message-page {
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

h2 {
  color: #2c3e50;
  font-size: 22px;
  margin-bottom: 25px;
  font-weight: 600;
}

.message-form {
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  padding: 30px;
  margin-bottom: 40px;
  transition: all 0.3s ease;
}

.message-form:hover {
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
}

.message-list {
  margin-bottom: 40px;
}

.message-item {
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  padding: 25px;
  margin-bottom: 30px;
  display: flex;
  transition: all 0.3s ease;
}

.message-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
}

.message-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 20px;
  border: 3px solid rgba(102, 126, 234, 0.2);
}

.message-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.message-content {
  flex: 1;
}

.message-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
}

.message-nickname {
  font-weight: bold;
  color: #2c3e50;
  font-size: 18px;
}

.message-time {
  color: #7f8c8d;
  font-size: 14px;
}

.message-text {
  color: #666;
  line-height: 1.8;
  margin-bottom: 15px;
  font-size: 16px;
}

.message-actions {
  display: flex;
  gap: 20px;
  align-items: center;
  margin-bottom: 20px;
}

.message-actions .el-button--text {
  color: #7f8c8d;
  padding: 0;
  font-size: 14px;
  transition: all 0.3s ease;
}

.message-actions .el-button--text:hover {
  color: #667eea;
}

.message-like {
  color: #7f8c8d;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.message-like:hover {
  color: #667eea;
}

.message-like i {
  font-size: 16px;
}

.reply-input-section {
  margin-top: 20px;
  padding: 20px;
  background: rgba(102, 126, 234, 0.05);
  border-radius: 15px;
  border-left: 4px solid #667eea;
}

.reply-list {
  background: rgba(245, 247, 250, 0.7);
  border-radius: 15px;
  padding: 20px;
  margin-top: 20px;
}

.reply-item {
  display: flex;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.reply-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.reply-avatar {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 15px;
  border: 2px solid rgba(102, 126, 234, 0.2);
}

.reply-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.reply-content {
  flex: 1;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.reply-nickname {
  font-weight: bold;
  color: #2c3e50;
  font-size: 16px;
}

.reply-time {
  color: #7f8c8d;
  font-size: 12px;
}

.reply-text {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.el-pagination.is-background .el-pager li:not(.disabled).active {
  background-color: #667eea;
}

.el-pagination.is-background .el-pager li:not(.disabled):hover {
  color: #667eea;
}

/* 表单样式优化 */
.el-input__inner, .el-textarea__inner {
  border-radius: 8px;
  border-color: rgba(102, 126, 234, 0.2);
}

.el-input__inner:focus, .el-textarea__inner:focus {
  border-color: #667eea;
}

.el-button--primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: #667eea;
  border-radius: 8px;
  padding: 12px 25px;
  transition: all 0.3s ease;
}

.el-button--primary:hover, .el-button--primary:focus {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
  border-color: #5a6fd8;
  transform: translateY(-2px);
}

@media (max-width: 768px) {
  .message-item {
    flex-direction: column;
  }
  
  .message-avatar {
    margin-right: 0;
    margin-bottom: 15px;
  }
  
  .reply-item {
    flex-direction: column;
  }
  
  .reply-avatar {
    margin-right: 0;
    margin-bottom: 10px;
  }
  
  h1 {
    font-size: 2rem;
  }
}
</style>