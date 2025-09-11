<template>
  <div class="about-container">
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
            <div v-for="skill in category.skills" :key="skill.id" class="skill-item">
              <div class="skill-header">
                <span class="skill-name">{{ skill.name }}</span>
                <span class="skill-proficiency">{{ skill.proficiency }}%</span>
              </div>
              <el-progress 
                :percentage="skill.proficiency" 
                :color="skill.color || category.color"
                :show-text="false"
                class="skill-progress"
              />
              <p class="skill-description">{{ skill.description }}</p>
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
        <div v-for="project in featuredProjects" :key="project.id" class="project-item">
          <div class="project-cover">
            <img :src="getProjectCoverUrl(project.coverImage)" :alt="project.title">
            <div class="project-overlay">
              <el-button-group>
                <el-button 
                  v-if="project.demoUrl" 
                  type="primary" 
                  size="mini"
                  @click="openUrl(project.demoUrl)"
                >
                  <i class="el-icon-view"></i> 预览
                </el-button>
                <el-button 
                  v-if="project.githubUrl" 
                  type="info" 
                  size="mini"
                  @click="openUrl(project.githubUrl)"
                >
                  <i class="el-icon-link"></i> 源码
                </el-button>
              </el-button-group>
            </div>
          </div>
          <div class="project-info">
            <h5 class="project-title">{{ project.title }}</h5>
            <p class="project-summary">{{ project.summary }}</p>
            <div class="project-tech">
              <el-tag
                v-for="tech in project.technologies.split(',')"
                :key="tech"
                size="mini"
                class="tech-tag"
              >
                {{ tech.trim() }}
              </el-tag>
            </div>
            <div class="project-stats">
              <span class="stat-item">
                <i class="el-icon-view"></i>
                {{ project.viewCount }}
              </span>
              <span class="stat-item">
                <i class="el-icon-star-off"></i>
                {{ project.likeCount }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

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
      loading: false
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
        const response = await axios.get('/api/about/info')
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
        this.$message.error('获取信息失败')
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
    
    getAvatarUrl(avatar) {
      if (!avatar) {
        return '/images/default-avatar.svg'
      }
      // 如果是完整的URL，直接返回
      if (avatar.startsWith('http://') || avatar.startsWith('https://')) {
        return avatar
      }
      // 如果是相对路径，添加后端服务器前缀
      if (avatar.startsWith('/uploads/')) {
        return `http://localhost:8081${avatar}`
      }
      // 其他情况，假设是文件名，添加完整路径
      return `http://localhost:8081/uploads/${avatar}`
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
        return `http://localhost:8081${coverImage}`
      }
      // 其他情况，假设是文件名，添加完整路径
      return `http://localhost:8081/uploads/${coverImage}`
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
    }
  }
}
</script>

<style scoped>
.about-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
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
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 30px;
  align-items: start;
  grid-auto-flow: dense;
  grid-auto-rows: minmax(50px, auto);
}

.skill-category {
  margin-bottom: 25px;
  break-inside: avoid;
  display: flex;
  flex-direction: column;
  background: #fafafa;
  border: 1px solid #EBEEF5;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s ease;
}

.skill-category:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.1);
}

/* 根据技能数量动态调整网格跨度 */
.skill-category[data-skill-count="1"] {
  grid-row: span 3;
}

.skill-category[data-skill-count="2"] {
  grid-row: span 4;
}

.skill-category[data-skill-count="3"] {
  grid-row: span 5;
}

.skill-category[data-skill-count="4"] {
  grid-row: span 6;
}

.skill-category[data-skill-count="5"] {
  grid-row: span 7;
}

.skill-category[data-skill-count="6"] {
  grid-row: span 8;
}

/* 对于技能数量更多的分类 */
.skill-category[data-skill-count="7"],
.skill-category[data-skill-count="8"] {
  grid-row: span 9;
}

/* 使用现代CSS Grid的masonry布局（如果支持） */
@supports (grid-template-rows: masonry) {
  .skills-container {
    grid-template-rows: masonry;
    grid-auto-rows: auto;
  }
  
  .skill-category {
    grid-row: auto;
  }
}

/* 为了更好的视觉效果，调整技能项目样式 */
.skills-grid {
  flex: 1;
}

.category-title {
  margin: 0 0 15px 0;
  font-size: 16px;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.skills-grid {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.skill-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  background: transparent;
}

.skill-item:last-child {
  border-bottom: none;
}

.skill-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.skill-name {
  font-weight: bold;
  color: #303133;
}

.skill-proficiency {
  color: #409EFF;
  font-weight: bold;
}

.skill-progress {
  margin-bottom: 10px;
}

.skill-description {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.timeline {
  padding: 20px 0;
}

.timeline-card {
  margin-top: 10px;
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
    gap: 12px;
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
}
</style>