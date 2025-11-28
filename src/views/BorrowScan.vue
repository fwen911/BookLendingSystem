<template>
  <div class="borrow-scan-container">
    <h1 class="page-title">借阅扫码</h1>
    
    <div class="scan-section">
      <!-- 扫描模式切换 -->
      <div class="mode-switch">
        <button 
          class="mode-button" 
          :class="{ active: currentMode === 'scan' }"
          @click="switchMode('scan')"
        >
          扫码模式
        </button>
        <button 
          class="mode-button" 
          :class="{ active: currentMode === 'manual' }"
          @click="switchMode('manual')"
        >
          手动输入
        </button>
      </div>
      
      <!-- 扫码区域 -->
      <div class="scan-container" v-show="currentMode === 'scan'">
        <div class="scan-instructions">
          <p>将图书二维码对准摄像头进行扫描</p>
          <div class="scan-tip">首次使用需授权摄像头访问权限</div>
        </div>
        
        <!-- 模拟扫码区域 -->
        <div class="mock-scan-area" @click="simulateScan">
          <div class="scan-box">
            <div class="scan-line"></div>
            <div class="scan-placeholder">📷 点击此处模拟扫码</div>
          </div>
        </div>
      </div>
      
      <!-- 手动输入区域 -->
      <div class="manual-input-container" v-show="currentMode === 'manual'">
        <form @submit.prevent="handleManualSubmit">
          <div class="form-group">
            <label for="bookCode">图书编码</label>
            <input
              id="bookCode"
              v-model="manualBookCode"
              type="text"
              placeholder="请输入图书编码"
              required
              @input="handleInput"
            />
          </div>
          <button type="submit" class="submit-button">确认</button>
        </form>
      </div>
    </div>
    
    <!-- 图书信息区域 -->
    <div class="book-info-section" v-if="currentBook">
      <div class="section-header">
        <h3>图书信息</h3>
        <button class="clear-button" @click="clearBookInfo">清空</button>
      </div>
      
      <div class="book-details">
        <div class="book-cover">
          <img :src="'/cover.png'" alt="{{ currentBook.title }}" />
        </div>
        <div class="book-info-content">
          <h4 class="book-title">{{ currentBook.title }}</h4>
          <p class="book-author">作者：{{ currentBook.author }}</p>
          <p class="book-code">编码：{{ currentBook.bookCode }}</p>
          <p class="book-status">
            状态：<span :class="'status-' + (currentBook.status === 1 ? 'available' : 'borrowed')">
              {{ currentBook.status === 1 ? '在馆' : '已借出' }}
            </span>
          </p>
        </div>
      </div>
      
      <!-- 操作按钮 -->
      <div class="action-buttons">
        <button 
          class="borrow-button"
          @click="showBorrowModal"
          :disabled="currentBook.status !== 1"
        >
          借阅
        </button>
        <button 
          class="return-button"
          @click="showReturnModal"
          :disabled="currentBook.status !== 0"
        >
          归还
        </button>
      </div>
    </div>
    
    <!-- 借阅弹窗 -->
    <div class="modal" v-if="showModal === 'borrow'">
      <div class="modal-content">
        <h3>借阅图书</h3>
        <p>书名：{{ currentBook?.title }}</p>
        <div class="modal-form">
          <div class="form-group">
            <label for="borrowPhone">手机号</label>
            <input
              id="borrowPhone"
              v-model="borrowForm.phone"
              type="tel"
              placeholder="请输入借阅人手机号"
              @input="validatePhone"
            />
            <div class="error-tip" v-if="phoneError">{{ phoneError }}</div>
          </div>
        </div>
        <div class="modal-actions">
          <button class="cancel-button" @click="closeModal">取消</button>
          <button 
            class="confirm-button"
            @click="confirmBorrow"
            :disabled="!isPhoneValid"
          >
            确认借阅
          </button>
        </div>
      </div>
    </div>
    
    <!-- 归还弹窗 -->
    <div class="modal" v-if="showModal === 'return'">
      <div class="modal-content">
        <h3>归还图书</h3>
        <p>书名：{{ currentBook?.title }}</p>
        <div class="borrower-info" v-if="borrowRecord">
          <p><strong>借阅人：</strong>{{ borrowRecord.userName }}</p>
          <p><strong>借阅时间：</strong>{{ formatDate(borrowRecord.borrowTime) }}</p>
          <p><strong>到期时间：</strong>{{ formatDate(borrowRecord.dueTime) }}</p>
        </div>
        <div class="modal-actions">
          <button class="cancel-button" @click="closeModal">取消</button>
          <button class="confirm-button" @click="confirmReturn">确认归还</button>
        </div>
      </div>
    </div>
    
    <!-- 操作历史 -->
    <div class="history-section">
      <h3>最近操作</h3>
      <div class="history-list">
        <div 
          v-for="(item, index) in operationHistory" 
          :key="index"
          class="history-item"
          :class="item.type"
        >
          <div class="history-content">
            <div class="history-book">{{ item.bookTitle }}</div>
            <div class="history-info">
              {{ item.type === 'borrow' ? '借阅' : '归还' }} · 
              {{ item.userPhone }} · 
              {{ formatDateTime(item.timestamp) }}
            </div>
          </div>
          <div class="history-status" :class="item.status">
            {{ item.status === 'success' ? '成功' : '失败' }}
          </div>
        </div>
      </div>
      <div class="empty-history" v-if="operationHistory.length === 0">
        暂无操作记录
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
  name: 'BorrowScan',
  data() {
    return {
      currentMode: 'scan', // 'scan' 或 'manual'
      manualBookCode: '',
      currentBook: null,
      borrowRecord: null,
      showModal: null, // 'borrow', 'return' 或 null
      borrowForm: {
        phone: ''
      },
      phoneError: '',
      isPhoneValid: false,
      operationHistory: [],
      showToast: false,
      toastMessage: '',
      scanResult: null
    }
  },
  mounted() {
    // 初始化时可以加载最近的操作历史
    this.loadRecentHistory()
  },
  methods: {
    // 切换模式
    switchMode(mode) {
      this.currentMode = mode
      this.clearBookInfo()
    },
    
    // 模拟扫码（实际项目中需要集成真正的扫码库）
    simulateScan() {
      // 模拟生成一个随机的图书编码
      const mockCodes = ['BK001', 'BK002', 'BK003', 'BK004', 'BK005']
      const randomCode = mockCodes[Math.floor(Math.random() * mockCodes.length)]
      this.processBookCode(randomCode)
    },
    
    // 处理手动输入
    handleManualSubmit() {
      if (this.manualBookCode.trim()) {
        this.processBookCode(this.manualBookCode.trim())
      }
    },
    
    // 处理输入事件
    handleInput() {
      // 可以添加实时验证逻辑
    },
    
    // 处理图书编码
    async processBookCode(code) {
      try {
        const response = await axios.get(`/api/book/code/${code}`)
        if (response.data.code === 200) {
          this.currentBook = response.data.data
          
          // 如果图书已借出，获取借阅记录
          if (this.currentBook.status === 0) {
            await this.loadBorrowRecord(this.currentBook.id)
          }
        } else {
          this.showToastMessage('未找到对应图书')
        }
      } catch (error) {
        console.error('查询图书失败:', error)
        this.showToastMessage('查询图书失败')
      }
    },
    
    // 加载借阅记录
    async loadBorrowRecord(bookId) {
      try {
        const response = await axios.get(`/api/borrow/current/${bookId}`)
        if (response.data.code === 200) {
          this.borrowRecord = response.data.data
        }
      } catch (error) {
        console.error('加载借阅记录失败:', error)
      }
    },
    
    // 清除图书信息
    clearBookInfo() {
      this.currentBook = null
      this.borrowRecord = null
      this.manualBookCode = ''
      this.showModal = null
    },
    
    // 显示借阅弹窗
    showBorrowModal() {
      this.showModal = 'borrow'
      this.borrowForm.phone = ''
      this.phoneError = ''
      this.isPhoneValid = false
    },
    
    // 显示归还弹窗
    showReturnModal() {
      this.showModal = 'return'
    },
    
    // 关闭弹窗
    closeModal() {
      this.showModal = null
    },
    
    // 验证手机号
    validatePhone() {
      const phoneRegex = /^1[3-9]\d{9}$/
      if (!this.borrowForm.phone) {
        this.phoneError = '请输入手机号'
        this.isPhoneValid = false
      } else if (!phoneRegex.test(this.borrowForm.phone)) {
        this.phoneError = '手机号格式不正确'
        this.isPhoneValid = false
      } else {
        this.phoneError = ''
        this.isPhoneValid = true
      }
    },
    
    // 确认借阅
    async confirmBorrow() {
      try {
        const response = await axios.post('/api/borrow', {
          bookId: this.currentBook.id,
          phone: this.borrowForm.phone
        })
        
        if (response.data.code === 200) {
          this.showToastMessage('借阅成功')
          this.addHistoryRecord('borrow', 'success')
          
          // 更新图书状态
          this.currentBook.status = 0
          this.loadBorrowRecord(this.currentBook.id)
        } else {
          this.showToastMessage(response.data.message || '借阅失败')
          this.addHistoryRecord('borrow', 'error')
        }
      } catch (error) {
        console.error('借阅失败:', error)
        this.showToastMessage('借阅失败')
        this.addHistoryRecord('borrow', 'error')
      } finally {
        this.closeModal()
      }
    },
    
    // 确认归还
    async confirmReturn() {
      try {
        const response = await axios.put('/api/borrow/return', {
          bookId: this.currentBook.id
        })
        
        if (response.data.code === 200) {
          this.showToastMessage('归还成功')
          this.addHistoryRecord('return', 'success')
          
          // 更新图书状态
          this.currentBook.status = 1
          this.borrowRecord = null
        } else {
          this.showToastMessage(response.data.message || '归还失败')
          this.addHistoryRecord('return', 'error')
        }
      } catch (error) {
        console.error('归还失败:', error)
        this.showToastMessage('归还失败')
        this.addHistoryRecord('return', 'error')
      } finally {
        this.closeModal()
      }
    },
    
    // 添加历史记录
    addHistoryRecord(type, status) {
      const record = {
        type,
        status,
        bookTitle: this.currentBook.title,
        userPhone: type === 'borrow' ? this.borrowForm.phone : this.borrowRecord?.userPhone,
        timestamp: new Date()
      }
      
      this.operationHistory.unshift(record)
      
      // 限制历史记录数量
      if (this.operationHistory.length > 10) {
        this.operationHistory.pop()
      }
    },
    
    // 加载最近历史
    loadRecentHistory() {
      // 这里可以从服务器加载最近的操作历史
      // 现在使用模拟数据
      this.operationHistory = [
        {
          type: 'borrow',
          status: 'success',
          bookTitle: 'JavaScript高级程序设计',
          userPhone: '13800138000',
          timestamp: new Date(Date.now() - 3600000)
        },
        {
          type: 'return',
          status: 'success',
          bookTitle: '设计模式',
          userPhone: '13900139000',
          timestamp: new Date(Date.now() - 7200000)
        }
      ]
    },
    
    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return date.toLocaleDateString('zh-CN')
    },
    
    // 格式化日期时间
    formatDateTime(date) {
      if (!date) return ''
      const d = new Date(date)
      return d.toLocaleString('zh-CN')
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
.borrow-scan-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro', sans-serif;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #1d1d1f;
}

