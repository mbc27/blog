<template>
  <div class="forgot-password-container">
    <div class="forgot-password-background">
      <div class="background-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
      </div>
    </div>
    
    <div class="forgot-password-card">
      <div class="card-header">
        <h2 class="title">忘记密码</h2>
        <p class="subtitle">{{ getSubtitle() }}</p>
      </div>
      
      <!-- 步骤1：输入邮箱 -->
      <div v-if="step === 1" class="step-content">
        <el-form :model="emailForm" :rules="emailRules" ref="emailForm" class="forgot-form">
          <el-form-item prop="email">
            <el-input 
              v-model="emailForm.email" 
              placeholder="请输入注册时使用的邮箱"
              prefix-icon="el-icon-message"
              size="large">
            </el-input>
          </el-form-item>
          
          <el-form-item class="submit-section">
            <el-button 
              type="primary" 
              @click="sendVerificationCode" 
              :loading="loading"
              size="large"
              class="submit-btn">
              发送验证码
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 步骤2：输入验证码 -->
      <div v-if="step === 2" class="step-content">
        <div class="email-info">
          <p>验证码已发送至：<strong>{{ emailForm.email }}</strong></p>
          <p class="countdown-text" v-if="countdown > 0">
            {{ countdown }}秒后可重新发送
          </p>
          <el-button v-else type="text" @click="resendCode" class="resend-btn">
            重新发送验证码
          </el-button>
        </div>
        
        <el-form :model="codeForm" :rules="codeRules" ref="codeForm" class="forgot-form">
          <el-form-item prop="code">
            <el-input 
              v-model="codeForm.code" 
              placeholder="请输入6位验证码"
              prefix-icon="el-icon-key"
              size="large"
              maxlength="6">
            </el-input>
          </el-form-item>
          
          <el-form-item class="submit-section">
            <el-button 
              type="primary" 
              @click="verifyCode" 
              :loading="loading"
              size="large"
              class="submit-btn">
              验证
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 步骤3：设置新密码 -->
      <div v-if="step === 3" class="step-content">
        <el-form :model="passwordForm" :rules="passwordRules" ref="passwordForm" class="forgot-form">
          <el-form-item prop="newPassword">
            <el-input 
              v-model="passwordForm.newPassword" 
              type="password"
              placeholder="请输入新密码"
              prefix-icon="el-icon-lock"
              size="large"
              show-password>
            </el-input>
          </el-form-item>
          
          <el-form-item prop="confirmPassword">
            <el-input 
              v-model="passwordForm.confirmPassword" 
              type="password"
              placeholder="请再次输入新密码"
              prefix-icon="el-icon-lock"
              size="large"
              show-password>
            </el-input>
          </el-form-item>
          
          <el-form-item class="submit-section">
            <el-button 
              type="primary" 
              @click="resetPassword" 
              :loading="loading"
              size="large"
              class="submit-btn">
              重置密码
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 返回登录 -->
      <div class="back-to-login">
        <el-button type="text" @click="backToLogin" class="back-btn">
          返回登录
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
import api from '../api';

