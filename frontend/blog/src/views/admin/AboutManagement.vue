<template>
  <div class="about-management">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>关于我管理</span>
      </div>
      
      <el-tabs v-model="activeTab" type="border-card">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <el-form :model="basicInfo" :rules="basicRules" ref="basicForm" label-width="120px">
            <!-- 头像上传 -->
            <el-form-item label="头像" prop="avatar">
              <div class="avatar-upload-container">
                <el-upload
                  class="avatar-uploader"
                  :action="uploadUrl"
                  :headers="uploadHeaders"
                  :show-file-list="false"
                  :on-success="handleAvatarSuccess"
                  :on-error="handleAvatarError"
                  :before-upload="beforeAvatarUpload"
                  accept="image/*"
                >
                  <img v-if="basicInfo.avatar" :src="getFullImageUrl(basicInfo.avatar)" class="avatar-preview">
                  <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                </el-upload>
                <div class="avatar-upload-tips">
                  <p>点击上传头像</p>
                  <p class="tips-text">支持 JPG、PNG 格式，文件大小不超过 2MB</p>
                  <el-button v-if="basicInfo.avatar" size="mini" type="danger" @click="removeAvatar">删除头像</el-button>
                </div>
              </div>
            </el-form-item>
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="姓名" prop="name">
                  <el-input v-model="basicInfo.name" placeholder="请输入姓名"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="职位" prop="title">
                  <el-input v-model="basicInfo.title" placeholder="请输入职位"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="basicInfo.email" placeholder="请输入邮箱"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="电话" prop="phone">
                  <el-input v-model="basicInfo.phone" placeholder="请输入电话"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="所在地" prop="location">
                  <el-input v-model="basicInfo.location" placeholder="请输入所在地"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="GitHub" prop="github">
                  <el-input v-model="basicInfo.github" placeholder="请输入GitHub地址"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-form-item label="个人简介" prop="introduction">
              <el-input type="textarea" :rows="4" v-model="basicInfo.introduction" placeholder="请输入个人简介"></el-input>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="saveBasicInfo">保存基本信息</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 工作经历 -->
        <el-tab-pane label="工作经历" name="work">
          <div class="section-header">
            <el-button type="primary" @click="showWorkDialog">添加工作经历</el-button>
          </div>
          
          <el-table :data="workExperiences" style="width: 100%">
            <el-table-column prop="companyName" label="公司名称" width="200"></el-table-column>
            <el-table-column prop="position" label="职位" width="150"></el-table-column>
            <el-table-column prop="startDate" label="开始时间" width="120"></el-table-column>
            <el-table-column prop="endDate" label="结束时间" width="120">
              <template slot-scope="scope">
                {{ scope.row.isCurrent ? '至今' : scope.row.endDate }}
              </template>
            </el-table-column>
            <el-table-column prop="location" label="工作地点" width="120"></el-table-column>
            <el-table-column label="操作" width="150">
              <template slot-scope="scope">
                <el-button size="mini" @click="editWork(scope.row)">编辑</el-button>
                <el-button size="mini" type="danger" @click="deleteWork(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 教育背景 -->
        <el-tab-pane label="教育背景" name="education">
          <div class="section-header">
            <el-button type="primary" @click="showEducationDialog">添加教育背景</el-button>
          </div>
          
          <el-table :data="educations" style="width: 100%">
            <el-table-column prop="schoolName" label="学校名称" width="200"></el-table-column>
            <el-table-column prop="degree" label="学历" width="100"></el-table-column>
            <el-table-column prop="major" label="专业" width="150"></el-table-column>
            <el-table-column prop="startDate" label="开始时间" width="120"></el-table-column>
            <el-table-column prop="endDate" label="结束时间" width="120"></el-table-column>
            <el-table-column prop="gpa" label="GPA" width="80"></el-table-column>
            <el-table-column label="操作" width="150">
              <template slot-scope="scope">
                <el-button size="mini" @click="editEducation(scope.row)">编辑</el-button>
                <el-button size="mini" type="danger" @click="deleteEducation(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 技能管理 -->
        <el-tab-pane label="技能管理" name="skills">
          <div class="section-header">
            <el-button type="primary" @click="showSkillDialog">添加技能</el-button>
          </div>
          
          <el-table :data="skills" style="width: 100%">
            <el-table-column prop="name" label="技能名称" width="150"></el-table-column>
            <el-table-column prop="categoryId" label="分类" width="120">
              <template slot-scope="scope">
                {{ getCategoryName(scope.row.categoryId) }}
              </template>
            </el-table-column>
            <el-table-column prop="proficiency" label="熟练度" width="100">
              <template slot-scope="scope">
                {{ scope.row.proficiency }}%
              </template>
            </el-table-column>
            <el-table-column prop="yearsExperience" label="经验年限" width="100"></el-table-column>
            <el-table-column label="操作" width="150">
              <template slot-scope="scope">
                <el-button size="mini" @click="editSkill(scope.row)">编辑</el-button>
                <el-button size="mini" type="danger" @click="deleteSkill(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 兴趣爱好 -->
        <el-tab-pane label="兴趣爱好" name="interests">
          <div class="section-header">
            <el-button type="primary" @click="showInterestDialog">添加兴趣爱好</el-button>
          </div>
          
          <el-table :data="interests" style="width: 100%">
            <el-table-column prop="name" label="兴趣名称" width="150"></el-table-column>
            <el-table-column prop="description" label="描述" width="200"></el-table-column>
            <el-table-column prop="level" label="等级" width="100"></el-table-column>
            <el-table-column prop="yearsExperience" label="经验年限" width="100"></el-table-column>
            <el-table-column label="操作" width="150">
              <template slot-scope="scope">
                <el-button size="mini" @click="editInterest(scope.row)">编辑</el-button>
                <el-button size="mini" type="danger" @click="deleteInterest(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 项目管理 -->
        <el-tab-pane label="项目管理" name="projects">
          <div class="section-header">
            <el-button type="primary" @click="showProjectDialog">添加项目</el-button>
          </div>
          
          <el-table :data="projects" style="width: 100%">
            <el-table-column prop="name" label="项目名称" width="200"></el-table-column>
            <el-table-column prop="title" label="项目标题" width="200"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
                  {{ scope.row.status === 1 ? '进行中' : '已完成' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="isFeatured" label="是否精选" width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.isFeatured ? 'success' : 'info'">
                  {{ scope.row.isFeatured ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template slot-scope="scope">
                <el-button size="mini" @click="editProject(scope.row)">编辑</el-button>
                <el-button size="mini" type="danger" @click="deleteProject(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 工作经历对话框 -->
    <el-dialog :title="workDialogTitle" :visible.sync="workDialogVisible" width="600px">
      <el-form :model="currentWork" :rules="workRules" ref="workForm" label-width="120px">
        <el-form-item label="公司名称" prop="companyName">
          <el-input v-model="currentWork.companyName" placeholder="请输入公司名称"></el-input>
        </el-form-item>
        <el-form-item label="职位" prop="position">
          <el-input v-model="currentWork.position" placeholder="请输入职位"></el-input>
        </el-form-item>
        <el-form-item label="部门" prop="department">
          <el-input v-model="currentWork.department" placeholder="请输入部门"></el-input>
        </el-form-item>
        <el-form-item label="工作地点" prop="location">
          <el-input v-model="currentWork.location" placeholder="请输入工作地点"></el-input>
        </el-form-item>
        <el-form-item label="开始时间" prop="startDate">
          <el-date-picker v-model="currentWork.startDate" type="date" placeholder="选择开始时间" format="yyyy-MM-dd" value-format="yyyy-MM-dd"></el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endDate">
          <el-date-picker v-model="currentWork.endDate" type="date" placeholder="选择结束时间" format="yyyy-MM-dd" value-format="yyyy-MM-dd" :disabled="currentWork.isCurrent"></el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="currentWork.isCurrent">当前工作</el-checkbox>
        </el-form-item>
        <el-form-item label="工作描述" prop="jobDescription">
          <el-input type="textarea" :rows="3" v-model="currentWork.jobDescription" placeholder="请输入工作描述"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="workDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveWork">确定</el-button>
      </div>
    </el-dialog>

    <!-- 教育背景对话框 -->
    <el-dialog :title="educationDialogTitle" :visible.sync="educationDialogVisible" width="600px">
      <el-form :model="currentEducation" :rules="educationRules" ref="educationForm" label-width="120px">
        <el-form-item label="学校名称" prop="schoolName">
          <el-input v-model="currentEducation.schoolName" placeholder="请输入学校名称"></el-input>
        </el-form-item>
        <el-form-item label="学历" prop="degree">
          <el-select v-model="currentEducation.degree" placeholder="请选择学历">
            <el-option label="专科" value="专科"></el-option>
            <el-option label="本科" value="本科"></el-option>
            <el-option label="硕士" value="硕士"></el-option>
            <el-option label="博士" value="博士"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="专业" prop="major">
          <el-input v-model="currentEducation.major" placeholder="请输入专业"></el-input>
        </el-form-item>
        <el-form-item label="开始时间" prop="startDate">
          <el-date-picker v-model="currentEducation.startDate" type="date" placeholder="选择开始时间" format="yyyy-MM-dd" value-format="yyyy-MM-dd"></el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endDate">
          <el-date-picker v-model="currentEducation.endDate" type="date" placeholder="选择结束时间" format="yyyy-MM-dd" value-format="yyyy-MM-dd"></el-date-picker>
        </el-form-item>
        <el-form-item label="GPA" prop="gpa">
          <el-input-number v-model="currentEducation.gpa" :precision="2" :step="0.1" :max="4.0" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input type="textarea" :rows="3" v-model="currentEducation.description" placeholder="请输入描述"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="educationDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEducation">确定</el-button>
      </div>
    </el-dialog>

    <!-- 技能对话框 -->
    <el-dialog :title="skillDialogTitle" :visible.sync="skillDialogVisible" width="500px">
      <el-form :model="currentSkill" :rules="skillRules" ref="skillForm" label-width="120px">
        <el-form-item label="技能名称" prop="name">
          <el-input v-model="currentSkill.name" placeholder="请输入技能名称"></el-input>
        </el-form-item>
        <el-form-item label="技能分类" prop="categoryId">
          <el-select v-model="currentSkill.categoryId" placeholder="请选择分类">
            <el-option label="编程语言" :value="1"></el-option>
            <el-option label="前端技术" :value="2"></el-option>
            <el-option label="后端技术" :value="3"></el-option>
            <el-option label="数据库" :value="4"></el-option>
            <el-option label="开发工具" :value="5"></el-option>
            <el-option label="其他技能" :value="6"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="熟练度" prop="proficiency">
          <el-slider v-model="currentSkill.proficiency" :max="100" show-input></el-slider>
        </el-form-item>
        <el-form-item label="经验年限" prop="yearsExperience">
          <el-input-number v-model="currentSkill.yearsExperience" :precision="1" :step="0.5" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input type="textarea" :rows="3" v-model="currentSkill.description" placeholder="请输入技能描述"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="skillDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveSkill">确定</el-button>
      </div>
    </el-dialog>

    <!-- 兴趣爱好对话框 -->
    <el-dialog :title="interestDialogTitle" :visible.sync="interestDialogVisible" width="500px">
      <el-form :model="currentInterest" :rules="interestRules" ref="interestForm" label-width="120px">
        <el-form-item label="兴趣名称" prop="name">
          <el-input v-model="currentInterest.name" placeholder="请输入兴趣名称"></el-input>
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <div class="icon-selector">
            <el-select v-model="currentInterest.icon" placeholder="请选择图标" filterable>
              <el-option
                v-for="icon in iconOptions"
                :key="icon.value"
                :label="icon.label"
                :value="icon.value">
                <i :class="icon.value" style="margin-right: 8px;"></i>
                {{ icon.label }}
              </el-option>
            </el-select>
            <div class="icon-preview" v-if="currentInterest.icon">
              <i :class="currentInterest.icon" :style="{color: currentInterest.color, fontSize: '24px'}"></i>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="颜色" prop="color">
          <el-color-picker v-model="currentInterest.color" show-alpha></el-color-picker>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input type="textarea" :rows="3" v-model="currentInterest.description" placeholder="请输入描述"></el-input>
        </el-form-item>
        <el-form-item label="等级" prop="level">
          <el-select v-model="currentInterest.level" placeholder="请选择等级">
            <el-option label="初学者" value="初学者"></el-option>
            <el-option label="业余爱好者" value="业余爱好者"></el-option>
            <el-option label="熟练" value="熟练"></el-option>
            <el-option label="专业" value="专业"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="经验年限" prop="yearsExperience">
          <el-input-number v-model="currentInterest.yearsExperience" :precision="1" :step="0.5" :min="0"></el-input-number>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="interestDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveInterest">确定</el-button>
      </div>
    </el-dialog>

    <!-- 项目对话框 -->
    <el-dialog :title="projectDialogTitle" :visible.sync="projectDialogVisible" width="700px">
      <el-form :model="currentProject" :rules="projectRules" ref="projectForm" label-width="120px">
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="currentProject.name" placeholder="请输入项目名称"></el-input>
        </el-form-item>
        <el-form-item label="项目标题" prop="title">
          <el-input v-model="currentProject.title" placeholder="请输入项目标题"></el-input>
        </el-form-item>
        <el-form-item label="项目描述" prop="description">
          <el-input type="textarea" :rows="4" v-model="currentProject.description" placeholder="请输入项目描述"></el-input>
        </el-form-item>
        <el-form-item label="技术栈" prop="technologies">
          <el-input v-model="currentProject.technologies" placeholder="请输入技术栈，用逗号分隔"></el-input>
        </el-form-item>
        <el-form-item label="项目链接" prop="projectUrl">
          <el-input v-model="currentProject.projectUrl" placeholder="请输入项目链接"></el-input>
        </el-form-item>
        <el-form-item label="源码链接" prop="sourceUrl">
          <el-input v-model="currentProject.sourceUrl" placeholder="请输入源码链接"></el-input>
        </el-form-item>
        <el-form-item label="开始时间" prop="startDate">
          <el-date-picker v-model="currentProject.startDate" type="date" placeholder="选择开始时间" format="yyyy-MM-dd" value-format="yyyy-MM-dd"></el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endDate">
          <el-date-picker v-model="currentProject.endDate" type="date" placeholder="选择结束时间" format="yyyy-MM-dd" value-format="yyyy-MM-dd"></el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="currentProject.isFeatured">精选项目</el-checkbox>
        </el-form-item>
        <el-form-item label="项目状态" prop="status">
          <el-select v-model="currentProject.status" placeholder="请选择状态">
            <el-option label="进行中" :value="1"></el-option>
            <el-option label="已完成" :value="0"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="projectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveProject">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'AboutManagement',
  data() {
    return {
      activeTab: 'basic',
      
      // 基本信息
      basicInfo: {
        name: '',
        title: '',
        introduction: '',
        location: '',
        email: '',
        phone: '',
        github: '',
        linkedin: '',
        website: '',
        wechat: '',
        qq: '',
        avatar: ''
      },
      
      // 头像上传相关
      uploadUrl: 'http://localhost:8080/api/upload/avatar',
      uploadHeaders: {
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      },
      
      // 工作经历
      workExperiences: [],
      workDialogVisible: false,
      workDialogTitle: '添加工作经历',
      currentWork: {
        companyName: '',
        position: '',
        department: '',
        location: '',
        startDate: '',
        endDate: '',
        isCurrent: false,
        jobDescription: ''
      },
      
      // 教育背景
      educations: [],
      educationDialogVisible: false,
      educationDialogTitle: '添加教育背景',
      currentEducation: {
        schoolName: '',
        degree: '',
        major: '',
        startDate: '',
        endDate: '',
        gpa: 0,
        description: ''
      },
      
      // 技能
      skills: [],
      skillDialogVisible: false,
      skillDialogTitle: '添加技能',
      currentSkill: {
        name: '',
        categoryId: 1,
        proficiency: 50,
        yearsExperience: 0,
        description: ''
      },
      
      // 兴趣爱好
      interests: [],
      interestDialogVisible: false,
      interestDialogTitle: '添加兴趣爱好',
      currentInterest: {
        name: '',
        description: '',
        level: '',
        yearsExperience: 0,
        icon: '',
        color: '#409EFF'
      },
      
      // 图标选项
      iconOptions: [
        { value: 'el-icon-star-off', label: '通用/默认' },
        { value: 'el-icon-cpu', label: '编程' },
        { value: 'el-icon-reading', label: '阅读' },
        { value: 'el-icon-camera', label: '摄影' },
        { value: 'el-icon-map-location', label: '旅行' },
        { value: 'el-icon-headset', label: '音乐' },
        { value: 'el-icon-bicycle', label: '运动' },
        { value: 'el-icon-brush', label: '绘画' },
        { value: 'el-icon-film', label: '电影' },
        { value: 'el-icon-coffee-cup', label: '咖啡' },
        { value: 'el-icon-food', label: '美食' },
        { value: 'el-icon-basketball', label: '篮球' },
        { value: 'el-icon-football', label: '足球' },
        { value: 'el-icon-game-handle', label: '游戏' },
        { value: 'el-icon-microphone', label: '唱歌' },
        { value: 'el-icon-guitar', label: '吉他' },
        { value: 'el-icon-piano', label: '钢琴' },
        { value: 'el-icon-trophy', label: '竞技' },
        { value: 'el-icon-medal', label: '奖牌' },
        { value: 'el-icon-magic-stick', label: '魔术' },
        { value: 'el-icon-present', label: '礼物' },
        { value: 'el-icon-shopping-bag-1', label: '购物' },
        { value: 'el-icon-suitcase', label: '商务' },
        { value: 'el-icon-school', label: '学习' },
        { value: 'el-icon-notebook-1', label: '笔记' },
        { value: 'el-icon-edit', label: '写作' },
        { value: 'el-icon-chat-dot-round', label: '聊天' },
        { value: 'el-icon-phone', label: '通讯' },
        { value: 'el-icon-video-camera', label: '视频' },
        { value: 'el-icon-picture', label: '图片' },
        { value: 'el-icon-collection', label: '收藏' },
        { value: 'el-icon-heart', label: '喜爱' },
        { value: 'el-icon-thumb', label: '点赞' },
        { value: 'el-icon-setting', label: '设置' },
        { value: 'el-icon-tools', label: '工具' }
      ],
      
      // 项目
      projects: [],
      projectDialogVisible: false,
      projectDialogTitle: '添加项目',
      currentProject: {
        name: '',
        title: '',
        description: '',
        technologies: '',
        projectUrl: '',
        sourceUrl: '',
        startDate: '',
        endDate: '',
        isFeatured: false,
        status: 1
      },
      
      // 表单验证规则
      basicRules: {
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        title: [{ required: true, message: '请输入职位', trigger: 'blur' }]
      },
      workRules: {
        companyName: [{ required: true, message: '请输入公司名称', trigger: 'blur' }],
        position: [{ required: true, message: '请输入职位', trigger: 'blur' }]
      },
      educationRules: {
        schoolName: [{ required: true, message: '请输入学校名称', trigger: 'blur' }],
        degree: [{ required: true, message: '请选择学历', trigger: 'change' }]
      },
      skillRules: {
        name: [{ required: true, message: '请输入技能名称', trigger: 'blur' }],
        categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }]
      },
      interestRules: {
        name: [{ required: true, message: '请输入兴趣名称', trigger: 'blur' }]
      },
      projectRules: {
        name: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
        title: [{ required: true, message: '请输入项目标题', trigger: 'blur' }]
      }
    }
  },
  
  created() {
    this.loadData()
  },
  
  methods: {
    // 加载所有数据
    async loadData() {
      await this.loadBasicInfo()
      await this.loadWorkExperiences()
      await this.loadEducations()
      await this.loadSkills()
      await this.loadInterests()
      await this.loadProjects()
    },
    
    // 加载基本信息
    async loadBasicInfo() {
      try {
        const response = await axios.get('/api/admin/about/basic')
        if (response.data.code === 200) {
          this.basicInfo = response.data.data || {}
        }
      } catch (error) {
        console.error('加载基本信息失败:', error)
      }
    },
    
    // 保存基本信息
    async saveBasicInfo() {
      this.$refs.basicForm.validate(async (valid) => {
        if (valid) {
          try {
            const response = await axios.put('/api/admin/about/basic', this.basicInfo)
            if (response.data.code === 200) {
              this.$message.success('保存成功')
            } else {
              this.$message.error(response.data.message || '保存失败')
            }
          } catch (error) {
            this.$message.error('保存失败')
          }
        }
      })
    },
    
    // 头像上传成功回调
    handleAvatarSuccess(response) {
      console.log('头像上传成功:', response)
      if (response.code === 200) {
        this.basicInfo.avatar = response.data.url
        this.$message.success('头像上传成功')
        // 自动保存基本信息
        this.saveBasicInfo()
      } else {
        this.$message.error(response.message || '头像上传失败')
      }
    },
    
    // 头像上传失败回调
    handleAvatarError(error) {
      console.error('头像上传失败:', error)
      this.$message.error('头像上传失败，请重试')
    },
    
    // 头像上传前的校验
    beforeAvatarUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isImage) {
        this.$message.error('只能上传图片文件!')
        return false
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!')
        return false
      }
      
      // 更新上传头信息
      this.uploadHeaders = {
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      }
      
      return true
    },
    
    // 删除头像
    removeAvatar() {
      this.$confirm('确定要删除头像吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.basicInfo.avatar = ''
        this.saveBasicInfo()
        this.$message.success('头像已删除')
      }).catch(() => {
        // 用户取消删除
      })
    },
    
    // 获取完整的图片URL
    getFullImageUrl(url) {
      if (!url) return ''
      if (url.startsWith('http')) {
        return url
      }
      return process.env.VUE_APP_API_BASE_URL + url
    },
    
    // 加载工作经历
    async loadWorkExperiences() {
      try {
        const response = await axios.get('/api/admin/about/work-experience')
        if (response.data.code === 200) {
          this.workExperiences = response.data.data || []
        }
      } catch (error) {
        console.error('加载工作经历失败:', error)
      }
    },
    
    // 显示工作经历对话框
    showWorkDialog() {
      this.workDialogTitle = '添加工作经历'
      this.currentWork = {
        companyName: '',
        position: '',
        department: '',
        location: '',
        startDate: '',
        endDate: '',
        isCurrent: false,
        jobDescription: ''
      }
      this.workDialogVisible = true
    },
    
    // 编辑工作经历
    editWork(work) {
      this.workDialogTitle = '编辑工作经历'
      this.currentWork = { ...work }
      this.workDialogVisible = true
    },
    
    // 保存工作经历
    async saveWork() {
      this.$refs.workForm.validate(async (valid) => {
        if (valid) {
          try {
            let response
            if (this.currentWork.id) {
              response = await axios.put(`/api/admin/about/work-experience/${this.currentWork.id}`, this.currentWork)
            } else {
              response = await axios.post('/api/admin/about/work-experience', this.currentWork)
            }
            
            if (response.data.code === 200) {
              this.$message.success('保存成功')
              this.workDialogVisible = false
              this.loadWorkExperiences()
            } else {
              this.$message.error(response.data.message || '保存失败')
            }
          } catch (error) {
            this.$message.error('保存失败')
          }
        }
      })
    },
    
    // 删除工作经历
    async deleteWork(id) {
      this.$confirm('确定要删除这条工作经历吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await axios.delete(`/api/admin/about/work-experience/${id}`)
          if (response.data.code === 200) {
            this.$message.success('删除成功')
            this.loadWorkExperiences()
          } else {
            this.$message.error(response.data.message || '删除失败')
          }
        } catch (error) {
          this.$message.error('删除失败')
        }
      })
    },
    
    // 加载教育背景
    async loadEducations() {
      try {
        const response = await axios.get('/api/admin/about/education')
        if (response.data.code === 200) {
          this.educations = response.data.data || []
        }
      } catch (error) {
        console.error('加载教育背景失败:', error)
      }
    },
    
    // 显示教育背景对话框
    showEducationDialog() {
      this.educationDialogTitle = '添加教育背景'
      this.currentEducation = {
        schoolName: '',
        degree: '',
        major: '',
        startDate: '',
        endDate: '',
        gpa: 0,
        description: ''
      }
      this.educationDialogVisible = true
    },
    
    // 编辑教育背景
    editEducation(education) {
      this.educationDialogTitle = '编辑教育背景'
      this.currentEducation = { ...education }
      this.educationDialogVisible = true
    },
    
    // 保存教育背景
    async saveEducation() {
      this.$refs.educationForm.validate(async (valid) => {
        if (valid) {
          try {
            let response
            if (this.currentEducation.id) {
              response = await axios.put(`/api/admin/about/education/${this.currentEducation.id}`, this.currentEducation)
            } else {
              response = await axios.post('/api/admin/about/education', this.currentEducation)
            }
            
            if (response.data.code === 200) {
              this.$message.success('保存成功')
              this.educationDialogVisible = false
              this.loadEducations()
            } else {
              this.$message.error(response.data.message || '保存失败')
            }
          } catch (error) {
            this.$message.error('保存失败')
          }
        }
      })
    },
    
    // 删除教育背景
    async deleteEducation(id) {
      this.$confirm('确定要删除这条教育背景吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await axios.delete(`/api/admin/about/education/${id}`)
          if (response.data.code === 200) {
            this.$message.success('删除成功')
            this.loadEducations()
          } else {
            this.$message.error(response.data.message || '删除失败')
          }
        } catch (error) {
          this.$message.error('删除失败')
        }
      })
    },
    
    // 加载技能
    async loadSkills() {
      try {
        const response = await axios.get('/api/admin/about/skills')
        if (response.data.code === 200) {
          this.skills = response.data.data || []
        }
      } catch (error) {
        console.error('加载技能失败:', error)
      }
    },
    
    // 显示技能对话框
    showSkillDialog() {
      this.skillDialogTitle = '添加技能'
      this.currentSkill = {
        name: '',
        categoryId: 1,
        proficiency: 50,
        yearsExperience: 0,
        description: ''
      }
      this.skillDialogVisible = true
    },
    
    // 编辑技能
    editSkill(skill) {
      this.skillDialogTitle = '编辑技能'
      this.currentSkill = { ...skill }
      this.skillDialogVisible = true
    },
    
    // 保存技能
    async saveSkill() {
      this.$refs.skillForm.validate(async (valid) => {
        if (valid) {
          try {
            let response
            if (this.currentSkill.id) {
              response = await axios.put(`/api/admin/about/skills/${this.currentSkill.id}`, this.currentSkill)
            } else {
              response = await axios.post('/api/admin/about/skills', this.currentSkill)
            }
            
            if (response.data.code === 200) {
              this.$message.success('保存成功')
              this.skillDialogVisible = false
              this.loadSkills()
            } else {
              this.$message.error(response.data.message || '保存失败')
            }
          } catch (error) {
            this.$message.error('保存失败')
          }
        }
      })
    },
    
    // 删除技能
    async deleteSkill(id) {
      this.$confirm('确定要删除这个技能吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await axios.delete(`/api/admin/about/skills/${id}`)
          if (response.data.code === 200) {
            this.$message.success('删除成功')
            this.loadSkills()
          } else {
            this.$message.error(response.data.message || '删除失败')
          }
        } catch (error) {
          this.$message.error('删除失败')
        }
      })
    },
    
    // 加载兴趣爱好
    async loadInterests() {
      try {
        const response = await axios.get('/api/admin/about/interests')
        if (response.data.code === 200) {
          this.interests = response.data.data || []
        }
      } catch (error) {
        console.error('加载兴趣爱好失败:', error)
      }
    },
    
    // 显示兴趣爱好对话框
    showInterestDialog() {
      this.interestDialogTitle = '添加兴趣爱好'
      this.currentInterest = {
        name: '',
        description: '',
        level: '',
        yearsExperience: 0
      }
      this.interestDialogVisible = true
    },
    
    // 编辑兴趣爱好
    editInterest(interest) {
      this.interestDialogTitle = '编辑兴趣爱好'
      this.currentInterest = { ...interest }
      this.interestDialogVisible = true
    },
    
    // 保存兴趣爱好
    async saveInterest() {
      this.$refs.interestForm.validate(async (valid) => {
        if (valid) {
          try {
            let response
            if (this.currentInterest.id) {
              response = await axios.put(`/api/admin/about/interests/${this.currentInterest.id}`, this.currentInterest)
            } else {
              response = await axios.post('/api/admin/about/interests', this.currentInterest)
            }
            
            if (response.data.code === 200) {
              this.$message.success('保存成功')
              this.interestDialogVisible = false
              this.loadInterests()
            } else {
              this.$message.error(response.data.message || '保存失败')
            }
          } catch (error) {
            this.$message.error('保存失败')
          }
        }
      })
    },
    
    // 删除兴趣爱好
    async deleteInterest(id) {
      this.$confirm('确定要删除这个兴趣爱好吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await axios.delete(`/api/admin/about/interests/${id}`)
          if (response.data.code === 200) {
            this.$message.success('删除成功')
            this.loadInterests()
          } else {
            this.$message.error(response.data.message || '删除失败')
          }
        } catch (error) {
          this.$message.error('删除失败')
        }
      })
    },
    
    // 加载项目
    async loadProjects() {
      try {
        const response = await axios.get('/api/admin/about/projects')
        if (response.data.code === 200) {
          this.projects = response.data.data || []
        }
      } catch (error) {
        console.error('加载项目失败:', error)
      }
    },
    
    // 显示项目对话框
    showProjectDialog() {
      this.projectDialogTitle = '添加项目'
      this.currentProject = {
        name: '',
        title: '',
        description: '',
        technologies: '',
        projectUrl: '',
        sourceUrl: '',
        startDate: '',
        endDate: '',
        isFeatured: false,
        status: 1
      }
      this.projectDialogVisible = true
    },
    
    // 编辑项目
    editProject(project) {
      this.projectDialogTitle = '编辑项目'
      this.currentProject = { ...project }
      this.projectDialogVisible = true
    },
    
    // 保存项目
    async saveProject() {
      this.$refs.projectForm.validate(async (valid) => {
        if (valid) {
          try {
            let response
            if (this.currentProject.id) {
              response = await axios.put(`/api/admin/about/projects/${this.currentProject.id}`, this.currentProject)
            } else {
              response = await axios.post('/api/admin/about/projects', this.currentProject)
            }
            
            if (response.data.code === 200) {
              this.$message.success('保存成功')
              this.projectDialogVisible = false
              this.loadProjects()
            } else {
              this.$message.error(response.data.message || '保存失败')
            }
          } catch (error) {
            this.$message.error('保存失败')
          }
        }
      })
    },
    
    // 删除项目
    async deleteProject(id) {
      this.$confirm('确定要删除这个项目吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await axios.delete(`/api/admin/about/projects/${id}`)
          if (response.data.code === 200) {
            this.$message.success('删除成功')
            this.loadProjects()
          } else {
            this.$message.error(response.data.message || '删除失败')
          }
        } catch (error) {
          this.$message.error('删除失败')
        }
      })
    },
    
    // 获取分类名称
    getCategoryName(categoryId) {
      const categories = {
        1: '编程语言',
        2: '前端技术',
        3: '后端技术',
        4: '数据库',
        5: '开发工具',
        6: '其他技能'
      }
      return categories[categoryId] || '未知分类'
    }
  }
}
</script>

<style scoped>
.about-management {
  padding: 20px;
}

.section-header {
  margin-bottom: 20px;
}

.dialog-footer {
  text-align: right;
}

.el-slider {
  margin: 12px 0;
}

/* 头像上传样式 */
.avatar-upload-container {
  display: flex;
  align-items: flex-start;
  gap: 20px;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}

.avatar-uploader:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
  display: block;
}

.avatar-preview {
  width: 120px;
  height: 120px;
  display: block;
  object-fit: cover;
  border-radius: 6px;
}

.avatar-upload-tips {
  flex: 1;
  padding-left: 10px;
}

.avatar-upload-tips p {
  margin: 0 0 8px 0;
  color: #606266;
}

.tips-text {
  font-size: 12px;
  color: #909399;
}

.avatar-upload-tips .el-button {
  margin-top: 10px;
}

/* 图标选择器样式 */
.icon-selector {
  display: flex;
  align-items: center;
  gap: 15px;
}

.icon-selector .el-select {
  flex: 1;
}

.icon-preview {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background-color: #f5f7fa;
}

.icon-preview i {
  font-size: 24px;
}
</style>