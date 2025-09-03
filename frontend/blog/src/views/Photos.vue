<template>
  <div class="photos-page">
    <div class="container">
      <div class="page-header">
        <h1>我的相册</h1>
        <div class="header-decoration"></div>
      </div>
      
      <div class="photo-categories">
        <div class="category-title">
          <i class="el-icon-collection-tag"></i>
          <span>分类浏览</span>
        </div>
        <el-radio-group v-model="activeCategory">
          <el-radio-button label="all">
            <i class="el-icon-picture"></i> 全部
          </el-radio-button>
          <el-radio-button 
            v-for="category in categories" 
            :key="category.id" 
            :label="category.code">
            <i :class="getCategoryIcon(category.name)"></i> {{ category.name }}
          </el-radio-button>
        </el-radio-group>
      </div>
      
      <div class="photos-stats" v-if="!loading">
        <div class="stat-item">
          <i class="el-icon-picture-outline"></i>
          <div class="stat-content">
            <div class="stat-value">{{ total }}</div>
            <div class="stat-label">总照片数</div>
          </div>
        </div>
        <div class="stat-item">
          <i class="el-icon-collection"></i>
          <div class="stat-content">
            <div class="stat-value">{{ categories.length }}</div>
            <div class="stat-label">分类数</div>
          </div>
        </div>
        <div class="stat-item">
          <i class="el-icon-view"></i>
          <div class="stat-content">
            <div class="stat-value">{{ totalViews }}</div>
            <div class="stat-label">总浏览量</div>
          </div>
        </div>
      </div>
      
      <el-skeleton :loading="loading" animated :count="6" v-if="loading">
        <template slot="template">
          <div class="photo-grid">
            <div class="photo-item" v-for="i in 6" :key="i">
              <div class="photo-card">
                <div class="photo-image">
                  <el-skeleton-item variant="image" style="width: 100%; height: 200px;" />
                </div>
                <div class="photo-info">
                  <el-skeleton-item variant="h3" style="width: 50%;" />
                  <el-skeleton-item variant="text" style="width: 100%;" />
                  <el-skeleton-item variant="text" style="width: 100%;" />
                </div>
              </div>
            </div>
          </div>
        </template>
      </el-skeleton>
      
      <template v-else>
        <transition-group name="photo-fade" tag="div" class="photo-grid">
          <div class="photo-item" v-for="photo in filteredPhotos" :key="photo.id">
            <div class="photo-card" @click="previewImage(photo)">
              <div class="photo-image">
                <img :src="photo.url" :alt="photo.title" />
                <div class="photo-overlay">
                  <i class="el-icon-zoom-in"></i>
                </div>
              </div>
              <div class="photo-info">
                <h3>{{ photo.title }}</h3>
                <p>{{ photo.description }}</p>
                <div class="photo-meta">
                  <span class="photo-date"><i class="el-icon-date"></i> {{ photo.date }}</span>
                  <span class="photo-views"><i class="el-icon-view"></i> {{ photo.views }}</span>
                </div>
                <div class="photo-category" v-if="getCategoryName(photo.category)">
                  <el-tag size="small" :type="getCategoryType(photo.category)">
                    <i :class="getCategoryIcon(getCategoryName(photo.category))"></i> 
                    {{ getCategoryName(photo.category) }}
                  </el-tag>
                </div>
              </div>
            </div>
          </div>
        </transition-group>
        
        <el-empty 
          v-if="filteredPhotos.length === 0" 
          description="暂无图片" 
          :image-size="200">
          <template slot="description">
            <p class="empty-text">当前分类下暂无图片</p>
          </template>
          <el-button type="primary" @click="activeCategory = 'all'">查看全部图片</el-button>
        </el-empty>
      </template>
      
      <div class="pagination">
        <el-pagination
          background
          layout="total, prev, pager, next, jumper"
          :current-page.sync="currentPage"
          :page-size="pageSize"
          :total="total"
          @current-change="handlePageChange">
        </el-pagination>
      </div>
      
      <!-- 图片预览对话框 -->
      <el-dialog
        :visible.sync="previewVisible"
        width="80%"
        center
        custom-class="photo-preview-dialog"
        :show-close="true"
        @close="closePreview">
        <div class="photo-preview-container">
          <div class="preview-image">
            <img :src="previewPhoto.url" :alt="previewPhoto.title" />
          </div>
          <div class="preview-info">
            <h2>{{ previewPhoto.title }}</h2>
            <p class="preview-description">{{ previewPhoto.description }}</p>
            <div class="preview-meta">
              <div class="meta-item">
                <i class="el-icon-date"></i>
                <span>上传日期: {{ previewPhoto.date }}</span>
              </div>
              <div class="meta-item">
                <i class="el-icon-view"></i>
                <span>浏览次数: {{ previewPhoto.views }}</span>
              </div>
              <div class="meta-item" v-if="getCategoryName(previewPhoto.category)">
                <i class="el-icon-collection-tag"></i>
                <span>所属分类: {{ getCategoryName(previewPhoto.category) }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="preview-navigation" v-if="filteredPhotos.length > 1">
          <el-button type="text" @click="prevImage" :disabled="previewIndex <= 0">
            <i class="el-icon-arrow-left"></i> 上一张
          </el-button>
          <span class="preview-counter">{{ previewIndex + 1 }} / {{ filteredPhotos.length }}</span>
          <el-button type="text" @click="nextImage" :disabled="previewIndex >= filteredPhotos.length - 1">
            下一张 <i class="el-icon-arrow-right"></i>
          </el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import api from '../api'

export default {
  name: 'Photos',
  data() {
    return {
      activeCategory: 'all',
      photos: [],
      categories: [],
      loading: false,
      currentPage: 1,
      pageSize: 12,
      total: 0,
      previewVisible: false,
      previewPhoto: {},
      previewIndex: 0
    }
  },
  created() {
    this.fetchCategories()
    this.fetchPhotos()
  },
  computed: {
    filteredPhotos() {
      if (this.activeCategory === 'all') {
        return this.photos
      } else {
        return this.photos.filter(photo => photo.category === this.activeCategory)
      }
    },
    totalViews() {
      return this.photos.reduce((sum, photo) => sum + (photo.views || 0), 0)
    }
  },
  methods: {
    getCategoryIcon(categoryName) {
      if (!categoryName) return 'el-icon-picture';
      
      const iconMap = {
        '风景': 'el-icon-picture-outline',
        '人物': 'el-icon-user',
        '动物': 'el-icon-cherry',
        '植物': 'el-icon-sugar',
        '建筑': 'el-icon-office-building',
        '美食': 'el-icon-food',
        '旅行': 'el-icon-place',
        '生活': 'el-icon-coffee-cup',
        '艺术': 'el-icon-brush',
        '科技': 'el-icon-cpu',
        '其他': 'el-icon-more',
        'ceshi': 'el-icon-s-flag'
      };
      
      return iconMap[categoryName] || 'el-icon-picture';
    },
    
    getCategoryName(categoryId) {
      if (categoryId === 'all') return '全部';
      const category = this.categories.find(c => c.code === categoryId);
      return category ? category.name : '';
    },
    
    getCategoryType(categoryId) {
      if (categoryId === 'all') return '';
      
      // 根据分类ID返回不同的标签类型
      const types = ['', 'success', 'warning', 'danger', 'info'];
      const index = parseInt(categoryId) % types.length;
      return types[index];
    },
    
    previewImage(photo) {
      this.previewPhoto = photo;
      this.previewIndex = this.filteredPhotos.findIndex(p => p.id === photo.id);
      this.previewVisible = true;
    },
    
    closePreview() {
      this.previewVisible = false;
    },
    
    prevImage() {
      if (this.previewIndex > 0) {
        this.previewIndex--;
        this.previewPhoto = this.filteredPhotos[this.previewIndex];
      }
    },
    
    nextImage() {
      if (this.previewIndex < this.filteredPhotos.length - 1) {
        this.previewIndex++;
        this.previewPhoto = this.filteredPhotos[this.previewIndex];
      }
    },
    
    // 获取完整的图片URL
    getFullImageUrl(url) {
      if (!url) return ''
      // 如果已经是完整URL，直接返回
      if (url.startsWith('http')) {
        return url
      }
      // 如果是相对路径，添加服务器地址
      if (url.startsWith('/')) {
        return `http://localhost:8080${url}`
      }
      // 如果不是以/开头，也添加服务器地址和/uploads/前缀
      return `http://localhost:8080/uploads/${url}`
    },
    
    async fetchCategories() {
      try {
        const response = await api.photo.getCategories()
        if (response.code === 200) {
          // 确保数据存在并正确映射
          const data = response.data || []
          this.categories = data.map(category => ({
            id: category.id,
            name: category.categoryName || category.name,
            code: category.id.toString()
          }))
        }
      } catch (error) {
        console.error('获取相册分类失败:', error)
        // 静默失败，不显示错误提示
        this.categories = []
      }
    },
    
    async fetchPhotos() {
      try {
        this.loading = true
        const params = {
          current: this.currentPage,
          size: this.pageSize
        }
        
        if (this.activeCategory !== 'all') {
          params.categoryId = this.activeCategory
        }
        
        const response = await api.photo.getList(params)
        if (response.code === 200) {
          // 确保response.data和response.data.records存在
          if (response.data && Array.isArray(response.data.records)) {
            this.photos = response.data.records.map(photo => ({
              id: photo.id,
              url: this.getFullImageUrl(photo.url) || 'https://via.placeholder.com/400x300',
              title: photo.description || photo.title || '无标题',
              description: photo.description || '暂无描述',
              date: this.formatDate(photo.createTime),
              views: photo.views || 0,
              category: photo.categoryId ? photo.categoryId.toString() : 'all'
            }))
            this.total = response.data.total || 0
          } else {
            // 如果数据格式不正确，设置为空数组
            this.photos = []
            this.total = 0
            console.warn('相册数据格式不正确:', response.data)
          }
        }
      } catch (error) {
        console.error('获取相册列表失败:', error)
        // 静默失败，不显示错误提示
        this.photos = []
        this.total = 0
        
        // 添加一些示例图片，以便在API失败时仍然显示一些内容
        this.photos = Array.from({ length: 4 }, (_, i) => ({
          id: i + 1,
          url: `https://placehold.co/400x300/e9ecef/495057?text=示例图片 ${i + 1}`,
          title: `示例标题 ${i + 1}`,
          description: '这是一个示例图片描述，当API不可用时显示。',
          date: '2025-08-27',
          views: 0,
          category: 'all'
        }))
      } finally {
        this.loading = false
      }
    },
    
    handlePageChange(page) {
      this.currentPage = page
      this.fetchPhotos()
    },
    
    handleCategoryChange() {
      this.currentPage = 1
      this.fetchPhotos()
    },
    
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    }
  },
  watch: {
    activeCategory() {
      this.handleCategoryChange()
    }
  }
}
</script>

