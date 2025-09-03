<template>
  <div class="settings-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span class="card-title">
          <i class="el-icon-setting"></i>
          系统设置
        </span>
      </div>
      
      <el-form :model="settingsForm" :rules="rules" ref="settingsForm" label-width="120px">
        <el-tabs v-model="activeTab" type="border-card">
          <!-- 基本设置 -->
          <el-tab-pane label="基本设置" name="basic">
            <el-form-item label="网站名称" prop="siteName">
              <el-input v-model="settingsForm.siteName" placeholder="请输入网站名称"></el-input>
            </el-form-item>
            
            <el-form-item label="网站描述" prop="siteDescription">
              <el-input 
                type="textarea" 
                v-model="settingsForm.siteDescription" 
                placeholder="请输入网站描述"
                :rows="3">
              </el-input>
            </el-form-item>
            
            <el-form-item label="网站关键词" prop="siteKeywords">
              <el-input v-model="settingsForm.siteKeywords" placeholder="请输入网站关键词，用逗号分隔"></el-input>
            </el-form-item>
            
            <el-form-item label="网站Logo" prop="siteLogo">
              <el-input v-model="settingsForm.siteLogo" placeholder="请输入Logo地址">
                <el-button slot="append" @click="uploadLogo">上传</el-button>
              </el-input>
            </el-form-item>
            
            <el-form-item label="网站图标" prop="siteFavicon">
              <el-input v-model="settingsForm.siteFavicon" placeholder="请输入图标地址">
                <el-button slot="append" @click="uploadFavicon">上传</el-button>
              </el-input>
            </el-form-item>
            
            <el-form-item label="备案号" prop="siteIcp">
              <el-input v-model="settingsForm.siteIcp" placeholder="请输入备案号"></el-input>
            </el-form-item>
            
            <el-form-item label="版权信息" prop="siteCopyright">
              <el-input v-model="settingsForm.siteCopyright" placeholder="请输入版权信息"></el-input>
            </el-form-item>
          </el-tab-pane>
          
          <!-- 联系方式 -->
          <el-tab-pane label="联系方式" name="contact">
            <el-form-item label="作者" prop="siteAuthor">
              <el-input v-model="settingsForm.siteAuthor" placeholder="请输入作者名称"></el-input>
            </el-form-item>
            
            <el-form-item label="邮箱" prop="siteEmail">
              <el-input v-model="settingsForm.siteEmail" placeholder="请输入邮箱地址"></el-input>
            </el-form-item>
            
            <el-form-item label="QQ" prop="siteQq">
              <el-input v-model="settingsForm.siteQq" placeholder="请输入QQ号"></el-input>
            </el-form-item>
            
            <el-form-item label="微信" prop="siteWechat">
              <el-input v-model="settingsForm.siteWechat" placeholder="请输入微信号"></el-input>
            </el-form-item>
            
            <el-form-item label="GitHub" prop="siteGithub">
              <el-input v-model="settingsForm.siteGithub" placeholder="请输入GitHub地址"></el-input>
            </el-form-item>
            
            <el-form-item label="Gitee" prop="siteGitee">
              <el-input v-model="settingsForm.siteGitee" placeholder="请输入Gitee地址"></el-input>
            </el-form-item>
          </el-tab-pane>
          
          <!-- 功能设置 -->
          <el-tab-pane label="功能设置" name="features">
            <el-form-item label="开启评论">
              <el-switch v-model="settingsForm.commentEnabled"></el-switch>
            </el-form-item>
            
            <el-form-item label="开启留言">
              <el-switch v-model="settingsForm.messageEnabled"></el-switch>
            </el-form-item>
            
            <el-form-item label="开启注册">
              <el-switch v-model="settingsForm.registerEnabled"></el-switch>
            </el-form-item>
            
            <el-form-item label="邮箱验证">
              <el-switch v-model="settingsForm.emailVerifyEnabled"></el-switch>
            </el-form-item>
            
            <el-form-item label="每页文章数" prop="articlesPerPage">
              <el-input-number 
                v-model="settingsForm.articlesPerPage" 
                :min="1" 
                :max="50"
                controls-position="right">
              </el-input-number>
            </el-form-item>
            
            <el-form-item label="每页评论数" prop="commentsPerPage">
              <el-input-number 
                v-model="settingsForm.commentsPerPage" 
                :min="1" 
                :max="100"
                controls-position="right">
              </el-input-number>
            </el-form-item>
          </el-tab-pane>
        </el-tabs>
        
        <div class="form-actions">
          <el-button type="primary" @click="saveSettings" :loading="saving">
            <i class="el-icon-check"></i>
            保存设置
          </el-button>
          <el-button @click="resetForm">
            <i class="el-icon-refresh"></i>
            重置
          </el-button>
          <el-button type="info" @click="initSettings">
            <i class="el-icon-setting"></i>
            恢复默认
          </el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { adminSettings } from '@/api'

