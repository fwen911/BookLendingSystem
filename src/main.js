import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

const app = createApp(App)

// 简化配置，确保基本功能正常
console.log('应用已启动')

app.use(router)

// 挂载应用
app.mount('#app')