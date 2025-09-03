<template>
  <div class="login-container">
    <el-card class="login-card">
      <div slot="header" class="header">
        <span>{{ isLogin ? '登录' : '注册' }}</span>
      </div>
      <div v-if="!isLogin && !registerEnabled" class="register-disabled">
        <el-alert
          title="注册功能已关闭"
          type="warning"
          description="系统当前不允许新用户注册，请联系管理员或稍后再试。"
          show-icon
          :closable="false">
        </el-alert>
      </div>
      <el-form :model="form" :rules="rules" ref="loginForm" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item v-if="!isLogin" label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码"></el-input>
        </el-form-item>
        <el-form-item v-if="!isLogin" label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称"></el-input>
        </el-form-item>
        <el-form-item v-if="!isLogin" label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">{{ isLogin ? '登录' : '注册' }}</el-button>
          <el-button @click="switchMode">{{ isLogin ? '去注册' : '去登录' }}</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { mapActions } from 'vuex';
import api from '../api';

export default {
  name: 'Login',
  data() {
    // 密码确认验证
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.form.password) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    };
    
    return {
      isLogin: true,
      loading: false,
      registerEnabled: true, // 默认允许注册
      siteSettings: {}, // 系统设置
      form: {
        username: '',
        password: '',
        confirmPassword: '',
        nickname: '',
        email: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ],
        nickname: [
          { required: true, message: '请输入昵称', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ]
      }
    };
  },
  created() {
    // 获取系统设置
    this.loadSiteSettings();
  },
  methods: {
    ...mapActions(['login', 'register']),
    
    // 加载系统设置
    async loadSiteSettings() {
      try {
        const response = await api.system.getPublicSettings();
        if (response.code === 200) {
          this.siteSettings = response.data;
          console.log('获取到的系统设置:', this.siteSettings);
          
          // 检查是否允许注册
          this.registerEnabled = this.siteSettings.user_register === '1' || this.siteSettings.user_register === 1;
          console.log('系统设置加载成功，注册功能状态:', this.registerEnabled ? '开启' : '关闭');
          console.log('用户注册设置原始值:', this.siteSettings.user_register);
          
          // 如果注册功能关闭且当前在注册页面，则切换到登录页面
          if (!this.registerEnabled && !this.isLogin) {
            this.isLogin = true;
            this.$message.warning('注册功能已关闭，请联系管理员');
          }
        }
      } catch (error) {
        console.error('获取系统设置失败:', error);
      }
    },
    
    // 提交表单
    submitForm() {
      this.$refs.loginForm.validate(async valid => {
        if (valid) {
          this.loading = true;
          try {
            if (this.isLogin) {
              // 登录
              await this.login({
                username: this.form.username,
                password: this.form.password
              });
              this.$message.success('登录成功');
              
              // 检查是否有重定向URL
              const redirectUrl = localStorage.getItem('redirectUrl');
              let targetUrl = redirectUrl || '/';
              
              // 清除重定向URL
              if (redirectUrl) {
                localStorage.removeItem('redirectUrl');
              }
              
              // 只有当目标路径与当前路径不同时才进行导航
              if (this.$route.path !== targetUrl) {
                this.$router.push(targetUrl).catch(err => {
                  // 忽略重复导航错误
                  if (err.name !== 'NavigationDuplicated') {
                    console.error('Navigation error:', err);
                  }
                });
              } else {
                // 如果已经在目标页面，直接刷新页面状态
                this.$router.go(0);
              }
            } else {
              // 注册
              await this.register({
                username: this.form.username,
                password: this.form.password,
                nickname: this.form.nickname,
                email: this.form.email
              });
              this.$message.success('注册成功，请登录');
              this.isLogin = true;
              this.resetForm();
            }
          } catch (error) {
            this.$message.error(error.message || '操作失败');
          } finally {
            this.loading = false;
          }
        }
      });
    },
    
    // 切换登录/注册模式
    switchMode() {
      // 如果要切换到注册模式，但注册功能已关闭，则提示用户
      if (this.isLogin && !this.registerEnabled) {
        this.$message.warning('注册功能已关闭，请联系管理员');
        return;
      }
      
      this.isLogin = !this.isLogin;
      this.resetForm();
    },
    
    // 重置表单
    resetForm() {
      this.$refs.loginForm.resetFields();
    }
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
}

.login-card {
  width: 400px;
}

.header {
  text-align: center;
  font-size: 20px;
  font-weight: bold;
}

.register-disabled {
  margin-bottom: 20px;
}

.el-alert {
  margin-bottom: 15px;
}
</style>