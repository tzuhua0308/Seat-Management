<template>
  <div
    class="seat-card"
    :class="statusClass"
    :title="tooltipText"
    @click="$emit('click')"
    role="button"
    :aria-label="ariaLabel"
  >
    <span class="seat-label">{{ seat.floorNo }}樓: {{ seat.seatNo }}</span>
    <span v-if="occupant" class="seat-emp">[員編: {{ occupant }}]</span>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  seat: { type: Object, required: true },
  status: { type: String, required: true },  // 'empty' | 'occupied' | 'pending'
  occupant: { type: String, default: null }
})
defineEmits(['click'])

const statusClass = computed(() => ({
  'seat-empty':    props.status === 'empty',
  'seat-occupied': props.status === 'occupied',
  'seat-pending':  props.status === 'pending'
}))

const tooltipText = computed(() => {
  if (props.status === 'occupied') return `員編 ${props.occupant} 佔用 — 點擊可清除`
  if (props.status === 'pending')  return `已選擇員編 ${props.occupant} — 點擊可清除`
  return '空位 — 選擇員工後點擊指派'
})

const ariaLabel = computed(() =>
  `${props.seat.floorNo}樓 ${props.seat.seatNo}，狀態：${props.status}`
)
</script>

<style scoped>
.seat-card {
  border-radius: 6px;
  border: 1px solid #d0d0d0;
  padding: 10px 8px;
  font-size: 13px;
  cursor: pointer;
  text-align: center;
  line-height: 1.5;
  transition: filter 0.15s, transform 0.1s;
  user-select: none;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  min-height: 56px;
  justify-content: center;
}
.seat-card:hover {
  filter: brightness(0.93);
  transform: translateY(-1px);
}
.seat-card:active {
  transform: translateY(0);
}
.seat-empty {
  background: #f5f5f5;
  color: #555;
  border-color: #ccc;
}
.seat-occupied {
  background: #e74c3c;
  color: #fff;
  border-color: #c0392b;
}
.seat-pending {
  background: #f39c12;
  color: #fff;
  border-color: #d68910;
}
.seat-label {
  font-weight: 500;
}
.seat-emp {
  font-size: 11px;
  opacity: 0.9;
}
</style>
