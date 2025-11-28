/**
 * 图书相关API服务
 */
import api from '../axios'

/**
 * 获取图书列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} params.title - 书名（可选）
 * @param {string} params.author - 作者（可选）
 * @param {number} params.categoryId - 分类ID（可选）
 * @param {number} params.status - 状态（可选，0:已借出, 1:在馆）
 * @returns {Promise} 响应结果
 */
export const getBookList = (params) => {
  return api.get('/api/book', { params })
}

/**
 * 获取图书详情
 * @param {number} id - 图书ID
 * @returns {Promise} 响应结果
 */
export const getBookDetail = (id) => {
  return api.get(`/api/book/${id}`)
}

/**
 * 根据编码查询图书
 * @param {string} code - 图书编码
 * @returns {Promise} 响应结果
 */
export const getBookByCode = (code) => {
  return api.get(`/api/book/code/${code}`)
}

/**
 * 获取可用图书列表
 * @param {Object} params - 查询参数
 * @returns {Promise} 响应结果
 */
export const getAvailableBooks = (params) => {
  return api.get('/api/book/available', { params })
}

/**
 * 新增图书
 * @param {Object} book - 图书信息
 * @returns {Promise} 响应结果
 */
export const addBook = (book) => {
  return api.post('/book', book)
}

/**
 * 更新图书
 * @param {number} id - 图书ID
 * @param {Object} book - 图书信息
 * @returns {Promise} 响应结果
 */
export const updateBook = (id, book) => {
  return api.put(`/book/${id}`, book)
}

/**
 * 删除图书
 * @param {number} id - 图书ID
 * @returns {Promise} 响应结果
 */
export const deleteBook = (id) => {
  return api.delete(`/book/${id}`)
}

/**
 * 批量删除图书
 * @param {Array} ids - 图书ID数组
 * @returns {Promise} 响应结果
 */
export const batchDeleteBooks = (ids) => {
  return api.delete('/book/batch', { data: ids })
}

export default {
  getBookList,
  getBookDetail,
  getBookByCode,
  getAvailableBooks,
  addBook,
  updateBook,
  deleteBook,
  batchDeleteBooks
}