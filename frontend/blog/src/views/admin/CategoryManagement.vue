<template>
  <div class="category-management">
    <el-card shadow="hover">
      <div slot="header" class="clearfix">
        <span>分类管理</span>
        <el-button style="float: right" type="primary" size="small" @click="handleAddCategory">新增分类</el-button>
      </div>
      
      <el-table
        :data="categories"
        style="width: 100%"
        v-loading="loading">
        <el-table-column
          prop="id"
          label="ID"
          width="80">
        </el-table-column>
        <el-table-column
          prop="name"
          label="分类名称"
          width="180">
        </el-table-column>
        <el-table-column
          prop="description"
          label="描述">
          <template slot-scope="scope">
            <el-tooltip :content="scope.row.description" placement="top" :disabled="!scope.row.description || scope.row.description.length < 30">
              <span>{{ scope.row.description | ellipsis(30) }}</span>
            </el-tooltip>
          </template>
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
              @click="handleDelete(scope.row)"
              :disabled="scope.row.articleCount > 0">删除</el-button>
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

    <!-- 分类编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form :model="categoryForm" :rules="categoryRules" ref="categoryForm" label-width="100px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称"></el-input>
        </el-form-item>
        <el-form-item label="分类描述" prop="description">
          <el-input type="textarea" v-model="categoryForm.description" :rows="3" placeholder="请输入分类描述"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitCategory" :loading="submitLoading">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'CategoryManagement',
  data() {
    return {
      categories: [],
      loading: false,
      pagination: {
        current: 1,
        size: 10,
        total: 0
      },
      dialogVisible: false,
      dialogTitle: '新增分类',
      categoryForm: {
        id: null,
        name: '',
        description: ''
      },
      categoryRules: {
        name: [
          { required: true, message: '请输入分类名称', trigger: 'blur' },
          { max: 50, message: '长度不能超过50个字符', trigger: 'blur' }
        ],
        description: [
          { max: 200, message: '长度不能超过200个字符', trigger: 'blur' }
        ]
      },
      submitLoading: false
    }
  },
  filters: {
    ellipsis(value, maxLength) {
      if (!value) return ''
      if (value.length <= maxLength) return value
      return value.slice(0, maxLength) + '...'
    }
  },
  created() {
    this.fetchCategories()
  },
  methods: {
    fetchCategories() {
      this.loading = true
      const params = {
        current: this.pagination.current,
        size: this.pagination.size
      }
      
      axios.get('/api/category', { 
        params,
        headers: {
          Authorization: this.$store.getters.token
        }
      })
        .then(response => {
          if (response.data && response.data.data) {
            // 检查返回的数据是否是分页对象
            if (response.data.data.records) {
              this.categories = response.data.data.records
              this.pagination.total = response.data.data.total
            } else {
              this.categories = response.data.data
              this.pagination.total = response.data.total || response.data.data.length
            }
          }
        })
        .catch(error => {
          console.error('获取分类列表失败', error)
          this.$message.error('获取分类列表失败')
        })
        .finally(() => {
          this.loading = false
        })
    },
    handleSizeChange(val) {
      this.pagination.size = val
      this.fetchCategories()
    },
    handleCurrentChange(val) {
      this.pagination.current = val
      this.fetchCategories()
    },
    handleAddCategory() {
      this.dialogTitle = '新增分类'
      this.categoryForm = {
        id: null,
        name: '',
        description: ''
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.categoryForm && this.$refs.categoryForm.clearValidate()
      })
    },
    handleEdit(row) {
      this.dialogTitle = '编辑分类'
      this.categoryForm = {
        id: row.id,
        name: row.name,
        description: row.description
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.categoryForm && this.$refs.categoryForm.clearValidate()
      })
    },
    handleDelete(row) {
      if (row.articleCount > 0) {
        this.$message.warning('该分类下有文章，不能删除')
        return
      }
      
      this.$confirm('确认删除该分类吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        axios.delete(`/api/category/${row.id}`, {
          headers: {
            Authorization: this.$store.getters.token
          }
        })
          .then(() => {
            this.$message.success('删除成功')
            this.fetchCategories()
          })
          .catch(error => {
            console.error('删除分类失败', error)
            this.$message.error('删除分类失败')
          })
      }).catch(() => {})
    },
    submitCategory() {
      this.$refs.categoryForm.validate(valid => {
        if (valid) {
          this.submitLoading = true
          const method = this.categoryForm.id ? 'put' : 'post'
          const url = this.categoryForm.id ? `/api/category/${this.categoryForm.id}` : '/api/category'
          
          axios[method](url, this.categoryForm, {
            headers: {
              Authorization: this.$store.getters.token
            }
          })
            .then(() => {
              this.$message.success(`${this.categoryForm.id ? '更新' : '添加'}分类成功`)
              this.dialogVisible = false
              this.fetchCategories()
            })
            .catch(error => {
              console.error(`${this.categoryForm.id ? '更新' : '添加'}分类失败`, error)
              this.$message.error(`${this.categoryForm.id ? '更新' : '添加'}分类失败`)
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
.category-management {
  padding: 20px;
}
.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style>