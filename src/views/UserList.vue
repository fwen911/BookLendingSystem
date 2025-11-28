<template>
  <div class="user-list-container">
    <h1 class="page-title">用户管理</h1>
    
    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="search-filter">
        <input
          type="text"
          v-model="searchKeyword"
          placeholder="搜索姓名、手机号或学号"
          class="search-input"
          @input="handleSearch"
        />
        <select v-model="selectedStatus" class="status-select" @change="onStatusChange">
          <option value="">全部状态</option>
          <option value="1">正常</option>
          <option value="0">禁用</option>
        </select>
      </div>
      <button class="add-button" @click="showAddModal">新增用户</button>
    </div>
    
    <!-- 用户表格 -->
    <div class="table-container">
      <table class="user-table">
        <thead>
          <tr>
            <th width="15%">学号</th>
            <th width="15%">姓名</th>
            <th width="20%">手机号</th>
            <th width="15%">班级</th>
            <th width="10%">状态</th>
            <th width="15%">注册时间</th>
            <th width="10%">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in paginatedUsers" :key="user.id">
            <td>{{ user.studentId }}</td>
            <td>{{ user.name }}</td>
            <td>{{ user.phone }}</td>
            <td>{{ user.className }}</td>
            <td>
              <span class="status-badge" :class="user.status === 1 ? 'active' : 'disabled'">
                {{ user.status === 1 ? '正常' : '禁用' }}
              </span>
            </td>
            <td>{{ formatDate(user.createdTime) }}</td>
            <td>
              <div class="action-buttons">
                <button class="edit-button" @click="showEditModal(user)">编辑</button>
                <button 
                  class="status-button" 
                  @click="toggleUserStatus(user)"
                  :class="user.status === 1 ? 'disable-button' : 'enable-button'"
                >
                  {{ user.status === 1 ? '禁用' : '启用' }}
                </button>
                <button class="reset-button" @click="resetPassword(user)">重置密码</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      
      <!-- 空状态 -->
      <div class="empty-state" v-if="filteredUsers.length === 0">
        <p>暂无用户数据</p>
      </div>
    </div>
    
    <!-- 分页 -->
    <div class="pagination" v-if="filteredUsers.length > 0">
      <div class="pagination-info">
        共 {{ filteredUsers.length }} 条记录，第 {{ currentPage }} / {{ totalPages }} 页
      </div>
      <div class="pagination-controls">
        <button 
          class="page-button" 
          :disabled="currentPage === 1"
          @click="changePage(1)"
        >
          首页
        </button>
        <button 
          class="page-button" 
          :disabled="currentPage === 1"
          @click="changePage(currentPage - 1)"
        >
          上一页
        </button>
        <button 
          v-for="page in visiblePages" 
          :key="page"
          class="page-button"
          :class="{ active: page === currentPage }"
          @click="changePage(page)"
        >
          {{ page }}
        </button>
        <button 
          class="page-button" 
          :disabled="currentPage === totalPages"
          @click="changePage(currentPage + 1)"
        >
          下一页
        </button>
        <button 
          class="page-button" 
          :disabled="currentPage === totalPages"
          @click="changePage(totalPages)"
        >
          末页
        </button>
      </div>
    </div>
    
    <!-- 新增/编辑用户弹窗 -->
    <div class="modal" v-if="showModal">
      <div class="modal-content">
        <h2 class="modal-title">{{ isEditMode ? '编辑用户' : '新增用户' }}</h2>
        
        <form @submit.prevent="saveUser">
          <div class="form-group">
            <label for="studentId">学号 *</label>
            <input
              id="studentId"
              v-model="formData.studentId"
              type="text"
              required
              :disabled="isEditMode"
              placeholder="请输入学号"
            />
          </div>
          
          <div class="form-group">
            <label for="name">姓名 *</label>
            <input
              id="name"
              v-model="formData.name"
              type="text"
              required
              placeholder="请输入姓名"
            />
          </div>
          
          <div class="form-group">
            <label for="phone">手机号 *</label>
            <input
              id="phone"
              v-model="formData.phone"
              type="tel"
              required
              placeholder="请输入手机号"
              @input="validatePhone"
            />
            <div class="error-tip" v-if="phoneError">{{ phoneError }}</div>
          </div>
          
          <div class="form-group">
            <label for="className">班级</label>
            <input
              id="className"
              v-model="formData.className"
              type="text"
              placeholder="请输入班级"
            />
          </div>
          
          <div class="form-group" v-if="isEditMode">
            <label for="status">状态</label>
            <select id="status" v-model="formData.status">
              <option :value="1">正常</option>
              <option :value="0">禁用</option>
            </select>
          </div>
          
          <div class="modal-actions">
            <button type="button" class="cancel-button" @click="closeModal">取消</button>
            <button type="submit" class="save-button" :disabled="!isFormValid">保存</button>
          </div>
        </form>
      </div>
    </div>
    
    <!-- 状态变更确认弹窗 -->
    <div class="modal" v-if="showStatusConfirm">
      <div class="modal-content confirm-modal">
        <h3>确认操作</h3>
        <p>{{ confirmMessage }}</p>
        <div class="modal-actions">
          <button class="cancel-button" @click="cancelConfirm">取消</button>
          <button class="confirm-button" @click="confirmStatusChange">确认</button>
        </div>
      </div>
    </div>
    
    <!-- 密码重置弹窗 -->
    <div class="modal" v-if="showResetModal">
      <div class="modal-content confirm-modal">
        <h3>重置密码</h3>
        <p>确定要将用户 "{{ resetTarget?.name }}" 的密码重置为默认密码 "123456" 吗？</p>
        <div class="modal-actions">
          <button class="cancel-button" @click="cancelReset">取消</button>
          <button class="reset-confirm-button" @click="confirmResetPassword">确认重置</button>
        </div>
      </div>
    </div>
    
    <!-- Toast提示 -->
    <div class="toast" v-if="showToast">
      {{ toastMessage }}
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'UserList',
  data() {
    return {
      users: [],
      searchKeyword: '',
      selectedStatus: '',
      currentPage: 1,
      pageSize: 10,
      showModal: false,
      isEditMode: false,
      formData: {
        studentId: '',
        name: '',
        phone: '',
        className: '',
        status: 1
      },
      selectedUser: null,
      phoneError: '',
      showStatusConfirm: false,
      confirmMessage: '',
      statusChangeTarget: null,
      newStatus: null,
      showResetModal: false,
      resetTarget: null,
      showToast: false,
      toastMessage: '',
      searchTimer: null,
      loading: false
    }
  },
  computed: {
    // 筛选后的用户列表
    filteredUsers() {
      let filtered = [...this.users]
      
      // 按关键词搜索
      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase()
        filtered = filtered.filter(user => 
          user.name.toLowerCase().includes(keyword) ||
          user.phone.includes(keyword) ||
          user.studentId.toLowerCase().includes(keyword)
        )
      }
      
      // 按状态筛选
      if (this.selectedStatus !== '') {
        filtered = filtered.filter(user => user.status === parseInt(this.selectedStatus))
      }
      
      return filtered
    },
    
    // 总页数
    totalPages() {
      return Math.ceil(this.filteredUsers.length / this.pageSize)
    },
    
    // 分页后的用户列表
    paginatedUsers() {
      const startIndex = (this.currentPage - 1) * this.pageSize
      const endIndex = startIndex + this.pageSize
      return this.filteredUsers.slice(startIndex, endIndex)
    },
    
    // 可见页码
    visiblePages() {
      const total = this.totalPages
      const current = this.currentPage
      const pages = []
      
      if (total <= 7) {
        // 如果总页数不超过7页，显示所有页码
        for (let i = 1; i <= total; i++) {
          pages.push(i)
        }
      } else {
        // 如果总页数超过7页，显示当前页附近的页码
        pages.push(1)
        
        if (current > 4) {
          pages.push('...')
        }
        
        const start = Math.max(2, current - 2)
        const end = Math.min(total - 1, current + 2)
        
        for (let i = start; i <= end; i++) {
          pages.push(i)
        }
        
        if (current < total - 3) {
          pages.push('...')
        }
        
        pages.push(total)
      }
      
      return pages
    },
    
    // 表单验证
    isFormValid() {
      // 手机号格式验证
      const phoneRegex = /^1[3-9]\d{9}$/
      return (
        this.formData.studentId.trim() !== '' &&
        this.formData.name.trim() !== '' &&
        phoneRegex.test(this.formData.phone) &&
        !this.phoneError
      )
    }
  },
  mounted() {
    this.loadUsers()
  },
  methods: {
    // 加载用户数据
    async loadUsers() {
      this.loading = true
      try {
        const response = await axios.get('/api/user')
        if (response.data.code === 200) {
          this.users = response.data.data
        }
      } catch (error) {
        console.error('加载用户失败:', error)
        this.showToastMessage('加载用户失败')
      } finally {
        this.loading = false
      }
    },
    
    // 处理搜索输入
    handleSearch() {
      // 清除之前的定时器
      if (this.searchTimer) {
        clearTimeout(this.searchTimer)
      }
      
      // 300ms防抖
      this.searchTimer = setTimeout(() => {
        this.currentPage = 1 // 搜索后重置到第一页
      }, 300)
    },
    
    // 状态筛选变化处理
    onStatusChange() {
      this.currentPage = 1 // 切换状态后重置到第一页
    },
    
    // 切换页码
    changePage(page) {
      if (page >= 1 && page <= this.totalPages) {
        this.currentPage = page
      }
    },
    
    // 显示新增用户弹窗
    showAddModal() {
      this.isEditMode = false
      this.formData = {
        studentId: '',
        name: '',
        phone: '',
        className: '',
        status: 1
      }
      this.phoneError = ''
      this.showModal = true
    },
    
    // 显示编辑用户弹窗
    showEditModal(user) {
      this.isEditMode = true
      this.selectedUser = user
      this.formData = {
        studentId: user.studentId,
        name: user.name,
        phone: user.phone,
        className: user.className,
        status: user.status
      }
      this.phoneError = ''
      this.showModal = true
    },
    
    // 关闭弹窗
    closeModal() {
      this.showModal = false
      this.selectedUser = null
      this.phoneError = ''
    },
    
    // 验证手机号
    validatePhone() {
      const phoneRegex = /^1[3-9]\d{9}$/
      if (!this.formData.phone) {
        this.phoneError = '请输入手机号'
      } else if (!phoneRegex.test(this.formData.phone)) {
        this.phoneError = '手机号格式不正确'
      } else {
        this.phoneError = ''
      }
    },
    
    // 保存用户
    async saveUser() {
      try {
        let response
        
        if (this.isEditMode) {
          // 编辑用户
          response = await axios.put(`/api/user/${this.selectedUser.id}`, this.formData)
        } else {
          // 新增用户
          response = await axios.post('/api/user', this.formData)
        }
        
        if (response.data.code === 200) {
          this.showToastMessage(this.isEditMode ? '编辑成功' : '新增成功')
          this.closeModal()
          this.loadUsers() // 重新加载用户列表
        } else {
          this.showToastMessage(response.data.message || (this.isEditMode ? '编辑失败' : '新增失败'))
        }
      } catch (error) {
        console.error('保存用户失败:', error)
        this.showToastMessage('保存失败')
      }
    },
    
    // 切换用户状态
    toggleUserStatus(user) {
      this.statusChangeTarget = user
      this.newStatus = user.status === 1 ? 0 : 1
      this.confirmMessage = `确定要将用户 "${user.name}" ${user.status === 1 ? '禁用' : '启用'}吗？`
      this.showStatusConfirm = true
    },
    
    // 取消确认
    cancelConfirm() {
      this.showStatusConfirm = false
      this.statusChangeTarget = null
    },
    
    // 确认状态变更
    async confirmStatusChange() {
      try {
        const response = await axios.put(`/api/user/${this.statusChangeTarget.id}/status`, {
          status: this.newStatus
        })
        
        if (response.data.code === 200) {
          this.showToastMessage(this.newStatus === 1 ? '启用成功' : '禁用成功')
          this.cancelConfirm()
          this.loadUsers() // 重新加载用户列表
        } else {
          this.showToastMessage(response.data.message || '操作失败')
        }
      } catch (error) {
        console.error('状态变更失败:', error)
        this.showToastMessage('操作失败')
      }
    },
    
    // 重置密码
    resetPassword(user) {
      this.resetTarget = user
      this.showResetModal = true
    },
    
    // 取消重置
    cancelReset() {
      this.showResetModal = false
      this.resetTarget = null
    },
    
    // 确认重置密码
    async confirmResetPassword() {
      try {
        const response = await axios.put(`/api/user/${this.resetTarget.id}/password`, {
          newPassword: '123456'
        })
        
        if (response.data.code === 200) {
          this.showToastMessage('密码重置成功')
          this.cancelReset()
        } else {
          this.showToastMessage(response.data.message || '密码重置失败')
        }
      } catch (error) {
        console.error('密码重置失败:', error)
        this.showToastMessage('密码重置失败')
      }
    },
    
    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN')
    },
    
    // 显示Toast消息
    showToastMessage(message) {
      this.toastMessage = message
      this.showToast = true
      setTimeout(() => {
        this.showToast = false
      }, 3000)
    }
  }
}
</script>

