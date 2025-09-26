<template>
  <div class="system-setting">
    <el-card class="el-card is-hover-shadow">
      <div slot="header" class="clearfix">
        <span>系统设置</span>
      </div>
      
      <el-form
        ref="settingsForm"
        :model="settings"
        :rules="rules"
        label-width="120px"
        v-loading="loading"
      >
        <!-- 网站基本信息 -->
        <el-divider content-position="left">网站基本信息</el-divider>
        
        <el-form-item label="网站标题" prop="siteTitle">
          <el-input v-model="settings.siteTitle" placeholder="请输入网站标题"></el-input>
        </el-form-item>
        
        <el-form-item label="网站描述" prop="siteDescription">
          <el-input
            type="textarea"
            :rows="3"
            v-model="settings.siteDescription"
            placeholder="请输入网站描述"
          ></el-input>
        </el-form-item>
        
        <el-form-item label="网站关键词" prop="siteKeywords">
          <el-input v-model="settings.siteKeywords" placeholder="请输入网站关键词，用逗号分隔"></el-input>
        </el-form-item>
        
        <el-form-item label="网站作者" prop="siteAuthor">
          <el-input v-model="settings.siteAuthor" placeholder="请输入网站作者"></el-input>
        </el-form-item>
        
        <!-- 邮箱设置 -->
        <el-divider content-position="left">邮箱设置</el-divider>
        
        <el-form-item label="邮箱服务器" prop="emailHost">
          <el-input v-model="settings.emailHost" placeholder="如：smtp.qq.com"></el-input>
        </el-form-item>
        
        <el-form-item label="邮箱端口" prop="emailPort">
          <el-input-number v-model="settings.emailPort" :min="1" :max="65535"></el-input-number>
        </el-form-item>
        
        <el-form-item label="邮箱用户名" prop="emailUsername">
          <el-input v-model="settings.emailUsername" placeholder="请输入邮箱用户名"></el-input>
        </el-form-item>
        
        <el-form-item label="邮箱密码" prop="emailPassword">
          <el-input
            type="password"
            v-model="settings.emailPassword"
            placeholder="请输入邮箱密码"
            show-password
          ></el-input>
        </el-form-item>
        
        <el-form-item label="发件人邮箱" prop="emailFrom">
          <el-input v-model="settings.emailFrom" placeholder="请输入发件人邮箱"></el-input>
        </el-form-item>
        
        <!-- 功能设置 -->
        <el-divider content-position="left">功能设置</el-divider>
        
        <el-form-item label="评论审核">
          <el-switch
            v-model="settings.commentAudit"
            :active-value="1"
            :inactive-value="0"
            active-text="开启"
            inactive-text="关闭"
          ></el-switch>
        </el-form-item>
        
        <el-form-item label="用户注册">
          <el-switch
            v-model="settings.userRegister"
            :active-value="1"
            :inactive-value="0"
            active-text="开启"
            inactive-text="关闭"
          ></el-switch>
        </el-form-item>
        
        <el-form-item label="每页文章数" prop="articlesPerPage">
          <el-input-number v-model="settings.articlesPerPage" :min="1" :max="100"></el-input-number>
        </el-form-item>
        
        <el-form-item label="每页评论数" prop="commentsPerPage">
          <el-input-number v-model="settings.commentsPerPage" :min="1" :max="100"></el-input-number>
        </el-form-item>
        
        <!-- 关于页面设置 -->
        <el-divider content-position="left">关于页面设置</el-divider>
        
        <el-form-item label="博客介绍" prop="aboutBlogIntro">
          <el-input
            type="textarea"
            :rows="3"
            v-model="settings.aboutBlogIntro"
            placeholder="请输入博客介绍内容"
          ></el-input>
        </el-form-item>
        
        <el-form-item label="技术栈" prop="aboutTechStack">
          <el-input
            type="textarea"
            :rows="3"
            v-model="settings.aboutTechStack"
            placeholder="请输入技术栈内容"
          ></el-input>
        </el-form-item>
        
        <el-form-item label="关于作者" prop="aboutAuthor">
          <el-input
            type="textarea"
            :rows="3"
            v-model="settings.aboutAuthor"
            placeholder="请输入关于作者的内容"
          ></el-input>
        </el-form-item>
        
        <el-form-item label="联系方式" prop="aboutContact">
          <el-input
            type="textarea"
            :rows="3"
            v-model="settings.aboutContact"
            placeholder="请输入联系方式内容"
          ></el-input>
        </el-form-item>
        
        <!-- 联系信息设置 -->        <el-divider content-position="left">联系信息设置</el-divider>
        
        <el-form-item label="联系邮箱" prop="aboutEmail">
          <el-input v-model="settings.aboutEmail" placeholder="请输入联系邮箱"></el-input>
        </el-form-item>
        
        <el-form-item label="GitHub" prop="aboutGithub">
          <el-input v-model="settings.aboutGithub" placeholder="请输入GitHub地址"></el-input>
        </el-form-item>
        
        <el-form-item label="微信号" prop="aboutWechat">
          <el-input v-model="settings.aboutWechat" placeholder="请输入微信号"></el-input>
        </el-form-item>
        
        <el-form-item label="微信公众号" prop="aboutWechatPublic">
          <el-input v-model="settings.aboutWechatPublic" placeholder="请输入微信公众号"></el-input>
        </el-form-item>
        
        <!-- AI助手设置 -->
        <el-divider content-position="left">AI助手设置</el-divider>
        
        <el-form-item label="AI功能">
          <el-switch
            v-model="settings.aiEnabled"
            :active-value="1"
            :inactive-value="0"
            active-text="开启"
            inactive-text="关闭"
          ></el-switch>
          <div class="form-tip">开启后用户可以使用AI助手功能</div>
        </el-form-item>
        
        <el-form-item label="AI API Key" prop="deepseekApiKey">
          <el-input
            type="password"
            v-model="settings.deepseekApiKey"
            placeholder="请输入AI服务的API Key"
            show-password
          ></el-input>
          <div class="form-tip">用于访问AI服务的API密钥</div>
        </el-form-item>
        
        <el-form-item label="AI API地址" prop="deepseekApiUrl">
          <el-input v-model="settings.deepseekApiUrl" placeholder="请输入AI API接口地址"></el-input>
          <div class="form-tip">AI服务的API接口地址</div>
        </el-form-item>
        
        <el-form-item label="AI模型" prop="deepseekModel">
          <el-input
            v-model="settings.deepseekModel"
            placeholder="请输入AI模型名称"
          ></el-input>
          <div class="form-tip">要使用的AI模型名称（如：deepseek-chat、gpt-3.5-turbo、claude-3-sonnet等）</div>
        </el-form-item>
        
        <el-form-item label="最大Token数" prop="aiMaxTokens">
          <el-input-number v-model="settings.aiMaxTokens" :min="100" :max="8000" :step="100"></el-input-number>
          <div class="form-tip">AI回复的最大Token数量，影响回复长度</div>
        </el-form-item>
        
        <el-form-item label="创造性参数" prop="aiTemperature">
          <el-slider
            v-model="settings.aiTemperature"
            :min="0"
            :max="1"
            :step="0.1"
            show-input
            :format-tooltip="formatTemperatureTooltip"
          ></el-slider>
          <div class="form-tip">控制AI回复的创造性，0为最保守，1为最创新</div>
        </el-form-item>
        
        <el-form-item label="系统提示词">
          <el-input
            type="textarea"
            :rows="3"
            v-model="settings.aiSystemPrompt"
            placeholder="请输入AI助手的系统提示词"
          ></el-input>
          <div class="form-tip">定义AI助手的角色和行为规范</div>
        </el-form-item>
        
        <el-form-item label="写作助手提示词">
          <el-input
            type="textarea"
            :rows="3"
            v-model="settings.aiWritingPrompt"
            placeholder="请输入写作助手的提示词"
          ></el-input>
          <div class="form-tip">用于写作辅助功能的专用提示词</div>
        </el-form-item>
        
        <el-form-item label="润色助手提示词">
          <el-input
            type="textarea"
            :rows="3"
            v-model="settings.aiPolishPrompt"
            placeholder="请输入文章润色的提示词"
          ></el-input>
          <div class="form-tip">用于文章润色功能的专用提示词</div>
        </el-form-item>
        
        <!-- AI功能测试 -->
        <el-form-item label="功能测试">
          <el-button type="primary" @click="testAiConnection" :loading="testingAi">
            <i class="el-icon-connection"></i> 测试AI连接
          </el-button>
          <div class="ai-status" v-if="aiTestResult">
            <el-tag :type="aiTestResult.success ? 'success' : 'danger'">
              {{ aiTestResult.message }}
            </el-tag>
          </div>
        </el-form-item>
        
        <!-- 二维码设置 -->
        <el-divider content-position="left">二维码设置</el-divider>
        
        <el-form-item label="微信二维码">
          <div class="qr-upload-container">
            <el-upload
              class="qr-uploader"
              :action="uploadUrl"
              :show-file-list="false"
              :on-success="handleWechatQRSuccess"
              :before-upload="beforeQRUpload"
              :headers="uploadHeaders"
              accept="image/*">
              <div class="qr-preview" v-if="settings.wechatQrCode">
                <img :src="getImageUrl(settings.wechatQrCode)" alt="微信二维码" @error="handleImageError">
                <div class="qr-overlay">
                  <i class="el-icon-edit"></i>
                  <span>更换二维码</span>
                </div>
              </div>
              <div class="qr-placeholder" v-else>
                <i class="el-icon-plus"></i>
                <div class="upload-text">上传微信二维码</div>
              </div>
            </el-upload>
            <div class="qr-actions" v-if="settings.wechatQrCode">
              <el-button size="mini" type="danger" @click="removeWechatQR">删除</el-button>
            </div>
          </div>
        </el-form-item>
        
        <el-form-item label="微信公众号二维码">
          <div class="qr-upload-container">
            <el-upload
              class="qr-uploader"
              :action="uploadUrl"
              :show-file-list="false"
              :on-success="handleWechatPublicQRSuccess"
              :before-upload="beforeQRUpload"
              :headers="uploadHeaders"
              accept="image/*">
              <div class="qr-preview" v-if="settings.wechatPublicQrCode">
                <img :src="getImageUrl(settings.wechatPublicQrCode)" alt="微信公众号二维码" @error="handleImageError">
                <div class="qr-overlay">
                  <i class="el-icon-edit"></i>
                  <span>更换二维码</span>
                </div>
              </div>
              <div class="qr-placeholder" v-else>
                <i class="el-icon-plus"></i>
                <div class="upload-text">上传微信公众号二维码</div>
              </div>
            </el-upload>
            <div class="qr-actions" v-if="settings.wechatPublicQrCode">
              <el-button size="mini" type="danger" @click="removeWechatPublicQR">删除</el-button>
            </div>
          </div>
        </el-form-item>
        
        <!-- 操作按钮 -->
        <el-form-item>
          <el-button type="primary" @click="saveSettings" :loading="loading">
            <i class="el-icon-check"></i> 保存设置
          </el-button>
          <el-button @click="resetForm">
            <i class="el-icon-refresh"></i> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import api from '@/api'

