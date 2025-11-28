/**
 * 用户相关API服务
 */
import api from '../axios'

/**
 * 获取用户列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} params.name - 用户名（可选）
 * @param {string} params.phone - 手机号（可选）
 * @param {number} params.status - 状态（可选，0:禁用, 1:启用）
 * @returns {Promise} 响应结果
 */
export const getUserList = (params) => {
  return api.get('/api/user', { params })
}

/**
 * 获取用户详情
 * @param {number} id - 用户ID
 * @returns {Promise} 响应结果
 */
export const getUserDetail = (id) => {
  return api.get(`/api/user/${id}`)
}

/**
 * 根据手机号查询用户
 * @param {string} phone - 手机号
 * @returns {Promise} 响应结果
 */
export const getUserByPhone = (phone) => {
  return api.get(`/api/user/phone/${phone}`)
}

/**
 * 新增用户
 * @param {Object} user - 用户信息
 * @returns {Promise} 响应结果
 */
export const addUser = (user) => {
  return api.post('/api/user', user)
}

/**
 * 更新用户
 * @param {number} id - 用户ID
 * @param {Object} user - 用户信息
 * @returns {Promise} 响应结果
 */
export const updateUser = (id, user) => {
  return api.put(`/api/user/${id}`, user)
}

/**
 * 删除用户
 * @param {number} id - 用户ID
 * @returns {Promise} 响应结果
 */
export const deleteUser = (id) => {
  return api.delete(`/api/user/${id}`)
}

/**
 * 批量删除用户
 * @param {Array} ids - 用户ID数组
 * @returns {Promise} 响应结果
 */
export const batchDeleteUsers = (ids) => {
  return api.delete('/api/user/batch', { data: ids })
}

/**
 * 更新用户状态
 * @param {number} id - 用户ID
 * @param {number} status - 状态（0:禁用, 1:启用）
 * @returns {Promise} 响应结果
 */
export const updateUserStatus = (id, status) => {
  return api.put(`/api/user/${id}/status`, { status })
}

/**
 * 修改用户密码
 * @param {number} id - 用户ID
 * @param {Object} data - 密码信息
 * @param {string} data.oldPassword - 旧密码
 * @param {string} data.newPassword - 新密码
 * @returns {Promise} 响应结果
 */
export const updateUserPassword = (id, data) => {
  return api.put(`/api/user/${id}/password`, data)
}

/**
 * 重置用户密码
 * @param {number} id - 用户ID
 * @returns {Promise} 响应结果
 */
export const resetUserPassword = (id) => {
  return api.put(`/api/user/${id}/resetPassword`)
}

export default {
  getUserList,
  getUserDetail,
  getUserByPhone,
  addUser,
  updateUser,
  deleteUser,
  batchDeleteUsers,
  updateUserStatus,
  updateUserPassword,
  resetUserPassword
}