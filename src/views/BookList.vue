<template>
  <div class="book-list-container">
    <h1 class="page-title">图书管理</h1>
    
    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="search-filter">
        <input
          type="text"
          v-model="searchKeyword"
          placeholder="搜索书名、作者或编码"
          class="search-input"
          @input="handleSearch"
        />
        <select v-model="selectedCategory" class="category-select" @change="onCategoryChange">
          <option value="">全部分类</option>
          <option v-for="category in categories" :key="category.id" :value="category.id">
            {{ category.name }}
          </option>
        </select>
      </div>
      <button class="add-button" @click="showAddModal">新增图书</button>
    </div>
    
    <!-- 图书表格 -->
    <div class="table-container">
      <table class="book-table">
        <thead>
          <tr>
            <th width="10%">图书编码</th>
            <th width="20%">书名</th>
            <th width="15%">作者</th>
            <th width="15%">分类</th>
            <th width="10%">状态</th>
            <th width="15%">创建时间</th>
            <th width="15%">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="book in paginatedBooks" :key="book.id">
            <td>{{ book.bookCode }}</td>
            <td>{{ book.title }}</td>
            <td>{{ book.author }}</td>
            <td>{{ getCategoryName(book.categoryId) }}</td>
            <td>
              <span class="status-badge" :class="book.status === 1 ? 'available' : 'borrowed'">
                {{ book.status === 1 ? '在馆' : '已借出' }}
              </span>
            </td>
            <td>{{ formatDate(book.createdTime) }}</td>
            <td>
              <div class="action-buttons">
                <button class="edit-button" @click="showEditModal(book)">编辑</button>
                <button 
                  class="delete-button" 
                  @click="confirmDelete(book)"
                  :disabled="book.status === 0"
                >
                  删除
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      
      <!-- 空状态 -->
      <div class="empty-state" v-if="filteredBooks.length === 0">
        <p>暂无图书数据</p>
      </div>
    </div>
    
    <!-- 分页 -->
    <div class="pagination" v-if="filteredBooks.length > 0">
      <div class="pagination-info">
        共 {{ filteredBooks.length }} 条记录，第 {{ currentPage }} / {{ totalPages }} 页
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
    
    <!-- 新增/编辑图书弹窗 -->
    <div class="modal" v-if="showModal">
      <div class="modal-content">
        <h2 class="modal-title">{{ isEditMode ? '编辑图书' : '新增图书' }}</h2>
        
        <form @submit.prevent="saveBook">
          <div class="form-group">
            <label for="bookCode">图书编码 *</label>
            <input
              id="bookCode"
              v-model="formData.bookCode"
              type="text"
              required
              :disabled="isEditMode"
              placeholder="请输入图书编码"
            />
          </div>
          
          <div class="form-group">
            <label for="title">书名 *</label>
            <input
              id="title"
              v-model="formData.title"
              type="text"
              required
              placeholder="请输入书名"
            />
          </div>
          
          <div class="form-group">
            <label for="author">作者 *</label>
            <input
              id="author"
              v-model="formData.author"
              type="text"
              required
              placeholder="请输入作者"
            />
          </div>
          
          <div class="form-group">
            <label for="categoryId">分类 *</label>
            <select id="categoryId" v-model="formData.categoryId" required>
              <option value="">请选择分类</option>
              <option v-for="category in categories" :key="category.id" :value="category.id">
                {{ category.name }}
              </option>
            </select>
          </div>
          
          <div class="form-group" v-if="isEditMode">
            <label for="status">状态</label>
            <select id="status" v-model="formData.status">
              <option :value="1">在馆</option>
              <option :value="0">已借出</option>
            </select>
          </div>
          
          <div class="modal-actions">
            <button type="button" class="cancel-button" @click="closeModal">取消</button>
            <button type="submit" class="save-button">保存</button>
          </div>
        </form>
      </div>
    </div>
    
    <!-- 删除确认弹窗 -->
    <div class="modal" v-if="showDeleteConfirm">
      <div class="modal-content confirm-modal">
        <h3>确认删除</h3>
        <p>确定要删除图书 "{{ selectedBook?.title }}" 吗？</p>
        <div class="modal-actions">
          <button class="cancel-button" @click="cancelDelete">取消</button>
          <button class="delete-button" @click="deleteBook">确认删除</button>
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
  name: 'BookList',
  data() {
    return {
      books: [],
      categories: [],
      searchKeyword: '',
      selectedCategory: '',
      currentPage: 1,
      pageSize: 10,
      showModal: false,
      isEditMode: false,
      formData: {
        bookCode: '',
        title: '',
        author: '',
        categoryId: '',
        status: 1
      },
      selectedBook: null,
      showDeleteConfirm: false,
      showToast: false,
      toastMessage: '',
      searchTimer: null,
      loading: false
    }
  },
  computed: {
    // 筛选后的图书列表
    filteredBooks() {
      let filtered = [...this.books]
      
      // 按关键词搜索
      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase()
        filtered = filtered.filter(book => 
          book.title.toLowerCase().includes(keyword) ||
          book.author.toLowerCase().includes(keyword) ||
          book.bookCode.toLowerCase().includes(keyword)
        )
      }
      
      // 按分类筛选
      if (this.selectedCategory) {
        filtered = filtered.filter(book => book.categoryId === parseInt(this.selectedCategory))
      }
      
      return filtered
    },
    
    // 总页数
    totalPages() {
      return Math.ceil(this.filteredBooks.length / this.pageSize)
    },
    
    // 分页后的图书列表
    paginatedBooks() {
      const startIndex = (this.currentPage - 1) * this.pageSize
      const endIndex = startIndex + this.pageSize
      return this.filteredBooks.slice(startIndex, endIndex)
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
    }
  },
  mounted() {
    this.loadBooks()
    this.loadCategories()
  },
  methods: {
    // 加载图书数据
    async loadBooks() {
      this.loading = true
      try {
        const response = await axios.get('/api/book')
        if (response.data.code === 200) {
          this.books = response.data.data
        }
      } catch (error) {
        console.error('加载图书失败:', error)
        this.showToastMessage('加载图书失败')
      } finally {
        this.loading = false
      }
    },
    
    // 加载分类数据
    async loadCategories() {
      try {
        const response = await axios.get('/api/category')
        if (response.data.code === 200) {
          this.categories = response.data.data
        }
      } catch (error) {
        console.error('加载分类失败:', error)
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
    
    // 分类变化处理
    onCategoryChange() {
      this.currentPage = 1 // 切换分类后重置到第一页
    },
    
    // 切换页码
    changePage(page) {
      if (page >= 1 && page <= this.totalPages) {
        this.currentPage = page
      }
    },
    
    // 显示新增图书弹窗
    showAddModal() {
      this.isEditMode = false
      this.formData = {
        bookCode: '',
        title: '',
        author: '',
        categoryId: '',
        status: 1
      }
      this.showModal = true
    },
    
    // 显示编辑图书弹窗
    showEditModal(book) {
      this.isEditMode = true
      this.selectedBook = book
      this.formData = {
        bookCode: book.bookCode,
        title: book.title,
        author: book.author,
        categoryId: book.categoryId,
        status: book.status
      }
      this.showModal = true
    },
    
    // 关闭弹窗
    closeModal() {
      this.showModal = false
      this.selectedBook = null
    },
    
    // 保存图书
    async saveBook() {
      try {
        let response
        
        if (this.isEditMode) {
          // 编辑图书
          response = await axios.put(`/api/book/${this.selectedBook.id}`, this.formData)
        } else {
          // 新增图书
          response = await axios.post('/api/book', this.formData)
        }
        
        if (response.data.code === 200) {
          this.showToastMessage(this.isEditMode ? '编辑成功' : '新增成功')
          this.closeModal()
          this.loadBooks() // 重新加载图书列表
        } else {
          this.showToastMessage(response.data.message || (this.isEditMode ? '编辑失败' : '新增失败'))
        }
      } catch (error) {
        console.error('保存图书失败:', error)
        this.showToastMessage('保存失败')
      }
    },
    
    // 确认删除
    confirmDelete(book) {
      this.selectedBook = book
      this.showDeleteConfirm = true
    },
    
    // 取消删除
    cancelDelete() {
      this.showDeleteConfirm = false
      this.selectedBook = null
    },
    
    // 删除图书
    async deleteBook() {
      try {
        const response = await axios.delete(`/api/book/${this.selectedBook.id}`)
        
        if (response.data.code === 200) {
          this.showToastMessage('删除成功')
          this.cancelDelete()
          this.loadBooks() // 重新加载图书列表
        } else {
          this.showToastMessage(response.data.message || '删除失败')
        }
      } catch (error) {
        console.error('删除图书失败:', error)
        this.showToastMessage('删除失败')
      }
    },
    
    // 获取分类名称
    getCategoryName(categoryId) {
      const category = this.categories.find(c => c.id === categoryId)
      return category ? category.name : '未知'
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
.book-list-container {
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

.category-select {
  padding: 10px 16px;
  border: 1px solid #d1d1d6;
  border-radius: 8px;
  font-size: 16px;
  outline: none;
  background-color: white;
  min-width: 150px;
  transition: border-color 0.2s ease;
}

.category-select:focus {
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

.book-table {
  width: 100%;
  border-collapse: collapse;
}

.book-table th,
.book-table td {
  padding: 16px;
  text-align: left;
  border-bottom: 1px solid #f2f2f7;
}

.book-table th {
  background-color: #f5f5f7;
  font-weight: 600;
  color: #1d1d1f;
}

.book-table tr:hover {
  background-color: #fafafa;
}

/* 状态徽章 */
.status-badge {
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 14px;
  font-weight: 500;
}

.status-badge.available {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.status-badge.borrowed {
  background-color: #fce4ec;
  color: #c62828;
}

/* 操作按钮组 */
.action-buttons {
  display: flex;
  gap: 8px;
}

.edit-button,
.delete-button {
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.edit-button {
  background-color: #f2f2f7;
  color: #1d1d1f;
}

.edit-button:hover {
  background-color: #e9e9ec;
}

.delete-button {
  background-color: #fff0f0;
  color: #ff3b30;
}

.delete-button:hover:not(:disabled) {
  background-color: #ffe0e0;
}

.delete-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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
.save-button {
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

.save-button {
  background-color: #0071e3;
  color: white;
}

.save-button:hover {
  background-color: #0077ed;
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
  
  .book-table {
    display: block;
    overflow-x: auto;
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