export default {
  name: 'Settings',
  data() {
    return {
      activeTab: 'basic',
      saving: false,
      settingsForm: {
        siteName: '',
        siteDescription: '',
        siteKeywords: '',
        siteLogo: '',
        siteFavicon: '',
        siteIcp: '',
        siteCopyright: '',
        siteAuthor: '',
        siteEmail: '',
        siteQq: '',
        siteWechat: '',
        siteGithub: '',
        siteGitee: '',
        commentEnabled: true,
        messageEnabled: true,
        registerEnabled: true,
        emailVerifyEnabled: false,
        articlesPerPage: 10,
        commentsPerPage: 10
      },
      rules: {
        siteName: [
          { required: true, message: '请输入网站名称', trigger: 'blur' }
        ],
        siteDescription: [
          { required: true, message: '请输入网站描述', trigger: 'blur' }
        ],
        siteAuthor: [
          { required: true, message: '请输入作者名称', trigger: 'blur' }
        ],
        siteEmail: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        articlesPerPage: [
          { required: true, message: '请输入每页文章数', trigger: 'blur' }
        ],
        commentsPerPage: [
          { required: true, message: '请输入每页评论数', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.loadSettings()
  },
  methods: {
    async loadSettings() {
      try {
        const response = await adminSettings.getSettings()
        if (response.code === 200) {
          this.settingsForm = { ...response.data }
        }
      } catch (error) {
        console.error('加载系统设置失败:', error)
        this.$message.error('加载系统设置失败')
      }
    },
    
    async saveSettings() {
      this.$refs.settingsForm.validate(async (valid) => {
        if (valid) {
          this.saving = true
          try {
            const response = await adminSettings.updateSettings(this.settingsForm)
            if (response.code === 200) {
              this.$message.success('系统设置保存成功')
            } else {
              this.$message.error(response.message || '保存失败')
            }
          } catch (error) {
            console.error('保存系统设置失败:', error)
            this.$message.error('保存系统设置失败')
          } finally {
            this.saving = false
          }
        }
      })
    },
    
    resetForm() {
      this.$refs.settingsForm.resetFields()
      this.loadSettings()
    },
    
    async initSettings() {
      this.$confirm('确定要恢复默认设置吗？此操作将覆盖当前设置。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await adminSettings.initSettings()
          if (response.code === 200) {
            this.$message.success('恢复默认设置成功')
            this.loadSettings()
          } else {
            this.$message.error(response.message || '恢复默认设置失败')
          }
        } catch (error) {
          console.error('恢复默认设置失败:', error)
          this.$message.error('恢复默认设置失败')
        }
      })
    },
    
    uploadLogo() {
      this.$message.info('文件上传功能待实现')
    },
    
    uploadFavicon() {
      this.$message.info('文件上传功能待实现')
    }
  }
}
</script>

<style scoped>
.settings-container {
  padding: 20px;
}

.box-card {
  max-width: 1000px;
  margin: 0 auto;
}

.card-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.form-actions {
  margin-top: 30px;
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.form-actions .el-button {
  margin: 0 10px;
}

.el-form-item {
  margin-bottom: 22px;
}

.el-tabs--border-card {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.el-tab-pane {
  padding: 20px;
}
</style>