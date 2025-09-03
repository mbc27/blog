<template>
  <div class="message-management">
    <el-card shadow="hover">
      <div slot="header" class="clearfix">
        <span>留言管理</span>
        <el-button
          style="float: right; padding: 3px 0"
          type="text"
          @click="refreshMessages"
        >
          刷新
        </el-button>
      </div>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索留言内容、昵称或邮箱"
          style="width: 300px; margin-right: 10px"
          clearable
          @keyup.enter.native="searchMessages"
        >
          <el-button slot="append" icon="el-icon-search" @click="searchMessages"></el-button>
        </el-input>
        <el-button
          type="danger"
          :disabled="selectedMessages.length === 0"
          @click="batchDeleteMessages"
        >
          批量删除 ({{ selectedMessages.length }})
        </el-button>
      </div>

      <!-- 留言表格 -->
      <el-table
        :data="messages"
        style="width: 100%; margin-top: 20px"
        @selection-change="handleSelectionChange"
        v-loading="loading"
      >
        <el-table-column type="selection" width="55"></el-table-column>
        
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        
        <el-table-column prop="nickname" label="昵称" width="120">
          <template slot-scope="scope">
            <el-tag size="small">{{ scope.row.nickname }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="email" label="邮箱" width="180"></el-table-column>
        
        <el-table-column prop="content" label="留言内容" min-width="200">
          <template slot-scope="scope">
            <div class="message-content">
              {{ scope.row.content }}
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="ip" label="IP地址" width="120">
          <template slot-scope="scope">
            <el-tag type="info" size="mini">{{ scope.row.ip }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="level" label="层级" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.level === 1 ? 'primary' : 'success'" size="mini">
              {{ scope.row.level === 1 ? '主留言' : '回复' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button-group>
              <el-button
                size="mini"
                @click="viewMessage(scope.row)"
              >
                查看
              </el-button>
              <el-button
                size="mini"
                type="primary"
                @click="editMessage(scope.row)"
              >
                编辑
              </el-button>
              <el-button
                size="mini"
                type="danger"
                @click="deleteMessage(scope.row.id)"
              >
                删除
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        style="margin-top: 20px; text-align: center"
      >
      </el-pagination>
    </el-card>

    <!-- 查看留言对话框 -->
    <el-dialog title="留言详情" :visible.sync="viewDialogVisible" width="60%">
      <div v-if="currentMessage">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="ID">{{ currentMessage.id }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{ currentMessage.nickname }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ currentMessage.email }}</el-descriptions-item>
          <el-descriptions-item label="IP地址">{{ currentMessage.ip }}</el-descriptions-item>
          <el-descriptions-item label="层级">
            {{ currentMessage.level === 1 ? '主留言' : '回复' }}
          </el-descriptions-item>
          <el-descriptions-item label="父留言ID">
            {{ currentMessage.parentId || '无' }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">
            {{ formatDate(currentMessage.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="留言内容" :span="2">
            <div class="message-content-detail">{{ currentMessage.content }}</div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- 编辑留言对话框 -->
    <el-dialog title="编辑留言" :visible.sync="editDialogVisible" width="50%">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email"></el-input>
        </el-form-item>
        <el-form-item label="留言内容">
          <el-input
            type="textarea"
            :rows="4"
            v-model="editForm.content"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="updateMessage">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import api from '@/api'

export default {
  name: 'MessageManagement',
  data() {
    return {
      messages: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      searchKeyword: '',
      selectedMessages: [],
      viewDialogVisible: false,
      editDialogVisible: false,
      currentMessage: null,
      editForm: {
        id: null,
        nickname: '',
        email: '',
        content: ''
      }
    }
  },
  mounted() {
    this.fetchMessages()
  },
  methods: {
    async fetchMessages() {
      this.loading = true
      try {
        const response = await api.message.getAdminList({
          current: this.currentPage,
          size: this.pageSize
        })
        console.log('留言列表响应:', response)
        if (response.code === 200) {
          this.messages = response.data.records
          this.total = response.data.total
        } else {
          this.$message.error(response.message || '获取留言列表失败')
        }
      } catch (error) {
        this.$message.error('获取留言列表失败')
        console.error('Error fetching messages:', error)
      } finally {
        this.loading = false
      }
    },

    refreshMessages() {
      this.currentPage = 1
      this.fetchMessages()
    },

    async searchMessages() {
      this.loading = true
      try {
        const response = await api.message.getAdminList({
          current: 1,
          size: this.pageSize,
          keyword: this.searchKeyword
        })
        console.log('搜索留言响应:', response)
        if (response.code === 200) {
          this.messages = response.data.records
          this.total = response.data.total
          this.currentPage = 1
        } else {
          this.$message.error(response.message || '搜索失败')
        }
      } catch (error) {
        this.$message.error('搜索失败')
        console.error('Error searching messages:', error)
      } finally {
        this.loading = false
      }
    },

    handleSelectionChange(selection) {
      this.selectedMessages = selection
    },

    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
      this.fetchMessages()
    },

    handleCurrentChange(val) {
      this.currentPage = val
      this.fetchMessages()
    },

    viewMessage(message) {
      this.currentMessage = message
      this.viewDialogVisible = true
    },

    editMessage(message) {
      this.editForm = {
        id: message.id,
        nickname: message.nickname,
        email: message.email,
        content: message.content
      }
      this.editDialogVisible = true
    },

    async updateMessage() {
      try {
        const response = await api.message.update(this.editForm.id, this.editForm)
        if (response.code === 200) {
          this.$message.success('更新成功')
          this.editDialogVisible = false
          this.fetchMessages()
        } else {
          this.$message.error(response.message || '更新失败')
        }
      } catch (error) {
        this.$message.error('更新失败')
        console.error('Error updating message:', error)
      }
    },

    deleteMessage(id) {
      this.$confirm('此操作将永久删除该留言及其所有回复, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await api.message.delete(id)
          if (response.code === 200) {
            this.$message.success('删除成功')
            this.fetchMessages()
          } else {
            this.$message.error(response.message || '删除失败')
          }
        } catch (error) {
          this.$message.error('删除失败')
          console.error('Error deleting message:', error)
        }
      })
    },

    batchDeleteMessages() {
      if (this.selectedMessages.length === 0) {
        this.$message.warning('请选择要删除的留言')
        return
      }

      this.$confirm(`此操作将永久删除选中的 ${this.selectedMessages.length} 条留言, 是否继续?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const ids = this.selectedMessages.map(message => message.id)
          const response = await api.message.batchDelete(ids)
          if (response.code === 200) {
            this.$message.success('批量删除成功')
            this.fetchMessages()
          } else {
            this.$message.error(response.message || '批量删除失败')
          }
        } catch (error) {
          this.$message.error('批量删除失败')
          console.error('Error batch deleting messages:', error)
        }
      })
    },

    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleString('zh-CN')
    }
  }
}
</script>

<style scoped>
.message-management {
  padding: 20px;
}

.search-bar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.message-content {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.message-content-detail {
  max-height: 200px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-break: break-all;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}

.clearfix:after {
  clear: both;
}
</style>