<style scoped>
.user-list-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro', sans-serif;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #1d1d1f;
}

/* 操作栏 */
.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 16px;
}

.search-filter {
  display: flex;
  gap: 16px;
  align-items: center;
  flex: 1;
  min-width: 300px;
}

.search-input {
  padding: 10px 16px;
  border: 1px solid #d1d1d6;
  border-radius: 8px;
  font-size: 16px;
  outline: none;
  flex: 1;
  transition: border-color 0.2s ease;
}

.search-input:focus {
  border-color: #0071e3;
}

.status-select {
  padding: 10px 16px;
  border: 1px solid #d1d1d6;
  border-radius: 8px;
  font-size: 16px;
  outline: none;
  background-color: white;
  min-width: 120px;
  transition: border-color 0.2s ease;
}

.status-select:focus {
  border-color: #0071e3;
}

.add-button {
  padding: 10px 24px;
  background-color: #0071e3;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.add-button:hover {
  background-color: #0077ed;
}

/* 表格容器 */
.table-container {
  background-color: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
  margin-bottom: 24px;
}

.user-table {
  width: 100%;
  border-collapse: collapse;
}

.user-table th,
.user-table td {
  padding: 16px;
  text-align: left;
  border-bottom: 1px solid #f2f2f7;
}

.user-table th {
  background-color: #f5f5f7;
  font-weight: 600;
  color: #1d1d1f;
}

