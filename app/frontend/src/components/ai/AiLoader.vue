<template>
  <div class="ai-loader">
    <div class="orb">
      <span></span><span></span><span></span>
    </div>
    <div class="text">
      <div class="title">{{ currentText }}</div>
      <div v-if="hint" class="hint">{{ hint }}</div>
    </div>
    <div class="scan"></div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'

const props = defineProps({
  steps: {
    type: Array,
    default: () => ['解析输入…', '匹配知识库…', '组织讲解结构…', '生成结论…']
  },
  hint: { type: String, default: '' },
  interval: { type: Number, default: 900 }
})

const idx = ref(0)
let timer = null
const currentText = computed(() => props.steps[idx.value % props.steps.length])

onMounted(() => {
  timer = setInterval(() => {
    idx.value += 1
  }, props.interval)
})

onBeforeUnmount(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.ai-loader {
  position: relative;
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 20px;
  border-radius: 14px;
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.05), rgba(56, 189, 248, 0.05));
  border: 1px solid rgba(37, 99, 235, 0.16);
  overflow: hidden;
}

.orb {
  display: flex;
  gap: 5px;
  align-items: center;
}

.orb span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #2563eb;
  animation: bounce 1.2s infinite ease-in-out;
}

.orb span:nth-child(2) {
  animation-delay: 0.15s;
  background: #3b82f6;
}

.orb span:nth-child(3) {
  animation-delay: 0.3s;
  background: #38bdf8;
}

@keyframes bounce {
  0%, 80%, 100% { transform: translateY(0); opacity: 0.45; }
  40% { transform: translateY(-7px); opacity: 1; }
}

.text .title {
  font-size: 13px;
  font-weight: 650;
  color: #1d4ed8;
}

.text .hint {
  font-size: 11.5px;
  color: #8494ae;
  margin-top: 3px;
}

.scan {
  position: absolute;
  top: 0;
  bottom: 0;
  width: 35%;
  background: linear-gradient(90deg, transparent, rgba(37, 99, 235, 0.12), transparent);
  animation: scanMove 1.8s infinite linear;
}

@keyframes scanMove {
  0% { left: -35%; }
  100% { left: 100%; }
}
</style>
