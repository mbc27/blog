import axios from 'axios'
import store from '../store'
import { Message } from 'element-ui'

// 创建axios实例
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API || 'http://localhost:8080/api', // API的基础URL
  timeout: 10000 // 请求超时时间
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 优先从localStorage获取token，确保页面刷新后仍能获取到token
    const token = localStorage.getItem('token') || store.getters.token
    if (token) {
      // 确保使用正确的Bearer格式
      config.headers['Authorization'] = `Bearer ${token}`
      console.log(`请求 ${config.url} 添加Authorization头: Bearer ${token}`)
    } else {
      console.log(`请求 ${config.url} 没有token`)
    }
    
    // 减少日志输出，只在开发环境打印
    if (process.env.NODE_ENV === 'development') {
      console.log('请求配置:', config)
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    // 检查后端返回的业务状态码，而不是HTTP状态码
    if (res.code !== 200) {
      Message({
        message: res.message || '请求失败',
        type: 'error',
        duration: 5 * 1000
      })

      // 401: 未登录或token过期
      if (res.code === 401) {
        store.dispatch('logout').then(() => {
          // 重新登录
          location.reload()
        })
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    } else {
      return response.data
    }
  },
  error => {
    console.error('响应错误:', error)
    // 处理网络错误或服务器错误
    if (error.response) {
      // 服务器返回了错误状态码
      switch (error.response.status) {
        case 401:
          Message({
            message: '未授权，请重新登录',
            type: 'error',
            duration: 5 * 1000
          })
          // 清除token并跳转到登录页
          store.dispatch('logout').then(() => {
            location.reload()
          })
          break
        case 403:
          Message({
            message: '拒绝访问，请检查您的权限',
            type: 'error',
            duration: 5 * 1000
          })
          break
        case 404:
          Message({
            message: '请求的资源不存在',
            type: 'error',
            duration: 5 * 1000
          })
          break
        case 500:
          Message({
            message: '服务器内部错误',
            type: 'error',
            duration: 5 * 1000
          })
          break
        default:
          Message({
            message: error.response.data.message || '请求失败',
            type: 'error',
            duration: 5 * 1000
          })
      }
    } else {
      // 请求未发出或网络错误
      Message({
        message: '网络错误，请检查您的网络连接',
        type: 'error',
        duration: 5 * 1000
      })
    }
    return Promise.reject(error)
  }
)

