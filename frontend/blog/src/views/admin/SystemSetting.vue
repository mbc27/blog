<template>
  <div class="system-setting">
    <el-card class="box-card">
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
        aboutContact: ''
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
      api.system.getSettings()
        .then(response => {
          if (response && response.code === 200) {
            // 将后端返回的设置数据映射到前端数据结构
            const data = response.data || {}
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
              aboutContact: data.about_contact || '如果您有任何问题或建议，欢迎通过留言板与我联系，我会尽快回复。'
            }
          } else {
            this.$message.error(response.message || '获取设置失败')
          }
        })
        .catch(error => {
          console.error('获取设置失败', error)
          this.$message.error('获取设置失败：' + (error.message || '未知错误'))
        })
        .finally(() => {
          this.loading = false
        })
    },
    
    saveSettings() {
      this.$refs.settingsForm.validate((valid) => {
        if (valid) {
          this.loading = true
          
          // 将前端数据结构映射到后端期望的格式
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
            about_contact: this.settings.aboutContact
          }
          
          console.log('保存系统设置，数据：', data)
          
          api.system.updateSettings(data)
            .then(response => {
              if (response && response.code === 200) {
                this.$message.success('保存设置成功')
                // 重新加载设置，确保数据一致性
                this.fetchSettings()
              } else {
                this.$message.error(response.message || '保存设置失败')
              }
            })
            .catch(error => {
              console.error('保存设置失败', error)
              this.$message.error('保存设置失败：' + (error.message || '未知错误'))
            })
            .finally(() => {
              this.loading = false
            })
        } else {
          this.loading = false
        }
      })
    },
    
    resetForm() {
      this.$refs.settingsForm.resetFields()
      this.fetchSettings()
    }
  }
}
</script>

<style scoped>
.system-setting {
  padding: 20px;
}

.box-card {
  max-width: 800px;
}

.el-divider {
  margin: 30px 0 20px 0;
}

.el-divider__text {
  font-weight: bold;
  color: #409EFF;
}
</style>