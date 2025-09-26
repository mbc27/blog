<template>
  <div class="ai-settings">
    <div class="page-header">
      <h2>AI助手配置</h2>
      <p>配置AI助手的相关参数和功能</p>
    </div>

    <!-- AI状态卡片 -->
    <el-card class="status-card" shadow="hover">
      <div slot="header" class="card-header">
        <span>AI服务状态</span>
        <el-button 
          type="primary" 
          size="small" 
          @click="testConnection"
          :loading="testing"
        >
          测试连接
        </el-button>
      </div>
      <div class="status-info">
        <div class="status-item">
          <span class="label">服务状态：</span>
          <el-tag :type="aiStatus.enabled ? 'success' : 'danger'">
            {{ aiStatus.enabled ? '已启用' : '已禁用' }}
          </el-tag>
        </div>
        <div class="status-item">
          <span class="label">连接状态：</span>
          <el-tag :type="aiStatus.connected ? 'success' : 'warning'">
            {{ aiStatus.connected ? '连接正常' : '连接异常' }}
          </el-tag>
        </div>
        <div class="status-item" v-if="modelInfo.model">
          <span class="label">当前模型：</span>
          <span>{{ modelInfo.model }}</span>
        </div>
      </div>
    </el-card>

    <!-- 配置表单 -->
    <el-card class="config-card" shadow="hover">
      <div slot="header" class="card-header">
        <span>配置管理</span>
        <el-button 
          type="success" 
          size="small" 
          @click="saveConfigs"
          :loading="saving"
        >
          保存配置
        </el-button>
      </div>

      <el-tabs v-model="activeTab" type="border-card">
        <!-- API配置 -->
        <el-tab-pane label="API配置" name="api">
          <el-form :model="configs" label-width="150px">
            <el-form-item label="DeepSeek API密钥">
              <el-input 
                v-model="configs['deepseek.api.key']" 
                type="password" 
                placeholder="请输入DeepSeek API密钥"
                show-password
              />
              <div class="form-tip">
                请在 <a href="https://platform.deepseek.com" target="_blank">DeepSeek官网</a> 获取API密钥
              </div>
            </el-form-item>
            
            <el-form-item label="API地址">
              <el-input 
                v-model="configs['deepseek.api.url']" 
                placeholder="DeepSeek API请求地址"
              />
            </el-form-item>
            
            <el-form-item label="模型名称">
              <el-input 
                v-model="configs['deepseek.model']" 
                placeholder="使用的DeepSeek模型"
              />
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 模型配置 -->
        <el-tab-pane label="模型配置" name="model">
          <el-form :model="configs" label-width="150px">
            <el-form-item label="启用AI功能">
              <el-switch 
                v-model="aiEnabled"
                @change="handleAiEnabledChange"
                active-text="启用" 
                inactive-text="禁用"
              />
            </el-form-item>
            
            <el-form-item label="最大Token数">
              <el-input-number 
                v-model="maxTokens" 
                :min="100" 
                :max="8000" 
                :step="100"
                @change="handleMaxTokensChange"
              />
              <div class="form-tip">控制AI回复的最大长度</div>
            </el-form-item>
            
            <el-form-item label="温度参数">
              <el-slider 
                v-model="temperature" 
                :min="0" 
                :max="1" 
                :step="0.1"
                show-input
                @change="handleTemperatureChange"
              />
              <div class="form-tip">控制AI回复的创造性，0为最保守，1为最创造性</div>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 提示词配置 -->
        <el-tab-pane label="提示词配置" name="prompt">
          <el-form :model="configs" label-width="150px">
            <el-form-item label="系统提示词">
              <el-input 
                v-model="configs['ai.system.prompt']" 
                type="textarea" 
                :rows="4"
                placeholder="AI助手的系统提示词"
              />
            </el-form-item>
            
            <el-form-item label="写作助手提示词">
              <el-input 
                v-model="configs['ai.writing.prompt']" 
                type="textarea" 
                :rows="4"
                placeholder="写作辅助功能的提示词"
              />
            </el-form-item>
            
            <el-form-item label="文章润色提示词">
              <el-input 
                v-model="configs['ai.polish.prompt']" 
                type="textarea" 
                :rows="4"
                placeholder="文章润色功能的提示词"
              />
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button @click="loadConfigs">重新加载</el-button>
      <el-button type="warning" @click="initDefaults">恢复默认</el-button>
      <el-button type="primary" @click="saveConfigs" :loading="saving">保存所有配置</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AiSettings',
  data() {
    return {
      activeTab: 'api',
      configs: {},
      aiStatus: {
        enabled: false,
        connected: false
      },
      modelInfo: {},
      testing: false,
      saving: false,
      aiEnabled: false,
      maxTokens: 2000,
      temperature: 0.7
    }
  },
  async mounted() {
    await this.loadConfigs()
    await this.loadAiStatus()
  },
  methods: {
    async loadConfigs() {
      try {
        const response = await this.$api.get('/admin/ai/configs')
        if (response.data.code === 200) {
          const configList = response.data.data
          this.configs = {}
          
          configList.forEach(config => {
            this.configs[config.configKey] = config.configValue
          })
          
          // 设置表单值
          this.aiEnabled = this.configs['ai.enabled'] === '1'
          this.maxTokens = parseInt(this.configs['ai.max.tokens']) || 2000
          this.temperature = parseFloat(this.configs['ai.temperature']) || 0.7
        }
      } catch (error) {
        console.error('加载配置失败:', error)
        this.$message({
          message: '加载配置失败',
          type: 'error',
          duration: 3000
        })
      }
    },

    async loadAiStatus() {
      try {
        const response = await this.$api.get('/admin/ai/status')
        if (response.data.code === 200) {
          this.aiStatus = response.data.data
          this.modelInfo = response.data.data.config || {}
        }
      } catch (error) {
        console.error('加载AI状态失败:', error)
      }
    },

    async testConnection() {
      this.testing = true
      try {
        const response = await this.$api.post('/admin/ai/test-connection')
        if (response.data.code === 200) {
          const result = response.data.data
          this.$message({
            type: result.connected ? 'success' : 'error',
            message: result.message
          })
          
          if (result.connected && result.modelInfo) {
            this.modelInfo = result.modelInfo
          }
        }
      } catch (error) {
        console.error('测试连接失败:', error)
        this.$message({
          message: '测试连接失败',
          type: 'error',
          duration: 3000
        })
      } finally {
        this.testing = false
      }
    },

    async saveConfigs() {
      this.saving = true
      try {
        const response = await this.$api.put('/admin/ai/configs', this.configs)
        if (response.data.code === 200) {
          this.$message({
            message: '配置保存成功',
            type: 'success',
            duration: 3000
          })
          await this.loadAiStatus()
        } else {
          this.$message({
            message: response.data.message || '保存失败',
            type: 'error',
            duration: 3000
          })
        }
      } catch (error) {
        console.error('保存配置失败:', error)
        this.$message({
          message: '保存配置失败',
          type: 'error',
          duration: 3000
        })
      } finally {
        this.saving = false
      }
    },

    async initDefaults() {
      try {
        await this.$confirm('确定要恢复默认配置吗？这将覆盖当前所有配置。', '确认操作', {
          type: 'warning'
        })
        
        const response = await this.$api.post('/admin/ai/init-defaults')
        if (response.data.code === 200) {
          this.$message({
            message: '默认配置已恢复',
            type: 'success',
            duration: 3000
          })
          await this.loadConfigs()
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('恢复默认配置失败:', error)
          this.$message({
            message: '恢复默认配置失败',
            type: 'error',
            duration: 3000
          })
        }
      }
    },

    handleAiEnabledChange(value) {
      this.configs['ai.enabled'] = value ? '1' : '0'
    },

    handleMaxTokensChange(value) {
      this.configs['ai.max.tokens'] = value.toString()
    },

    handleTemperatureChange(value) {
      this.configs['ai.temperature'] = value.toString()
    }
  }
}
</script>

<style scoped>
.ai-settings {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #303133;
}

.page-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.status-card, .config-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-item .label {
  font-weight: 500;
  color: #606266;
  min-width: 80px;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.form-tip a {
  color: #409EFF;
  text-decoration: none;
}

.form-tip a:hover {
  text-decoration: underline;
}

.action-buttons {
  text-align: center;
  padding: 20px 0;
}

.action-buttons .el-button {
  margin: 0 8px;
}
</style>