// 封装API请求方法
export default {
  // 用户相关接口
  user: {
    // 登录
    login: (data) => {
      return service.post('/auth/login', data).catch(error => {
        console.error('Login API error:', error);
        throw new Error('请求失败');
      });
    },
    // 注册
    register(data) {
      return service.post('/auth/register', data)
    },
    // 获取用户信息
    getInfo() {
      console.log('获取用户信息...')
      // 检查token是否存在
      const token = localStorage.getItem('token')
      if (!token) {
        return Promise.reject(new Error('未登录，无法获取用户信息'))
      }
      
      return service.get('/user/info')
        .then(response => {
          console.log('获取用户信息成功:', response)
          return response
        })
        .catch(error => {
          console.error('获取用户信息失败:', error)
          // 记录更详细的错误信息
          if (error.response) {
            console.error('错误状态码:', error.response.status)
            console.error('错误数据:', error.response.data)
          } else if (error.request) {
            console.error('请求已发送但未收到响应:', error.request)
          } else {
            console.error('请求配置错误:', error.message)
          }
          // 减少日志输出，避免undefined错误
          if (error.config) {
            console.error('请求配置:', error.config)
          }
          // 返回一个更具体的错误信息
          return Promise.reject(new Error('获取用户信息失败: ' + (error.response?.data?.message || error.message || '请求失败')))
        })
    },
    // 更新用户信息
    updateInfo(data) {
      return service.put('/user/info', data)
    },
    // 修改密码
    changePassword(data) {
      return service.put('/user/password', data)
    },
    // 获取用户列表（管理员）
    getList(params) {
      return service.get('/user/list', { params });
    },
    // 更新用户状态（管理员）
    updateStatus(userId, status) {
      return service.put(`/user/status?id=${userId}&status=${status}`)
    },
    // 更新用户角色（管理员）
    updateRole(userId, role) {
      return service.put(`/user/role?id=${userId}&role=${role}`)
    }
  },

  // 文章相关接口
  article: {
    // 获取文章列表（所有已发布的文章）
    getList(params) {
      return service.get('/article/list', { params });
    },
    // 获取当前用户的文章列表
    getMyList(params) {
      return service.get('/article/my', { params });
    },
    // 获取待审核的文章列表（管理员）
    getPendingList(params) {
      return service.get('/article/pending', { params });
    },
    // 获取文章详情
    getDetail(id) {
      return service.get(`/article/${id}`)
    },
    // 添加文章（所有用户，普通用户需要审核）
    add(data) {
      return service.post('/article', data)
    },
    // 更新文章
    update(id, data) {
      return service.put(`/article/${id}`, data)
    },
    // 删除文章
    delete(id) {
      return service.delete(`/article/${id}`)
    },
    // 审核通过文章（管理员）
    approve(id) {
      return service.put(`/article/approve/${id}`)
    },
    // 审核拒绝文章（管理员）
    reject(id) {
      return service.put(`/article/reject/${id}`)
    },
    // 点赞或取消点赞文章
    like(id) {
      return service.post(`/article/${id}/like`)
    },
    // 检查是否已点赞文章
    checkLikeStatus(id) {
      return service.get(`/article/${id}/like/status`)
    }
  },

  // 分类相关接口
  category: {
    // 获取所有分类
    getAll() {
      return service.get('/category/list')
    },
    // 获取分类列表（管理员）
    getList(params) {
      return service.get('/admin/categories', { params })
    },
    // 添加分类（管理员）
    add(data) {
      return service.post('/admin/categories', data)
    },
    // 更新分类（管理员）
    update(id, data) {
      return service.put(`/admin/categories/${id}`, data)
    },
    // 删除分类（管理员）
    delete(id) {
      return service.delete(`/admin/categories/${id}`)
    }
  },

  // 标签相关接口
  tag: {
    // 获取所有标签
    getAll() {
      return service.get('/tags')
    },
    // 获取标签列表（管理员）
    getList(params) {
      return service.get('/admin/tags', { params })
    },
    // 添加标签（管理员）
    add(data) {
      return service.post('/admin/tags', data)
    },
    // 更新标签（管理员）
    update(id, data) {
      return service.put(`/admin/tags/${id}`, data)
    },
    // 删除标签（管理员）
    delete(id) {
      return service.delete(`/admin/tags/${id}`)
    }
  },

  // 评论相关接口
  comment: {
    // 获取文章评论
    getByArticle(articleId, params) {
      return service.get(`/comment/list?articleId=${articleId}`, { params })
    },
    // 添加评论（包括回复）
    add(data) {
      return service.post('/comment', data)
    },
    // 获取评论树
    getTree(articleId) {
      return service.get(`/comment/tree?articleId=${articleId}`)
    },
    // 获取评论列表（管理员）
    getList(params) {
      return service.get('/admin/comments', { params })
    },
    // 更新评论状态（管理员）
    updateStatus(id, status) {
      return service.put(`/admin/comments/${id}/status`, { status })
    },
    // 删除评论（管理员）
    delete(id) {
      return service.delete(`/admin/comments/${id}`)
    }
  },

  // 照片相关接口
  photo: {
    // 获取照片列表
    getList(params) {
      return service.get('/photo/list', { params })
    },
    // 获取照片分类列表
    getCategories() {
      return service.get('/photo/category/list')
    },
    // 获取照片列表（管理员）
    getAdminList(params) {
      return service.get('/admin/photos', { params })
    },
    // 添加照片（管理员）
    add(data) {
      return service.post('/admin/photos', data)
    },
    // 更新照片（管理员）
    update(id, data) {
      return service.put(`/admin/photos/${id}`, data)
    },
    // 删除照片（管理员）
    delete(id) {
      return service.delete(`/admin/photos/${id}`)
    },
    // 获取照片分类列表（管理员）
    getCategoryList(params) {
      return service.get('/admin/photo/categories', { params })
    },
    // 添加照片分类（管理员）
    addCategory(data) {
      return service.post('/admin/photo/categories', data)
    },
    // 更新照片分类（管理员）
    updateCategory(id, data) {
      return service.put(`/admin/photo/categories/${id}`, data)
    },
    // 删除照片分类（管理员）
    deleteCategory(id) {
      return service.delete(`/admin/photo/categories/${id}`)
    }
  },

  // 留言相关接口
  message: {
    // 获取留言列表
    getList(params) {
      return service.get('/message/list', { params })
    },
    // 获取留言树
    getTree() {
      return service.get('/message/tree')
    },
    // 添加留言
    add(data) {
      return service.post('/message', data)
    },
    // 回复留言
    reply(data) {
      return service.post('/message/reply', data)
    },
    // 点赞留言
    like(messageId) {
      return service.post(`/message/${messageId}/like`)
    },
    // 获取留言列表（管理员）
    getAdminList(params) {
      return service.get('/admin/messages', { params })
    },
    // 获取留言详情（管理员）
    getDetail(id) {
      return service.get(`/admin/messages/${id}`)
    },
    // 更新留言状态（管理员）
    updateStatus(id, status) {
      return service.put(`/admin/messages/${id}/status`, { status })
    },
    // 回复留言（管理员）
    adminReply(data) {
      return service.post('/admin/messages/reply', data)
    },
    // 删除留言（管理员）
    delete(id) {
      return service.delete(`/admin/messages/${id}`)
    },
    // 更新留言（管理员）
    update(id, data) {
      return service.put(`/admin/messages`, data)
    },
    // 批量删除留言（管理员）
    batchDelete(ids) {
      return service.delete('/admin/messages/batch', { data: ids })
    },
    // 获取最近留言
    getRecentMessages(params) {
      return service.get('/message/list', { params: { ...params, current: 1, size: 5 } })
    }
  },

  // 友链相关接口
  friendLink: {
    // 获取前台友链列表
    getList() {
      return service.get('/friend-links')
    },
    // 申请友链
    apply(data) {
      return service.post('/friend-links/apply', data)
    },
    // 获取友链列表（管理员）
    getAdminList(params) {
      return service.get('/admin/friend-links', { params })
    },
    // 获取友链详情（管理员）
    getDetail(id) {
      return service.get(`/admin/friend-links/${id}`)
    },
    // 添加友链（管理员）
    add(data) {
      return service.post('/admin/friend-links', data)
    },
    // 更新友链（管理员）
    update(data) {
      return service.put('/admin/friend-links', data)
    },
    // 删除友链（管理员）
    delete(id) {
      return service.delete(`/admin/friend-links/${id}`)
    },
    // 批量删除友链（管理员）
    batchDelete(ids) {
      return service.delete('/admin/friend-links/batch', { data: ids })
    },
    // 审核友链（管理员）
    audit(id, status) {
      return service.put(`/admin/friend-links/${id}/audit?status=${status}`)
    }
  },

  // 系统设置相关接口（管理员）
  system: {
    // 获取公共系统设置（无需认证）
    getPublicSettings() {
      return service.get('/settings/public')
    },
    // 获取系统设置
    getSettings() {
      return service.get('/admin/settings')
    },
    // 更新系统设置
    updateSettings(data) {
      // 确保数据类型正确
      const processedData = { ...data }
      
      // 特别处理布尔值或数字转字符串
      if (processedData.comment_audit !== undefined) {
        processedData.comment_audit = processedData.comment_audit === true || 
          processedData.comment_audit === 1 || 
          processedData.comment_audit === '1' ? '1' : '0'
      }
      
      if (processedData.user_register !== undefined) {
        processedData.user_register = processedData.user_register === true || 
          processedData.user_register === 1 || 
          processedData.user_register === '1' ? '1' : '0'
      }
      
      console.log('发送系统设置更新请求，处理后的数据：', processedData)
      return service.put('/admin/settings', processedData)
    },
    // 初始化系统设置
    initSettings() {
      return service.post('/admin/settings/init')
    },
    // 获取统计数据
    getStatistics() {
      return service.get('/admin/statistics')
    }
  },

  // 文件上传
  upload(file) {
    const formData = new FormData()
    formData.append('file', file)
    return service.post('/admin/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 管理员用户管理
  adminUser: {
    // 获取用户列表
    getList(params) {
      return service.get('/admin/users', { params })
    },
    
    // 获取用户详情
    getById(id) {
      return service.get(`/admin/users/${id}`)
    },
    
    // 更新用户信息
    update(id, data) {
      return service.put(`/admin/users/${id}`, data)
    },
    
    // 创建用户
    create(data) {
      return service.post('/admin/users', data)
    },
    
    // 更新用户状态
    updateStatus(id, status) {
      return service.put(`/admin/users/${id}/status?status=${status}`)
    },
    
    // 更新用户角色
    updateRole(id, role) {
      return service.put(`/admin/users/${id}/role?role=${role}`)
    },
    
    // 重置用户密码
    resetPassword(id) {
      return service.put(`/admin/users/${id}/reset-password`)
    },
    
    // 删除用户
    delete(id) {
      return service.delete(`/admin/users/${id}`)
    }
  },

  // 统计相关接口
  statistics: {
    // 获取网站统计信息
    getSiteStatistics() {
      return service.get('/statistics/site')
    },
    
    // 获取总访问量
    getTotalViews() {
      return service.get('/statistics/total-views')
    }
  }
}
