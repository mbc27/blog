<template>
  <div class="admin-dashboard">
    <!-- 顶部导航栏 -->
    <div class="top-header">
      <div class="header-left">
        <i class="el-icon-s-fold toggle-sidebar" @click="toggleSidebar"></i>
        <breadcrumb />
      </div>
      <div class="header-right">
        <el-dropdown trigger="click">
          <span class="el-dropdown-link">
            <el-avatar :size="30" :src="userAvatar"></el-avatar>
            <span class="username">{{ nickname }}</span>
            <i class="el-icon-arrow-down el-icon--right"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item @click.native="goToProfile">个人中心</el-dropdown-item>
            <el-dropdown-item @click.native="handleLogout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>

    <!-- 主体内容区域 -->
    <div class="main-content">
      <!-- 左侧边栏 -->
      <div class="sidebar-container" :class="{ 'collapsed': isCollapse }">
        <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          :collapse="isCollapse"
          router>
          <el-menu-item index="/admin">
            <i class="el-icon-s-home"></i>
            <span slot="title">控制台</span>
          </el-menu-item>
          <el-submenu index="1">
            <template slot="title">
              <i class="el-icon-document"></i>
              <span>内容管理</span>
            </template>
            <el-menu-item index="/admin/article">文章管理</el-menu-item>
            <el-menu-item index="/admin/article-review">文章审核</el-menu-item>
            <el-menu-item index="/admin/category">分类管理</el-menu-item>
            <el-menu-item index="/admin/tag">标签管理</el-menu-item>
            <el-menu-item index="/admin/comment">评论管理</el-menu-item>
          </el-submenu>
          <el-submenu index="2">
            <template slot="title">
              <i class="el-icon-picture"></i>
              <span>相册管理</span>
            </template>
            <el-menu-item index="/admin/photo">照片管理</el-menu-item>
            <el-menu-item index="/admin/photo-category">相册分类</el-menu-item>
          </el-submenu>
          <el-menu-item index="/admin/message">
            <i class="el-icon-chat-line-square"></i>
            <span slot="title">留言管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/friend">
            <i class="el-icon-link"></i>
            <span slot="title">友链管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/user">
            <i class="el-icon-user"></i>
            <span slot="title">用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/about">
            <i class="el-icon-user-solid"></i>
            <span slot="title">关于我管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/setting">
            <i class="el-icon-setting"></i>
            <span slot="title">系统设置</span>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 右侧内容区域 -->
      <div class="content-container" :class="{ 'sidebar-collapsed': isCollapse }">
        <div class="content-main">
          <router-view />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex';
import Breadcrumb from '@/components/Breadcrumb';

export default {
  name: 'AdminDashboard',
  components: {
    Breadcrumb
  },
  data() {
    return {
      isCollapse: false,
      isMobile: false
    };
  },
  computed: {
    ...mapGetters(['user']),
    activeMenu() {
      return this.$route.path;
    },
    nickname() {
      return this.user ? this.user.nickname || this.user.username : '';
    },
    userAvatar() {
      return this.user && this.user.avatar ? this.user.avatar : 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';
    },
    sidebarWidth() {
      if (this.isMobile) {
        return this.isCollapse ? '0px' : '200px';
      }
      return this.isCollapse ? '64px' : '200px';
    }
  },
  created() {
    this.checkAdminPermission();
    this.checkDevice();
    window.addEventListener('resize', this.handleResize);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize);
  },
  methods: {
    ...mapActions(['logout']),
    
    // 检查管理员权限
    checkAdminPermission() {
      console.log('Dashboard checking admin permission, user:', this.user);
      console.log('User role:', this.user ? this.user.role : 'No user');
      
      if (!this.user) {
        this.$message.error('请先登录');
        this.$router.push('/login');
        return;
      }
      
      if (this.user.role !== 0) {
        console.log('Not admin, role is:', this.user.role);
        this.$message.error('您没有管理员权限');
        this.$router.push('/');
      } else {
        console.log('Admin permission verified');
      }
    },
    
    // 切换侧边栏
    toggleSidebar() {
      this.isCollapse = !this.isCollapse;
    },
    
    // 跳转到个人中心
    goToProfile() {
      this.$router.push('/user/profile');
    },
    
    // 退出登录
    handleLogout() {
      this.$confirm('确认退出登录?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.logout();
        this.$router.push('/login');
        this.$message.success('退出登录成功');
      }).catch(() => {});
    },
    
    // 检测设备类型
    checkDevice() {
      this.isMobile = window.innerWidth <= 768;
      // 移动端默认折叠侧边栏
      if (this.isMobile) {
        this.isCollapse = true;
      }
    },
    
    // 处理窗口大小变化
    handleResize() {
      this.checkDevice();
    }
  }
};
</script>

<style scoped>
.admin-dashboard {
  height: 100vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  margin: 0;
  padding: 0;
}

/* 顶部导航栏 */
.top-header {
  background-color: #fff;
  color: #333;
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1001;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
}

.toggle-sidebar {
  font-size: 20px;
  margin-right: 15px;
  cursor: pointer;
}

/* 主体内容区域 */
.main-content {
  display: flex;
  height: calc(100vh - 60px);
  position: absolute;
  top: 60px;
  left: 0;
  right: 0;
  bottom: 0;
  margin: 0;
  padding: 0;
}

/* 左侧边栏 */
.sidebar-container {
  width: 200px;
  background-color: #304156;
  color: #bfcbd9;
  transition: width 0.3s ease;
  overflow: hidden;
  flex-shrink: 0;
}

.sidebar-container.collapsed {
  width: 64px;
}

.el-menu-vertical {
  width: 100%;
  height: 100%;
  overflow-y: auto;
  border-right: none;
}

.el-menu-vertical:not(.el-menu--collapse) {
  width: 200px;
}

.el-menu-vertical.el-menu--collapse {
  width: 64px;
}

.el-menu {
  border-right: none;
  height: 100%;
}

/* 右侧内容区域 */
.content-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  width: calc(100% - 200px);
  transition: width 0.3s ease;
}

.content-container.sidebar-collapsed {
  width: calc(100% - 64px);
}

.content-main {
  background-color: #f0f2f5;
  padding: 0;
  overflow-y: auto;
  height: 100%;
  flex: 1;
}

/* 响应式布局 */
@media (max-width: 768px) {
  .sidebar-container {
    position: fixed;
    left: 0;
    top: 60px;
    height: calc(100vh - 60px);
    z-index: 1000;
    transform: translateX(-100%);
    transition: transform 0.3s ease;
  }
  
  .sidebar-container:not(.collapsed) {
    transform: translateX(0);
    box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
  }
  
  .content-container {
    width: 100%;
    margin-left: 0;
  }
  
  .content-container.sidebar-collapsed {
    width: 100%;
  }
  
  .content-main {
    padding: 10px;
  }
  
  .header-left {
    flex: 1;
  }
  
  .toggle-sidebar {
    display: block !important;
  }
}

@media (max-width: 480px) {
  .top-header {
    padding: 0 10px;
    height: 50px;
  }
  
  .main-content {
    height: calc(100vh - 50px);
    margin-top: 50px;
  }
  
  .sidebar-container {
    top: 50px;
    height: calc(100vh - 50px);
  }
  
  .content-main {
    padding: 5px;
  }
  
  .username {
    display: none;
  }
  
  .el-avatar {
    width: 25px !important;
    height: 25px !important;
  }
}

.username {
  margin-left: 8px;
  margin-right: 5px;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin-left: 8px;
  margin-right: 5px;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  cursor: pointer;
}
</style>yle>