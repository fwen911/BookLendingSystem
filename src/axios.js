import axios from 'axios'

// 创建axios实例
const service = axios.create({
  baseURL: process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080', // API基础URL
  timeout: 10000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 可以在这里添加token等认证信息
    // const token = localStorage.getItem('token')
    // if (token) {
    //   config.headers['Authorization'] = `Bearer ${token}`
    // }
    
    console.log('Request:', config.method.toUpperCase(), config.url)
    if (config.data) {
      console.log('Request Data:', config.data)
    }
    
    return config
  },
  error => {
    console.error('Request Error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    console.log('Response:', response.config.url, res)
    
    // 根据后端返回的code进行处理
    if (res.code !== 200 && res.code !== 201) {
      // 处理错误情况
      console.error('Business Error:', res.message || '请求失败')
      
      // 这里可以根据错误码进行特殊处理
      // if (res.code === 401) {
      //   // 未授权，跳转到登录页
      //   window.location.href = '/login'
      // }
      
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    return res
  },
  error => {
    console.error('Response Error:', error)
    
    let errorMessage = '网络错误，请稍后重试'
    
    if (error.response) {
      // 服务器返回错误
      switch (error.response.status) {
        case 400:
          errorMessage = '请求参数错误'
          break
        case 401:
          errorMessage = '未授权，请重新登录'
          break
        case 403:
          errorMessage = '拒绝访问'
          break
        case 404:
          errorMessage = '请求地址不存在'
          break
        case 500:
          errorMessage = '服务器内部错误'
          break
        default:
          errorMessage = `请求失败: ${error.response.status}`
      }
    } else if (error.request) {
      // 请求已发出但没有收到响应
      errorMessage = '网络连接失败，请检查网络设置'
    }
    
    // 可以在这里使用全局消息提示组件
    // ElMessage.error(errorMessage)
    
    return Promise.reject(error)
  }
)

// 导出常用的请求方法
export default {
  get(url, params) {
    return service.get(url, { params })
  },
  post(url, data) {
    return service.post(url, data)
  },
  put(url, data) {
    return service.put(url, data)
  },
  delete(url) {
    return service.delete(url)
  },
  patch(url, data) {
    return service.patch(url, data)
  }
}