/* 扫描区域 */
.scan-section {
  background-color: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
}

/* 模式切换 */
.mode-switch {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  border-bottom: 1px solid #f2f2f7;
  padding-bottom: 16px;
}

.mode-button {
  padding: 10px 24px;
  border: none;
  background-color: transparent;
  color: #6e6e73;
  font-size: 16px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s ease;
  position: relative;
}

.mode-button:hover {
  background-color: #f2f2f7;
  color: #1d1d1f;
}

.mode-button.active {
  color: #0071e3;
  font-weight: 500;
}

.mode-button.active::after {
  content: '';
  position: absolute;
  bottom: -17px;
  left: 0;
  right: 0;
  height: 3px;
  background-color: #0071e3;
  border-radius: 2px;
}

/* 扫码容器 */
.scan-container {
  text-align: center;
}

.scan-instructions {
  margin-bottom: 24px;
}

.scan-instructions p {
  font-size: 16px;
  color: #1d1d1f;
  margin-bottom: 8px;
}

.scan-tip {
  font-size: 14px;
  color: #8e8e93;
}

/* 模拟扫码区域 */
.mock-scan-area {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.scan-box {
  width: 300px;
  height: 300px;
  border: 2px solid #0071e3;
  border-radius: 12px;
  position: relative;
  overflow: hidden;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f7;
}

.scan-box:hover {
  border-color: #0077ed;
}

.scan-line {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background-color: #0071e3;
  animation: scan 2s linear infinite;
}

.scan-placeholder {
  font-size: 18px;
  color: #8e8e93;
}

@keyframes scan {
  0% {
    top: 0;
  }
  50% {
    top: 100%;
  }
  100% {
    top: 0;
  }
}

/* 手动输入容器 */
.manual-input-container {
  max-width: 400px;
  margin: 0 auto;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #1d1d1f;
}

.form-group input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #d1d1d6;
  border-radius: 8px;
  font-size: 16px;
  outline: none;
  transition: border-color 0.2s ease;
}

