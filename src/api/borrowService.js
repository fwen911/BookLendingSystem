/**
 * 借阅相关API服务
 */
import api from '../axios'

/**
 * 借阅图书
 * @param {Object} data - 借阅信息
 * @param {number} data.bookId - 图书ID
 * @param {string} data.phone - 借阅人手机号
 * @returns {Promise} 响应结果
 */
export const borrowBook = (data) => {
  return api.post('/api/borrow', data)
}

/**
 * 归还图书
 * @param {Object} data - 归还信息
 * @param {number} data.bookId - 图书ID
 * @returns {Promise} 响应结果
 */
export const returnBook = (data) => {
  return api.put('/api/borrow/return', data)
}

/**
 * 续借图书
 * @param {Object} data - 续借信息
 * @param {number} data.recordId - 借阅记录ID
 * @returns {Promise} 响应结果
 */
export const renewBook = (data) => {
  return api.put('/api/borrow/renew', data)
}

/**
 * 获取借阅记录列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} params.userName - 用户名（可选）
 * @param {string} params.bookTitle - 书名（可选）
 * @param {number} params.status - 状态（可选，0:未归还, 1:已归还, 2:已续借）
 * @returns {Promise} 响应结果
 */
export const getBorrowList = (params) => {
  return api.get('/api/borrow', { params })
}

/**
 * 获取当前借阅记录（按图书ID）
 * @param {number} bookId - 图书ID
 * @returns {Promise} 响应结果
 */
export const getCurrentBorrowRecord = (bookId) => {
  return api.get(`/borrow/current/${bookId}`)
}

/**
 * 获取用户的借阅记录
 * @param {string} phone - 用户手机号
 * @param {Object} params - 其他查询参数
 * @returns {Promise} 响应结果
 */
export const getUserBorrowRecords = (phone, params = {}) => {
  return api.get(`/borrow/user/${phone}`, { params })
}

/**
 * 获取借阅统计数据
 * @returns {Promise} 响应结果
 */
export const getBorrowStatistics = () => {
  return api.get('/borrow/statistics')
}

/**
 * 批量归还图书
 * @param {Array} bookIds - 图书ID数组
 * @returns {Promise} 响应结果
 */
export const batchReturnBooks = (bookIds) => {
  return api.put('/borrow/batch/return', { bookIds })
}

/**
 * 获取逾期借阅记录
 * @param {Object} params - 查询参数
 * @returns {Promise} 响应结果
 */
export const getOverdueRecords = (params = {}) => {
  return api.get('/borrow/overdue', { params })
}

export default {
  borrowBook,
  returnBook,
  renewBook,
  getBorrowList,
  getCurrentBorrowRecord,
  getUserBorrowRecords,
  getBorrowStatistics,
  batchReturnBooks,
  getOverdueRecords
}