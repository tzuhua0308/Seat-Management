<template>
  <section class="floor-section">
    <h3 class="floor-title">{{ floor.floorNo }} 樓</h3>
    <div class="seats-grid">
      <SeatCard
        v-for="seat in floor.seats"
        :key="seat.floorSeatSeq"
        :seat="seat"
        :status="store.getSeatStatus(seat)"
        :occupant="store.getSeatOccupant(seat)"
        @click="store.onSeatClick(seat)"
      />
    </div>
  </section>
</template>

<script setup>
import SeatCard from './SeatCard.vue'
import { useSeatingStore } from '../store/seating'

defineProps({
  floor: { type: Object, required: true }
})

const store = useSeatingStore()
</script>

<style scoped>
.floor-section {
  margin-bottom: 20px;
}
.floor-title {
  font-size: 14px;
  font-weight: 600;
  color: #003F87;
  margin-bottom: 8px;
  padding-bottom: 4px;
  border-bottom: 2px solid #003F87;
}
.seats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
}
@media (max-width: 600px) {
  .seats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