export default {
  name: 'SystemSetting',
  data() {
    return {
      loading: false,
      testingAi: false,
      aiTestResult: null,
      settings: {
        siteTitle: '',
        siteDescription: '',
        siteKeywords: '',
        siteAuthor: '',
        siteLogo: '',
        siteFavicon: '',
        emailHost: '',
        emailPort: 587,
        emailUsername: '',
        emailPassword: '',
        emailFrom: '',
        commentAudit: 1, // 默认开启评论审核
        userRegister: 1, // 默认开启用户注册
        articlesPerPage: 10,
        commentsPerPage: 10,
        aboutBlogIntro: '',
        aboutTechStack: '',
        aboutAuthor: '',
        aboutContact: '',
        aboutEmail: '',
        aboutGithub: '',
        aboutWechat: '',
        aboutWechatPublic: '',
        wechatQrCode: '',
        wechatPublicQrCode: '',
        // AI配置
        aiEnabled: 0,
        deepseekApiKey: '',
        deepseekApiUrl: 'https://api.deepseek.com/v1/chat/completions',
        deepseekModel: 'deepseek-chat',
        aiMaxTokens: 2000,
        aiTemperature: 0.7,
        aiSystemPrompt: '你是一个智能的博客助手，可以帮助用户回答问题和提供写作建议。请以友好和专业的方式回应。',
        aiWritingPrompt: '你是一个专业的写作助手。请根据用户提供的内容和要求，提供具体的写作建议和改进意见。',
        aiPolishPrompt: '你是一个专业的文章编辑。请对用户提供的文章内容进行润色，提升语言表达、逻辑结构和可读性。'
      },
      uploadUrl: '/upload/image',
      uploadHeaders: {
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      },
      rules: {
        siteTitle: [
          { required: true, message: '请输入网站标题', trigger: 'blur' }
        ],
        siteDescription: [
          { required: true, message: '请输入网站描述', trigger: 'blur' }
        ],
        articlesPerPage: [
          { required: true, message: '请输入每页文章数', trigger: 'blur' },
          { type: 'number', min: 1, max: 100, message: '每页文章数必须在1-100之间', trigger: 'blur' }
        ],
        commentsPerPage: [
          { required: true, message: '请输入每页评论数', trigger: 'blur' },
          { type: 'number', min: 1, max: 100, message: '每页评论数必须在1-100之间', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.fetchSettings()
  },
  methods: {
    fetchSettings() {
      this.loading = true
      // 同时获取系统设置和AI配置
      Promise.all([
        api.system.getSettings(),
        this.fetchAiSettings()
      ]).then(([systemResponse, aiResponse]) => {
        if (systemResponse && systemResponse.code === 200) {
          // 将后端返回的设置数据映射到前端数据结构
          const data = systemResponse.data || {}
          console.log('获取到的系统设置数据：', data)
          
          // 特别处理评论审核和用户注册设置
          const commentAudit = data.comment_audit !== undefined ? 
            (data.comment_audit === '1' || data.comment_audit === 1 ? 1 : 0) : 1
          
          const userRegister = data.user_register !== undefined ? 
            (data.user_register === '1' || data.user_register === 1 ? 1 : 0) : 1
          
          console.log('解析后的评论审核设置：', commentAudit)
          console.log('解析后的用户注册设置：', userRegister)
          
          this.settings = {
            siteTitle: data.site_title || '',
            siteDescription: data.site_description || '',
            siteKeywords: data.site_keywords || '',
            siteAuthor: data.site_author || '',
            siteLogo: data.site_logo || '',
            siteFavicon: data.site_favicon || '',
            emailHost: data.email_host || '',
            emailPort: parseInt(data.email_port) || 587,
            emailUsername: data.email_username || '',
            emailPassword: data.email_password || '',
            emailFrom: data.email_from || '',
            // 确保正确解析布尔值
            commentAudit: commentAudit,
            userRegister: userRegister,
            articlesPerPage: parseInt(data.articles_per_page) || 10,
            commentsPerPage: parseInt(data.comments_per_page) || 10,
            aboutBlogIntro: data.about_blog_intro || '这是一个基于Vue2和SpringBoot的个人博客系统，具有文章，表白墙，图片墙，收藏夹，音乐播放，视频播放，留言，友链，后台管理等功能。',
            aboutTechStack: data.about_tech_stack || '本网站采用前后端分离进行实现，前端使用Vue2和Element UI，后端使用Java SpringBoot和MySQL。',
            aboutAuthor: data.about_author || '热爱编程，喜欢分享技术知识和生活感悟。希望通过这个博客平台与大家交流学习，共同进步。',
            aboutContact: data.about_contact || '如果您有任何问题或建议，欢迎通过留言板与我联系，我会尽快回复。',
            aboutEmail: data.about_email || '',
            aboutGithub: data.about_github || '',
            aboutWechat: data.about_wechat || '',
            aboutWechatPublic: data.about_wechat_public || '',
            wechatQrCode: data.wechat_qr_code || '',
            wechatPublicQrCode: data.wechat_official_qr_code || '',
            // AI配置从单独的接口获取
            ...aiResponse
          }
        } else {
          this.$message({
            message: systemResponse.message || '获取设置失败',
            type: 'error',
            duration: 3000
          })
        }
      }).catch(error => {
        console.error('获取设置失败', error)
        this.$message({
          message: '获取设置失败：' + (error.message || '未知错误'),
          type: 'error',
          duration: 3000
        })
      }).finally(() => {
        this.loading = false
      })
    },

    // 获取AI配置
    async fetchAiSettings() {
      try {
        const response = await this.$axios.get('/admin/ai/configs')
        if (response.data.code === 200) {
          const configList = response.data.data || []
          // 将配置列表转换为键值对对象
          const configs = {}
          configList.forEach(config => {
            configs[config.configKey] = config.configValue
          })
          
          return {
            aiEnabled: configs['ai.enabled'] === '1' ? 1 : 0,
            deepseekApiKey: configs['ai.api.key'] || '',
            deepseekApiUrl: configs['ai.api.url'] || 'https://api.deepseek.com/v1/chat/completions',
            deepseekModel: configs['ai.model'] || 'deepseek-chat',
            aiMaxTokens: parseInt(configs['ai.max.tokens']) || 2000,
            aiTemperature: parseFloat(configs['ai.temperature']) || 0.7,
            aiSystemPrompt: configs['ai.system.prompt'] || '你是一个智能的博客助手，可以帮助用户回答问题和提供写作建议。请以友好和专业的方式回应。',
            aiWritingPrompt: configs['ai.writing.prompt'] || '你是一个专业的写作助手。请根据用户提供的内容和要求，提供具体的写作建议和改进意见。',
            aiPolishPrompt: configs['ai.polish.prompt'] || '你是一个专业的文章编辑。请对用户提供的文章内容进行润色，提升语言表达、逻辑结构和可读性。'
          }
        }
      } catch (error) {
        console.error('获取AI配置失败', error)
      }
      // 返回默认值
      return {
        aiEnabled: 0,
        deepseekApiKey: '',
        deepseekApiUrl: 'https://api.deepseek.com/v1/chat/completions',
        deepseekModel: 'deepseek-chat',
        aiMaxTokens: 2000,
        aiTemperature: 0.7,
        aiSystemPrompt: '你是一个智能的博客助手，可以帮助用户回答问题和提供写作建议。请以友好和专业的方式回应。',
        aiWritingPrompt: '你是一个专业的写作助手。请根据用户提供的内容和要求，提供具体的写作建议和改进意见。',
        aiPolishPrompt: '你是一个专业的文章编辑。请对用户提供的文章内容进行润色，提升语言表达、逻辑结构和可读性。'
      }
    },
    
    saveSettings() {
      this.$refs.settingsForm.validate((valid) => {
        if (valid) {
          this.loading = true
          
          // 分别保存系统设置和AI配置
          Promise.all([
            this.saveSystemSettings(),
            this.saveAiSettings()
          ]).then(() => {
            this.$message({
              message: '保存设置成功',
              type: 'success',
              duration: 3000
            })
            // 重新加载设置，确保数据一致性
            this.fetchSettings()
          }).catch(error => {
            console.error('保存设置失败', error)
            this.$message({
              message: '保存设置失败：' + (error.message || '未知错误'),
              type: 'error',
              duration: 3000
            })
          }).finally(() => {
            this.loading = false
          })
        } else {
          this.loading = false
        }
      })
    },

    // 保存系统设置（不包含AI配置）
    async saveSystemSettings() {
      const data = {
        site_title: this.settings.siteTitle,
        site_description: this.settings.siteDescription,
        site_keywords: this.settings.siteKeywords,
        site_author: this.settings.siteAuthor,
        site_logo: this.settings.siteLogo,
        site_favicon: this.settings.siteFavicon,
        email_host: this.settings.emailHost,
        email_port: this.settings.emailPort.toString(),
        email_username: this.settings.emailUsername,
        email_password: this.settings.emailPassword,
        email_from: this.settings.emailFrom,
        // 确保开关值正确转换为字符串
        comment_audit: (this.settings.commentAudit === 1 || this.settings.commentAudit === true || this.settings.commentAudit === '1') ? '1' : '0',
        user_register: (this.settings.userRegister === 1 || this.settings.userRegister === true || this.settings.userRegister === '1') ? '1' : '0',
        articles_per_page: this.settings.articlesPerPage.toString(),
        comments_per_page: this.settings.commentsPerPage.toString(),
        about_blog_intro: this.settings.aboutBlogIntro,
        about_tech_stack: this.settings.aboutTechStack,
        about_author: this.settings.aboutAuthor,
        about_contact: this.settings.aboutContact,
        about_email: this.settings.aboutEmail,
        about_github: this.settings.aboutGithub,
        about_wechat: this.settings.aboutWechat,
        about_wechat_public: this.settings.aboutWechatPublic,
        wechat_qr_code: this.settings.wechatQrCode,
        wechat_official_qr_code: this.settings.wechatPublicQrCode
      }
      
      console.log('保存系统设置，数据：', data)
      
      const response = await api.system.updateSettings(data)
      if (!response || response.code !== 200) {
        throw new Error(response.message || '保存系统设置失败')
      }
    },

    // 保存AI配置
    async saveAiSettings() {
      const configs = {
        'ai.enabled': (this.settings.aiEnabled === 1 || this.settings.aiEnabled === true || this.settings.aiEnabled === '1') ? '1' : '0',
        'ai.api.key': this.settings.deepseekApiKey,
        'ai.api.url': this.settings.deepseekApiUrl,
        'ai.model': this.settings.deepseekModel,
        'ai.max.tokens': this.settings.aiMaxTokens.toString(),
        'ai.temperature': this.settings.aiTemperature.toString(),
        'ai.system.prompt': this.settings.aiSystemPrompt,
        'ai.writing.prompt': this.settings.aiWritingPrompt,
        'ai.polish.prompt': this.settings.aiPolishPrompt
      }
      
      console.log('保存AI配置，数据：', configs)
      
      const response = await this.$axios.put('/admin/ai/configs', configs)
      if (!response.data || response.data.code !== 200) {
        throw new Error(response.data.message || '保存AI配置失败')
      }
    },
    
    resetForm() {
      this.$refs.settingsForm.resetFields()
      this.fetchSettings()
    },
    
    // 二维码上传前的验证
    beforeQRUpload(file) {
      const isImage = file.type.indexOf('image/') === 0
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
          message: '上传图片大小不能超过 2MB!',
          type: 'error',
          duration: 3000
        })
        return false
      }
      return true
    },
    
    // 微信二维码上传成功
    handleWechatQRSuccess(response) {
      console.log('微信二维码上传响应:', response)
      if (response.code === 200) {
        let qrUrl = response.data.url;
        
        // 如果返回的是完整URL，转换为相对路径以使用前端代理
        if (qrUrl.startsWith('http://localhost:8080')) {
          qrUrl = qrUrl.replace('http://localhost:8080', '');
        }
        
        this.settings.wechatQrCode = qrUrl;
        this.$message({
          message: '微信二维码上传成功',
          type: 'success',
          duration: 3000
        })
        console.log('设置微信二维码URL:', qrUrl)
        // 自动保存设置
        this.saveSettings()
      } else {
        this.$message({
          message: response.message || '上传失败',
          type: 'error',
          duration: 3000
        })
      }
    },
    
    // 微信公众号二维码上传成功
    handleWechatPublicQRSuccess(response) {
      console.log('微信公众号二维码上传响应:', response)
      if (response.code === 200) {
        let qrUrl = response.data.url;
        
        // 如果返回的是完整URL，转换为相对路径以使用前端代理
        if (qrUrl.startsWith('http://localhost:8080')) {
          qrUrl = qrUrl.replace('http://localhost:8080', '');
        }
        
        this.settings.wechatPublicQrCode = qrUrl;
        this.$message({
          message: '微信公众号二维码上传成功',
          type: 'success',
          duration: 3000
        })
        console.log('设置微信公众号二维码URL:', qrUrl)
        // 自动保存设置
        this.saveSettings()
      } else {
        this.$message({
          message: response.message || '上传失败',
          type: 'error',
          duration: 3000
        })
      }
    },
    
    // 删除微信二维码
    removeWechatQR() {
      this.$confirm('确定要删除微信二维码吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.settings.wechatQrCode = ''
        this.$message({
          message: '删除成功',
          type: 'success',
          duration: 3000
        })
      }).catch(() => {})
    },
    
    // 删除微信公众号二维码
    removeWechatPublicQR() {
      this.$confirm('确定要删除微信公众号二维码吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.settings.wechatPublicQrCode = ''
        this.$message({
          message: '删除成功',
          type: 'success',
          duration: 3000
        })
      }).catch(() => {})
    },
    
    // 获取图片URL
    getImageUrl(url) {
      if (!url) return '';
      
      // 如果是完整URL，转换为相对路径
      if (url.startsWith('http://localhost:8080')) {
        return url.replace('http://localhost:8080', '');
      }
      
      // 确保以/开头
      if (!url.startsWith('/')) {
        url = '/' + url;
      }
      
      // 确保有正确的路径前缀
      if (!url.startsWith('/images/') && !url.startsWith('/uploads/')) {
        url = '/images' + url;
      }
      
      return url;
    },
    
    // 图片加载错误处理
    handleImageError(event) {
      console.error('图片加载失败:', event.target.src);
      // 设置默认占位符
      event.target.style.backgroundColor = '#f5f5f5';
      event.target.style.display = 'flex';
      event.target.style.alignItems = 'center';
      event.target.style.justifyContent = 'center';
      event.target.innerHTML = '<span style="color: #999;">图片加载失败</span>';
    },
    
    // 温度参数格式化
    formatTemperatureTooltip(value) {
      if (value <= 0.3) return `${value} (保守)`
      if (value <= 0.7) return `${value} (平衡)`
      return `${value} (创新)`
    },
    
    // 测试AI连接
    testAiConnection() {
      if (!this.settings.deepseekApiKey) {
        this.$message({
          message: '请先配置DeepSeek API Key',
          type: 'warning',
          duration: 3000
        })
        return
      }
      
      this.testingAi = true
      this.aiTestResult = null
      
      // 调用后端API测试连接
      api.ai.testConnection({
        apiKey: this.settings.deepseekApiKey,
        apiUrl: this.settings.deepseekApiUrl,
        model: this.settings.deepseekModel
      })
        .then(response => {
          if (response && response.code === 200) {
            this.aiTestResult = {
              success: true,
              message: 'AI连接测试成功！'
            }
            this.$message({
              message: 'AI连接测试成功',
              type: 'success',
              duration: 3000
            })
          } else {
            this.aiTestResult = {
              success: false,
              message: response.message || 'AI连接测试失败'
            }
            this.$message({
              message: response.message || 'AI连接测试失败',
              type: 'error',
              duration: 3000
            })
          }
        })
        .catch(error => {
          console.error('AI连接测试失败', error)
          this.aiTestResult = {
            success: false,
            message: 'AI连接测试失败：' + (error.message || '网络错误')
          }
          this.$message({
            message: 'AI连接测试失败：' + (error.message || '网络错误'),
            type: 'error',
            duration: 3000
          })
        })
        .finally(() => {
          this.testingAi = false
        })
    }
  }
}
</script>

