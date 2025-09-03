<template>
  <div class="user-profile-container">
    <el-card class="profile-card">
      <div slot="header" class="header">
        <span>个人信息</span>
      </div>
      <el-form :model="userForm" :rules="rules" ref="userForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="userForm.username" disabled></el-input>
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="userForm.nickname"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone"></el-input>
        </el-form-item>
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            action="/api/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload">
            <img v-if="userForm.avatar" :src="userForm.avatar" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="updateProfile" :loading="loading">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex';

export default {
  name: 'UserProfile',
  data() {
    return {
      loading: false,
      userForm: {
        id: '',
        username: '',
        nickname: '',
        email: '',
        phone: '',
        avatar: ''
      },
      rules: {
        nickname: [
          { required: true, message: '请输入昵称', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ]
      }
    };
  },
  computed: {
    ...mapGetters(['user'])
  },
  created() {
    this.getUserInfo();
  },
  methods: {
    ...mapActions(['updateUserInfo']),
    
    // 获取用户信息
    async getUserInfo() {
      try {
        // 如果store中已有用户信息，直接使用
        if (this.user) {
          this.userForm = { ...this.user };
        } else {
          // 否则从API获取
          const response = await this.$axios.get('/api/user/info');
          if (response.data.code === 200) {
            this.userForm = response.data.data;
          }
        }
      } catch (error) {
        this.$message.error('获取用户信息失败');
      }
    },
    
    // 更新个人信息
    updateProfile() {
      this.$refs.userForm.validate(async valid => {
        if (valid) {
          this.loading = true;
          try {
            await this.updateUserInfo(this.userForm);
            this.$message.success('个人信息更新成功');
          } catch (error) {
            this.$message.error(error.message || '更新失败');
          } finally {
            this.loading = false;
          }
        }
      });
    },
    
    // 头像上传成功回调
    handleAvatarSuccess(res) {
      if (res.code === 200) {
        this.userForm.avatar = res.data;
      } else {
        this.$message.error('上传失败');
      }
    },
    
    // 头像上传前的验证
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG 或 PNG 格式!');
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!');
      }
      return isJPG && isLt2M;
    }
  }
};
</script>

<style scoped>
.user-profile-container {
  padding: 20px;
}

.profile-card {
  max-width: 600px;
  margin: 0 auto;
}

.header {
  font-size: 18px;
  font-weight: bold;
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
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}

.avatar {
  width: 100px;
  height: 100px;
  display: block;
}
</style>