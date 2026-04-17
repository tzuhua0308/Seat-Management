<template>
  <div id="app-root">
    <header class="app-header">
      <div class="header-inner">
        <span class="brand">🏦 玉山銀行</span>
        <span class="title">員工座位安排系統</span>
      </div>
    </header>
    <main class="app-main">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { RouterView } from 'vue-router'
</script>

<style scoped>
.app-header {
  background: #003F87;
  color: #fff;
  padding: 0 24px;
  height: 56px;
  display: flex;
  align-items: center;
}
.header-inner {
  display: flex;
  align-items: center;
  gap: 12px;
}
.brand {
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 1px;
}
.title {
  font-size: 15px;
  opacity: 0.85;
  border-left: 1px solid rgba(255,255,255,0.4);
  padding-left: 12px;
}
.app-main {
  padding: 24px;
  max-width: 1100px;
  margin: 0 auto;
}
</style>
