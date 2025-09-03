<template>
  <div class="about">
    <div class="container">
      <div class="about-header">
        <h1>关于我</h1>
        <div class="divider">
          <span></span>
        </div>
      </div>
      
      <div class="about-content" v-loading="loading">
        <div class="about-card">
          <div class="card-icon">
            <i class="el-icon-info"></i>
          </div>
          <div class="card-content">
            <h3>博客介绍</h3>
            <p>{{ aboutData.blogIntro }}</p>
          </div>
        </div>
        
        <div class="about-card">
          <div class="card-icon">
            <i class="el-icon-cpu"></i>
          </div>
          <div class="card-content">
            <h3>技术栈</h3>
            <p>{{ aboutData.techStack }}</p>
          </div>
        </div>
        
        <div class="about-card">
          <div class="card-icon">
            <i class="el-icon-user"></i>
          </div>
          <div class="card-content">
            <h3>关于作者</h3>
            <p>{{ aboutData.author }}</p>
          </div>
        </div>
        
        <div class="about-card">
          <div class="card-icon">
            <i class="el-icon-connection"></i>
          </div>
          <div class="card-content">
            <h3>联系方式</h3>
            <p>{{ aboutData.contact }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from '@/api'

export default {
  name: 'About',
  data() {
    return {
      loading: true,
      aboutData: {
        blogIntro: '这是一个基于Vue2和SpringBoot的个人博客系统，具有文章，表白墙，图片墙，收藏夹，音乐播放，视频播放，留言，友链，后台管理等功能。',
        techStack: '本网站采用前后端分离进行实现，前端使用Vue2和Element UI，后端使用Java SpringBoot和MySQL。',
        author: '热爱编程，喜欢分享技术知识和生活感悟。希望通过这个博客平台与大家交流学习，共同进步。',
        contact: '如果您有任何问题或建议，欢迎通过留言板与我联系，我会尽快回复。'
      }
    }
  },
  created() {
    this.fetchAboutData()
  },
  methods: {
    fetchAboutData() {
      this.loading = true
      api.system.getPublicSettings()
        .then(response => {
          console.log('API响应:', response)
          if (response && response.code === 200) {
            const data = response.data || {}
            console.log('API数据:', data)
            
            // 检查API返回的数据中是否包含关于页面的字段
            if (data.about_blog_intro || data.about_tech_stack || data.about_author || data.about_contact) {
              console.log('从API获取到关于页面数据')
              this.aboutData = {
                blogIntro: data.about_blog_intro || this.aboutData.blogIntro,
                techStack: data.about_tech_stack || this.aboutData.techStack,
                author: data.about_author || this.aboutData.author,
                contact: data.about_contact || this.aboutData.contact
              }
              console.log('更新后的关于页面数据:', this.aboutData)
            } else {
              console.error('API返回的数据中没有关于页面的字段')
            }
          }
        })
        .catch(error => {
          console.error('获取关于页面数据失败', error)
        })
        .finally(() => {
          this.loading = false
        })
        .catch(error => {
          console.error('获取关于页面数据失败', error)
        })
        .finally(() => {
          this.loading = false
        })
    }
  }
}
</script>

<style scoped>
.about {
  padding: 80px 0;
  background-color: #f9f9f9;
  min-height: calc(100vh - 60px);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.about-header {
  text-align: center;
  margin-bottom: 50px;
}

.about-header h1 {
  color: #333;
  font-size: 36px;
  font-weight: 600;
  margin-bottom: 15px;
}

.divider {
  display: flex;
  align-items: center;
  justify-content: center;
}

.divider span {
  display: block;
  width: 80px;
  height: 4px;
  background: linear-gradient(90deg, #409EFF, #67C23A);
  border-radius: 2px;
}

.about-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: 30px;
}

.about-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
  padding: 30px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  display: flex;
  align-items: flex-start;
}

.about-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 25px rgba(0, 0, 0, 0.12);
}

.card-icon {
  margin-right: 20px;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409EFF, #67C23A);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.card-icon i {
  color: white;
  font-size: 24px;
}

.card-content {
  flex: 1;
}

.card-content h3 {
  color: #333;
  font-size: 20px;
  margin-bottom: 15px;
  font-weight: 500;
}

.card-content p {
  line-height: 1.8;
  color: #666;
  margin: 0;
  font-size: 16px;
}

@media (max-width: 1100px) {
  .about-content {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 600px) {
  .about-header h1 {
    font-size: 28px;
  }
  
  .about-card {
    padding: 20px;
  }
  
  .card-icon {
    width: 40px;
    height: 40px;
  }
  
  .card-icon i {
    font-size: 20px;
  }
  
  .card-content h3 {
    font-size: 18px;
  }
  
  .card-content p {
    font-size: 14px;
  }
}
</style>