<template>
  <div class="app-container">
    <!-- 简化的顶部导航 -->
    <header class="navbar">
      <div class="navbar-content">
        <div class="logo">图书借阅管理系统</div>
      </div>
    </header>
    
    <!-- 主内容区域 -->
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: Arial, sans-serif;
  color: #333;
  background-color: #f5f5f7;
}

/* 应用容器 */
.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 顶部导航 */
.header {
  background-color: #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  padding: 0 20px;
  height: 60px;
  display: flex;
  align-items: center;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #0071e3;
}

/* 主内容区域 */
.main {
  flex: 1;
  padding: 20px;
}
</style>