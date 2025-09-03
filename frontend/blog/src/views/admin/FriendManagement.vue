<template>
  <div class="friend-management">
    <el-card>
      <div slot="header" class="header">
        <span>友链管理</span>
        <el-button type="primary" size="small" @click="handleCreate">
          添加友链
        </el-button>
      </div>

      <el-table :data="friendList" v-loading="loading">
        <el-table-column prop="name" label="名称" min-width="120"></el-table-column>
        <el-table-column prop="url" label="链接" min-width="200">
          <template slot-scope="{row}">
            <a :href="row.url" target="_blank" class="link">{{ row.url }}</a>
          </template>
        </el-table-column>
        <el-table-column prop="avatar" label="头像" width="80">
          <template slot-scope="{row}">
            <el-avatar :src="row.avatar" size="small" v-if="row.avatar"></el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80"></el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="{row}">
            <el-button size="mini" @click="handleEdit(row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.current"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pagination.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        class="pagination"
      ></el-pagination>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog 
      :title="dialogTitle" 
      :visible.sync="dialogVisible" 
      width="500px"
      @closed="handleDialogClosed">
      <el-form :model="form" :rules="rules" ref="form" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入友链名称"></el-input>
        </el-form-item>
        <el-form-item label="链接" prop="url">
          <el-input v-model="form.url" placeholder="请输入友链URL"></el-input>
        </el-form-item>
        <el-form-item label="头像" prop="avatar">
          <el-input v-model="form.avatar" placeholder="请输入头像URL"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input 
            type="textarea" 
            v-model="form.description" 
            placeholder="请输入友链描述"
            :rows="3"
          ></el-input>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0"></el-input-number>
        </el-form-item>
      </el-form>
      
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'FriendManagement',
  data() {
    return {
      loading: false,
      friendList: [],
      pagination: {
        current: 1,
        size: 10,
        total: 0
      },
      dialogVisible: false,
      dialogTitle: '添加友链',
      submitting: false,
      form: {
        id: null,
        name: '',
        url: '',
        avatar: '',
        description: '',
        sortOrder: 0
      },
      rules: {
        name: [
          { required: true, message: '请输入友链名称', trigger: 'blur' }
        ],
        url: [
          { required: true, message: '请输入友链URL', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getFriendList()
  },
  methods: {
    async getFriendList() {
      this.loading = true
      try {
        const params = {
          page: this.pagination.current,
          size: this.pagination.size
        }
        const response = await this.$axios.get('/api/friend/list', { params })
        if (response.data.code === 200) {
          this.friendList = response.data.data.records
          this.pagination.total = response.data.data.total
        }
      } catch (error) {
        this.$message.error('获取友链列表失败')
      } finally {
        this.loading = false
      }
    },

    handleCreate() {
      this.dialogTitle = '添加友链'
      this.form = {
        id: null,
        name: '',
        url: '',
        avatar: '',
        description: '',
        sortOrder: 0
      }
      this.dialogVisible = true
    },

    handleEdit(row) {
      this.dialogTitle = '编辑友链'
      this.form = { ...row }
      this.dialogVisible = true
    },

    async handleDelete(row) {
      try {
        await this.$confirm('确定要删除这个友链吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const response = await this.$axios.delete(`/api/friend/${row.id}`)
        if (response.data.code === 200) {
          this.$message.success('删除成功')
          this.getFriendList()
        }
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    },

    async submitForm() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          this.submitting = true
          try {
            const url = this.form.id ? '/api/friend/update' : '/api/friend/create'
            const response = await this.$axios.post(url, this.form)
            if (response.data.code === 200) {
              this.$message.success(this.form.id ? '更新成功' : '添加成功')
              this.dialogVisible = false
              this.getFriendList()
            }
          } catch (error) {
            this.$message.error(error.response?.data?.message || '操作失败')
          } finally {
            this.submitting = false
          }
        }
      })
    },

    handleDialogClosed() {
      this.$refs.form.clearValidate()
    },

    handleSizeChange(size) {
      this.pagination.size = size
      this.pagination.current = 1
      this.getFriendList()
    },

    handleCurrentChange(current) {
      this.pagination.current = current
      this.getFriendList()
    }
  }
}
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.link {
  color: #409EFF;
  text-decoration: none;
}

.link:hover {
  text-decoration: underline;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>