<style scoped>
.system-setting {
  padding: 20px;
}

.el-divider {
  margin: 30px 0 20px 0;
}

.el-divider__text {
  font-weight: bold;
  color: #409EFF;
}

/* 二维码上传样式 */
.qr-upload-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.qr-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.qr-uploader:hover {
  border-color: #409EFF;
}

.qr-placeholder {
  width: 150px;
  height: 150px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #fafafa;
  color: #8c939d;
  transition: all 0.3s ease;
}

.qr-placeholder:hover {
  background-color: #f5f7fa;
  color: #409EFF;
}

.qr-placeholder i {
  font-size: 28px;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 14px;
  text-align: center;
}

.qr-preview {
  width: 150px;
  height: 150px;
  position: relative;
  overflow: hidden;
}

.qr-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.qr-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.qr-preview:hover .qr-overlay {
  opacity: 1;
}

.qr-overlay i {
  font-size: 20px;
  margin-bottom: 5px;
}

.qr-overlay span {
  font-size: 12px;
}

.qr-actions {
  margin-top: 10px;
}

/* AI配置样式 */
.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
  line-height: 1.4;
}

.ai-status {
  margin-top: 10px;
}

.el-slider {
  margin: 10px 0;
}

.el-form-item__content .el-input-number {
  width: 200px;
}

.el-form-item__content .el-select {
  width: 200px;
}

/* AI功能开关样式 */
.el-switch {
  margin-right: 10px;
}

/* 测试按钮样式 */
.el-button--primary {
  margin-right: 10px;
}
</style>