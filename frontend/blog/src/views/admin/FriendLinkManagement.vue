<template>
  <div class="friend-link-management">
    <el-card shadow="hover">
      <div slot="header" class="clearfix">
        <span>友链管理</span>
        <el-button
          style="float: right; padding: 3px 0"
          type="text"
          @click="refreshFriendLinks"
        >
          刷新
        </el-button>
      </div>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索友链名称、描述或网址"
          style="width: 300px; margin-right: 10px"
          clearable
          @keyup.enter.native="searchFriendLinks"
        >
          <el-button slot="append" icon="el-icon-search" @click="searchFriendLinks"></el-button>
        </el-input>
        <el-button
          type="primary"
          @click="showAddDialog"
        >
          新增友链
        </el-button>
        <el-button
          type="danger"
          :disabled="selectedFriendLinks.length === 0"
          @click="batchDeleteFriendLinks"
        >
          批量删除 ({{ selectedFriendLinks.length }})
        </el-button>
      </div>

      <!-- 友链表格 -->
      <el-table
        :data="friendLinks"
        style="width: 100%; margin-top: 20px"
        @selection-change="handleSelectionChange"
        v-loading="loading"
      >
        <el-table-column type="selection" width="55"></el-table-column>
        
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        
        <el-table-column prop="name" label="友链名称" width="150">
          <template slot-scope="scope">
            <el-tag size="small">{{ scope.row.name }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="description" label="描述" width="200" show-overflow-tooltip></el-table-column>
        
        <el-table-column prop="url" label="网址" width="200" show-overflow-tooltip>
          <template slot-scope="scope">
            <el-link :href="scope.row.url" target="_blank" type="primary">{{ scope.row.url }}</el-link>
          </template>
        </el-table-column>
        
        <el-table-column prop="avatar" label="头像" width="100">
          <template slot-scope="scope">
            <el-image
              v-if="scope.row.avatar"
              :src="scope.row.avatar"
              style="width: 40px; height: 40px; border-radius: 50%"
              fit="cover"
            ></el-image>
            <span v-else>无头像</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag
              :type="scope.row.status === 1 ? 'success' : scope.row.status === 2 ? 'danger' : 'warning'"
              size="small"
            >
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="sortOrder" label="排序" width="80"></el-table-column>
        
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="250">
          <template slot-scope="scope">
            <el-button-group>
              <el-button size="mini" type="primary" @click="viewFriendLink(scope.row)">查看</el-button>
              <el-button size="mini" type="warning" @click="editFriendLink(scope.row)">编辑</el-button>
              <el-button 
                size="mini" 
                type="success" 
                @click="auditFriendLink(scope.row.id, 1)"
                v-if="scope.row.status !== 1"
              >
                通过
              </el-button>
              <el-button 
                size="mini" 
                type="danger" 
                @click="auditFriendLink(scope.row.id, 2)"
                v-if="scope.row.status !== 2"
              >
                拒绝
              </el-button>
              <el-button size="mini" type="danger" @click="deleteFriendLink(scope.row.id)">删除</el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        >
        </el-pagination>
      </div>
    </el-card>

    <!-- 查看友链对话框 -->
    <el-dialog title="友链详情" :visible.sync="viewDialogVisible" width="50%">
      <div v-if="currentFriendLink">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="ID">{{ currentFriendLink.id }}</el-descriptions-item>
          <el-descriptions-item label="名称">{{ currentFriendLink.name }}</el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">{{ currentFriendLink.description }}</el-descriptions-item>
          <el-descriptions-item label="网址" :span="2">
            <el-link :href="currentFriendLink.url" target="_blank" type="primary">{{ currentFriendLink.url }}</el-link>
          </el-descriptions-item>
          <el-descriptions-item label="头像" :span="2">
            <el-image
              v-if="currentFriendLink.avatar"
              :src="currentFriendLink.avatar"
              style="width: 60px; height: 60px; border-radius: 50%"
              fit="cover"
            ></el-image>
            <span v-else>无头像</span>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag
              :type="currentFriendLink.status === 1 ? 'success' : currentFriendLink.status === 2 ? 'danger' : 'warning'"
              size="small"
            >
              {{ getStatusText(currentFriendLink.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="排序">{{ currentFriendLink.sortOrder }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDate(currentFriendLink.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatDate(currentFriendLink.updateTime) }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- 编辑友链对话框 -->
    <el-dialog :title="editForm.id ? '编辑友链' : '新增友链'" :visible.sync="editDialogVisible" width="50%">
      <el-form :model="editForm" :rules="editRules" ref="editForm" label-width="100px">
        <el-form-item label="友链名称" prop="name">
          <el-input v-model="editForm.name" placeholder="请输入友链名称"></el-input>
        </el-form-item>
        <el-form-item label="友链描述" prop="description">
          <el-input v-model="editForm.description" type="textarea" placeholder="请输入友链描述"></el-input>
        </el-form-item>
        <el-form-item label="友链网址" prop="url">
          <el-input v-model="editForm.url" placeholder="请输入友链网址"></el-input>
        </el-form-item>
        <el-form-item label="友链头像" prop="avatar">
          <el-input v-model="editForm.avatar" placeholder="请输入友链头像地址"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="editForm.status" placeholder="请选择状态">
            <el-option label="待审核" :value="0"></el-option>
            <el-option label="已通过" :value="1"></el-option>
            <el-option label="已拒绝" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="editForm.sortOrder" :min="0" placeholder="排序值"></el-input-number>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveFriendLink">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import api from '@/api'

export default {
  name: 'FriendLinkManagement',
  data() {
    return {
      friendLinks: [],
      loading: false,
      searchKeyword: '',
      selectedFriendLinks: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      viewDialogVisible: false,
      editDialogVisible: false,
      currentFriendLink: null,
      editForm: {
        id: null,
        name: '',
        description: '',
        url: '',
        avatar: '',
        status: 0,
        sortOrder: 0
      },
      editRules: {
        name: [
          { required: true, message: '请输入友链名称', trigger: 'blur' }
        ],
        url: [
          { required: true, message: '请输入友链网址', trigger: 'blur' },
          { type: 'url', message: '请输入正确的网址格式', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '请选择状态', trigger: 'change' }
        ]
      }
    }
  },
  mounted() {
    this.fetchFriendLinks()
  },
  methods: {
    async fetchFriendLinks() {
      this.loading = true
      try {
        const response = await api.friendLink.getAdminList({
          current: this.currentPage,
          size: this.pageSize,
          keyword: this.searchKeyword
        })
        console.log('友链列表响应:', response)
        if (response.code === 200) {
          this.friendLinks = response.data.records
          this.total = response.data.total
        } else {
          this.$message.error(response.message || '获取友链列表失败')
        }
      } catch (error) {
        this.$message.error('获取友链列表失败')
        console.error('Error fetching friend links:', error)
      } finally {
        this.loading = false
      }
    },

    refreshFriendLinks() {
      this.currentPage = 1
      this.searchKeyword = ''
      this.fetchFriendLinks()
    },

    async searchFriendLinks() {
      this.currentPage = 1
      this.fetchFriendLinks()
    },

    handleSelectionChange(selection) {
      this.selectedFriendLinks = selection
    },

    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
      this.fetchFriendLinks()
    },

    handleCurrentChange(val) {
      this.currentPage = val
      this.fetchFriendLinks()
    },

    viewFriendLink(friendLink) {
      this.currentFriendLink = friendLink
      this.viewDialogVisible = true
    },

    showAddDialog() {
      this.editForm = {
        id: null,
        name: '',
        description: '',
        url: '',
        avatar: '',
        status: 0,
        sortOrder: 0
      }
      this.editDialogVisible = true
    },

    editFriendLink(friendLink) {
      this.editForm = {
        id: friendLink.id,
        name: friendLink.name,
        description: friendLink.description,
        url: friendLink.url,
        avatar: friendLink.avatar,
        status: friendLink.status,
        sortOrder: friendLink.sortOrder
      }
      this.editDialogVisible = true
    },

    async saveFriendLink() {
      this.$refs.editForm.validate(async (valid) => {
        if (valid) {
          try {
            let response
            if (this.editForm.id) {
              response = await api.friendLink.update(this.editForm)
            } else {
              response = await api.friendLink.add(this.editForm)
            }
            
            if (response.code === 200) {
              this.$message.success(this.editForm.id ? '更新成功' : '新增成功')
              this.editDialogVisible = false
              this.fetchFriendLinks()
            } else {
              this.$message.error(response.message || '操作失败')
            }
          } catch (error) {
            this.$message.error('操作失败')
            console.error('Error saving friend link:', error)
          }
        }
      })
    },

    async auditFriendLink(id, status) {
      try {
        const response = await api.friendLink.audit(id, status)
        if (response.code === 200) {
          this.$message.success(response.message)
          this.fetchFriendLinks()
        } else {
          this.$message.error(response.message || '审核失败')
        }
      } catch (error) {
        this.$message.error('审核失败')
        console.error('Error auditing friend link:', error)
      }
    },

    deleteFriendLink(id) {
      this.$confirm('此操作将永久删除该友链, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await api.friendLink.delete(id)
          if (response.code === 200) {
            this.$message.success('删除成功')
            this.fetchFriendLinks()
          } else {
            this.$message.error(response.message || '删除失败')
          }
        } catch (error) {
          this.$message.error('删除失败')
          console.error('Error deleting friend link:', error)
        }
      })
    },

    batchDeleteFriendLinks() {
      if (this.selectedFriendLinks.length === 0) {
        this.$message.warning('请选择要删除的友链')
        return
      }
      
      this.$confirm(`此操作将永久删除选中的 ${this.selectedFriendLinks.length} 个友链, 是否继续?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const ids = this.selectedFriendLinks.map(item => item.id)
          const response = await api.friendLink.batchDelete(ids)
          if (response.code === 200) {
            this.$message.success('批量删除成功')
            this.fetchFriendLinks()
          } else {
            this.$message.error(response.message || '批量删除失败')
          }
        } catch (error) {
          this.$message.error('批量删除失败')
          console.error('Error batch deleting friend links:', error)
        }
      })
    },

    getStatusText(status) {
      const statusMap = {
        0: '待审核',
        1: '已通过',
        2: '已拒绝'
      }
      return statusMap[status] || '未知'
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
.friend-link-management {
  padding: 20px;
}

.search-bar {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  text-align: right;
}
</style>