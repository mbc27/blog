<template>
  <div class="page-container login-container">
    <div class="login-background">
      <div class="background-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
      </div>
    </div>
    
    <div class="login-card">
      <div class="card-header">
        <h2 class="title">{{ isLogin ? '欢迎回来' : '加入我们' }}</h2>
        <p class="subtitle">{{ isLogin ? '登录您的账户继续精彩旅程' : '创建账户开始您的博客之旅' }}</p>
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
      
      <el-form :model="form" :rules="rules" ref="loginForm" class="login-form">
        <el-form-item prop="username">
          <el-input 
            v-model="form.username" 
            placeholder="请输入用户名"
            prefix-icon="el-icon-user"
            size="large">
          </el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="请输入密码"
            prefix-icon="el-icon-lock"
            size="large"
            show-password>
          </el-input>
        </el-form-item>
        
        <el-form-item v-if="!isLogin" prop="confirmPassword">
          <el-input 
            v-model="form.confirmPassword" 
            type="password" 
            placeholder="请再次输入密码"
            prefix-icon="el-icon-lock"
            size="large"
            show-password>
          </el-input>
        </el-form-item>
        
        <el-form-item v-if="!isLogin" prop="nickname">
          <el-input 
            v-model="form.nickname" 
            placeholder="请输入昵称"
            prefix-icon="el-icon-user-solid"
            size="large">
          </el-input>
        </el-form-item>
        
        <el-form-item v-if="!isLogin" prop="email">
          <el-input 
            v-model="form.email" 
            placeholder="请输入邮箱"
            prefix-icon="el-icon-message"
            size="large">
          </el-input>
        </el-form-item>
        
        <!-- 登录页面的记住密码选项 -->
        <div v-if="isLogin" class="form-options">
          <el-checkbox v-model="rememberPassword" class="remember-checkbox">
            记住密码
          </el-checkbox>
          <el-button type="text" @click="showForgotPassword" class="forgot-password-btn">
            忘记密码？
          </el-button>
        </div>
        
        <!-- 注册页面的协议复选框 -->
        <div v-if="!isLogin" class="form-options">
          <el-checkbox v-model="agreeTerms" class="terms-checkbox">
            注册即同意授权以上所填写的信息
          </el-checkbox>
        </div>
        
        <el-form-item class="submit-section">
          <el-button 
            type="primary" 
            @click="submitForm" 
            :loading="loading"
            :disabled="!isLogin && !agreeTerms"
            size="large"
            class="submit-btn">
            {{ isLogin ? '立即登录' : '立即注册' }}
          </el-button>
        </el-form-item>
        
        <div class="switch-mode">
          <span class="switch-text">
            {{ isLogin ? '还没有账户？' : '已有账户？' }}
          </span>
          <el-button type="text" @click="switchMode" class="switch-btn">
            {{ isLogin ? '立即注册' : '立即登录' }}
          </el-button>
        </div>
      </el-form>
    </div>
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
      rememberPassword: false, // 记住密码选项
      agreeTerms: false, // 同意注册协议
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
    // 加载记住的密码
    this.loadRememberedCredentials();
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
      // 注册时检查是否同意协议
      if (!this.isLogin && !this.agreeTerms) {
        this.$message.warning('请先同意注册协议');
        return;
      }
      
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
              
              // 处理记住密码
              this.handleRememberPassword();
              
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
            if (error.message && error.message.includes('用户名或密码错误')) {
              this.$message.error('用户名或密码错误，请重新输入');
            } else if (error.message && error.message.includes('用户不存在')) {
              this.$message.error('该用户不存在，请检查用户名或先注册');
            } else if (error.message && error.message.includes('密码错误')) {
              this.$message.error('密码错误，请重新输入或使用忘记密码功能');
            } else if (error.message && error.message.includes('账户被禁用')) {
              this.$message.error('您的账户已被禁用，请联系管理员');
            } else {
              this.$message.error(error.message || '登录失败，请检查网络连接或稍后重试');
            }
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
      this.agreeTerms = false;
    },
    
    // 处理记住密码功能
    handleRememberPassword() {
      if (this.rememberPassword) {
        // 保存用户名和密码到localStorage
        localStorage.setItem('rememberedUsername', this.form.username);
        localStorage.setItem('rememberedPassword', this.form.password);
        localStorage.setItem('rememberPassword', 'true');
      } else {
        // 清除保存的用户名和密码
        localStorage.removeItem('rememberedUsername');
        localStorage.removeItem('rememberedPassword');
        localStorage.removeItem('rememberPassword');
      }
    },
    
    // 加载记住的凭据
    loadRememberedCredentials() {
      const remembered = localStorage.getItem('rememberPassword');
      if (remembered === 'true') {
        this.rememberPassword = true;
        this.form.username = localStorage.getItem('rememberedUsername') || '';
        this.form.password = localStorage.getItem('rememberedPassword') || '';
      }
    },
    
    // 显示忘记密码页面
    showForgotPassword() {
      this.$router.push('/forgot-password');
    }
  }
};
</script>

