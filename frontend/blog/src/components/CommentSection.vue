<template>
  <div class="comment-section">
    <!-- 评论输入框 -->
    <div class="comment-input-container">
      <div class="comment-input-header">
        <div class="user-info" v-if="isAuthenticated">
          <el-avatar :src="user.avatar || defaultAvatar" size="small"></el-avatar>
          <span class="username">{{ user.nickname || '我' }}</span>
        </div>
        <div class="comment-tips" v-else>
          <i class="el-icon-info"></i>
          <span>登录后才能发表评论</span>
        </div>
      </div>
      
      <div class="comment-input">
        <el-input
          type="textarea"
          :rows="3"
          v-model="newComment"
          :placeholder="isAuthenticated ? '说点什么吧...' : '请登录后发表评论'"
          class="comment-textarea"
          :disabled="!isAuthenticated"
        ></el-input>
        
        <div class="comment-toolbar">
          <div class="emoji-picker">
            <el-button 
              type="text" 
              :disabled="!isAuthenticated"
              class="emoji-button"
              @click="toggleEmojiPanel"
            >
              <i class="el-icon-sunny"></i> 表情
            </el-button>
            
            <div v-show="showEmojiPicker" class="emoji-panel">
              <div class="emoji-container">
                <div class="emoji-categories">
                  <span 
                    v-for="(category, index) in emojiCategories" 
                    :key="index"
                    :class="['emoji-category', { active: currentEmojiCategory === index }]"
                    @click="currentEmojiCategory = index"
                  >
                    {{ category.icon }}
                  </span>
                </div>
                <div class="emoji-list">
                  <span 
                    v-for="emoji in currentCategoryEmojis" 
                    :key="emoji"
                    class="emoji-item"
                    @click="insertEmoji(emoji)"
                  >
                    {{ emoji }}
                  </span>
                </div>
              </div>
            </div>
          </div>
          
          <div class="comment-actions">
            <el-button 
              v-if="isAuthenticated"
              type="primary" 
              size="small" 
              @click="submitComment" 
              :loading="submitting"
              icon="el-icon-s-promotion"
            >
              发表评论
            </el-button>
            <el-button 
              v-else
              type="primary" 
              size="small" 
              @click="goToLogin"
              icon="el-icon-user"
            >
              登录后评论
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 评论列表 -->
    <div class="comment-list" v-if="comments.length">
      <div v-for="comment in comments" :key="comment.id" class="comment-item">
        <div class="comment-header">
          <el-avatar :src="comment.avatar || defaultAvatar" size="small"></el-avatar>
          <span class="comment-author">{{ comment.nickname }}</span>
          <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
        </div>
        <div class="comment-content">{{ comment.content }}</div>
        
        <!-- 评论操作 -->
        <div class="comment-actions-bar">
          <el-button 
            type="text" 
            size="mini" 
            @click="toggleReplyInput(comment.id)"
            icon="el-icon-chat-dot-round"
          >
            回复 ({{ comment.replyCount || 0 }})
          </el-button>
          <span class="comment-like">
            <i class="el-icon-thumb"></i>
            {{ comment.likeCount || 0 }}
          </span>
        </div>

        <!-- 回复输入框 -->
        <div v-if="replyingTo === comment.id" class="reply-input">
          <el-input
            type="textarea"
            :rows="2"
            v-model="replyContent"
            :placeholder="`回复 @${comment.nickname}`"
            class="reply-textarea"
          ></el-input>
          <div class="reply-toolbar">
            <div class="emoji-picker">
              <el-button 
                type="text"
                class="emoji-button"
                @click="toggleReplyEmojiPanel"
              >
                <i class="el-icon-sunny"></i> 表情
              </el-button>
              
              <div v-show="showReplyEmojiPicker" class="emoji-panel">
                <div class="emoji-container">
                  <div class="emoji-categories">
                    <span 
                      v-for="(category, index) in emojiCategories" 
                      :key="index"
                      :class="['emoji-category', { active: currentEmojiCategory === index }]"
                      @click="currentEmojiCategory = index"
                    >
                      {{ category.icon }}
                    </span>
                  </div>
                  <div class="emoji-list">
                    <span 
                      v-for="emoji in currentCategoryEmojis" 
                      :key="emoji"
                      class="emoji-item"
                      @click="insertReplyEmoji(emoji)"
                    >
                      {{ emoji }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="reply-actions">
              <el-button size="mini" @click="cancelReply" icon="el-icon-close">取消</el-button>
              <el-button 
                type="primary" 
                size="mini" 
                @click="submitReply(comment)" 
                :loading="replySubmitting"
                icon="el-icon-s-promotion"
              >
                回复
              </el-button>
            </div>
          </div>
        </div>

        <!-- 子评论 -->
        <div class="child-comments" v-if="comment.children && comment.children.length">
          <div 
            v-for="child in comment.children" 
            :key="child.id" 
            class="child-comment-item"
          >
            <div class="comment-header">
              <el-avatar :src="child.avatar || defaultAvatar" size="small"></el-avatar>
              <span class="comment-author">{{ child.nickname }}</span>
              <span class="reply-to" v-if="child.replyToNickname">
                回复 @{{ child.replyToNickname }}
              </span>
              <span class="comment-time">{{ formatTime(child.createTime) }}</span>
            </div>
            <div class="comment-content">{{ child.content }}</div>
            <div class="comment-actions-bar">
              <el-button 
                type="text" 
                size="mini" 
                @click="replyToChild(comment, child)"
                icon="el-icon-chat-dot-round"
              >
                回复
              </el-button>
              <span class="comment-like">
                <i class="el-icon-thumb"></i>
                {{ child.likeCount || 0 }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="no-comments">
      <i class="el-icon-chat-dot-round no-comments-icon"></i>
      <p>暂无评论，快来发表第一条评论吧！</p>
      <el-button 
        v-if="isAuthenticated" 
        type="primary" 
        size="small" 
        @click="focusCommentInput"
        plain
      >
        写下我的评论
      </el-button>
      <el-button 
        v-else 
        type="primary" 
        size="small" 
        @click="goToLogin"
        plain
      >
        登录后评论
      </el-button>
    </div>

    <!-- 分页 -->
    <el-pagination
      v-if="pagination.total > pagination.size"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pagination.current"
      :page-sizes="[5, 10, 20, 50]"
      :page-size="pagination.size"
      layout="total, sizes, prev, pager, next, jumper"
      :total="pagination.total"
      class="pagination"
    ></el-pagination>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import api from '../api'

export default {
  name: 'CommentSection',
  props: {
    articleId: {
      type: [String, Number],
      required: true
    }
  },
  data() {
    return {
      comments: [],
      newComment: '',
      submitting: false,
      replyingTo: null,
      replyContent: '',
      replySubmitting: false,
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      pagination: {
        current: 1,
        size: 10,
        total: 0
      },
      siteSettings: {}, // 系统设置
      showEmojiPicker: false,
      showReplyEmojiPicker: false,
      currentEmojiCategory: 0,
      emojiCategories: [
        { name: '表情', icon: '😀', emojis: ['😀', '😃', '😄', '😁', '😆', '😅', '😂', '🤣', '😊', '😇', '🙂', '🙃', '😉', '😌', '😍', '🥰', '😘', '😗', '😙', '😚', '😋', '😛', '😝', '😜', '🤪', '🤨', '🧐', '🤓', '😎', '🤩', '🥳'] },
        { name: '手势', icon: '👍', emojis: ['👍', '👎', '👌', '✌️', '🤞', '🤟', '🤘', '🤙', '👈', '👉', '👆', '👇', '☝️', '👋', '🤚', '🖐️', '✋', '🖖', '👏', '🙌', '👐', '🤲', '🤝', '🙏', '✍️'] },
        { name: '动物', icon: '🐱', emojis: ['🐱', '🐶', '🐭', '🐹', '🐰', '🦊', '🐻', '🐼', '🐨', '🐯', '🦁', '🐮', '🐷', '🐸', '🐵', '🐔', '🐧', '🐦', '🐤', '🦆', '🦅', '🦉', '🦇', '🐺', '🐗', '🐴', '🦄', '🐝', '🐛', '🦋', '🐌'] },
        { name: '食物', icon: '🍎', emojis: ['🍎', '🍐', '🍊', '🍋', '🍌', '🍉', '🍇', '🍓', '🍈', '🍒', '🍑', '🥭', '🍍', '🥥', '🥝', '🍅', '🍆', '🥑', '🥦', '🥬', '🥒', '🌶️', '🌽', '🥕', '🧄', '🧅', '🥔', '🍠', '🥐', '🥯', '🍞'] },
        { name: '爱心', icon: '❤️', emojis: ['❤️', '🧡', '💛', '💚', '💙', '💜', '🖤', '🤍', '🤎', '💔', '❣️', '💕', '💞', '💓', '💗', '💖', '💘', '💝', '💟', '☮️', '✝️', '☪️', '🕉️', '☸️', '✡️', '🔯', '🕎', '☯️', '☦️', '🛐', '⛎'] }
      ]
    }
  },
  computed: {
    ...mapGetters(['isAuthenticated', 'user']),
    currentCategoryEmojis() {
      if (this.currentEmojiCategory >= 0 && this.currentEmojiCategory < this.emojiCategories.length) {
        return this.emojiCategories[this.currentEmojiCategory].emojis;
      }
      return [];
    }
  },
  watch: {
    articleId: {
      immediate: true,
      handler() {
        if (this.articleId) {
          this.loadSiteSettings()
        }
      }
    }
  },
  methods: {
    // 加载系统设置
    async loadSiteSettings() {
      try {
        const response = await api.system.getPublicSettings()
        if (response.code === 200) {
          this.siteSettings = response.data
          console.log('获取到的系统设置:', this.siteSettings)
          
          // 设置每页评论数
          const commentsPerPage = parseInt(this.siteSettings.comments_per_page)
          this.pagination.size = !isNaN(commentsPerPage) ? commentsPerPage : 10
          console.log('系统设置加载成功，每页评论数:', this.pagination.size)
          
          // 记录评论审核设置
          console.log('评论审核设置:', this.siteSettings.comment_audit)
          
          this.getComments() // 加载评论列表
        } else {
          this.getComments() // 即使获取设置失败，也加载评论列表
        }
      } catch (error) {
        console.error('获取系统设置失败:', error)
        this.getComments() // 即使获取设置失败，也加载评论列表
      }
    },
    
    focusCommentInput() {
      this.$nextTick(() => {
        const textarea = this.$el.querySelector('.comment-textarea textarea');
        if (textarea) {
          textarea.focus();
        }
      });
    },
    
    toggleEmojiPanel() {
      this.showEmojiPicker = !this.showEmojiPicker;
      // 关闭回复表情面板
      if (this.showEmojiPicker) {
        this.showReplyEmojiPicker = false;
      }
      // 点击外部关闭表情面板
      if (this.showEmojiPicker) {
        this.$nextTick(() => {
          const closePanel = (e) => {
            if (!this.$el.querySelector('.emoji-picker').contains(e.target)) {
              this.showEmojiPicker = false;
              document.removeEventListener('click', closePanel);
            }
          };
          setTimeout(() => {
            document.addEventListener('click', closePanel);
          }, 100);
        });
      }
    },
    
    toggleReplyEmojiPanel() {
      this.showReplyEmojiPicker = !this.showReplyEmojiPicker;
      // 关闭主评论表情面板
      if (this.showReplyEmojiPicker) {
        this.showEmojiPicker = false;
      }
      // 点击外部关闭表情面板
      if (this.showReplyEmojiPicker) {
        this.$nextTick(() => {
          const closePanel = (e) => {
            const emojiPicker = this.$el.querySelector('.reply-input .emoji-picker');
            if (emojiPicker && !emojiPicker.contains(e.target)) {
              this.showReplyEmojiPicker = false;
              document.removeEventListener('click', closePanel);
            }
          };
          setTimeout(() => {
            document.addEventListener('click', closePanel);
          }, 100);
        });
      }
    },
    
    insertEmoji(emoji) {
      this.newComment += emoji;
      this.showEmojiPicker = false;
    },
    
    insertReplyEmoji(emoji) {
      this.replyContent += emoji;
      this.showReplyEmojiPicker = false;
    },
    
    async getComments() {
      try {
        const params = {
          current: this.pagination.current,
          size: this.pagination.size
        }
        const response = await api.comment.getByArticle(this.articleId, params)
        if (response.code === 200) {
          const comments = response.data.records || []
          
          // 只处理顶级评论（level=1），回复评论已经在children中
          this.comments = comments
            .filter(comment => comment.level === 1 || !comment.parentId) // 只显示顶级评论
            .map(comment => {
              return {
                ...comment,
                nickname: comment.user ? comment.user.nickname : comment.nickname || '匿名用户',
                avatar: comment.user ? comment.user.avatar : comment.avatar || this.defaultAvatar,
                replyCount: comment.children ? comment.children.length : 0,
                likeCount: comment.likeCount || 0,
                isLiked: comment.isLiked || false,
                children: (comment.children || []).map(child => {
                  return {
                    ...child,
                    nickname: child.user ? child.user.nickname : child.nickname || '匿名用户',
                    avatar: child.user ? child.user.avatar : child.avatar || this.defaultAvatar,
                    replyToNickname: child.replyToUser ? child.replyToUser.nickname : child.replyToNickname,
                    likeCount: child.likeCount || 0,
                    isLiked: child.isLiked || false
                  }
                })
              }
            })
          
          this.pagination.total = response.data.total || 0
          // 通知父组件更新评论数
          this.$emit('comment-count-updated', this.pagination.total)
        }
      } catch (error) {
        console.error('获取评论失败:', error)
        this.comments = []
        this.pagination.total = 0
      }
    },

    async submitComment() {
      if (!this.isAuthenticated) {
        this.goToLogin()
        return
      }

      if (!this.newComment.trim()) {
        this.$message.warning('评论内容不能为空')
        return
      }

      this.submitting = true
      try {
        const commentData = {
          articleId: this.articleId,
          content: this.newComment.trim()
        }
        const response = await api.comment.add(commentData)
        if (response.code === 200) {
          // 根据系统设置判断是否需要审核
          const commentAudit = this.siteSettings.comment_audit === '1' || this.siteSettings.comment_audit === 1
          
          if (commentAudit) {
            this.$message.success('评论发表成功，等待审核后显示')
          } else {
            this.$message.success('评论发表成功')
          }
          
          this.newComment = ''
          this.getComments()
        }
      } catch (error) {
        console.error('评论发表失败:', error)
        this.$message.error('评论发表失败')
      } finally {
        this.submitting = false
      }
    },

    toggleReplyInput(commentId) {
      if (!this.isAuthenticated) {
        this.goToLogin()
        return
      }
      
      if (this.replyingTo === commentId) {
        this.cancelReply()
      } else {
        this.replyingTo = commentId
        this.replyContent = ''
      }
    },

    async submitReply(parentComment) {
      if (!this.replyContent.trim()) {
        this.$message.warning('回复内容不能为空')
        return
      }

      this.replySubmitting = true
      try {
        const replyData = {
          articleId: this.articleId,
          parentId: parentComment.id,
          content: this.replyContent.trim(),
          toUserId: parentComment.userId,
          level: 2 // 回复层级
        }
        const response = await api.comment.add(replyData)
        if (response.code === 200) {
          // 根据系统设置判断是否需要审核
          const commentAudit = this.siteSettings.comment_audit === '1' || this.siteSettings.comment_audit === 1
          
          if (commentAudit) {
            this.$message.success('回复成功，等待审核后显示')
          } else {
            this.$message.success('回复成功')
          }
          
          this.cancelReply()
          this.getComments()
        }
      } catch (error) {
        console.error('回复失败:', error)
        this.$message.error('回复失败')
      } finally {
        this.replySubmitting = false
      }
    },

    replyToChild(parentComment, childComment) {
      if (!this.isAuthenticated) {
        this.goToLogin()
        return
      }
      
      this.replyingTo = parentComment.id
      this.replyContent = `@${childComment.nickname} `
    },

    cancelReply() {
      this.replyingTo = null
      this.replyContent = ''
    },


    goToLogin() {
      this.$router.push('/login')
    },

    formatTime(timeString) {
      if (!timeString) return ''
      const date = new Date(timeString)
      const now = new Date()
      const diff = now - date
      
      if (diff < 60000) { // 1分钟内
        return '刚刚'
      } else if (diff < 3600000) { // 1小时内
        return Math.floor(diff / 60000) + '分钟前'
      } else if (diff < 86400000) { // 1天内
        return Math.floor(diff / 3600000) + '小时前'
      } else if (diff < 2592000000) { // 30天内
        return Math.floor(diff / 86400000) + '天前'
      } else {
        return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
      }
    },

    handleSizeChange(size) {
      this.pagination.size = size
      this.pagination.current = 1
      this.getComments()
    },

    handleCurrentChange(current) {
      this.pagination.current = current
      this.getComments()
    }
  }
}
</script>

<style>
/* emoji面板样式 */
.emoji-panel {
  position: absolute;
  z-index: 1000;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  width: 320px;
  margin-top: 10px;
  left: 0;
  border: 1px solid #ebeef5;
}

.emoji-container {
  padding: 10px;
}

.emoji-categories {
  display: flex;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
  margin-bottom: 10px;
  overflow-x: auto;
}

.emoji-category {
  padding: 5px 10px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.emoji-category:hover {
  background: #f5f7fa;
}

.emoji-category.active {
  background: #ecf5ff;
  color: #409EFF;
}

.emoji-list {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 5px;
  max-height: 200px;
  overflow-y: auto;
}

.emoji-item {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  padding: 5px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.emoji-item:hover {
  background: #f5f7fa;
  transform: scale(1.2);
}
</style>

<style scoped>
.comment-section {
  padding: 20px 0;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}

/* 评论输入区域样式 */
.comment-input-container {
  margin-bottom: 30px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  transition: all 0.3s ease;
}

.comment-input-container:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}

.comment-input-header {
  padding: 15px 20px;
  background: linear-gradient(135deg, #f6f9fc 0%, #eef1f5 100%);
  border-bottom: 1px solid #eee;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.username {
  font-weight: 600;
  color: #333;
}

.comment-tips {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #909399;
  font-size: 14px;
}

.comment-input {
  padding: 20px;
}

.comment-textarea {
  margin-bottom: 15px;
}

.comment-textarea >>> .el-textarea__inner {
  border-radius: 8px;
  border-color: #dcdfe6;
  transition: all 0.3s ease;
  padding: 12px 15px;
  font-size: 14px;
  resize: none;
}

.comment-textarea >>> .el-textarea__inner:focus {
  border-color: #409EFF;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.comment-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.emoji-picker {
  position: relative;
}

.emoji-button {
  color: #606266;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.emoji-button:hover {
  color: #409EFF;
}

.emoji-button i {
  font-size: 16px;
}

.comment-actions {
  text-align: right;
}

/* Emoji选择器样式 */
.emoji-container {
  padding: 10px;
}

.emoji-categories {
  display: flex;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
  margin-bottom: 10px;
  overflow-x: auto;
}

.emoji-category {
  padding: 5px 10px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.emoji-category:hover {
  background: #f5f7fa;
}

.emoji-category.active {
  background: #ecf5ff;
  color: #409EFF;
}

.emoji-list {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 5px;
  max-height: 200px;
  overflow-y: auto;
}

.emoji-item {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  padding: 5px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.emoji-item:hover {
  background: #f5f7fa;
  transform: scale(1.2);
}

/* 评论列表样式 */
.comment-item {
  padding: 20px;
  margin-bottom: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  border-left: 4px solid transparent;
}

.comment-item:hover {
  border-left-color: #409EFF;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.comment-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  gap: 12px;
}

.comment-author {
  font-weight: 600;
  color: #333;
  font-size: 15px;
}

.reply-to {
  color: #409EFF;
  font-size: 13px;
  background: #ecf5ff;
  padding: 2px 8px;
  border-radius: 4px;
}

.comment-time {
  color: #909399;
  font-size: 13px;
  margin-left: auto;
}

.comment-content {
  color: #333;
  line-height: 1.8;
  margin-bottom: 15px;
  word-wrap: break-word;
  font-size: 15px;
  padding: 0 5px;
}

.comment-actions-bar {
  display: flex;
  gap: 20px;
  align-items: center;
  padding-top: 10px;
  border-top: 1px dashed #eee;
}

.comment-actions-bar .el-button--text {
  color: #606266;
  padding: 0;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.comment-actions-bar .el-button--text:hover {
  color: #409EFF;
}

.comment-like {
  color: #606266;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.comment-like:hover {
  color: #f56c6c;
}

.comment-like i {
  font-size: 15px;
}

/* 回复区域样式 */
.reply-input {
  margin-top: 15px;
  padding: 15px;
  background: #f6f9fc;
  border-radius: 8px;
  box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.05);
}

.reply-textarea {
  margin-bottom: 12px;
}

.reply-textarea >>> .el-textarea__inner {
  border-radius: 6px;
  border-color: #dcdfe6;
  background: white;
}

.reply-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.reply-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

/* 子评论样式 */
.child-comments {
  margin: 15px 0 0 40px;
  padding: 15px;
  border-left: 3px solid #409EFF;
  background: #f9f9f9;
  border-radius: 0 8px 8px 0;
  box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.03);
}

.child-comment-item {
  padding: 15px 0;
  border-bottom: 1px dashed #e0e0e0;
}

.child-comment-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.child-comment-item:first-child {
  padding-top: 0;
}

/* 无评论状态 */
.no-comments {
  text-align: center;
  color: #909399;
  padding: 60px 0;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.no-comments-icon {
  font-size: 48px;
  color: #c0c4cc;
  margin-bottom: 10px;
}

.no-comments p {
  margin: 0 0 15px;
  font-size: 16px;
}

/* 分页样式 */
.pagination {
  margin-top: 30px;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .comment-input-container {
    border-radius: 8px;
  }
  
  .child-comments {
    margin-left: 20px;
    padding: 10px;
  }
  
  .comment-header {
    flex-wrap: wrap;
  }
  
  .comment-time {
    margin-left: 0;
    width: 100%;
    margin-top: 5px;
  }
  
  .emoji-list {
    grid-template-columns: repeat(6, 1fr);
  }
  
  .comment-toolbar,
  .reply-toolbar {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .comment-actions,
  .reply-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
