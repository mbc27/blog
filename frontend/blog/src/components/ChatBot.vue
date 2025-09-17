<template>
  <div class="chatbot-container" v-if="!isAdminRoute">
    <!-- 机器人图标按钮 -->
    <div 
      class="chatbot-icon" 
      @click="toggleChat"
      :class="{ 'active': isOpen }"
    >
      <svg 
        v-if="!isOpen"
        class="robot-icon" 
        viewBox="0 0 24 24" 
        fill="none" 
        xmlns="http://www.w3.org/2000/svg"
      >
        <path 
          d="M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM21 9V7L15 1H9L3 7V9C3 10.1 3.9 11 5 11V13C5 15.2 6.8 17 9 17H15C17.2 17 19 15.2 19 13V11C20.1 11 21 10.1 21 9ZM7 9C7 9.6 7.4 10 8 10S9 9.6 9 10S8.4 11 8 11S7 10.6 7 10V9ZM17 9V10C17 10.6 16.6 11 16 11S15 10.6 15 10S15.4 9 16 9S17 8.4 17 9ZM8 13C8 14.1 8.9 15 10 15H14C15.1 15 16 14.1 16 13H8Z" 
          fill="currentColor"
        />
      </svg>
      
      <!-- 关闭图标 -->
      <svg 
        v-else
        class="close-icon" 
        viewBox="0 0 24 24" 
        fill="none" 
        xmlns="http://www.w3.org/2000/svg"
      >
        <path 
          d="M18 6L6 18M6 6L18 18" 
          stroke="currentColor" 
          stroke-width="2" 
          stroke-linecap="round" 
          stroke-linejoin="round"
        />
      </svg>
    </div>

    <!-- 聊天窗口 -->
    <div 
      class="chat-window" 
      v-show="isOpen"
      :class="{ 'show': isOpen }"
    >
      <div class="chat-header">
        <h3>AI助手</h3>
        <span class="status" :class="{ 'online': aiEnabled, 'offline': !aiEnabled }">
          {{ aiEnabled ? '在线' : '离线' }}
        </span>
      </div>
      
      <div class="chat-content" ref="chatContent">
        <!-- 欢迎消息 -->
        <div class="welcome-message" v-if="messages.length === 0">
          <div class="bot-avatar">🤖</div>
          <div class="message">
            <p v-if="aiEnabled">您好！我是您的AI助手，有什么可以帮助您的吗？</p>
            <p v-else>AI助手功能当前已关闭，如需使用请联系管理员开启。</p>
          </div>
        </div>
        
        <!-- 消息列表 -->
        <div class="message-list">
          <div 
            v-for="message in messages" 
            :key="message.id"
            class="message-item"
            :class="{ 'user-message': message.role === 'user', 'ai-message': message.role === 'assistant' }"
          >
            <div class="message-avatar">
              <span v-if="message.role === 'user'">👤</span>
              <span v-else>🤖</span>
            </div>
            <div class="message-content">
              <div class="message-text">{{ message.content }}</div>
              <div class="message-time">{{ formatTime(message.createTime) }}</div>
            </div>
          </div>
        </div>
        
        <!-- 加载状态 -->
        <div v-if="isLoading" class="loading-message">
          <div class="bot-avatar">🤖</div>
          <div class="message">
            <div class="typing-indicator">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="chat-input">
        <input 
          v-model="inputMessage"
          type="text" 
          placeholder="请输入您的问题..."
          :disabled="!aiEnabled || isLoading"
          @keyup.enter="sendMessage"
          @focus="scrollToBottom"
        />
        <button 
          @click="sendMessage"
          :disabled="!aiEnabled || isLoading || !inputMessage.trim()"
          class="send-btn"
        >
          <span v-if="!isLoading">发送</span>
          <span v-else>...</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ChatBot',
  data() {
    return {
      isOpen: false,
      aiEnabled: false,
      currentSession: null,
      messages: [],
      inputMessage: '',
      isLoading: false
    }
  },
  computed: {
    isAdminRoute() {
      return this.$route.path.startsWith('/admin')
    }
  },
  async mounted() {
    await this.checkAiStatus()
  },
  methods: {
    async toggleChat() {
      this.isOpen = !this.isOpen
      if (this.isOpen && !this.currentSession) {
        await this.createSession()
      }
      if (this.isOpen) {
        this.$nextTick(() => {
          this.scrollToBottom()
        })
      }
    },

    async checkAiStatus() {
      try {
        // 这里可以调用API检查AI状态，暂时设为true用于测试
        this.aiEnabled = true
      } catch (error) {
        console.error('检查AI状态失败:', error)
        this.aiEnabled = false
      }
    },

    async createSession() {
      try {
        const response = await this.$axios.post('/ai/chat/session', {
          sessionType: 1 // 普通对话
        })
        if (response.data.code === 200) {
          this.currentSession = response.data.data
          await this.loadSessionMessages()
        }
      } catch (error) {
        console.error('创建会话失败:', error)
        this.$message.error('创建会话失败')
      }
    },

    async loadSessionMessages() {
      if (!this.currentSession) return
      
      try {
        const response = await this.$axios.get(`/ai/chat/session/${this.currentSession.sessionId}`)
        if (response.data.code === 200) {
          this.messages = response.data.data.messages || []
          this.$nextTick(() => {
            this.scrollToBottom()
          })
        }
      } catch (error) {
        console.error('加载消息失败:', error)
      }
    },

    async sendMessage() {
      if (!this.inputMessage.trim() || !this.aiEnabled || this.isLoading) {
        return
      }

      if (!this.currentSession) {
        await this.createSession()
        if (!this.currentSession) {
          this.$message.error('无法创建会话')
          return
        }
      }

      const userMessage = this.inputMessage.trim()
      this.inputMessage = ''
      this.isLoading = true

      // 添加用户消息到界面
      const userMsg = {
        id: Date.now(),
        role: 'user',
        content: userMessage,
        createTime: new Date()
      }
      this.messages.push(userMsg)
      this.scrollToBottom()

      try {
        const response = await this.$api.ai.sendMessage({
          sessionId: this.currentSession.sessionId,
          message: userMessage
        })

        if (response.code === 200) {
          const aiMessage = response.data
          this.messages.push(aiMessage)
        } else {
          this.$message.error(response.message || 'AI回复失败')
        }
      } catch (error) {
        console.error('发送消息失败:', error)
        // 检查是否是超时错误
        if (error.code === 'ECONNABORTED' && error.message.includes('timeout')) {
          this.$message.error('AI正在思考中，请求超时，请稍后再试')
        } else {
          this.$message.error('发送消息失败: ' + (error.message || '未知错误'))
        }
        
        // 添加错误消息
        const errorMsg = {
          id: Date.now() + 1,
          role: 'assistant',
          content: '抱歉，我现在无法回复您的消息，请稍后再试。',
          createTime: new Date()
        }
        this.messages.push(errorMsg)
      } finally {
        this.isLoading = false
        this.$nextTick(() => {
          this.scrollToBottom()
        })
      }
    },

    formatTime(time) {
      if (!time) return ''
      
      const date = new Date(time)
      const now = new Date()
      const diff = now - date
      
      if (diff < 60000) { // 1分钟内
        return '刚刚'
      } else if (diff < 3600000) { // 1小时内
        return Math.floor(diff / 60000) + '分钟前'
      } else if (diff < 86400000) { // 24小时内
        return Math.floor(diff / 3600000) + '小时前'
      } else {
        return date.toLocaleDateString() + ' ' + date.toLocaleTimeString().slice(0, 5)
      }
    },

    scrollToBottom() {
      this.$nextTick(() => {
        const chatContent = this.$refs.chatContent
        if (chatContent) {
          chatContent.scrollTop = chatContent.scrollHeight
        }
      })
    }
  }
}
</script>

