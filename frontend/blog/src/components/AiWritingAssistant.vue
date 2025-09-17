<template>
  <div class="ai-writing-assistant">
    <!-- AI助手触发按钮 -->
    <div class="ai-toolbar">
      <el-button 
        size="small" 
        type="primary"
        @click="showAiDialog"
        icon="el-icon-magic-stick"
        :loading="aiLoading">
        AI助手
      </el-button>
      <el-button 
        size="small" 
        type="success"
        @click="quickContinueWriting"
        icon="el-icon-edit"
        :loading="aiLoading">
        续写
      </el-button>
      <el-button 
        size="small" 
        type="warning"
        @click="quickOptimize"
        icon="el-icon-star-on"
        :loading="aiLoading">
        优化
      </el-button>
    </div>

    <!-- AI助手弹窗 -->
    <el-dialog
      title="AI写作助手"
      :visible.sync="aiDialogVisible"
      width="70%"
      :close-on-click-modal="false"
      class="ai-assistant-dialog">
      
      <div class="ai-tab-content">
        <el-form :model="aiForm" label-width="100px">
          <el-form-item label="生成类型">
            <el-radio-group v-model="aiForm.generateType">
              <el-radio label="article">完整文章</el-radio>
              <el-radio label="outline">文章大纲</el-radio>
              <el-radio label="paragraph">段落内容</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="主题描述">
            <el-input
              v-model="aiForm.topic"
              type="textarea"
              rows="3"
              placeholder="请描述您想要生成的内容主题，例如：写一篇关于人工智能发展趋势的技术文章"
              maxlength="500"
              show-word-limit>
            </el-input>
          </el-form-item>
          
          <el-form-item label="写作风格">
            <el-select v-model="aiForm.style" placeholder="选择写作风格">
              <el-option label="专业技术" value="technical"></el-option>
              <el-option label="通俗易懂" value="popular"></el-option>
              <el-option label="学术严谨" value="academic"></el-option>
              <el-option label="轻松幽默" value="humorous"></el-option>
              <el-option label="新闻报道" value="news"></el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item>
            <el-button 
              type="primary" 
              @click="generateContent"
              :loading="aiLoading"
              icon="el-icon-magic-stick">
              生成内容
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- AI生成结果展示 -->
      <div v-if="aiResult" class="ai-result-section">
        <el-divider content-position="left">
          <i class="el-icon-magic-stick"></i> AI生成结果
        </el-divider>
        
        <div class="ai-result-content">
          <pre>{{ aiResult }}</pre>
        </div>
        
        <div class="ai-result-actions">
          <el-button size="small" @click="insertResult" type="primary" icon="el-icon-plus">插入到文章末尾</el-button>
          <el-button size="small" @click="replaceContent" type="success" icon="el-icon-refresh">替换当前内容</el-button>
          <el-button size="small" @click="replaceSelectedContent" type="warning" icon="el-icon-edit" v-if="selectedContent">替换选中内容</el-button>
          <el-button size="small" @click="copyResult" type="info" icon="el-icon-document-copy">复制结果</el-button>
          <el-button size="small" @click="clearResult" icon="el-icon-delete">清空结果</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'AiWritingAssistant',
  props: {
    content: {
      type: String,
      default: ''
    },
    selectedContent: {
      type: String,
      default: ''
    },
    title: {
      type: String,
      default: ''
    },
    category: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      aiDialogVisible: false,
      aiLoading: false,
      aiResult: '',
      aiForm: {
        generateType: 'article',
        topic: '',
        style: 'popular'
      }
    }
  },
  methods: {
    // 显示AI对话框
    showAiDialog() {
      this.aiDialogVisible = true;
    },

    // 快速续写
    async quickContinueWriting() {
      // 通知父组件获取最新的选中内容
      this.$emit('get-current-selection');
      
      // 等待一个tick确保选中内容已更新
      await this.$nextTick();
      
      // 判断是否有选中内容
      const hasSelectedContent = this.selectedContent && this.selectedContent.trim();
      const contentToProcess = hasSelectedContent ? this.selectedContent : this.content;
      
      console.log('快速续写 - 选中内容:', this.selectedContent);
      console.log('快速续写 - 处理内容:', contentToProcess.substring(0, 100) + '...');
      
      if (!contentToProcess.trim()) {
        this.$message.warning(hasSelectedContent ? '请先选择一些内容进行续写' : '请先输入一些内容，AI才能进行续写');
        return;
      }
      
      this.aiLoading = true;
      try {
        const response = await this.$api.ai.continueWriting({
          content: contentToProcess,
          length: 'medium',
          title: this.title
        });
        
        if (response.code === 200) {
          // 修复：安全地获取内容，避免 null 错误
          const continuedContent = response.data?.content || response.data || response.message || '';
          if (continuedContent) {
            if (hasSelectedContent) {
              // 有选中内容：替换选中内容
              this.$emit('selected-content-replaced', continuedContent);
              this.$message.success('选中内容续写完成并已替换！');
            } else {
              // 没有选中内容：追加到文章末尾
              this.$emit('content-generated', '\n\n' + continuedContent);
              this.$message.success('AI续写完成并已添加到文章末尾！');
            }
          } else {
            this.$message.error('AI续写返回内容为空');
          }
        } else {
          this.$message.error(response.message || 'AI续写失败');
        }
      } catch (error) {
        console.error('AI续写失败:', error);
        this.$message.error('AI续写失败，请稍后重试');
      } finally {
        this.aiLoading = false;
      }
    },

    // 快速优化
    async quickOptimize() {
      // 通知父组件获取最新的选中内容
      this.$emit('get-current-selection');
      
      // 等待一个tick确保选中内容已更新
      await this.$nextTick();
      
      // 判断是否有选中内容
      const hasSelectedContent = this.selectedContent && this.selectedContent.trim();
      const contentToProcess = hasSelectedContent ? this.selectedContent : this.content;
      
      console.log('快速优化 - 选中内容:', this.selectedContent);
      console.log('快速优化 - 处理内容:', contentToProcess.substring(0, 100) + '...');
      
      if (!contentToProcess.trim()) {
        this.$message.warning(hasSelectedContent ? '请先选择一些内容进行优化' : '请先输入内容，AI才能进行优化');
        return;
      }
      
      this.aiLoading = true;
      try {
        const response = await this.$api.ai.optimizeContent({
          content: contentToProcess,
          types: ['grammar', 'style', 'readability']
        });
        
        if (response.code === 200) {
          // 修复：安全地获取内容
          const optimizedContent = response.data?.content || response.data || response.message || '';
          if (optimizedContent) {
            if (hasSelectedContent) {
              // 有选中内容：替换选中内容
              this.$emit('selected-content-replaced', optimizedContent);
              this.$message.success('选中内容优化完成并已替换！');
            } else {
              // 没有选中内容：追加到文章末尾
              this.$emit('content-generated', '\n\n' + optimizedContent);
              this.$message.success('AI优化完成并已添加到文章末尾！');
            }
          } else {
            this.$message.error('AI优化返回内容为空');
          }
        } else {
          this.$message.error(response.message || 'AI优化失败');
        }
      } catch (error) {
        console.error('AI优化失败:', error);
        this.$message.error('AI优化失败，请稍后重试');
      } finally {
        this.aiLoading = false;
      }
    },

    // 生成内容
    async generateContent() {
      if (!this.aiForm.topic.trim()) {
        this.$message.warning('请输入主题描述');
        return;
      }
      
      this.aiLoading = true;
      try {
        let response;
        if (this.aiForm.generateType === 'article') {
          response = await this.$api.ai.generateArticle({
            topic: this.aiForm.topic,
            style: this.aiForm.style,
            title: this.title,
            category: this.category
          });
        } else if (this.aiForm.generateType === 'outline') {
          response = await this.$api.ai.generateOutline({
            topic: this.aiForm.topic,
            style: this.aiForm.style
          });
        } else {
          response = await this.$api.ai.assistWriting({
            prompt: this.aiForm.topic,
            type: 'paragraph',
            style: this.aiForm.style
          });
        }
        
        if (response.code === 200) {
          console.log('AI生成响应:', response);
          console.log('AI生成内容:', response.data);
          // 兼容处理：内容可能在 data 或 message 字段中
          const generatedContent = response.data || response.message || '';
          if (generatedContent) {
            this.aiResult = generatedContent;
            this.$message.success('AI生成完成！');
          } else {
            this.$message.error('AI生成返回内容为空');
          }
        } else {
          this.$message.error(response.message || 'AI生成失败');
        }
      } catch (error) {
        console.error('AI生成失败:', error);
        this.$message.error('AI生成失败，请稍后重试');
      } finally {
        this.aiLoading = false;
      }
    },



    // 插入结果到文章
    insertResult() {
      if (!this.aiResult) {
        this.$message.warning('没有可插入的内容');
        return;
      }
      
      this.$emit('content-generated', '\n\n' + this.aiResult);
      this.$message.success('内容已插入到文章末尾');
      this.aiDialogVisible = false;
    },

    // 替换当前内容
    replaceContent() {
      if (!this.aiResult) {
        this.$message.warning('没有可替换的内容');
        return;
      }
      
      this.$confirm('确定要替换当前文章内容吗？此操作不可撤销。', '确认替换', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$emit('content-replaced', this.aiResult);
        this.$message.success('内容已替换');
        this.aiDialogVisible = false;
      }).catch(() => {
        this.$message.info('已取消替换');
      });
    },

    // 替换选中内容
    replaceSelectedContent() {
      if (!this.aiResult) {
        this.$message.warning('没有可替换的内容');
        return;
      }
      
      if (!this.selectedContent) {
        this.$message.warning('没有选中内容');
        return;
      }
      
      this.$confirm('确定要替换选中的内容吗？此操作不可撤销。', '确认替换', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$emit('selected-content-replaced', this.aiResult);
        this.$message.success('选中内容已替换');
        this.aiDialogVisible = false;
      }).catch(() => {
        this.$message.info('已取消替换');
      });
    },

    // 复制结果
    copyResult() {
      if (!this.aiResult) {
        this.$message.warning('没有可复制的内容');
        return;
      }
      
      // 创建临时文本区域
      const textarea = document.createElement('textarea');
      textarea.value = this.aiResult;
      document.body.appendChild(textarea);
      textarea.select();
      document.execCommand('copy');
      document.body.removeChild(textarea);
      
      this.$message.success('内容已复制到剪贴板');
    },

    // 清空结果
    clearResult() {
      this.aiResult = '';
      this.aiForm = {
        generateType: 'article',
        topic: '',
        style: 'popular'
      };
      this.$message.success('结果已清空');
    }
  }
}
</script>