.form-group input:focus {
  border-color: #0071e3;
}

.submit-button {
  width: 100%;
  padding: 12px;
  background-color: #0071e3;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.submit-button:hover {
  background-color: #0077ed;
}

/* 图书信息区域 */
.book-info-section {
  background-color: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
}

.clear-button {
  padding: 6px 16px;
  border: none;
  background-color: #f2f2f7;
  color: #6e6e73;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.clear-button:hover {
  background-color: #e9e9ec;
}

/* 图书详情 */
.book-details {
  display: flex;
  gap: 20px;
  margin-bottom: 24px;
}

.book-cover {
  width: 120px;
  height: 180px;
  background-color: #f5f5f7;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.book-cover img {
  max-width: 90%;
  max-height: 90%;
  object-fit: contain;
}

.book-info-content {
  flex: 1;
}

.book-title {
  font-size: 20px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 12px;
}

.book-author,
.book-code {
  font-size: 16px;
  color: #6e6e73;
  margin-bottom: 8px;
}

.book-status {
  font-size: 16px;
  color: #1d1d1f;
  margin-bottom: 16px;
}

.status-available {
  color: #2e7d32;
  font-weight: 500;
}

.status-borrowed {
  color: #c62828;
  font-weight: 500;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 16px;
}

.borrow-button,
.return-button {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.borrow-button {
  background-color: #4caf50;
  color: white;
}

.borrow-button:hover:not(:disabled) {
  background-color: #45a049;
}

.return-button {
  background-color: #2196f3;
  color: white;
}

.return-button:hover:not(:disabled) {
  background-color: #0b7dda;
}

.borrow-button:disabled,
.return-button:disabled {
  background-color: #f2f2f7;
  color: #8e8e93;
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
}

.modal-content h3 {
  font-size: 20px;
  margin-bottom: 20px;
  color: #1d1d1f;
}

.modal-content p {
  margin-bottom: 20px;
  color: #6e6e73;
}

.modal-form {
  margin-bottom: 24px;
}

.error-tip {
  color: #ff3b30;
  font-size: 14px;
  margin-top: 8px;
}

.borrower-info {
  background-color: #f5f5f7;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 24px;
}

.borrower-info p {
  margin-bottom: 8px;
  color: #1d1d1f;
}

.borrower-info p:last-child {
  margin-bottom: 0;
}

.modal-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.cancel-button,
.confirm-button {
  padding: 12px 24px;
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

.confirm-button {
  background-color: #0071e3;
  color: white;
}

.confirm-button:hover:not(:disabled) {
  background-color: #0077ed;
}

.confirm-button:disabled {
  background-color: #f2f2f7;
  color: #8e8e93;
  cursor: not-allowed;
}

/* 操作历史 */
.history-section {
  background-color: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
}

.history-section h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 16px;
}

.history-list {
  max-height: 300px;
  overflow-y: auto;
}

.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 8px;
  background-color: #f5f5f7;
}

.history-item.borrow {
  border-left: 4px solid #4caf50;
}

.history-item.return {
  border-left: 4px solid #2196f3;
}

.history-content {
  flex: 1;
}

.history-book {
  font-weight: 500;
  color: #1d1d1f;
  margin-bottom: 4px;
}

.history-info {
  font-size: 14px;
  color: #6e6e73;
}

.history-status {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
}

.history-status.success {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.history-status.error {
  background-color: #ffebee;
  color: #c62828;
}

.empty-history {
  text-align: center;
  color: #8e8e93;
  padding: 40px 20px;
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
  .book-details {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .scan-box {
    width: 250px;
    height: 250px;
  }
}
</style>