<style scoped>
.chatbot-container {
  position: fixed;
  bottom: 90px; /* 在回到顶部按钮上方 */
  right: 30px; /* 与回到顶部按钮相同的右边距 */
  z-index: 1000;
}

.chatbot-icon {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
  color: white;
}

.chatbot-icon:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 25px rgba(102, 126, 234, 0.6);
}

.chatbot-icon.active {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
  box-shadow: 0 4px 20px rgba(255, 107, 107, 0.4);
}

.robot-icon, .close-icon {
  width: 26px;
  height: 26px;
  transition: transform 0.3s ease;
}

.chatbot-icon:hover .robot-icon,
.chatbot-icon:hover .close-icon {
  transform: scale(1.1);
}

.chat-window {
  position: absolute;
  bottom: 80px;
  right: 0;
  width: 320px;
  height: 400px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  opacity: 0;
  transform: translateY(20px) scale(0.95);
  transition: all 0.3s ease;
  z-index: 1001; /* 确保聊天窗口在最上层 */
}

.chat-window.show {
  opacity: 1;
  transform: translateY(0) scale(1);
}

.chat-header {
  padding: 16px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px 12px 0 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.status {
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.2);
}

.status.online {
  background: rgba(76, 175, 80, 0.8);
}

.status.offline {
  background: rgba(244, 67, 54, 0.8);
}