<style scoped>
.photos-page {
  padding: 40px 0 80px;
  background: linear-gradient(135deg, #f6f9fc 0%, #eef1f5 100%);
  min-height: 100vh;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
  position: relative;
}

h1 {
  color: #2c3e50;
  margin-bottom: 15px;
  font-size: 3rem;
  font-weight: 700;
  text-align: center;
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 1px;
  position: relative;
  display: inline-block;
}

.header-decoration {
  width: 100px;
  height: 4px;
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  margin: 0 auto;
  border-radius: 2px;
}

.photos-stats {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-bottom: 40px;
}

.stat-item {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  min-width: 180px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.stat-item i {
  font-size: 2.5rem;
  color: #6a11cb;
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 1.8rem;
  font-weight: 700;
  color: #2c3e50;
}

.stat-label {
  font-size: 0.9rem;
  color: #7f8c8d;
}

.photo-categories {
  margin-bottom: 40px;
  text-align: center;
  background: white;
  padding: 25px;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
}

.category-title {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #2c3e50;
  font-size: 1.2rem;
  font-weight: 600;
}

.category-title i {
  color: #6a11cb;
}

.photo-categories .el-radio-button__inner {
  padding: 12px 20px;
  border-radius: 8px;
  margin: 5px;
  transition: all 0.3s ease;
  height: auto;
  line-height: 1.5;
  display: flex;
  align-items: center;
  gap: 5px;
}

.photo-categories .el-radio-group {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 10px;
}

.photo-categories .el-radio-button:first-child .el-radio-button__inner,
.photo-categories .el-radio-button:last-child .el-radio-button__inner {
  border-radius: 8px;
}

.photo-categories .el-radio-button:not(:first-child) .el-radio-button__inner {
  border-left: 1px solid #dcdfe6;
}

.photo-categories .el-radio-button__orig-radio:checked + .el-radio-button__inner {
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  border-color: #6a11cb;
  box-shadow: 0 5px 15px rgba(106, 17, 203, 0.3);
  transform: translateY(-2px);
}

.photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 25px;
  margin-bottom: 40px;
}

.photo-item {
  transition: all 0.5s ease;
}

.photo-fade-enter-active, .photo-fade-leave-active {
  transition: all 0.5s;
}

.photo-fade-enter, .photo-fade-leave-to {
  opacity: 0;
  transform: translateY(30px);
}

.photo-card {
  background: white;
  border-radius: 15px;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
  height: 100%;
  display: flex;
  flex-direction: column;
  position: relative;
}

.photo-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15);
}

