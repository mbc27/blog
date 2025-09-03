<template>
  <div class="tag-management">
    <el-card shadow="hover">
      <div slot="header" class="clearfix">
        <span>标签管理</span>
        <el-button style="float: right" type="primary" size="small" @click="handleAddTag">新增标签</el-button>
      </div>
      
      <el-table
        :data="tags"
        style="width: 100%"
        v-loading="loading">
        <el-table-column
          type="index"
          label="序号"
          width="80"
          :index="indexMethod">
        </el-table-column>
        <el-table-column
          prop="name"
          label="标签名称"
          width="180">
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="创建时间"
          width="180">
        </el-table-column>
        <el-table-column
          label="操作"
          width="200">
          <template slot-scope="scope">
            <el-button
              size="mini"
              @click="handleEdit(scope.row)">编辑</el-button>
            <el-button
              size="mini"
              type="danger"
              @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pagination.current"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="pagination.size"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total">
        </el-pagination>
      </div>
    </el-card>

    <!-- 标签编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form :model="tagForm" :rules="tagRules" ref="tagForm" label-width="100px">
        <el-form-item label="标签名称" prop="name">
          <el-input v-model="tagForm.name" placeholder="请输入标签名称"></el-input>
        </el-form-item>
        <el-form-item label="标签颜色" prop="color">
          <el-color-picker v-model="tagForm.color"></el-color-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitTag" :loading="submitLoading">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'TagManagement',
  data() {
    return {
      tags: [],
      loading: false,
      pagination: {
        current: 1,
        size: 10,
        total: 0
      },
      dialogVisible: false,
      dialogTitle: '新增标签',
      tagForm: {
        id: null,
        name: '',
        color: '#409EFF'
      },
      tagRules: {
        name: [
          { required: true, message: '请输入标签名称', trigger: 'blur' },
          { max: 20, message: '长度不能超过20个字符', trigger: 'blur' }
        ]
      },
      submitLoading: false
    }
  },
  created() {
    this.fetchTags()
  },
  methods: {
    indexMethod(index) {
      return (this.pagination.current - 1) * this.pagination.size + index + 1
    },
    fetchTags() {
      this.loading = true
      const params = {
        current: this.pagination.current,
        size: this.pagination.size
      }
      
      axios.get('/api/tag', { 
        params,
        headers: {
          Authorization: this.$store.getters.token
        }
      })
        .then(response => {
          if (response.data && response.data.data) {
            // 检查返回的数据是否是分页对象
            if (response.data.data.records) {
              this.tags = response.data.data.records
              this.pagination.total = response.data.data.total
            } else {
              this.tags = response.data.data
              this.pagination.total = response.data.total || response.data.data.length
            }
          }
        })
        .catch(error => {
          console.error('获取标签列表失败', error)
          this.$message.error('获取标签列表失败')
        })
        .finally(() => {
          this.loading = false
        })
    },
    handleSizeChange(val) {
      this.pagination.size = val
      this.fetchTags()
    },
    handleCurrentChange(val) {
      this.pagination.current = val
      this.fetchTags()
    },
    handleAddTag() {
      this.dialogTitle = '新增标签'
      this.tagForm = {
        id: null,
        name: '',
        color: '#409EFF'
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.tagForm && this.$refs.tagForm.clearValidate()
      })
    },
    handleEdit(row) {
      this.dialogTitle = '编辑标签'
      this.tagForm = {
        id: row.id,
        name: row.name,
        color: row.color || '#409EFF'
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.tagForm && this.$refs.tagForm.clearValidate()
      })
    },
    handleDelete(row) {
      this.$confirm('确认删除该标签吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        axios.delete(`/api/tag/${row.id}`, {
          headers: {
            Authorization: this.$store.getters.token
          }
        })
          .then(() => {
            this.$message.success('删除成功')
            this.fetchTags()
          })
          .catch(error => {
            console.error('删除标签失败', error)
            this.$message.error('删除标签失败')
          })
      }).catch(() => {})
    },
    submitTag() {
      this.$refs.tagForm.validate(valid => {
        if (valid) {
          this.submitLoading = true
          const method = this.tagForm.id ? 'put' : 'post'
          const url = this.tagForm.id ? `/api/tag/${this.tagForm.id}` : '/api/tag'
          
          axios[method](url, this.tagForm, {
            headers: {
              Authorization: this.$store.getters.token
            }
          })
            .then(() => {
              this.$message.success(`${this.tagForm.id ? '更新' : '添加'}标签成功`)
              this.dialogVisible = false
              this.fetchTags()
            })
            .catch(error => {
              console.error(`${this.tagForm.id ? '更新' : '添加'}标签失败`, error)
              this.$message.error(`${this.tagForm.id ? '更新' : '添加'}标签失败`)
            })
            .finally(() => {
              this.submitLoading = false
            })
        } else {
          return false
        }
      })
    }
  }
}
</script>

<style scoped>
.tag-management {
  padding: 20px;
}
.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style>