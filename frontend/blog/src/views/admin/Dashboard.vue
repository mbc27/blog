<template>
  <div class="admin-dashboard">
    <el-container>
      <el-aside width="200px">
        <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
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
          <el-menu-item index="/admin/setting">
            <i class="el-icon-setting"></i>
            <span slot="title">系统设置</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header>
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
                <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </el-header>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
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
      isCollapse: false
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
    }
  },
  created() {
    this.checkAdminPermission();
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
    }
  }
};
</script>

<style scoped>
.admin-dashboard {
  height: 100vh;
}

.el-header {
  background-color: #fff;
  color: #333;
  line-height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
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

.el-aside {
  background-color: #304156;
  color: #bfcbd9;
  transition: width 0.3s;
}

.el-menu-vertical:not(.el-menu--collapse) {
  width: 200px;
  min-height: 400px;
}

.el-menu {
  border-right: none;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
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
</style>