.photo-card:before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 5px;
  background: linear-gradient(90deg, #6a11cb, #2575fc);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.3s ease;
}

.photo-card:hover:before {
  transform: scaleX(1);
}

.photo-image {
  position: relative;
  overflow: hidden;
  height: 220px;
}

.photo-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: transform 0.5s ease;
}

.photo-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.photo-overlay i {
  color: white;
  font-size: 2rem;
  background: rgba(0, 0, 0, 0.5);
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transform: scale(0.8);
  transition: transform 0.3s ease;
}

.photo-card:hover .photo-image img {
  transform: scale(1.1);
}

.photo-card:hover .photo-overlay {
  opacity: 1;
}

.photo-card:hover .photo-overlay i {
  transform: scale(1);
}

.photo-info {
  padding: 20px;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.photo-info h3 {
  margin: 0 0 12px 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
  line-height: 1.4;
}

.photo-info p {
  color: #666;
  margin-bottom: 15px;
  line-height: 1.6;
  font-size: 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex-grow: 1;
}

.photo-meta {
  display: flex;
  justify-content: space-between;
  color: #7f8c8d;
  font-size: 14px;
  margin-bottom: 12px;
}

.photo-date, .photo-views {
  display: flex;
  align-items: center;
  gap: 5px;
}

.photo-date i {
  color: #2575fc;
}

.photo-views i {
  color: #6a11cb;
}

.photo-category {
  margin-top: auto;
}

.photo-category .el-tag {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 0 10px;
  height: 24px;
  border-radius: 12px;
}

.empty-text {
  font-size: 16px;
  color: #909399;
  margin-bottom: 15px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

.el-pagination.is-background .el-pager li:not(.disabled).active {
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
}

.el-pagination.is-background .el-pager li:not(.disabled):hover {
  color: #6a11cb;
}

/* 图片预览对话框样式 */
.photo-preview-dialog {
  border-radius: 20px;
  overflow: hidden;
}

.photo-preview-dialog .el-dialog__header {
  display: none;
}

.photo-preview-dialog .el-dialog__body {
  padding: 0;
}

.photo-preview-container {
  display: flex;
  flex-direction: column;
}

.preview-image {
  width: 100%;
  max-height: 70vh;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #000;
}

.preview-image img {
  max-width: 100%;
  max-height: 70vh;
  object-fit: contain;
}

.preview-info {
  padding: 25px;
  background: white;
}

.preview-info h2 {
  margin: 0 0 15px 0;
  color: #2c3e50;
  font-size: 24px;
  font-weight: 600;
}

.preview-description {
  color: #666;
  margin-bottom: 20px;
  line-height: 1.6;
  font-size: 16px;
}

.preview-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #7f8c8d;
}

.meta-item i {
  color: #6a11cb;
}

.preview-navigation {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 25px;
  background: #f8f9fa;
  border-top: 1px solid #eee;
}

.preview-counter {
  color: #7f8c8d;
  font-size: 14px;
}

@media (min-width: 992px) {
  .photo-preview-container {
    flex-direction: row;
    height: 70vh;
  }
  
  .preview-image {
    width: 65%;
    max-height: 70vh;
  }
  
  .preview-info {
    width: 35%;
    overflow-y: auto;
    max-height: 70vh;
  }
}

@media (max-width: 992px) {
  .photos-stats {
    flex-direction: column;
    align-items: center;
    gap: 15px;
  }
  
  .stat-item {
    width: 100%;
    max-width: 300px;
  }
}

@media (max-width: 768px) {
  .photo-grid {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  }
  
  h1 {
    font-size: 2rem;
  }
}

@media (max-width: 480px) {
  .photo-grid {
    grid-template-columns: 1fr;
  }
  
  .photo-categories .el-radio-button__inner {
    padding: 10px 15px;
    font-size: 14px;
  }
}
</style>