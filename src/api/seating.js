import axios from 'axios'

/**
 * Axios 實例：統一設定 base URL、逾時與錯誤攔截。
 * 前端 XSS 防護：Vue 模板 {{ }} 預設進行 HTML Escape，
 * 避免在 v-html 直接渲染 API 回應。
 */
const http = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
})

// 回應攔截器：統一錯誤處理
http.interceptors.response.use(
  res => res.data,
  err => {
    const msg = err.response?.data?.message || '網路錯誤，請稍後再試'
    return Promise.reject(new Error(msg))
  }
)

/** 取得所有樓層座位 */
export const fetchFloors = () => http.get('/floors')

/** 取得員工清單 */
export const fetchEmployees = () => http.get('/employees')

/**
 * 送出座位異動
 * @param {Array<{floorSeatSeq: number, empId: string|null}>} changes
 */
export const submitSeats = (changes) => http.post('/seats', { changes })
