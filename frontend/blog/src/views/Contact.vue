<template>
  <div class="contact-container page-container">
    <div class="contact-content">
      <div class="page-header">
        <h1 class="page-title">联系我</h1>
        <p class="page-subtitle">有任何问题或建议，欢迎与我联系</p>
      </div>
      
      <div class="contact-cards">
        <!-- 联系信息卡片 -->
        <el-card class="contact-info-card" shadow="hover">
          <div slot="header" class="card-header">
            <i class="el-icon-phone"></i>
            <span>联系信息</span>
          </div>
          
          <div class="contact-info">
            <div class="info-item" v-if="contactInfo.email">
              <div class="info-icon">
                <i class="el-icon-message"></i>
              </div>
              <div class="info-content">
                <h4>邮箱</h4>
                <p>{{ contactInfo.email }}</p>
                <el-button type="text" @click="copyToClipboard(contactInfo.email)">
                  <i class="el-icon-copy-document"></i> 复制
                </el-button>
              </div>
            </div>
            
            <div class="info-item" v-if="contactInfo.github">
              <div class="info-icon">
                <i class="el-icon-link"></i>
              </div>
              <div class="info-content">
                <h4>GitHub</h4>
                <p>{{ contactInfo.github }}</p>
                <el-button type="text" @click="openLink(contactInfo.github)">
                  <i class="el-icon-top-right"></i> 访问
                </el-button>
              </div>
            </div>
            
            <div class="info-item" v-if="contactInfo.wechat">
              <div class="info-icon">
                <i class="el-icon-chat-dot-round"></i>
              </div>
              <div class="info-content">
                <h4>微信</h4>
                <p>{{ contactInfo.wechat }}</p>
                <el-button type="text" @click="showWechatQR">
                  <i class="el-icon-view"></i> 查看二维码
                </el-button>
              </div>
            </div>
            
            <div class="info-item" v-if="contactInfo.wechatPublic">
              <div class="info-icon">
                <i class="el-icon-s-promotion"></i>
              </div>
              <div class="info-content">
                <h4>微信公众号</h4>
                <p>{{ contactInfo.wechatPublic }}</p>
                <el-button type="text" @click="showWechatPublicQR">
                  <i class="el-icon-view"></i> 查看二维码
                </el-button>
              </div>
            </div>
          </div>
        </el-card>
        
        <!-- 发送消息卡片 -->
        <el-card class="message-card" shadow="hover">
          <div slot="header" class="card-header">
            <i class="el-icon-edit"></i>
            <span>发送消息</span>
          </div>
          
          <el-form 
            :model="messageForm" 
            :rules="messageRules" 
            ref="messageForm" 
            label-width="80px"
            class="message-form">
            
            <el-form-item label="姓名" prop="name">
              <el-input 
                v-model="messageForm.name" 
                placeholder="请输入您的姓名"
                prefix-icon="el-icon-user">
              </el-input>
            </el-form-item>
            
            <el-form-item label="邮箱" prop="email">
              <el-input 
                v-model="messageForm.email" 
                placeholder="请输入您的邮箱"
                prefix-icon="el-icon-message">
              </el-input>
            </el-form-item>
            
            <el-form-item label="主题" prop="subject">
              <el-input 
                v-model="messageForm.subject" 
                placeholder="请输入消息主题"
                prefix-icon="el-icon-document">
              </el-input>
            </el-form-item>
            
            <el-form-item label="内容" prop="content">
              <el-input 
                type="textarea" 
                :rows="6" 
                v-model="messageForm.content" 
                placeholder="请输入您要发送的消息内容..."
                maxlength="1000"
                show-word-limit>
              </el-input>
            </el-form-item>
            
            <el-form-item>
              <el-button 
                type="primary" 
                @click="sendMessage" 
                :loading="sending"
                class="send-btn">
                <i class="el-icon-s-promotion"></i>
                {{ sending ? '发送中...' : '发送消息' }}
              </el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </div>
    
    <!-- 微信二维码弹窗 -->
    <el-dialog
      title="微信二维码"
      :visible.sync="wechatQRVisible"
      width="400px"
      center
      :close-on-click-modal="true">
      <div class="qr-container">
        <div class="qr-image" v-if="contactInfo.wechatQrCode">
          <img :src="contactInfo.wechatQrCode" alt="微信二维码" />
          <p class="qr-tips">请扫描二维码添加微信</p>
        </div>
        <div class="qr-placeholder" v-else>
          <i class="el-icon-picture-outline"></i>
          <p>微信二维码</p>
          <p class="qr-tips">暂未设置微信二维码</p>
        </div>
      </div>
    </el-dialog>
    
    <!-- 微信公众号二维码弹窗 -->
    <el-dialog
      title="微信公众号二维码"
      :visible.sync="wechatPublicQRVisible"
      width="400px"
      center
      :close-on-click-modal="true">
      <div class="qr-container">
        <div class="qr-image" v-if="contactInfo.wechatPublicQrCode">
          <img :src="contactInfo.wechatPublicQrCode" alt="微信公众号二维码" />
          <p class="qr-tips">请扫描二维码关注公众号</p>
        </div>
        <div class="qr-placeholder" v-else>
          <i class="el-icon-picture-outline"></i>
          <p>微信公众号二维码</p>
          <p class="qr-tips">暂未设置微信公众号二维码</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import api from '../api'

