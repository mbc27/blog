const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave: false,
  
  devServer: {
    port: 8081,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        ws: true,
        secure: false,
        logLevel: 'debug'
      },
      // 添加图片资源代理
      '/images': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        ws: false,
        secure: false,
        logLevel: 'debug'
      },
      // 向后兼容的uploads路径代理
      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        ws: false,
        secure: false,
        logLevel: 'debug'
      }
    }
  },
  
  // 配置打包时的公共路径
  publicPath: process.env.NODE_ENV === 'production' ? './' : '/',
  
  // 配置静态资源处理
  chainWebpack: config => {
    // 处理图片资源
    config.module
      .rule('images')
      .test(/\.(png|jpe?g|gif|svg)(\?.*)?$/)
      .use('url-loader')
      .loader('url-loader')
      .options({
        limit: 8192,
        name: 'img/[name].[hash:8].[ext]'
      })
  }
})