.chat-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.welcome-message {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  margin-bottom: 16px;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message-item {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.message-item.user-message {
  flex-direction: row-reverse;
}

.message-avatar {
  font-size: 20px;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  border-radius: 50%;
  flex-shrink: 0;
}

.user-message .message-avatar {
  background: #667eea;
  color: white;
}

.message-content {
  flex: 1;
  max-width: 80%;
}

.message-text {
  background: #f8f9fa;
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.4;
  color: #333;
  word-wrap: break-word;
}

.user-message .message-text {
  background: #667eea;
  color: white;
  border-radius: 12px 12px 4px 12px;
}

.ai-message .message-text {
  border-radius: 12px 12px 12px 4px;
}

.message-time {
  font-size: 11px;
  color: #999;
  margin-top: 4px;
  text-align: right;
}

.user-message .message-time {
  text-align: left;
}

.message {
  background: #f8f9fa;
  padding: 12px 16px;
  border-radius: 12px 12px 12px 4px;
  flex: 1;
}

.message p {
  margin: 0;
  font-size: 14px;
  line-height: 1.4;
  color: #333;
}

.message p + p {
  margin-top: 8px;
}

.loading-message {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  margin-top: 16px;
}

.loading-message .bot-avatar {
  font-size: 20px;
  width: 32px;
  height: 32px;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
  background: #f8f9fa;
  border-radius: 12px 12px 12px 4px;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #999;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) {
  animation-delay: -0.32s;
}

.typing-indicator span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes typing {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.chat-input {
  padding: 16px 20px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 8px;
}

.chat-input input {
  flex: 1;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 20px;
  outline: none;
  font-size: 14px;
  transition: border-color 0.3s ease;
}

.chat-input input:focus {
  border-color: #667eea;
}

.chat-input input:disabled {
  background: #f8f9fa;
  color: #999;
}

.send-btn {
  padding: 10px 16px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.send-btn:hover:not(:disabled) {
  background: #5a6fd8;
  transform: translateY(-1px);
}

.send-btn:disabled {
  background: #ddd;
  color: #999;
  cursor: not-allowed;
  transform: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chatbot-container {
    bottom: 75px; /* 在移动端回到顶部按钮上方 */
    right: 20px; /* 与回到顶部按钮相同的右边距 */
  }
  
  .chatbot-icon {
    width: 45px;
    height: 45px;
  }
  
  .robot-icon, .close-icon {
    width: 22px;
    height: 22px;
  }
  
  .chat-window {
    width: 280px;
    height: 350px;
    bottom: 65px;
  }
}

/* 动画效果 */
@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10px);
  }
  60% {
    transform: translateY(-5px);
  }
}

.chatbot-icon {
  animation: bounce 2s infinite;
  animation-delay: 3s;
}

.chatbot-icon:hover {
  animation: none;
}
</style>