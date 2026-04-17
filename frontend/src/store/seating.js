import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { fetchFloors, fetchEmployees, submitSeats } from '../api/seating'

/**
 * Pinia Store：管理座位頁面的所有狀態。
 *
 * pendingChanges: Map<floorSeatSeq, empId|null>
 *   - empId 為字串 → 指派給該員工
 *   - null           → 清除此座位
 */
export const useSeatingStore = defineStore('seating', () => {
  // ── State ──────────────────────────────────────────────
  const floors = ref([])          // FloorSeatsResponse[]
  const employees = ref([])       // Employee[]
  const selectedEmpId = ref('')   // 下拉選單選中的員工
  const pendingChanges = ref(new Map())
  const loading = ref(false)
  const submitLoading = ref(false)
  const errorMsg = ref('')
  const successMsg = ref('')

  // ── Getters ─────────────────────────────────────────────

  /** 從 floors 建立 seq → seat 的快速查表 */
  const seatMap = computed(() => {
    const map = new Map()
    floors.value.forEach(f => {
      f.seats.forEach(s => map.set(s.floorSeatSeq, s))
    })
    return map
  })

  /** 取得座位的有效狀態（含 pending 狀態覆蓋） */
  function getSeatStatus(seat) {
    if (pendingChanges.value.has(seat.floorSeatSeq)) {
      const val = pendingChanges.value.get(seat.floorSeatSeq)
      return val === null ? 'empty' : 'pending'
    }
    return seat.occupiedByEmpId ? 'occupied' : 'empty'
  }

  /** 取得座位目前顯示的佔用員工編號 */
  function getSeatOccupant(seat) {
    if (pendingChanges.value.has(seat.floorSeatSeq)) {
      return pendingChanges.value.get(seat.floorSeatSeq) ?? null
    }
    return seat.occupiedByEmpId ?? null
  }

  /** 某員工目前（含 pending）佔用的 floorSeatSeq */
  function getEmpCurrentSeq(empId) {
    // 先看 pending
    for (const [seq, id] of pendingChanges.value.entries()) {
      if (id === empId) return seq
    }
    // 再看已儲存資料
    for (const f of floors.value) {
      for (const s of f.seats) {
        if (s.occupiedByEmpId === empId && !pendingChanges.value.has(s.floorSeatSeq)) {
          return s.floorSeatSeq
        }
      }
    }
    return null
  }

  // ── Actions ─────────────────────────────────────────────

  async function loadFloors() {
    loading.value = true
    try {
      const res = await fetchFloors()
      floors.value = res.data
    } catch (e) {
      errorMsg.value = e.message
    } finally {
      loading.value = false
    }
  }

  async function loadEmployees() {
    try {
      const res = await fetchEmployees()
      employees.value = res.data
    } catch (e) {
      errorMsg.value = e.message
    }
  }

  /**
   * 點擊座位事件。
   * - 若座位已佔用（或 pending occupied）→ 清除
   * - 若座位空位且有選擇員工 → 指派
   */
  function onSeatClick(seat) {
    clearMessages()
    const status = getSeatStatus(seat)
    const seq = seat.floorSeatSeq

    if (status === 'occupied' || status === 'pending') {
      // 清除座位
      pendingChanges.value.set(seq, null)
      return
    }

    // 空位
    if (!selectedEmpId.value) {
      errorMsg.value = '請先從下拉選單選擇員工'
      return
    }

    // 若該員工已有其他 pending 座位，先清除
    const oldSeq = getEmpCurrentSeq(selectedEmpId.value)
    if (oldSeq !== null && oldSeq !== seq) {
      pendingChanges.value.set(oldSeq, null)
    }

    pendingChanges.value.set(seq, selectedEmpId.value)
  }

  async function submitChanges() {
    if (pendingChanges.value.size === 0) {
      errorMsg.value = '尚無座位異動'
      return
    }
    submitLoading.value = true
    clearMessages()
    try {
      const changes = Array.from(pendingChanges.value.entries()).map(([seq, empId]) => ({
        floorSeatSeq: seq,
        empId: empId
      }))
      await submitSeats(changes)
      successMsg.value = '座位資訊已成功儲存！'
      pendingChanges.value.clear()
      selectedEmpId.value = ''
      await loadFloors()
    } catch (e) {
      errorMsg.value = e.message
    } finally {
      submitLoading.value = false
    }
  }

  function clearMessages() {
    errorMsg.value = ''
    successMsg.value = ''
  }

  return {
    floors, employees, selectedEmpId,
    pendingChanges, loading, submitLoading,
    errorMsg, successMsg, seatMap,
    getSeatStatus, getSeatOccupant, getEmpCurrentSeq,
    loadFloors, loadEmployees, onSeatClick, submitChanges, clearMessages
  }
})
