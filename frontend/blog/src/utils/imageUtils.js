/**
 * 图片URL处理工具
 */

/**
 * 获取标准化的图片URL
 * @param {string} url - 原始图片URL
 * @returns {string} - 标准化后的图片URL
 */
export function getImageUrl(url) {
  if (!url) return '';
  
  // 如果是完整的HTTP URL，在开发环境下转换为相对路径使用代理
  if (url.startsWith('http://') || url.startsWith('https://')) {
    if (process.env.NODE_ENV === 'development') {
      // 开发环境：提取路径部分，使用前端代理
      const urlObj = new URL(url);
      return urlObj.pathname;
    } else {
      // 生产环境：直接使用完整URL
      return url;
    }
  }
  
  // 确保以/开头
  if (!url.startsWith('/')) {
    url = '/' + url;
  }
  
  // 如果没有正确的路径前缀，添加/images前缀
  if (!url.startsWith('/images/') && !url.startsWith('/uploads/') && !url.startsWith('/api/')) {
    url = '/images' + url;
  }
  
  return url;
}

/**
 * 处理图片加载错误
 * @param {Event} event - 图片加载错误事件
 * @param {string} fallbackUrl - 备用图片URL（可选）
 */
export function handleImageError(event, fallbackUrl = null) {
  const img = event.target;
  const originalSrc = img.src;
  
  console.error('图片加载失败:', originalSrc);
  
  // 如果提供了备用URL且当前不是备用URL，尝试加载备用图片
  if (fallbackUrl && originalSrc !== fallbackUrl) {
    img.src = fallbackUrl;
    return;
  }
  
  // 尝试修复URL
  const fixedUrl = tryFixImageUrl(originalSrc);
  if (fixedUrl && fixedUrl !== originalSrc) {
    img.src = fixedUrl;
    return;
  }
  
  // 显示错误占位符
  showImageErrorPlaceholder(img);
}

/**
 * 尝试修复图片URL
 * @param {string} url - 原始URL
 * @returns {string|null} - 修复后的URL或null
 */
function tryFixImageUrl(url) {
  if (!url) return null;
  
  // 如果是相对路径，尝试添加完整前缀
  if (url.startsWith('/')) {
    // 在开发环境下，相对路径应该通过代理正常工作
    if (process.env.NODE_ENV === 'development') {
      return url + '?t=' + Date.now(); // 添加时间戳防止缓存
    } else {
      // 生产环境下，可能需要完整的URL
      return process.env.VUE_APP_IMAGE_BASE_URL + url;
    }
  }
  
  // 如果是IP地址，尝试替换为localhost（开发环境）
  if (process.env.NODE_ENV === 'development' && url.match(/\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}/)) {
    return url.replace(/https?:\/\/\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}(:\d+)?/g, 'http://localhost:8080');
  }
  
  return null;
}

/**
 * 显示图片错误占位符
 * @param {HTMLImageElement} img - 图片元素
 */
function showImageErrorPlaceholder(img) {
  // 设置错误样式
  img.style.backgroundColor = '#f5f5f5';
  img.style.border = '1px dashed #ddd';
  img.style.display = 'flex';
  img.style.alignItems = 'center';
  img.style.justifyContent = 'center';
  img.style.color = '#999';
  img.style.fontSize = '12px';
  img.style.minHeight = '60px';
  img.style.minWidth = '60px';
  
  // 创建错误提示元素
  const errorDiv = document.createElement('div');
  errorDiv.innerHTML = '<i class="el-icon-picture" style="font-size: 24px; margin-bottom: 5px;"></i><br>图片加载失败';
  errorDiv.style.textAlign = 'center';
  errorDiv.style.lineHeight = '1.2';
  
  // 清空原有内容并添加错误提示
  img.innerHTML = '';
  img.appendChild(errorDiv);
}

/**
 * 预加载图片
 * @param {string} url - 图片URL
 * @returns {Promise} - 加载结果Promise
 */
export function preloadImage(url) {
  return new Promise((resolve, reject) => {
    const img = new Image();
    img.onload = () => resolve(url);
    img.onerror = () => reject(new Error(`Failed to load image: ${url}`));
    img.src = getImageUrl(url);
  });
}

/**
 * 批量预加载图片
 * @param {string[]} urls - 图片URL数组
 * @returns {Promise} - 所有图片加载结果Promise
 */
export function preloadImages(urls) {
  const promises = urls.map(url => preloadImage(url).catch(err => {
    console.warn('图片预加载失败:', err.message);
    return null;
  }));
  return Promise.all(promises);
}

/**
 * 获取默认头像URL
 * @returns {string} - 默认头像URL
 */
export function getDefaultAvatarUrl() {
  return '/images/default-avatar.svg';
}

/**
 * 获取默认封面图URL
 * @returns {string} - 默认封面图URL
 */
export function getDefaultCoverUrl() {
  return '/images/default-project.svg';
}