export default {
  name: 'ForgotPassword',
  data() {
    // 确认密码验证
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    };
    
    return {
      step: 1, // 当前步骤：1-输入邮箱，2-输入验证码，3-设置新密码
      loading: false,
      countdown: 0, // 倒计时
      countdownTimer: null,
      
      // 邮箱表单
      emailForm: {
        email: ''
      },
      emailRules: {
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ]
      },
      
      // 验证码表单
      codeForm: {
        code: ''
      },
      codeRules: {
        code: [
          { required: true, message: '请输入验证码', trigger: 'blur' },
          { len: 6, message: '验证码为6位数字', trigger: 'blur' }
        ]
      },
      
      // 密码表单
      passwordForm: {
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      }
    };
  },
  methods: {
    // 获取副标题
    getSubtitle() {
      switch (this.step) {
        case 1:
          return '请输入您注册时使用的邮箱地址';
        case 2:
          return '请输入邮箱中收到的验证码';
        case 3:
          return '请设置您的新密码';
        default:
          return '';
      }
    },
    
    // 发送验证码
    async sendVerificationCode() {
      this.$refs.emailForm.validate(async valid => {
        if (valid) {
          this.loading = true;
          try {
            const response = await api.auth.sendForgotPasswordCode({
              email: this.emailForm.email
            });
            
            if (response.code === 200) {
              this.$message({
                message: '验证码已发送到您的邮箱，请查收（如未收到请检查垃圾邮件）',
                type: 'success',
                duration: 3000
              });
              this.step = 2;
              this.startCountdown();
            } else {
              this.$message({
                message: '验证码发送失败，请检查邮箱地址是否正确或稍后重试',
                type: 'error',
                duration: 3000
              });
            }
          } catch (error) {
            this.$message({
              message: '网络连接失败，请检查网络后重试',
              type: 'error',
              duration: 3000
            });
          } finally {
            this.loading = false;
          }
        }
      });
    },
    
    // 重新发送验证码
    async resendCode() {
      this.loading = true;
      try {
        const response = await api.auth.sendForgotPasswordCode({
          email: this.emailForm.email
        });
        
        if (response.code === 200) {
          this.$message({
            message: '验证码已重新发送，请查收邮件',
            type: 'success',
            duration: 3000
          });
          this.startCountdown();
        } else {
          this.$message({
            message: '验证码重新发送失败，请稍后重试',
            type: 'error',
            duration: 3000
          });
        }
      } catch (error) {
        this.$message({
          message: '网络连接失败，请检查网络后重试',
          type: 'error',
          duration: 3000
        });
      } finally {
        this.loading = false;
      }
    },
    
    // 验证验证码
    async verifyCode() {
      this.$refs.codeForm.validate(async valid => {
        if (valid) {
          this.loading = true;
          try {
            const response = await api.auth.verifyForgotPasswordCode({
              email: this.emailForm.email,
              code: this.codeForm.code
            });
            
            if (response.code === 200) {
              this.$message({
                message: '验证码验证通过，请设置新密码',
                type: 'success',
                duration: 3000
              });
              this.step = 3;
            } else {
              this.$message({
                message: '验证码错误或已过期，请重新获取验证码',
                type: 'error',
                duration: 3000
              });
            }
          } catch (error) {
            this.$message({
              message: '网络连接失败，请检查网络后重试',
              type: 'error',
              duration: 3000
            });
          } finally {
            this.loading = false;
          }
        }
      });
    },
    
    // 重置密码
    async resetPassword() {
      this.$refs.passwordForm.validate(async valid => {
        if (valid) {
          this.loading = true;
          try {
            const response = await api.auth.resetPassword({
              email: this.emailForm.email,
              code: this.codeForm.code,
              newPassword: this.passwordForm.newPassword
            });
            
            if (response.code === 200) {
              this.$message({
                message: '密码重置成功！3秒后自动跳转到登录页面...',
                type: 'success',
                duration: 3000
              });
              setTimeout(() => {
                this.$router.push('/login');
              }, 2000);
            } else {
              this.$message({
                message: '密码重置失败，请重新尝试或联系管理员',
                type: 'error',
                duration: 3000
              });
            }
          } catch (error) {
            this.$message({
              message: '网络连接失败，请检查网络后重试',
              type: 'error',
              duration: 3000
            });
          } finally {
            this.loading = false;
          }
        }
      });
    },
    
    // 开始倒计时
    startCountdown() {
      this.countdown = 60;
      this.countdownTimer = setInterval(() => {
        this.countdown--;
        if (this.countdown <= 0) {
          clearInterval(this.countdownTimer);
        }
      }, 1000);
    },
    
    // 返回登录
    backToLogin() {
      this.$router.push('/login');
    }
  },
  
  beforeDestroy() {
    // 清除倒计时
    if (this.countdownTimer) {
      clearInterval(this.countdownTimer);
    }
  }
};
</script>

<style scoped>
.forgot-password-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  overflow: hidden;
}

.forgot-password-background {
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

.forgot-password-card {
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

.step-content {
  margin-bottom: 20px;
}

.email-info {
  text-align: center;
  margin-bottom: 20px;
  padding: 15px;
  background: rgba(102, 126, 234, 0.1);
  border-radius: 12px;
}

.email-info p {
  margin: 5px 0;
  color: #666;
}

.countdown-text {
  color: #999;
  font-size: 14px;
}

.resend-btn {
  color: #667eea;
  font-size: 14px;
  padding: 0;
}

.resend-btn:hover {
  color: #5a6fd8;
}

.forgot-form {
  width: 100%;
  max-width: 350px;
  margin: 0 auto;
}

.forgot-form .el-form-item {
  margin-bottom: 20px;
}

.forgot-form .el-input {
  border-radius: 12px;
  width: 100%;
}

.forgot-form .el-input__inner {
  border: 2px solid #e1e8ed;
  border-radius: 12px;
  padding: 15px 20px 15px 45px;
  font-size: 16px;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.8);
  width: 100%;
  box-sizing: border-box;
}

.forgot-form .el-input__inner:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.forgot-form .el-input__prefix {
  left: 15px;
  color: #999;
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

.back-to-login {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #e1e8ed;
}

.back-btn {
  color: #667eea;
  font-weight: 600;
  padding: 0;
  font-size: 14px;
}

.back-btn:hover {
  color: #5a6fd8;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .forgot-password-card {
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
  
  .forgot-form {
    max-width: 100%;
  }
  
  .forgot-form .el-form-item {
    margin-bottom: 18px;
  }
  
  .forgot-form .el-input__inner {
    padding: 12px 15px 12px 40px;
    font-size: 14px;
  }
  
  .submit-btn {
    height: 45px;
    font-size: 14px;
    max-width: 100%;
  }
  
  .submit-section {
    margin-bottom: 12px;
  }
}

@media (max-height: 700px) {
  .forgot-password-card {
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
  
  .forgot-form .el-form-item {
    margin-bottom: 15px;
  }
  
  .submit-section {
    margin-bottom: 10px;
  }
}
</style>