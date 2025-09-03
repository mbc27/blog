<template>
  <div class="user-management">
    <div class="search-form">
      <el-form :model="searchForm" inline class="search-form-inline">
        <el-form-item label="用户名">
          <el-input 
            v-model="searchForm.username" 
            placeholder="请输入用户名" 
            clearable 
            style="width: 180px;"
            @keyup.enter.native="searchUsers">
          </el-input>
        </el-form-item>
        <el-form-item label="昵称">
          <el-input 
            v-model="searchForm.nickname" 
            placeholder="请输入昵称" 
            clearable 
            style="width: 180px;"
            @keyup.enter.native="searchUsers">
          </el-input>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="searchForm.role" placeholder="请选择角色" clearable style="width: 120px;" @change="searchUsers">
            <el-option label="管理员" :value="0"></el-option>
            <el-option label="普通用户" :value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px;" @change="searchUsers">
            <el-option label="正常" :value="1"></el-option>
            <el-option label="禁用" :value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchUsers" icon="el-icon-search">搜索</el-button>
          <el-button @click="resetSearch" icon="el-icon-refresh">重置</el-button>
          <el-button type="success" @click="exportUsers" icon="el-icon-download">导出</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table 
        :data="userList" 
        v-loading="loading"
        style="width: 100%"
        :current-page="currentPage"
        :page-size="pageSize">
        <el-table-column 
          label="序号" 
          width="80"
          :formatter="(row, column, cellValue, index) => {
            return (currentPage - 1) * pageSize + index + 1;
          }">
        </el-table-column>
        <el-table-column prop="username" label="用户名" width="120"></el-table-column>
        <el-table-column prop="nickname" label="昵称" width="120"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="180"></el-table-column>
        <el-table-column label="角色" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.role === 0 ? 'danger' : 'primary'">
              {{ scope.row.role === 0 ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
        <el-table-column label="操作" width="300">
          <template slot-scope="scope">
            <el-button size="mini" @click="editUser(scope.row)">编辑</el-button>
            <el-button 
              size="mini" 
              :type="scope.row.status === 1 ? 'warning' : 'success'"
              @click="toggleUserStatus(scope.row)">
              {{ scope.row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button size="mini" type="info" @click="resetPassword(scope.row)">重置密码</el-button>
            <el-button size="mini" type="danger" @click="deleteUser(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>
    </div>

    <!-- 编辑用户对话框 -->
    <el-dialog title="编辑用户" :visible.sync="editDialogVisible" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" disabled></el-input>
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email"></el-input>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="editForm.role" style="width: 100%">
            <el-option label="管理员" :value="0"></el-option>
            <el-option label="普通用户" :value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status" style="width: 100%">
            <el-option label="正常" :value="1"></el-option>
            <el-option label="禁用" :value="0"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveUser">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import api from '@/api'

export default {
  name: 'UserManagement',
  data() {
    return {
      loading: false,
      userList: [],
      searchForm: {
        username: '',
        nickname: '',
        role: null,
        status: null
      },
      currentPage: 1,
      pageSize: 10,
      total: 0,
      editDialogVisible: false,
      editForm: {
        id: null,
        username: '',
        nickname: '',
        email: '',
        role: 1,
        status: 1
      }
    }
  },
  created() {
    this.loadUsers()
  },
  methods: {
    async loadUsers() {
      try {
        this.loading = true
        
        // 过滤空值参数
        const params = {
          page: this.currentPage,
          size: this.pageSize
        }
        
        // 只添加非空的搜索条件
        Object.keys(this.searchForm).forEach(key => {
          const value = this.searchForm[key]
          if (value !== null && value !== undefined && value !== '') {
            params[key] = value
          }
        })
        
        console.log('搜索参数:', params)
        
        const response = await api.adminUser.getList(params)
        console.log('用户列表响应:', response)
        
        if (response.code === 200) {
          this.userList = response.data.records || []
          this.total = response.data.total || 0
          
          if (this.userList.length === 0 && this.total === 0) {
            this.$message.info('未找到符合条件的用户')
          }
        } else {
          this.$message.error('获取用户列表失败: ' + (response.message || '未知错误'))
        }
      } catch (error) {
        console.error('获取用户列表失败:', error)
        this.$message.error('获取用户列表失败: ' + (error.message || '网络错误'))
      } finally {
        this.loading = false
      }
    },

    searchUsers() {
      this.currentPage = 1
      this.loadUsers()
    },

    resetSearch() {
      this.searchForm = {
        username: '',
        nickname: '',
        role: null,
        status: null
      }
      this.currentPage = 1
      this.loadUsers()
    },

    exportUsers() {
      this.$message.info('导出功能开发中...')
    },

    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
      this.loadUsers()
    },

    handleCurrentChange(val) {
      this.currentPage = val
      this.loadUsers()
    },

    editUser(user) {
      this.editForm = {
        id: user.id,
        username: user.username,
        nickname: user.nickname,
        email: user.email,
        role: user.role,
        status: user.status
      }
      this.editDialogVisible = true
    },

    async saveUser() {
      try {
        // 调用完整的用户更新API
        const response = await api.adminUser.update(this.editForm.id, {
          nickname: this.editForm.nickname,
          email: this.editForm.email,
          role: this.editForm.role,
          status: this.editForm.status
        })
        
        if (response.code === 200) {
          this.$message.success('用户信息更新成功')
          this.editDialogVisible = false
          // 重新加载用户列表以显示最新数据
          await this.loadUsers()
        } else {
          this.$message.error('更新用户信息失败: ' + (response.message || '未知错误'))
        }
      } catch (error) {
        console.error('更新用户信息失败:', error)
        this.$message.error('更新用户信息失败: ' + (error.message || '网络错误'))
      }
    },

    async toggleUserStatus(user) {
      const newStatus = user.status === 1 ? 0 : 1
      const action = newStatus === 1 ? '启用' : '禁用'
      
      try {
        await this.$confirm(`确定要${action}用户 ${user.username} 吗？`, '确认操作', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const response = await api.adminUser.updateStatus(user.id, newStatus)
        if (response.code === 200) {
          this.$message.success(`${action}用户成功`)
          await this.loadUsers()
        } else {
          this.$message.error(`${action}用户失败: ` + (response.message || '未知错误'))
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error(`${action}用户失败:`, error)
          this.$message.error(`${action}用户失败: ` + (error.message || '网络错误'))
        }
      }
    },

    async resetPassword(user) {
      try {
        await this.$confirm(`确定要重置用户 ${user.username} 的密码吗？重置后密码将变为：123456`, '确认重置', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const response = await api.adminUser.resetPassword(user.id)
        if (response.code === 200) {
          this.$message.success('密码重置成功，新密码为：123456')
        } else {
          this.$message.error('重置密码失败: ' + (response.message || '未知错误'))
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('重置密码失败:', error)
          this.$message.error('重置密码失败: ' + (error.message || '网络错误'))
        }
      }
    },

    async deleteUser(user) {
      try {
        await this.$confirm(`确定要删除用户 ${user.username} 吗？此操作不可恢复！`, '确认删除', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const response = await api.adminUser.delete(user.id)
        if (response.code === 200) {
          this.$message.success('删除用户成功')
          await this.loadUsers()
        } else {
          this.$message.error('删除用户失败: ' + (response.message || '未知错误'))
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除用户失败:', error)
          this.$message.error('删除用户失败: ' + (error.message || '网络错误'))
        }
      }
    }
  }
}
</script>

<style scoped>
.user-management {
  padding: 20px;
}

.search-form {
  background: #f5f5f5;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.search-form-inline {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

.search-form-inline .el-form-item {
  margin-right: 15px;
  margin-bottom: 10px;
}

.search-form-inline .el-form-item:last-child {
  margin-left: auto;
}

.table-container {
  background: white;
  padding: 20px;
  border-radius: 4px;
}

.el-pagination {
  margin-top: 20px;
  text-align: right;
}
</style>