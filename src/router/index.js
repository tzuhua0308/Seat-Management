import { createRouter, createWebHistory } from 'vue-router'
import SeatingView from '../views/SeatingView.vue'

const routes = [
  {
    path: '/',
    name: 'seating',
    component: SeatingView
  }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
