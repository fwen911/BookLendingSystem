/**
 * 分类相关API服务
 */
import api from '../axios'

/**
 * 获取分类列表
 * @returns {Promise} 响应结果
 */
export const getCategoryList = () => {
  return api.get('/api/category')
}

/**
 * 获取分类详情
 * @param {number} id - 分类ID
 * @returns {Promise} 响应结果
 */
export const getCategoryDetail = (id) => {
  return api.get(`/api/category/${id}`)
}

/**
 * 新增分类
 * @param {Object} category - 分类信息
 * @param {string} category.name - 分类名称
 * @param {string} category.description - 分类描述（可选）
 * @returns {Promise} 响应结果
 */
export const addCategory = (category) => {
  return api.post('/api/category', category)
}

/**
 * 更新分类
 * @param {number} id - 分类ID
 * @param {Object} category - 分类信息
 * @returns {Promise} 响应结果
 */
export const updateCategory = (id, category) => {
  return api.put(`/api/category/${id}`, category)
}

/**
 * 删除分类
 * @param {number} id - 分类ID
 * @returns {Promise} 响应结果
 */
export const deleteCategory = (id) => {
  return api.delete(`/api/category/${id}`)
}

/**
 * 批量删除分类
 * @param {Array} ids - 分类ID数组
 * @returns {Promise} 响应结果
 */
export const batchDeleteCategories = (ids) => {
  return api.delete('/category/batch', { data: ids })
}

/**
 * 获取分类统计信息（包含每个分类下的图书数量）
 * @returns {Promise} 响应结果
 */
export const getCategoryStatistics = () => {
  return api.get('/category/statistics')
}

export default {
  getCategoryList,
  getCategoryDetail,
  addCategory,
  updateCategory,
  deleteCategory,
  batchDeleteCategories,
  getCategoryStatistics
}