<style scoped>
.login-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  overflow: hidden;
}

.login-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  z-index: 1;
}

.background-shapes {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 200px;
  height: 200px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 150px;
  height: 150px;
  top: 60%;
  right: 15%;
  animation-delay: 2s;
}

.shape-3 {
  width: 100px;
  height: 100px;
  bottom: 20%;
  left: 20%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

.login-card {
  position: relative;
  z-index: 2;
  width: 450px;
  max-width: 90vw;
  max-height: 90vh;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  overflow-y: auto;
}

.card-header {
  text-align: center;
  margin-bottom: 30px;
}

.title {
  font-size: 32px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 10px 0;
}

.subtitle {
  color: #666;
  font-size: 16px;
  margin: 0;
  line-height: 1.5;
}

.login-form {
  width: 100%;
  max-width: 350px;
  margin: 0 auto;
}

.login-form .el-form-item {
  margin-bottom: 20px;
}

.login-form .el-input {
  border-radius: 12px;
  width: 100%;
}

.login-form .el-input__inner {
  border: 2px solid #e1e8ed;
  border-radius: 12px;
  padding: 15px 20px 15px 45px;
  font-size: 16px;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.8);
  width: 100%;
  box-sizing: border-box;
}

.login-form .el-input__inner:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.login-form .el-input__prefix {
  left: 15px;
  color: #999;
}

.form-options {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.remember-checkbox,
.terms-checkbox {
  color: #666;
  font-size: 14px;
}

.remember-checkbox .el-checkbox__label,
.terms-checkbox .el-checkbox__label {
  color: #666;
}

.forgot-password-btn {
  color: #667eea;
  font-size: 14px;
  padding: 0;
}

.forgot-password-btn:hover {
  color: #5a6fd8;
}

.submit-section {
  margin-bottom: 15px;
}

.submit-btn {
  width: 100%;
  max-width: 350px;
  height: 50px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
}

.submit-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
}

.submit-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.switch-mode {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #e1e8ed;
}

.switch-text {
  color: #666;
  font-size: 14px;
  margin-right: 8px;
}

.switch-btn {
  color: #667eea;
  font-weight: 600;
  padding: 0;
  font-size: 14px;
}

.switch-btn:hover {
  color: #5a6fd8;
}

.register-disabled {
  margin-bottom: 20px;
}

.register-disabled .el-alert {
  border-radius: 12px;
  border: none;
  background: rgba(230, 162, 60, 0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-card {
    width: 95vw;
    max-height: 95vh;
    padding: 25px 20px;
    margin: 10px;
  }
  
  .card-header {
    margin-bottom: 25px;
  }
  
  .title {
    font-size: 28px;
  }
  
  .subtitle {
    font-size: 14px;
  }
  
  .login-form {
    max-width: 100%;
  }
  
  .login-form .el-form-item {
    margin-bottom: 18px;
  }
  
  .login-form .el-input__inner {
    padding: 12px 15px 12px 40px;
    font-size: 14px;
  }
  
  .submit-btn {
    height: 45px;
    font-size: 14px;
    max-width: 100%;
  }
  
  .form-options {
    margin-bottom: 18px;
  }
  
  .submit-section {
    margin-bottom: 12px;
  }
}

@media (max-height: 700px) {
  .login-card {
    max-height: 95vh;
    padding: 20px;
  }
  
  .card-header {
    margin-bottom: 20px;
  }
  
  .title {
    font-size: 28px;
    margin-bottom: 8px;
  }
  
  .login-form .el-form-item {
    margin-bottom: 15px;
  }
  
  .form-options {
    margin-bottom: 15px;
  }
  
  .submit-section {
    margin-bottom: 10px;
  }
}
</style>