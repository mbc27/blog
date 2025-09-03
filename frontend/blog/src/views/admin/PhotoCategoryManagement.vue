<template>
  <div class="photo-category-management">
    <el-card shadow="hover">
      <div slot="header" class="clearfix">
        <span>相册分类管理</span>
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
          prop="categoryName"
          label="分类名称"
          width="180">
        </el-table-column>
        <el-table-column
          prop="photoCount"
          label="照片数量"
          width="100">
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
              :disabled="scope.row.photoCount > 0">删除</el-button>
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
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="categoryForm.categoryName" placeholder="请输入分类名称"></el-input>
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
import { mapGetters } from 'vuex'

export default {
  name: 'PhotoCategoryManagement',
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
      dialogTitle: '新增相册分类',
      categoryForm: {
        id: null,
        categoryName: '',
        description: '',
        coverUrl: ''
      },
      categoryRules: {
        categoryName: [
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
  computed: {
    ...mapGetters(['token']),
    uploadHeaders() {
      const token = localStorage.getItem('token')
      return {
        'Authorization': token ? `Bearer ${token}` : ''
      }
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
      
      const token = localStorage.getItem('token')
      axios.get('/api/admin/photo/categories', { 
        params,
        headers: {
          'Authorization': token ? `Bearer ${token}` : ''
        }
      })
        .then(response => {
          console.log('API响应:', response.data)
          if (response.data && response.data.data) {
            this.categories = response.data.data.records || []
            this.pagination.total = response.data.data.total || 0
          } else {
            this.categories = []
            this.pagination.total = 0
          }
        })
        .catch(error => {
          console.error('获取相册分类列表失败', error)
          this.$message.error('获取相册分类列表失败')
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
      this.dialogTitle = '新增相册分类'
      this.categoryForm = {
        id: null,
        categoryName: '',
        description: '',
        coverUrl: ''
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.categoryForm && this.$refs.categoryForm.clearValidate()
      })
    },
    handleEdit(row) {
      this.dialogTitle = '编辑相册分类'
      this.categoryForm = {
        id: row.id,
        categoryName: row.categoryName,
        description: row.description
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.categoryForm && this.$refs.categoryForm.clearValidate()
      })
    },
    handleDelete(row) {
      if (row.photoCount > 0) {
        this.$message.warning('该分类下有照片，不能删除')
        return
      }
      
      this.$confirm('确认删除该相册分类吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const token = localStorage.getItem('token')
        axios.delete(`/api/admin/photo/categories/${row.id}`, {
          headers: {
            'Authorization': token ? `Bearer ${token}` : ''
          }
        })
          .then(() => {
            this.$message.success('删除成功')
            this.fetchCategories()
          })
          .catch(error => {
            console.error('删除相册分类失败', error)
            this.$message.error('删除相册分类失败')
          })
      }).catch(() => {})
    },
    handleCoverSuccess(res) {
      this.categoryForm.coverUrl = res.data
      this.$message.success('封面上传成功')
    },
    beforeCoverUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isImage) {
        this.$message.error('上传封面只能是图片格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传封面大小不能超过 2MB!')
      }
      return isImage && isLt2M
    },
    submitCategory() {
      this.$refs.categoryForm.validate(valid => {
        if (valid) {
          this.submitLoading = true
          const method = this.categoryForm.id ? 'put' : 'post'
          const url = this.categoryForm.id ? `/api/admin/photo/categories/${this.categoryForm.id}` : '/api/admin/photo/categories'
          
          const token = localStorage.getItem('token')
          const config = {
            headers: {
              'Authorization': token ? `Bearer ${token}` : ''
            }
          }
          
          axios[method](url, this.categoryForm, config)
            .then(() => {
              this.$message.success(`${this.categoryForm.id ? '更新' : '添加'}相册分类成功`)
              this.dialogVisible = false
              this.fetchCategories()
            })
            .catch(error => {
              console.error(`${this.categoryForm.id ? '更新' : '添加'}相册分类失败`, error)
              this.$message.error(`${this.categoryForm.id ? '更新' : '添加'}相册分类失败`)
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
.photo-category-management {
  padding: 20px;
}
.pagination-container {
  margin-top: 20px;
  text-align: right;
}
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>