export default {
  name: 'Contact',
  data() {
    return {
      contactInfo: {
        email: '',
        github: '',
        wechat: '',
        wechatPublic: '',
        wechatQrCode: '',
        wechatPublicQrCode: ''
      },
      messageForm: {
        name: '',
        email: '',
        subject: '',
        content: ''
      },
      messageRules: {
        name: [
          { required: true, message: '请输入您的姓名', trigger: 'blur' },
          { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        subject: [
          { required: true, message: '请输入消息主题', trigger: 'blur' },
          { min: 2, max: 100, message: '主题长度在 2 到 100 个字符', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入消息内容', trigger: 'blur' },
          { min: 10, max: 1000, message: '内容长度在 10 到 1000 个字符', trigger: 'blur' }
        ]
      },
      sending: false,
      wechatQRVisible: false,
      wechatPublicQRVisible: false
    }
  },
  created() {
    this.loadContactInfo()
  },
  methods: {
    // 加载联系信息
    async loadContactInfo() {
      try {
        console.log('开始加载联系信息...')
        // 获取系统设置中的联系信息
        const response = await api.system.getPublicSettings()
        console.log('API响应:', response)
        if (response.code === 200) {
          const settings = response.data
          console.log('设置数据:', settings)
          this.contactInfo = {
            email: settings.about_email || settings.site_email || '',
            github: settings.about_github || settings.site_github || '',
            wechat: settings.about_wechat || settings.site_wechat || '',
            wechatPublic: settings.about_wechat_public || '',
            wechatQrCode: settings.wechat_qr_code || '',
            wechatPublicQrCode: settings.wechat_official_qr_code || ''
          }
          console.log('联系信息已更新:', this.contactInfo)
          console.log('微信二维码URL:', this.contactInfo.wechatQrCode)
          console.log('微信公众号二维码URL:', this.contactInfo.wechatPublicQrCode)
        }
      } catch (error) {
        console.error('加载联系信息失败:', error)
        // 设置默认联系信息
        this.contactInfo = {
          email: 'contact@example.com',
          github: 'https://github.com',
          wechat: '微信号',
          wechatPublic: '公众号',
          wechatQrCode: '',
          wechatPublicQrCode: ''
        }
      }
    },
    
    // 发送消息
    async sendMessage() {
      this.$refs.messageForm.validate(async (valid) => {
        if (valid) {
          this.sending = true
          try {
            console.log('发送消息数据:', this.messageForm)
            const response = await api.contact.sendMessage(this.messageForm)
            console.log('发送消息响应:', response)
            if (response.code === 200) {
              this.$message({
                message: '消息发送成功！感谢您的留言，我会在24小时内回复您',
                type: 'success',
                duration: 3000
              })
              this.resetForm()
            } else {
              this.$message({
                message: '消息发送失败，请检查网络连接或稍后重试',
                type: 'error',
                duration: 3000
              })
            }
          } catch (error) {
            console.error('发送消息错误:', error)
            this.$message({
              message: '网络连接失败，请检查网络后重试',
              type: 'error',
              duration: 3000
            })
          } finally {
            this.sending = false
          }
        }
      })
    },
    
    // 重置表单
    resetForm() {
      this.$refs.messageForm.resetFields()
    },
    
    // 复制到剪贴板
    async copyToClipboard(text) {
      try {
        await navigator.clipboard.writeText(text)
        this.$message({
          message: '已复制到剪贴板',
          type: 'success',
          duration: 3000
        })
      } catch (error) {
        // 降级方案
        const textArea = document.createElement('textarea')
        textArea.value = text
        document.body.appendChild(textArea)
        textArea.select()
        document.execCommand('copy')
        document.body.removeChild(textArea)
        this.$message({
          message: '已复制到剪贴板',
          type: 'success',
          duration: 3000
        })
      }
    },
    
    // 打开链接
    openLink(url) {
      if (url) {
        window.open(url, '_blank')
      }
    },
    
    // 显示微信二维码
    showWechatQR() {
      this.wechatQRVisible = true
    },
    
    // 显示微信公众号二维码
    showWechatPublicQR() {
      this.wechatPublicQRVisible = true
    }
  }
}
</script>

<style scoped>
.contact-container {
  min-height: calc(100vh - 70px);
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px 20px 40px;
}

.contact-content {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 50px;
}

.page-title {
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

.page-subtitle {
  font-size: 18px;
  color: #666;
  margin: 0;
}

.contact-cards {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  align-items: start;
}

.contact-info-card,
.message-card {
  border-radius: 20px;
  border: none;
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.card-header i {
  margin-right: 10px;
  font-size: 20px;
  color: #667eea;
}

.contact-info {
  padding: 20px 0;
}

.info-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 15px;
  transition: all 0.3s ease;
}

.info-item:hover {
  background: #e9ecef;
  transform: translateY(-2px);
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-icon {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  flex-shrink: 0;
}

.info-icon i {
  color: white;
  font-size: 20px;
}

.info-content {
  flex: 1;
}

.info-content h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.info-content p {
  margin: 0 0 10px 0;
  color: #666;
  font-size: 14px;
  word-break: break-all;
}

.message-form {
  padding: 20px 0;
}

.message-form .el-form-item {
  margin-bottom: 25px;
}

.message-form .el-input__inner,
.message-form .el-textarea__inner {
  border-radius: 12px;
  border: 2px solid #e1e8ed;
  transition: all 0.3s ease;
}

.message-form .el-input__inner:focus,
.message-form .el-textarea__inner:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.send-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  padding: 12px 30px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.send-btn:hover {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
}

.qr-container {
  text-align: center;
  padding: 20px;
}

.qr-image {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 0 auto;
}

.qr-image img {
  width: 200px;
  height: 200px;
  object-fit: contain;
  border-radius: 15px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 15px;
}

.qr-placeholder {
  width: 200px;
  height: 200px;
  border: 2px dashed #ddd;
  border-radius: 15px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  background: #f8f9fa;
}

.qr-placeholder i {
  font-size: 48px;
  color: #ccc;
  margin-bottom: 10px;
}

.qr-placeholder p {
  margin: 5px 0;
  color: #666;
}

.qr-tips {
  font-size: 14px;
  color: #666;
  margin: 10px 0 0 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .contact-container {
    padding: 20px 15px;
  }
  
  .page-title {
    font-size: 36px;
  }
  
  .page-subtitle {
    font-size: 16px;
  }
  
  .contact-cards {
    grid-template-columns: 1fr;
    gap: 30px;
  }
  
  .info-item {
    flex-direction: column;
    text-align: center;
  }
  
  .info-icon {
    margin: 0 auto 15px;
  }
  
  .message-form {
    padding: 10px 0;
  }
}

@media (max-width: 480px) {
  .page-title {
    font-size: 28px;
  }
  
  .info-item {
    padding: 15px;
  }
  
  .card-header {
    font-size: 16px;
  }
}
</style>