import { createRouter, createWebHistory } from 'vue-router'

// 导入视图组件（稍后创建）
const Home = () => import('./views/Home.vue')
const BookList = () => import('./views/BookList.vue')
const BookDetail = () => import('./views/BookDetail.vue')
const UserList = () => import('./views/UserList.vue')
const UserDetail = () => import('./views/UserDetail.vue')
const BorrowScan = () => import('./views/BorrowScan.vue')

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/book/list',
    name: 'BookList',
    component: BookList
  },
  {
    path: '/book/:id',
    name: 'BookDetail',
    component: BookDetail,
    props: true
  },
  {
    path: '/user/list',
    name: 'UserList',
    component: UserList
  },
  {
    path: '/user/:id',
    name: 'UserDetail',
    component: UserDetail,
    props: true
  },
  {
    path: '/borrow-scan',
    name: 'BorrowScan',
    component: BorrowScan
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router