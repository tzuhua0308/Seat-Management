<template>
  <div class="seating-view">

    <!-- 員工選擇列 -->
    <div class="toolbar card">
      <div class="toolbar-row">
        <label for="emp-select" class="toolbar-label">選擇員工</label>
        <select
          id="emp-select"
          v-model="store.selectedEmpId"
          class="emp-select"
          @change="store.clearMessages()"
        >
          <option value="">-- 請選擇員工 --</option>
          <option
            v-for="emp in store.employees"
            :key="emp.empId"
            :value="emp.empId"
          >
            {{ emp.empId }} - {{ emp.name }}
          </option>
        </select>

        <span v-if="store.selectedEmpId" class="emp-seat-info">
          <template v-if="currentSeatInfo">
            目前座位：{{ currentSeatInfo }}
          </template>
          <template v-else>目前無座位</template>
        </span>
      </div>

      <!-- 圖例 -->
      <div class="legend">
        <span class="legend-item"><i class="dot dot-empty"></i> 空位</span>
        <span class="legend-item"><i class="dot dot-occupied"></i> 已佔用</span>
        <span class="legend-item"><i class="dot dot-pending"></i> 請選擇</span>
      </div>
    </div>

    <!-- 訊息列 -->
    <div v-if="store.errorMsg" class="alert alert-error">{{ store.errorMsg }}</div>
    <div v-if="store.successMsg" class="alert alert-success">{{ store.successMsg }}</div>

    <!-- 載入中 -->
    <div v-if="store.loading" class="loading">載入中...</div>

    <!-- 各樓層座位 -->
    <template v-else>
      <FloorSection
        v-for="floor in store.floors"
        :key="floor.floorNo"
        :floor="floor"
      />
    </template>

    <!-- 送出按鈕 -->
    <div class="footer-bar">
      <span class="pending-count" v-if="store.pendingChanges.size > 0">
        有 {{ store.pendingChanges.size }} 筆未儲存變更
      </span>
      <button
        class="submit-btn"
        :disabled="store.submitLoading"
        @click="store.submitChanges()"
      >
        {{ store.submitLoading ? '儲存中...' : '送出' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useSeatingStore } from '../store/seating'
import FloorSection from '../components/FloorSection.vue'

const store = useSeatingStore()

onMounted(async () => {
  await Promise.all([store.loadFloors(), store.loadEmployees()])
})

/** 顯示選中員工目前座位的文字描述 */
const currentSeatInfo = computed(() => {
  if (!store.selectedEmpId) return null
  const seq = store.getEmpCurrentSeq(store.selectedEmpId)
  if (!seq) return null
  const seat = store.seatMap.get(seq)
  return seat ? `${seat.floorNo}樓 ${seat.seatNo}` : null
})
</script>

<style scoped>
.seating-view {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 工具列 card */
.card {
  background: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 16px 20px;
}
.toolbar-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 12px;
}
.toolbar-label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  white-space: nowrap;
}
.emp-select {
  font-size: 14px;
  padding: 7px 10px;
  border: 1px solid #bbb;
  border-radius: 6px;
  background: #fff;
  color: #222;
  min-width: 220px;
  cursor: pointer;
}
.emp-seat-info {
  font-size: 12px;
  color: #666;
  background: #f0f4ff;
  padding: 4px 10px;
  border-radius: 4px;
}

/* 圖例 */
.legend {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}
.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #555;
}
.dot {
  display: inline-block;
  width: 14px;
  height: 14px;
  border-radius: 3px;
  border: 1px solid #ccc;
}
.dot-empty    { background: #f5f5f5; }
.dot-occupied { background: #e74c3c; border-color: #c0392b; }
.dot-pending  { background: #f39c12; border-color: #d68910; }

/* 訊息列 */
.alert {
  padding: 10px 16px;
  border-radius: 6px;
  font-size: 14px;
}
.alert-error   { background: #fdecea; color: #b71c1c; border: 1px solid #f5c6cb; }
.alert-success { background: #e8f5e9; color: #1b5e20; border: 1px solid #c3e6cb; }

.loading {
  text-align: center;
  color: #888;
  padding: 40px;
  font-size: 15px;
}

/* 送出列 */
.footer-bar {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-top: 4px;
}
.pending-count {
  font-size: 13px;
  color: #d68910;
  font-weight: 500;
}
.submit-btn {
  padding: 9px 28px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 6px;
  border: none;
  background: #003F87;
  color: #fff;
  cursor: pointer;
  letter-spacing: 1px;
  transition: background 0.15s;
}
.submit-btn:hover:not(:disabled) { background: #002d63; }
.submit-btn:disabled { background: #aaa; cursor: not-allowed; }
</style>