.user-table tr:hover {
  background-color: #fafafa;
}

/* 状态徽章 */
.status-badge {
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 14px;
  font-weight: 500;
}

.status-badge.active {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.status-badge.disabled {
  background-color: #f5f5f5;
  color: #8e8e93;
}

/* 操作按钮组 */
.action-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.edit-button,
.status-button,
.reset-button {
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  white-space: nowrap;
}

.edit-button {
  background-color: #f2f2f7;
  color: #1d1d1f;
}

.edit-button:hover {
  background-color: #e9e9ec;
}

.disable-button {
  background-color: #fff0f0;
  color: #ff3b30;
}

.disable-button:hover {
  background-color: #ffe0e0;
}

.enable-button {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.enable-button:hover {
  background-color: #dcedc8;
}

.reset-button {
  background-color: #e3f2fd;
  color: #1565c0;
}

.reset-button:hover {
  background-color: #bbdefb;
}

/* 空状态 */
.empty-state {
  padding: 60px 20px;
  text-align: center;
  color: #8e8e93;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  padding: 16px;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
}

.pagination-info {
  color: #6e6e73;
  font-size: 14px;
}

.pagination-controls {
  display: flex;
  gap: 8px;
}

.page-button {
  padding: 8px 12px;
  border: 1px solid #d1d1d6;
  border-radius: 6px;
  background-color: white;
  color: #1d1d1f;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.page-button:hover:not(:disabled) {
  background-color: #f2f2f7;
}

.page-button.active {
  background-color: #0071e3;
  color: white;
  border-color: #0071e3;
}

.page-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 模态框 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 12px;
  padding: 32px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-title {
  font-size: 20px;
  margin-bottom: 24px;
  color: #1d1d1f;
}

/* 表单样式 */
.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #1d1d1f;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 10px 16px;
  border: 1px solid #d1d1d6;
  border-radius: 8px;
  font-size: 16px;
  outline: none;
  transition: border-color 0.2s ease;
}

.form-group input:focus,
.form-group select:focus {
  border-color: #0071e3;
}

.form-group input:disabled {
  background-color: #f2f2f7;
  cursor: not-allowed;
}

.error-tip {
  color: #ff3b30;
  font-size: 14px;
  margin-top: 6px;
}

/* 确认弹窗 */
.confirm-modal {
  text-align: center;
}

/* 模态框操作按钮 */
.modal-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 32px;
}

