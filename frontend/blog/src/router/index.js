import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import store from '../store'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('../views/About.vue')
  },
  {
    path: '/article',
    name: 'Article',
    component: () => import('../views/Article.vue')
  },
  {
    path: '/article/:id',
    name: 'ArticleDetail',
    component: () => import('../views/ArticleDetail.vue')
  },
  {
    path: '/photos',
    name: 'Photos',
    component: () => import('../views/Photos.vue')
  },
  {
    path: '/message',
    name: 'Message',
    component: () => import('../views/Message.vue')
  },
  {
    path: '/friends',
    name: 'Friends',
    component: () => import('../views/Friends.vue')
  },
  {
    path: '/write',
    name: 'WriteArticle',
    component: () => import('../views/WriteArticle.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { guest: true }
  },
  {
    path: '/user/profile',
    name: 'UserProfile',
    component: () => import('../views/UserProfile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    component: () => import('../views/admin/Dashboard.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('../views/admin/AdminHome.vue'),
        meta: { title: '控制台' }
      },
      {
        path: 'article',
        name: 'AdminArticle',
        component: () => import('../views/admin/ArticleManagement.vue'),
        meta: { title: '文章管理' }
      },
      {
        path: 'article-review',
        name: 'ArticleReview',
        component: () => import('../views/admin/ArticleReview.vue'),
        meta: { title: '文章审核' }
      },
      {
        path: 'category',
        name: 'AdminCategory',
        component: () => import('../views/admin/CategoryManagement.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'tag',
        name: 'AdminTag',
        component: () => import('../views/admin/TagManagement.vue'),
        meta: { title: '标签管理' }
      },
      {
        path: 'comment',
        name: 'AdminComment',
        component: () => import('../views/admin/CommentManagement.vue'),
        meta: { title: '评论管理' }
      },
      {
        path: 'photo',
        name: 'AdminPhoto',
        component: () => import('../views/admin/PhotoManagement.vue'),
        meta: { title: '照片管理' }
      },
      {
        path: 'photo-category',
        name: 'AdminPhotoCategory',
        component: () => import('../views/admin/PhotoCategoryManagement.vue'),
        meta: { title: '相册分类' }
      },
      {
        path: 'message',
        name: 'AdminMessage',
        component: () => import('../views/admin/MessageManagement.vue'),
        meta: { title: '留言管理' }
      },
      {
        path: 'friend',
        name: 'AdminFriend',
        component: () => import('../views/admin/FriendLinkManagement.vue'),
        meta: { title: '友链管理' }
      },
      {
        path: 'user',
        name: 'AdminUser',
        component: () => import('../views/admin/UserManagement.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'setting',
        name: 'AdminSetting',
        component: () => import('../views/admin/SystemSetting.vue'),
        meta: { title: '系统设置' }
      },
        {
          path: 'friend-links',
          name: 'FriendLinkManagement',
          component: () => import('@/views/admin/FriendLinkManagement.vue'),
          meta: { title: '友链管理', requiresAuth: true, requiresAdmin: true }
        },
        {
          path: 'users',
          name: 'UserManagement',
          component: () => import('@/views/admin/UserManagement.vue'),
          meta: { title: '用户管理', requiresAuth: true, requiresAdmin: true }
        }
    ]
  },
  {
    path: '*',
    redirect: '/'
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes,
  scrollBehavior() {
    // 始终滚动到顶部
    return { x: 0, y: 0 }
  }
})

// 全局前置守卫
router.beforeEach(async (to, from, next) => {
  const token = localStorage.getItem('token')
  const requiresAdmin = to.matched.some(record => record.meta.requiresAdmin)
  const isGuest = to.matched.some(record => record.meta.guest)
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  
  console.log('Route guard - Path:', to.path)
  console.log('Route guard - Token exists:', !!token)
  console.log('Route guard - Requires auth:', requiresAuth)
  console.log('Route guard - Is guest:', isGuest)
  
  // 如果访问登录页面
  if (to.path === '/login') {
    // 如果有强制登录参数，允许访问
    if (to.query.force === 'true') {
      console.log('Force login requested, clearing auth and accessing login page')
      // 清除当前认证信息
      store.commit('CLEAR_AUTH')
      next()
    } else if (token) {
      // 如果已经有token且没有强制登录，重定向到首页
      console.log('Already authenticated, redirecting to home')
      next('/')
    } else {
      console.log('Accessing login page')
      next()
    }
    return
  }
  
  // 如果需要认证但没有token，跳转到登录页
  if (requiresAuth && !token) {
    console.log('Authentication required but no token, redirecting to login')
    next('/login')
    return
  }
  
  // 如果有token但没有用户信息，尝试获取用户信息
  if (token && !store.state.user) {
    console.log('Token found but no user info, attempting to get user info')
    try {
      await store.dispatch('getUserInfo')
    } catch (error) {
      console.error('Failed to get user info in route guard:', error)
      // 如果获取用户信息失败且访问需要认证的页面，跳转到登录页
      if (requiresAuth) {
        console.log('Failed to get user info for protected route, redirecting to login')
        localStorage.removeItem('token')
        next('/login')
        return
      }
    }
  }
  
  // 检查管理员权限
  if (requiresAdmin) {
    const user = store.state.user
    const isAdmin = user && user.role === 0
    if (!isAdmin) {
      console.log('Admin required but user is not admin, redirecting to home')
      next('/')
      return
    }
  }
  
  console.log('Navigation allowed')
  next()
})

export default router