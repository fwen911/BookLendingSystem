/**
 * 全局工具类
 * 提供项目中常用的工具函数
 */

// 消息提示函数
export const message = {
  // 成功消息
  success(text) {
    if (window.$message) {
      window.$message.success(text)
    } else {
      console.log('Success:', text)
      alert('成功: ' + text)
    }
  },
  
  // 错误消息
  error(text) {
    if (window.$message) {
      window.$message.error(text)
    } else {
      console.error('Error:', text)
      alert('错误: ' + text)
    }
  },
  
  // 警告消息
  warning(text) {
    if (window.$message) {
      window.$message.warning(text)
    } else {
      console.warn('Warning:', text)
      alert('警告: ' + text)
    }
  },
  
  // 信息消息
  info(text) {
    if (window.$message) {
      window.$message.info(text)
    } else {
      console.info('Info:', text)
    }
  }
}

// 日期格式化
export const formatDate = {
  // 格式化日期 YYYY-MM-DD
  toDate(date) {
    if (!date) return ''
    const d = new Date(date)
    const year = d.getFullYear()
    const month = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  },
  
  // 格式化日期时间 YYYY-MM-DD HH:mm:ss
  toDateTime(date) {
    if (!date) return ''
    const d = new Date(date)
    const dateStr = this.toDate(d)
    const hours = String(d.getHours()).padStart(2, '0')
    const minutes = String(d.getMinutes()).padStart(2, '0')
    const seconds = String(d.getSeconds()).padStart(2, '0')
    return `${dateStr} ${hours}:${minutes}:${seconds}`
  },
  
  // 格式化日期为相对时间（如：3分钟前）
  toRelativeTime(date) {
    if (!date) return ''
    const now = new Date()
    const d = new Date(date)
    const diff = now - d
    
    const minute = 60 * 1000
    const hour = minute * 60
    const day = hour * 24
    const week = day * 7
    const month = day * 30
    const year = day * 365
    
    if (diff < minute) {
      return '刚刚'
    } else if (diff < hour) {
      return Math.floor(diff / minute) + '分钟前'
    } else if (diff < day) {
      return Math.floor(diff / hour) + '小时前'
    } else if (diff < week) {
      return Math.floor(diff / day) + '天前'
    } else if (diff < month) {
      return Math.floor(diff / week) + '周前'
    } else if (diff < year) {
      return Math.floor(diff / month) + '个月前'
    } else {
      return Math.floor(diff / year) + '年前'
    }
  }
}

// 验证工具
export const validate = {
  // 验证手机号
  isPhone(phone) {
    return /^1[3-9]\d{9}$/.test(phone)
  },
  
  // 验证邮箱
  isEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)
  },
  
  // 验证非空
  isNotEmpty(value) {
    return value !== null && value !== undefined && value.trim() !== ''
  },
  
  // 验证数字范围
  isNumberInRange(value, min, max) {
    const num = Number(value)
    return !isNaN(num) && num >= min && num <= max
  }
}

// 本地存储工具
export const storage = {
  // 设置本地存储
  set(key, value) {
    try {
      const jsonValue = JSON.stringify(value)
      localStorage.setItem(key, jsonValue)
    } catch (error) {
      console.error('Error saving to localStorage:', error)
    }
  },
  
  // 获取本地存储
  get(key, defaultValue = null) {
    try {
      const item = localStorage.getItem(key)
      return item ? JSON.parse(item) : defaultValue
    } catch (error) {
      console.error('Error reading from localStorage:', error)
      return defaultValue
    }
  },
  
  // 删除本地存储
  remove(key) {
    try {
      localStorage.removeItem(key)
    } catch (error) {
      console.error('Error removing from localStorage:', error)
    }
  },
  
  // 清空本地存储
  clear() {
    try {
      localStorage.clear()
    } catch (error) {
      console.error('Error clearing localStorage:', error)
    }
  }
}

// 数组操作工具
export const arrayUtils = {
  // 数组去重
  unique(array, key) {
    if (!key) {
      return [...new Set(array)]
    }
    const seen = new Set()
    return array.filter(item => {
      const val = item[key]
      if (seen.has(val)) {
        return false
      }
      seen.add(val)
      return true
    })
  },
  
  // 数组分组
  groupBy(array, key) {
    return array.reduce((result, item) => {
      const group = item[key]
      if (!result[group]) {
        result[group] = []
      }
      result[group].push(item)
      return result
    }, {})
  }
}

// 延迟函数
export const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms))

// 防抖函数
export const debounce = (func, wait) => {
  let timeout
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout)
      func(...args)
    }
    clearTimeout(timeout)
    timeout = setTimeout(later, wait)
  }
}

// 节流函数
export const throttle = (func, limit) => {
  let inThrottle
  return function(...args) {
    if (!inThrottle) {
      func.apply(this, args)
      inThrottle = true
      setTimeout(() => inThrottle = false, limit)
    }
  }
}

// 格式化文件大小
export const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 生成随机ID
export const generateId = () => {
  return Date.now().toString(36) + Math.random().toString(36).substr(2)
}

// 复制文本到剪贴板
export const copyToClipboard = async (text) => {
  try {
    if (navigator.clipboard && window.isSecureContext) {
      await navigator.clipboard.writeText(text)
      return true
    } else {
      // 降级方案
      const textArea = document.createElement('textarea')
      textArea.value = text
      textArea.style.position = 'fixed'
      textArea.style.left = '-999999px'
      textArea.style.top = '-999999px'
      document.body.appendChild(textArea)
      textArea.focus()
      textArea.select()
      const result = document.execCommand('copy')
      textArea.remove()
      return result
    }
  } catch (error) {
    console.error('Failed to copy text:', error)
    return false
  }
}

// 深拷贝对象
export const deepClone = (obj) => {
  if (obj === null || typeof obj !== 'object') return obj
  if (obj instanceof Date) return new Date(obj.getTime())
  if (obj instanceof Array) return obj.map(item => deepClone(item))
  if (obj instanceof Object) {
    const clonedObj = {}
    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        clonedObj[key] = deepClone(obj[key])
      }
    }
    return clonedObj
  }
}

export default {
  message,
  formatDate,
  validate,
  storage,
  arrayUtils,
  delay,
  debounce,
  throttle,
  formatFileSize,
  generateId,
  copyToClipboard,
  deepClone
}