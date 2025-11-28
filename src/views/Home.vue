<template>
  <div class="home-container">
    <h1>图书借阅管理系统</h1>
    <p>欢迎使用图书借阅系统！</p>
    
    <!-- 简化的图书列表展示 -->
    <div v-if="books.length > 0" class="book-list">
      <h2>图书列表</h2>
      <div v-for="book in books" :key="book.id" class="simple-book-item">
        {{ book.title }} - {{ book.author }}
      </div>
    </div>
    
    <div v-else class="no-data">
      <p>正在加载数据...</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Home',
  data() {
    return {
      books: [
        { id: 1, title: '测试图书1', author: '作者A' },
        { id: 2, title: '测试图书2', author: '作者B' },
        { id: 3, title: '测试图书3', author: '作者C' }
      ]
    }
  },
  mounted() {
    console.log('Home组件已挂载');
    // 暂时使用模拟数据，避免API调用失败导致页面空白
  }
}
</script>

<style scoped>
.home-container {
  padding: 20px;
  text-align: center;
  min-height: 100vh;
  background-color: #f5f5f7;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro', sans-serif;
}

h1 {
  color: #0071e3;
  margin-bottom: 20px;
}

h2 {
  margin-bottom: 20px;
  color: #1d1d1f;
}

.book-list {
  margin-top: 30px;
}

.simple-book-item {
  padding: 10px;
  margin: 10px 0;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.no-data {
  margin-top: 30px;
  color: #666;
}
</style>