<style scoped>
.ai-writing-assistant {
  margin-bottom: 10px;
}

.ai-toolbar {
  display: flex;
  gap: 8px;
  margin-bottom: 10px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}

.ai-assistant-dialog {
  .el-dialog__body {
    padding: 20px;
  }
}

.ai-tab-content {
  padding: 20px 0;
}

.current-content-preview {
  margin: 15px 0;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}

.current-content-preview h4 {
  margin: 0 0 10px 0;
  color: #495057;
  font-size: 14px;
}

.content-preview {
  max-height: 150px;
  overflow-y: auto;
  padding: 10px;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  font-size: 13px;
  line-height: 1.5;
  color: #6c757d;
  white-space: pre-wrap;
}

.content-actions {
  margin-top: 8px;
  text-align: right;
}

.ai-result-section {
  margin-top: 20px;
  padding-top: 20px;
}

.ai-result-content {
  max-height: 300px;
  overflow-y: auto;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 6px;
  border: 1px solid #e9ecef;
  margin-bottom: 15px;
}

.ai-result-content pre {
  margin: 0;
  font-family: inherit;
  font-size: 14px;
  line-height: 1.6;
  color: #495057;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.ai-result-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-alert {
  margin-bottom: 20px;
}

.el-divider {
  margin: 20px 0;
}

.el-divider__text {
  background: white;
  padding: 0 15px;
  color: #409eff;
  font-weight: 500;
}
</style>