.cancel-button,
.save-button,
.confirm-button,
.reset-confirm-button {
  padding: 10px 24px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.cancel-button {
  background-color: #f2f2f7;
  color: #1d1d1f;
}

.cancel-button:hover {
  background-color: #e9e9ec;
}

.save-button,
.confirm-button {
  background-color: #0071e3;
  color: white;
}

.save-button:hover,
.confirm-button:hover {
  background-color: #0077ed;
}

.save-button:disabled {
  background-color: #f2f2f7;
  color: #8e8e93;
  cursor: not-allowed;
}

.reset-confirm-button {
  background-color: #ff9800;
  color: white;
}

.reset-confirm-button:hover {
  background-color: #f57c00;
}

/* Toast提示 */
.toast {
  position: fixed;
  top: 20px;
  right: 20px;
  background-color: #1d1d1f;
  color: white;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 16px;
  z-index: 2000;
  animation: slideIn 0.3s ease;
}

@keyframes slideIn {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .action-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-filter {
    flex-direction: column;
    align-items: stretch;
    min-width: auto;
  }
  
  .user-table {
    display: block;
    overflow-x: auto;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .pagination {
    flex-direction: column;
    align-items: stretch;
  }
  
  .pagination-controls {
    justify-content: center;
    flex-wrap: wrap;
  }
}
</style>