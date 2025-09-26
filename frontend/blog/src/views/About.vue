<template>
  <div class="about-container page-container">
    <!-- 个人基本信息 -->
    <el-card class="basic-info-card" shadow="hover">
      <div class="basic-info">
        <div class="avatar-section">
          <el-avatar 
            :size="120" 
            :src="getAvatarUrl(basicInfo.avatar)"
            class="avatar"
          />
        </div>
        <div class="info-section">
          <h1 class="name">{{ basicInfo.name || '姓名' }}</h1>
          <h3 class="title">{{ basicInfo.title || '职位' }}</h3>
          <p class="introduction">{{ basicInfo.introduction || '个人简介' }}</p>
          <div class="contact-info">
            <div class="contact-item" v-if="basicInfo.location">
              <i class="el-icon-location"></i>
              <span>{{ basicInfo.location }}</span>
            </div>
            <div class="contact-item" v-if="basicInfo.email">
              <i class="el-icon-message"></i>
              <span>{{ basicInfo.email }}</span>
            </div>
            <div class="contact-item" v-if="basicInfo.github">
              <i class="el-icon-link"></i>
              <a :href="basicInfo.github" target="_blank">GitHub</a>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 技能展示 -->
    <el-card class="section-card" shadow="hover">
      <div slot="header" class="section-header">
        <i class="el-icon-cpu"></i>
        <span>技能与经验</span>
      </div>
      <div class="skills-container" ref="skillsContainer">
        <div v-for="category in skillCategories" :key="category.id" class="skill-category" :data-skill-count="category.skills.length">
          <h4 class="category-title">
            <i :class="category.icon" :style="{color: category.color}"></i>
            {{ category.name }}
          </h4>
          <div class="skills-grid">
            <div 
              v-for="skill in category.skills" 
              :key="skill.id" 
              class="skill-item"
              @mouseenter="showTooltip($event, skill.description)"
              @mouseleave="hideTooltip"
              @mousemove="updateTooltipPosition($event)"
            >
              <div class="skill-content">
                <div class="skill-circle-container">
                  <el-progress
                    type="circle"
                    :percentage="skill.proficiency"
                    :color="getSkillColor(skill.proficiency)"
                    :width="50"
                    :stroke-width="4"
                    :show-text="true"
                    class="skill-circle-progress"
                  />
                </div>
                <div class="skill-info">
                  <h5 class="skill-name">{{ skill.name }}</h5>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 工作经历 -->
    <el-card class="section-card" shadow="hover">
      <div slot="header" class="section-header">
        <i class="el-icon-suitcase"></i>
        <span>工作经历</span>
      </div>
      <div class="timeline-container">
        <el-timeline class="timeline">
          <el-timeline-item
            v-for="work in workExperiences"
            :key="work.id"
            :timestamp="formatWorkPeriod(work)"
            placement="top"
            type="primary"
            size="large"
          >
            <el-card class="timeline-card">
              <div class="work-header">
                <div class="company-info">
                  <h4 class="position">{{ work.position }}</h4>
                  <h5 class="company">{{ work.companyName }}</h5>
                  <p class="location" v-if="work.location">
                    <i class="el-icon-location"></i>
                    {{ work.location }}
                  </p>
                </div>
                <el-tag v-if="work.isCurrent" type="success">当前工作</el-tag>
              </div>
              <p class="job-description">{{ work.jobDescription }}</p>
              <div class="achievements" v-if="work.achievements">
                <h6>主要成就：</h6>
                <p>{{ work.achievements }}</p>
              </div>
              <div class="technologies" v-if="work.technologies">
                <h6>技术栈：</h6>
                <el-tag
                  v-for="tech in work.technologies.split(',')"
                  :key="tech"
                  size="mini"
                  class="tech-tag"
                >
                  {{ tech.trim() }}
                </el-tag>
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
        
        <!-- 右侧装饰区域 -->
        <div class="timeline-decoration">
          <div class="decoration-card stats-card">
            <div class="stats-header">
              <i class="el-icon-trophy"></i>
              <h4>职业统计</h4>
            </div>
            <div class="stats-content">
              <div class="stat-item">
                <span class="stat-number">{{ workExperiences.length }}</span>
                <span class="stat-label">工作经历</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ calculateTotalWorkYears() }}</span>
                <span class="stat-label">工作年限</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ getUniqueCompaniesCount() }}</span>
                <span class="stat-label">服务公司</span>
              </div>
            </div>
          </div>
          
          <div class="decoration-card career-timeline">
            <div class="timeline-header">
              <i class="el-icon-time"></i>
              <h4>职业时间线</h4>
            </div>
            <div class="career-timeline-wrapper">
              <div v-if="sortedWorkExperiences.length === 0" class="no-data">
                暂无工作经历数据
              </div>
              <div v-else class="circular-timeline">
                <div class="timeline-center">
                  <div class="center-info">
                    <div class="total-years">{{ calculateTotalWorkYears() }}</div>
                    <div class="center-label">工作经验</div>
                  </div>
                </div>
                
                <div 
                  v-for="(work, index) in sortedWorkExperiences" 
                  :key="index"
                  class="timeline-segment"
                  :class="{ 'current-segment': work.endTime === '至今' || work.endDate === null }"
                  :style="getSegmentStyle(index)"
                >
                  <div class="segment-dot"></div>
                  <div class="segment-info">
                    <div class="segment-time">{{ formatYearMonth(work.startTime || work.startDate) }}</div>
                    <div class="segment-company">{{ work.companyName || work.company || '未知公司' }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <div class="decoration-card quote-card">
            <div class="quote-content">
              <i class="el-icon-chat-dot-round quote-icon"></i>
              <p class="quote-text">"持续学习，不断进步，用技术创造价值"</p>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 教育背景 -->
    <el-card class="section-card" shadow="hover">
      <div slot="header" class="section-header">
        <i class="el-icon-school"></i>
        <span>教育背景</span>
      </div>
      <div class="education-list">
        <div v-for="edu in educations" :key="edu.id" class="education-item">
          <div class="education-header">
            <div class="school-info">
              <h4 class="school-name">{{ edu.schoolName }}</h4>
              <h5 class="degree-major">{{ edu.degree }} · {{ edu.major }}</h5>
              <p class="education-period">
                {{ formatDate(edu.startDate) }} - {{ edu.endDate ? formatDate(edu.endDate) : '至今' }}
              </p>
            </div>
            <div class="gpa" v-if="edu.gpa">
              <span class="gpa-label">GPA:</span>
              <span class="gpa-value">{{ edu.gpa }}</span>
            </div>
          </div>
          <p class="education-description" v-if="edu.description">{{ edu.description }}</p>
          <div class="achievements" v-if="edu.achievements">
            <h6>主要成就：</h6>
            <p>{{ edu.achievements }}</p>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 个人兴趣 -->
    <el-card class="section-card" shadow="hover">
      <div slot="header" class="section-header">
        <i class="el-icon-star-off"></i>
        <span>个人兴趣</span>
      </div>
      <div class="interests-grid" :class="getInterestsGridClass()">
        <div v-for="interest in interests" :key="interest.id" class="interest-item">
          <div class="interest-icon">
            <i :class="interest.icon || 'el-icon-star-off'" :style="{color: interest.color || '#909399'}"></i>
          </div>
          <h5 class="interest-name">{{ interest.name }}</h5>
          <p class="interest-description">{{ interest.description }}</p>
          <el-tag size="mini" :type="getLevelType(interest.level)">{{ interest.level }}</el-tag>
        </div>
      </div>
    </el-card>

    <!-- 精选项目 -->
    <el-card class="section-card" shadow="hover">
      <div slot="header" class="section-header">
        <i class="el-icon-folder-opened"></i>
        <span>精选项目</span>
        <el-button type="text" @click="$router.push('/projects')" class="more-btn">
          查看更多 <i class="el-icon-arrow-right"></i>
        </el-button>
      </div>
      <div class="projects-grid">
        <div v-for="project in featuredProjects" :key="project.id" class="project-item" @click="handleProjectCardClick(project)">
          <div class="project-info">
            <div class="project-header">
              <h5 class="project-title">{{ project.title }}</h5>
              <div class="project-status">
                <el-tag
                  :type="getStatusType(project.status)"
                  size="mini"
                >
                  {{ getStatusText(project.status) }}
                </el-tag>
              </div>
            </div>
            <p class="project-summary">{{ project.summary }}</p>
            <div class="project-tech">
              <el-tag
                v-for="tech in getTechnologies(project.technologies)"
                :key="tech"
                size="mini"
                class="tech-tag"
              >
                {{ tech }}
              </el-tag>
            </div>
            
            <!-- 添加项目时间线 -->
            <div class="project-timeline">
              <div class="timeline-item">
                <span class="timeline-label">开始时间:</span>
                <span class="timeline-value">{{ formatDate(project.startDate) || '未设置' }}</span>
              </div>
              <div class="timeline-item">
                <span class="timeline-label">结束时间:</span>
                <span class="timeline-value">{{ formatDate(project.endDate) || '进行中' }}</span>
              </div>
              <div class="timeline-item">
                <span class="timeline-label">项目周期:</span>
                <span class="timeline-value">{{ calculateProjectDuration(project.startDate, project.endDate) }}</span>
              </div>
            </div>
            
            <div class="project-footer">
              <div class="project-stats">
                <span class="stat-item">
                  <i class="el-icon-view"></i>
                  {{ project.viewCount || 0 }}
                </span>
                <span class="stat-item like-item" :class="{ 'liked': project.isLiked }" @click.stop="handleProjectLike(project)">
                  <i class="el-icon-star-off"></i>
                  {{ project.likeCount || 0 }}
                </span>
              </div>
              
              <div class="project-actions">
                <el-button-group>
                  <el-button 
                    v-if="project.demoUrl" 
                    type="primary" 
                    size="mini"
                    @click.stop="handleProjectPreview(project)"
                  >
                    <i class="el-icon-view"></i> 预览
                  </el-button>
                  <el-button 
                    v-if="project.githubUrl" 
                    type="info" 
                    size="mini"
                    @click.stop="handleProjectSource(project)"
                  >
                    <i class="el-icon-link"></i> 源码
                  </el-button>
                </el-button-group>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 动态提示框 -->
    <div 
      v-if="tooltip.visible" 
      class="skill-tooltip"
      :style="{ left: tooltip.x + 'px', top: tooltip.y + 'px' }"
    >
      {{ tooltip.content }}
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import api from '../api'

export default {
  name: 'About',
  data() {
    return {
      basicInfo: {},
      skillCategories: [],
      workExperiences: [],
      educations: [],
      interests: [],
      featuredProjects: [],
      loading: false,
      tooltip: {
        visible: false,
        content: '',
        x: 0,
        y: 0
      },

    }
  },
  computed: {
    sortedWorkExperiences() {
      if (!this.workExperiences || this.workExperiences.length === 0) return []
      return [...this.workExperiences].sort((a, b) => {
        const dateA = new Date(a.startTime || a.startDate)
        const dateB = new Date(b.startTime || b.startDate)
        return dateA - dateB
      })
    }
  },
  created() {
    this.fetchAboutInfo()
  },
  mounted() {
    this.$nextTick(() => {
      this.optimizeSkillsLayout()
    })
  },
  updated() {
    this.$nextTick(() => {
      this.optimizeSkillsLayout()
    })
  },
  methods: {
    async fetchAboutInfo() {
      this.loading = true
      try {
        const response = await axios.get('/about/info')
        if (response.data.code === 200) {
          const data = response.data.data
          this.basicInfo = data.basicInfo || {}
          this.workExperiences = data.workExperiences || []
          this.educations = data.educations || []
          this.interests = data.interests || []
          this.featuredProjects = data.featuredProjects || []
          
          // 处理技能数据，按分类分组
          this.processSkills(data.skills || [])
          

          

        }
      } catch (error) {
        console.error('获取关于我信息失败:', error)
        this.$message({
          message: '获取信息失败',
          type: 'error',
          duration: 3000
        })
      } finally {
        this.loading = false
      }
    },
    
    processSkills(skills) {
      // 按分类分组技能
      const categoryMap = {}
      skills.forEach(skill => {
        if (!categoryMap[skill.categoryId]) {
          categoryMap[skill.categoryId] = {
            id: skill.categoryId,
            name: this.getCategoryName(skill.categoryId),
            icon: this.getCategoryIcon(skill.categoryId),
            color: this.getCategoryColor(skill.categoryId),
            skills: []
          }
        }
        categoryMap[skill.categoryId].skills.push(skill)
      })
      
      // 对每个分类下的技能按掌握程度从高到低排序
      Object.values(categoryMap).forEach(category => {
        category.skills.sort((a, b) => (b.proficiency || 0) - (a.proficiency || 0))
      })
      
      this.skillCategories = Object.values(categoryMap)
    },
    
    getCategoryName(categoryId) {
      const names = {
        1: '编程语言',
        2: '前端技术', 
        3: '后端技术',
        4: '数据库',
        5: '开发工具',
        6: '其他技能'
      }
      return names[categoryId] || '其他'
    },
    
    getCategoryIcon(categoryId) {
      const icons = {
        1: 'el-icon-cpu',
        2: 'el-icon-monitor',
        3: 'el-icon-server', 
        4: 'el-icon-coin',
        5: 'el-icon-tools',
        6: 'el-icon-star-off'
      }
      return icons[categoryId] || 'el-icon-star-off'
    },
    
    getCategoryColor(categoryId) {
      const colors = {
        1: '#409EFF',
        2: '#67C23A',
        3: '#E6A23C',
        4: '#F56C6C', 
        5: '#909399',
        6: '#606266'
      }
      return colors[categoryId] || '#909399'
    },

    // 根据掌握程度获取颜色，实现颜色深浅的直观展示
    getSkillColor(proficiency) {
      const percentage = proficiency || 0
      
      if (percentage >= 90) {
        // 90-100%: 深红色 (专家级) - 最高等级使用最红的颜色
        return '#D32F2F'
      } else if (percentage >= 80) {
        // 80-89%: 红橙色 (高级)
        return '#FF5722'
      } else if (percentage >= 70) {
        // 70-79%: 橙色 (熟练)
        return '#FF9800'
      } else if (percentage >= 60) {
        // 60-69%: 金色 (中级)
        return '#FFC107'
      } else if (percentage >= 50) {
        // 50-59%: 绿色 (中等)
        return '#4CAF50'
      } else if (percentage >= 40) {
        // 40-49%: 青色 (初级)
        return '#00BCD4'
      } else if (percentage >= 30) {
        // 30-39%: 蓝色 (入门)
        return '#2196F3'
      } else {
        // 0-29%: 灰蓝色 (新手)
        return '#607D8B'
      }
    },
    
    formatWorkPeriod(work) {
      const start = this.formatDate(work.startDate)
      const end = work.endDate ? this.formatDate(work.endDate) : '至今'
      return `${start} - ${end}`
    },
    
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}`
    },
    
    getLevelType(level) {
      const types = {
        '初级': 'info',
        '中级': 'warning', 
        '高级': 'success',
        '专业': 'danger'
      }
      return types[level] || 'info'
    },
    
    openUrl(url) {
      window.open(url, '_blank')
    },

    // 项目相关方法 - 与首页保持一致
    getTechnologies(techString) {
      if (!techString) return []
      return techString.split(',').map(tech => tech.trim()).filter(tech => tech)
    },

    // 获取项目状态类型
    getStatusType(status) {
      const statusMap = {
        'planning': 'warning',
        'developing': 'primary',
        'completed': 'success',
        'maintenance': 'info'
      }
      return statusMap[status] || 'info'
    },

    // 获取项目状态文本
    getStatusText(status) {
      const statusMap = {
        'planning': '规划中',
        'developing': '开发中',
        'completed': '已完成',
        'maintenance': '维护中'
      }
      return statusMap[status] || '未知'
    },

    // 计算项目周期
    calculateProjectDuration(startDate, endDate) {
      if (!startDate) return '未知';
      
      const start = new Date(startDate);
      const end = endDate ? new Date(endDate) : new Date();
      
      const diffTime = Math.abs(end - start);
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      
      if (diffDays < 30) {
        return `${diffDays}天`;
      } else if (diffDays < 365) {
        const months = Math.floor(diffDays / 30);
        const remainingDays = diffDays % 30;
        return remainingDays > 0 ? `${months}个月${remainingDays}天` : `${months}个月`;
      } else {
        const years = Math.floor(diffDays / 365);
        const remainingDays = diffDays % 365;
        const months = Math.floor(remainingDays / 30);
        return months > 0 ? `${years}年${months}个月` : `${years}年`;
      }
    },

    // 增加项目浏览量（静默增加，不显示消息）
    async incrementProjectView(project) {
      try {
        await this.$api.project.incrementView(project.id);
        project.viewCount = (project.viewCount || 0) + 1;
      } catch (error) {
        console.error('增加浏览量失败:', error);
      }
    },

    // 处理项目点赞切换
    async handleProjectLike(project) {
      try {
        const response = await this.$api.project.toggleLike(project.id);
        if (response.data && response.data.success) {
          project.likeCount = response.data.likeCount;
          project.isLiked = response.data.liked;
          
          if (response.data.liked) {
            this.$message({
              message: '点赞成功',
              type: 'success',
              duration: 3000
            });
          } else {
            this.$message({
              message: '取消点赞',
              type: 'success',
              duration: 3000
            });
          }
        }
      } catch (error) {
        console.error('点赞操作失败:', error);
        this.$message({
          message: '点赞操作失败',
          type: 'error',
          duration: 3000
        });
      }
    },

    // 处理项目卡片点击
    async handleProjectCardClick(project) {
      await this.incrementProjectView(project);
      // 如果有预览链接，优先打开预览，否则打开源码
      if (project.demoUrl) {
        this.openUrl(project.demoUrl);
      } else if (project.githubUrl) {
        this.openUrl(project.githubUrl);
      }
    },

    // 处理项目预览按钮点击
    async handleProjectPreview(project) {
      await this.incrementProjectView(project);
      this.openUrl(project.demoUrl);
    },

    // 处理项目源码按钮点击
    async handleProjectSource(project) {
      await this.incrementProjectView(project);
      this.openUrl(project.githubUrl);
    },
    
    getAvatarUrl(avatar) {
      if (!avatar) {
        return '/images/default-avatar.svg'
      }
      // 如果是完整的URL，直接返回
      if (avatar.startsWith('http://') || avatar.startsWith('https://')) {
        return avatar
      }
      // 如果是相对路径，添加后端服务器前缀
      if (avatar.startsWith('/uploads/') || avatar.startsWith('/images/')) {
        return `http://localhost:8080${avatar}`
      }
      // 其他情况，假设是文件名，添加完整路径
      return `http://localhost:8080/images/${avatar}`
    },
    
    getProjectCoverUrl(coverImage) {
      if (!coverImage) {
        return '/images/default-project.svg'
      }
      // 如果是完整的URL，直接返回
      if (coverImage.startsWith('http://') || coverImage.startsWith('https://')) {
        return coverImage
      }
      // 如果是相对路径，添加后端服务器前缀
      if (coverImage.startsWith('/uploads/')) {
        return `http://localhost:8080${coverImage}`
      }
      // 其他情况，假设是文件名，添加完整路径
      return `http://localhost:8080/images/${coverImage}`
    },
    
    optimizeSkillsLayout() {
      if (!this.$refs.skillsContainer) return
      
      const container = this.$refs.skillsContainer
      const categories = container.querySelectorAll('.skill-category')
      
      // 计算每个分类的高度和技能数量
      const categoryData = Array.from(categories).map(category => {
        const skillCount = parseInt(category.dataset.skillCount) || 0
        const height = category.offsetHeight
        return {
          element: category,
          skillCount,
          height,
          density: skillCount > 0 ? height / skillCount : height
        }
      })
      
      // 根据技能数量和高度动态调整布局
      categoryData.forEach(({ element, skillCount, height }) => {
        // 移除之前的动态样式
        element.style.gridRowEnd = ''
        
        // 根据内容高度动态设置grid-row-end
        const baseRowHeight = 100 // 基础行高
        const rowSpan = Math.max(1, Math.ceil(height / baseRowHeight))
        
        // 为技能较少的分类设置较小的行跨度
        if (skillCount <= 2) {
          element.style.gridRowEnd = `span ${Math.min(rowSpan, 2)}`
        } else if (skillCount <= 4) {
          element.style.gridRowEnd = `span ${Math.min(rowSpan, 3)}`
        } else {
          element.style.gridRowEnd = `span ${rowSpan}`
        }
      })
      
      // 使用CSS Grid的dense算法自动填充空隙
      container.style.gridAutoFlow = 'dense'
    },
    
    getInterestsGridClass() {
      const count = this.interests.length
      if (count === 0) return 'interests-empty'
      
      // 根据兴趣数量计算最优布局
      if (count <= 3) {
        return `interests-cols-${count}` // 1行，1-3列
      } else if (count === 4) {
        return 'interests-cols-2-rows-2' // 2行2列
      } else if (count === 5) {
        return 'interests-cols-3-rows-2' // 2行，第一行3个，第二行2个
      } else if (count === 6) {
        return 'interests-cols-3-rows-2' // 2行3列
      } else if (count <= 9) {
        return 'interests-cols-3-rows-3' // 3行3列
      } else if (count <= 12) {
        return 'interests-cols-4-rows-3' // 3行4列
      } else {
        return 'interests-cols-4' // 默认4列自动换行
      }
    },

    // 显示技能描述提示框
    showTooltip(event, description) {
      if (!description) return
      
      this.tooltip.content = description
      this.tooltip.visible = true
      this.updateTooltipPosition(event)
    },

    // 隐藏提示框
    hideTooltip() {
      this.tooltip.visible = false
      this.tooltip.content = ''
    },

    // 更新提示框位置
    updateTooltipPosition(event) {
      if (!this.tooltip.visible) return
      
      const offset = 15 // 提示框与鼠标的偏移距离
      this.tooltip.x = event.clientX + offset
      this.tooltip.y = event.clientY + offset
      
      // 防止提示框超出视窗边界
      this.$nextTick(() => {
        const tooltipEl = document.querySelector('.skill-tooltip')
        if (tooltipEl) {
          const rect = tooltipEl.getBoundingClientRect()
          const viewportWidth = window.innerWidth
          const viewportHeight = window.innerHeight
          
          // 如果提示框超出右边界，调整到鼠标左侧
          if (rect.right > viewportWidth) {
            this.tooltip.x = event.clientX - rect.width - offset
          }
          
          // 如果提示框超出下边界，调整到鼠标上方
          if (rect.bottom > viewportHeight) {
            this.tooltip.y = event.clientY - rect.height - offset
          }
        }
      })
    },

    // 计算总工作年限
    calculateTotalWorkYears() {
      if (!this.workExperiences || this.workExperiences.length === 0) return '0年'
      
      let totalMonths = 0
      this.workExperiences.forEach(work => {
        if (work.startDate) {
          const start = new Date(work.startDate)
          const end = work.endDate ? new Date(work.endDate) : new Date()
          const months = (end.getFullYear() - start.getFullYear()) * 12 + (end.getMonth() - start.getMonth())
          totalMonths += months
        }
      })
      
      const years = Math.floor(totalMonths / 12)
      const remainingMonths = totalMonths % 12
      
      if (years === 0) {
        return `${remainingMonths}个月`
      } else if (remainingMonths === 0) {
        return `${years}年`
      } else {
        return `${years}年${remainingMonths}个月`
      }
    },

    // 获取不重复的公司数量
    getUniqueCompaniesCount() {
      if (!this.workExperiences || this.workExperiences.length === 0) return 0
      const companies = new Set(this.workExperiences.map(work => work.companyName))
      return companies.size
    },

    // 格式化年份显示
    formatYear(dateString) {
      if (!dateString) return ''
      // 如果是"至今"，返回当前年份
      if (dateString === '至今') {
        return new Date().getFullYear().toString()
      }
      // 尝试解析日期
      try {
        const date = new Date(dateString)
        if (!isNaN(date.getTime())) {
          return date.getFullYear().toString()
        }
      } catch (e) {
        // 如果解析失败，尝试提取年份
      }
      // 提取年份
      const year = dateString.match(/(\d{4})/)?.[1]
      return year || dateString.substring(0, 4) || dateString
    },

    // 格式化时间显示（年月）
    formatYearMonth(dateString) {
      if (!dateString) return ''
      if (dateString === '至今') {
        const now = new Date()
        return `${now.getFullYear()}.${String(now.getMonth() + 1).padStart(2, '0')}`
      }
      
      try {
        const date = new Date(dateString)
        if (!isNaN(date.getTime())) {
          return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}`
        }
      } catch (e) {
        // 解析失败时的处理
      }
      
      // 尝试从字符串中提取年月
      const yearMatch = dateString.match(/(\d{4})/)?.[1]
      const monthMatch = dateString.match(/(\d{1,2})月/)?.[1] || dateString.match(/-(\d{2})-/)?.[1]
      
      if (yearMatch && monthMatch) {
        return `${yearMatch}.${String(monthMatch).padStart(2, '0')}`
      } else if (yearMatch) {
        return `${yearMatch}.01`
      }
      
      return dateString
    },

    // 获取环形时间线段样式
    getSegmentStyle(index) {
      const totalSegments = this.sortedWorkExperiences.length
      const anglePerSegment = 360 / totalSegments
      const angle = index * anglePerSegment
      const radius = 100 // 圆形轨道半径
      
      // 计算位置
      const radian = (angle - 90) * (Math.PI / 180) // -90度让第一个在顶部
      const x = Math.cos(radian) * radius
      const y = Math.sin(radian) * radius
      
      return {
        transform: `translate(calc(${x}px - 50%), calc(${y}px - 50%))`,
        '--segment-angle': `${angle}deg`
      }
    }
  }
}
</script>

<style scoped>
.about-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: calc(100vh - 70px);
}

.basic-info-card {
  margin-bottom: 30px;
}

.basic-info {
  display: flex;
  align-items: center;
  gap: 30px;
}

.avatar-section {
  flex-shrink: 0;
}

.avatar {
  border: 4px solid #f0f0f0;
}

.info-section {
  flex: 1;
}

.name {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 28px;
  font-weight: bold;
}

.title {
  margin: 0 0 15px 0;
  color: #409EFF;
  font-size: 18px;
  font-weight: normal;
}

.introduction {
  margin: 0 0 20px 0;
  color: #606266;
  line-height: 1.6;
  font-size: 16px;
}

.contact-info {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #909399;
}

.contact-item a {
  color: #409EFF;
  text-decoration: none;
}

.section-card {
  margin-bottom: 30px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.section-header i {
  margin-right: 8px;
  color: #409EFF;
}

.more-btn {
  padding: 0;
  font-size: 14px;
}

.skills-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
  align-items: start;
}

.skill-category {
  margin-bottom: 8px;
  break-inside: avoid;
  display: flex;
  flex-direction: column;
  background: #fafafa;
  border: 1px solid #EBEEF5;
  border-radius: 6px;
  padding: 12px;
  transition: all 0.3s ease;
}

.skill-category:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.1);
}



.category-title {
  margin: 0 0 6px 0;
  font-size: 14px;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
}

.skills-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
  padding: 4px 0;
}

.skill-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 8px 4px;
  border-radius: 4px;
  background: white;
  transition: all 0.3s ease;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
  min-height: 80px;
}

.skill-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.skill-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  width: 100%;
}

.skill-circle-container {
  flex-shrink: 0;
}

.skill-circle-progress {
  font-size: 10px;
}

/* 增大环形进度条中间百分比数字的字号 */
.skill-circle-progress >>> .el-progress__text {
  font-size: 12px !important;
  font-weight: 600 !important;
  color: #303133 !important;
}

.skill-info {
  width: 100%;
}

.skill-name {
  font-weight: 600;
  color: #303133;
  margin: 0;
  font-size: 13px;
  line-height: 1.3;
  word-break: break-word;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.skill-circle-container {
  cursor: pointer;
  transition: transform 0.2s ease;
}

.skill-circle-container:hover {
  transform: scale(1.05);
}

/* 动态提示框样式 */
.skill-tooltip {
  position: fixed;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 14px;
  line-height: 1.4;
  max-width: 250px;
  word-wrap: break-word;
  z-index: 9999;
  pointer-events: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  animation: tooltipFadeIn 0.2s ease-out;
}

@keyframes tooltipFadeIn {
  from {
    opacity: 0;
    transform: translateY(-5px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 时间线容器布局 */
.timeline-container {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 30px;
  align-items: start;
}

.timeline {
  padding: 20px 0;
}

.timeline-card {
  margin-top: 10px;
  max-width: 600px;
}

.work-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.position {
  margin: 0 0 5px 0;
  color: #303133;
  font-size: 18px;
}

.company {
  margin: 0 0 5px 0;
  color: #409EFF;
  font-size: 16px;
}

.location {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.job-description {
  margin: 0 0 15px 0;
  color: #606266;
  line-height: 1.6;
}

.achievements {
  margin-bottom: 15px;
}

.achievements h6,
.technologies h6 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 14px;
}

.achievements p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.tech-tag {
  margin: 2px 4px 2px 0;
}

/* 右侧装饰区域 */
.timeline-decoration {
  display: flex;
  flex-direction: column;
  gap: 20px;
  position: sticky;
  top: 20px;
}

.decoration-card {
  background: white;
  border: 1px solid #EBEEF5;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  transition: all 0.3s ease;
}

.decoration-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.1);
}

/* 统计卡片 */
.stats-header,
.timeline-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 15px;
  color: #303133;
}

.stats-header i,
.timeline-header i {
  font-size: 18px;
  color: #409EFF;
}

.stats-header h4,
.timeline-header h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.stats-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: #f8f9fa;
  border-radius: 6px;
}

.stat-number {
  font-size: 18px;
  font-weight: bold;
  color: #409EFF;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

/* 职业时间线 */
.career-timeline-content {
  padding: 10px;
  overflow: hidden;
  border-radius: 8px;
}

.timeline-svg {
  display: block;
  margin: 0 auto;
  width: 100%;
  height: auto;
  max-width: 280px;
}

/* 时间线文本样式 */
.timeline-year,
.timeline-company {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  user-select: none;
  pointer-events: none;
}

/* 时间线节点组 */
.timeline-node {
  cursor: pointer;
  transition: all 0.3s ease;
}

.timeline-node:hover .node-glow {
  r: 15;
  opacity: 0.3;
}

.timeline-node:hover .main-node {
  r: 9;
  stroke-width: 4;
}

.timeline-node:hover .timeline-year {
  font-size: 12px;
  font-weight: 700;
}

.timeline-node:hover .timeline-company {
  font-size: 10px;
  fill: #606266;
}

/* 节点光晕效果 */
.node-glow {
  transition: all 0.3s ease;
  opacity: 0.15;
}

/* 主节点样式 */
.main-node {
  transition: all 0.3s ease;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.15));
}

/* 时间线路径样式 */
.timeline-path {
  filter: drop-shadow(0 1px 3px rgba(64, 158, 255, 0.2));
  stroke-dasharray: 1000;
  stroke-dashoffset: 1000;
  animation: drawPath 3s ease-in-out forwards;
}

@keyframes drawPath {
  to {
    stroke-dashoffset: 0;
  }
}

/* 时间线背景装饰 */
.career-timeline {
  background: linear-gradient(135deg, #f8f9ff 0%, #ffffff 100%);
  border: 1px solid #e1e6ff;
  position: relative;
  overflow: hidden;
}

.career-timeline::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(64, 158, 255, 0.03) 0%, transparent 70%);
  animation: rotate 20s linear infinite;
  pointer-events: none;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 引言卡片 */
.quote-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
}

.quote-content {
  text-align: center;
  position: relative;
}

.quote-icon {
  font-size: 24px;
  opacity: 0.7;
  margin-bottom: 10px;
}

.quote-text {
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
  font-style: italic;
}

.education-list {
  display: grid;
  gap: 25px;
}

.education-item {
  padding: 20px;
  border: 1px solid #EBEEF5;
  border-radius: 8px;
  background: #fafafa;
}

.education-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.school-name {
  margin: 0 0 5px 0;
  color: #303133;
  font-size: 18px;
}

.degree-major {
  margin: 0 0 5px 0;
  color: #409EFF;
  font-size: 16px;
}

.education-period {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.gpa {
  text-align: right;
}

.gpa-label {
  color: #909399;
  font-size: 14px;
}

.gpa-value {
  color: #409EFF;
  font-size: 18px;
  font-weight: bold;
  margin-left: 5px;
}

.education-description {
  margin: 0 0 15px 0;
  color: #606266;
  line-height: 1.6;
}

.interests-grid {
  display: grid;
  gap: 10px;
  justify-items: center;
  align-items: start;
  padding: 0;
}

/* 动态布局样式 */
.interests-empty {
  display: none;
}

/* 1-3个兴趣：单行布局 */
.interests-cols-1 {
  grid-template-columns: 1fr;
  gap: 10px;
}

.interests-cols-2 {
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.interests-cols-3 {
  grid-template-columns: 1fr 1fr 1fr;
  gap: 10px;
}

/* 4个兴趣：2行2列 */
.interests-cols-2-rows-2 {
  grid-template-columns: 1fr 1fr;
  grid-template-rows: repeat(2, 1fr);
  gap: 10px;
}

/* 5-6个兴趣：2行3列 */
.interests-cols-3-rows-2 {
  grid-template-columns: 1fr 1fr 1fr;
  grid-template-rows: repeat(2, 1fr);
  gap: 10px;
}

/* 7-9个兴趣：3行3列 */
.interests-cols-3-rows-3 {
  grid-template-columns: 1fr 1fr 1fr;
  grid-template-rows: repeat(3, 1fr);
  gap: 10px;
}

/* 10-12个兴趣：3行4列 */
.interests-cols-4-rows-3 {
  grid-template-columns: 1fr 1fr 1fr 1fr;
  grid-template-rows: repeat(3, 1fr);
  gap: 10px;
}

/* 默认4列自动换行 */
.interests-cols-4 {
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}

.interest-item {
  text-align: center;
  padding: 20px;
  border: 1px solid #EBEEF5;
  border-radius: 12px;
  background: white;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  width: calc(100% - 20px);
  min-height: 160px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  margin: 10px;
}

.interest-item:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 32px rgba(64, 158, 255, 0.15);
  border-color: #409EFF;
}

.interest-icon {
  font-size: 36px;
  margin-bottom: 12px;
  flex-shrink: 0;
}

.interest-name {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
  flex-shrink: 0;
}

.interest-description {
  margin: 0 0 15px 0;
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.interest-item .el-tag {
  flex-shrink: 0;
  margin-top: auto;
}

.projects-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 25px;
}

.project-item {
  border: 1px solid #EBEEF5;
  border-radius: 8px;
  overflow: hidden;
  background: white;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.project-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.1);
}

.project-cover {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.project-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.project-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.project-item:hover .project-overlay {
  opacity: 1;
}

.project-info {
  padding: 20px;
}

.project-title {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 18px;
}

.project-summary {
  margin: 0 0 15px 0;
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
}

.project-tech {
  margin-bottom: 15px;
}

.project-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.project-status {
  display: flex;
  gap: 8px;
}

.project-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}

.project-actions {
  display: flex;
  gap: 8px;
}

.project-timeline {
  margin-bottom: 15px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 3px solid #409EFF;
}

.timeline-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
  font-size: 12px;
}

.timeline-item:last-child {
  margin-bottom: 0;
}

.timeline-label {
  color: #6c757d;
  font-weight: 500;
}

.timeline-value {
  color: #303133;
  font-weight: 600;
}

.project-stats {
  display: flex;
  gap: 15px;
  color: #909399;
  font-size: 14px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 4px 6px;
  border-radius: 4px;
}

.stat-item:hover {
  background: #f0f0f0;
  color: #495057;
}

.like-item {
  color: #dc3545;
}

.like-item.liked {
  color: #e74c3c;
  background: rgba(231, 76, 60, 0.1);
}

.like-item.liked i {
  color: #e74c3c;
}

@media (max-width: 768px) {
  .basic-info {
    flex-direction: column;
    text-align: center;
  }
  
  .contact-info {
    justify-content: center;
  }
  
  .skills-container {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .skills-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }
  
  .skill-item {
    padding: 8px 4px;
    min-height: 80px;
  }
  
  .skill-name {
    font-size: 11px;
  }
  
  .skill-content {
    gap: 6px;
  }
  
  .work-header {
    flex-direction: column;
    gap: 10px;
  }
  
  .education-header {
    flex-direction: column;
    gap: 10px;
  }
  
  .interests-grid,
  .interests-cols-1,
  .interests-cols-2,
  .interests-cols-3,
  .interests-cols-2-rows-2,
  .interests-cols-3-rows-2,
  .interests-cols-3-rows-3,
  .interests-cols-4-rows-3,
  .interests-cols-4 {
    grid-template-columns: 1fr !important;
    grid-template-rows: auto !important;
    gap: 5px !important;
  }
  
  .interest-item {
    min-height: 140px;
    padding: 15px;
    margin: 8px !important;
    width: calc(100% - 16px) !important;
  }
  
  .projects-grid {
    grid-template-columns: 1fr;
  }
  
  .timeline-container {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .timeline-decoration {
    position: static;
    order: -1;
  }
  
  .decoration-card {
    padding: 15px;
  }
  
  .stats-content {
    flex-direction: row;
    flex-wrap: wrap;
  }
  
  .stat-item {
    flex: 1;
    min-width: 80px;
    flex-direction: column;
    text-align: center;
    gap: 4px;
  }
}

/* 职业时间线 */
.career-timeline-wrapper {
  padding: 20px;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
}

.no-data {
  text-align: center;
  color: #909399;
  font-size: 14px;
  padding: 20px;
}

/* 环形时间线 */
.circular-timeline {
  position: relative;
  width: 240px;
  height: 240px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.circular-timeline::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 200px;
  height: 200px;
  border: 2px solid #e0e6ed;
  border-radius: 50%;
  transform: translate(-50%, -50%);
  z-index: 1;
}

.timeline-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 10;
  background: white;
  border-radius: 50%;
  width: 80px;
  height: 80px;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: 3px solid #409EFF;
}

.center-info {
  text-align: center;
}

.total-years {
  font-size: 16px;
  font-weight: 700;
  color: #409EFF;
  line-height: 1;
}

.center-label {
  font-size: 10px;
  color: #909399;
  margin-top: 2px;
}

.timeline-segment {
  position: absolute;
  top: 50%;
  left: 50%;
  transform-origin: center center;
  transition: all 0.3s ease;
  z-index: 2;
}

.segment-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #409EFF;
  border: 3px solid white;
  box-shadow: 0 2px 6px rgba(64, 158, 255, 0.3);
  cursor: pointer;
  transition: all 0.3s ease;
}

.timeline-segment.current-segment .segment-dot {
  background: #303133;
  box-shadow: 0 2px 6px rgba(48, 49, 51, 0.3);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    box-shadow: 0 2px 6px rgba(48, 49, 51, 0.3), 0 0 0 0 rgba(48, 49, 51, 0.7);
  }
  70% {
    box-shadow: 0 2px 6px rgba(48, 49, 51, 0.3), 0 0 0 8px rgba(48, 49, 51, 0);
  }
  100% {
    box-shadow: 0 2px 6px rgba(48, 49, 51, 0.3), 0 0 0 0 rgba(48, 49, 51, 0);
  }
}

.segment-dot:hover {
  transform: scale(1.2);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.5);
}

.segment-info {
  position: absolute;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  text-align: center;
  white-space: nowrap;
  background: rgba(255, 255, 255, 0.9);
  padding: 4px 8px;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  opacity: 0;
  transition: all 0.3s ease;
  pointer-events: none;
}

.timeline-segment:hover .segment-info {
  opacity: 1;
  transform: translateX(-50%) translateY(-5px);
}

.segment-time {
  font-size: 12px;
  font-weight: 600;
  color: #303133;
  line-height: 1;
}

.segment-company {
  font-size: 10px;
  color: #909399;
  margin-top: 2px;
  line-height: 1;
}

.timeline-segment.current-segment .segment-time {
  color: #303133;
}

.timeline-segment.current-segment .segment-company {
  color: #606266;
}

.timeline-point.current .point-dot {
  background: #303133;
  box-shadow: 0 2px 6px rgba(48, 49, 51, 0.3);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    box-shadow: 0 2px 6px rgba(48, 49, 51, 0.3), 0 0 0 0 rgba(48, 49, 51, 0.7);
  }
  70% {
    box-shadow: 0 2px 6px rgba(48, 49, 51, 0.3), 0 0 0 10px rgba(48, 49, 51, 0);
  }
  100% {
    box-shadow: 0 2px 6px rgba(48, 49, 51, 0.3), 0 0 0 0 rgba(48, 49, 51, 0);
  }
}

.point-dot:hover {
  transform: scale(1.2);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.5);
}

.point-info {
  text-align: center;
  position: absolute;
  width: 100%;
}

.point-year {
  font-size: 12px;
  font-weight: 600;
  color: #303133;
  position: absolute;
  top: -30px;
  left: 50%;
  transform: translateX(-50%);
  white-space: nowrap;
}

.point-company {
  font-size: 10px;
  color: #909399;
  line-height: 1.2;
  position: absolute;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  white-space: nowrap;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.timeline-point.current .point-year {
  color: #303133;
  font-weight: 700;
}

.timeline-point.current .point-company {
  color: #606266;
  font-weight: 500;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .timeline-axis {
    flex-direction: column;
    gap: 20px;
    padding: 20px 10px;
  }
  
  .axis-line {
    top: 0;
    bottom: 0;
    left: 50%;
    right: auto;
    width: 2px;
    height: auto;
    background: linear-gradient(180deg, #409EFF 0%, #67C23A 100%);
    transform: translateX(-50%);
  }
  
  .timeline-point {
    flex-direction: row;
    max-width: none;
    width: 100%;
    justify-content: flex-start;
    gap: 15px;
  }
  
  .point-info {
    margin-top: 0;
    text-align: left;
    min-height: auto;
  }
}
</style>