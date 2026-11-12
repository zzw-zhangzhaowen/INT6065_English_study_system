<template>
  <div class="radar-chart" :style="{ width: size + 'px', height: size + 'px' }">
    <svg :width="size" :height="size" :viewBox="`0 0 ${size} ${size}`" style="overflow: visible">
      <!-- 网格环 -->
      <polygon
        v-for="(ring, ri) in rings"
        :key="'r' + ri"
        :points="ring"
        fill="none"
        :stroke="gridColor"
        stroke-width="1"
        :stroke-dasharray="ri === rings.length - 1 ? '0' : '3 3'"
      />
      <!-- 轴线 -->
      <line
        v-for="(p, i) in axisPoints"
        :key="'a' + i"
        :x1="cx"
        :y1="cy"
        :x2="p.x"
        :y2="p.y"
        :stroke="gridColor"
        stroke-width="1"
      />
      <!-- 数据区 -->
      <polygon :points="dataPolygon" :fill="fillColor" :stroke="color" stroke-width="1.8" stroke-linejoin="round" />
      <!-- 数据点 -->
      <circle
        v-for="(p, i) in valuePoints"
        :key="'v' + i"
        :cx="p.x"
        :cy="p.y"
        r="3"
        :fill="color"
        stroke="#fff"
        stroke-width="1.5"
      />
      <!-- 标签 -->
      <text
        v-for="(p, i) in labelPoints"
        :key="'l' + i"
        :x="p.x"
        :y="p.y"
        :text-anchor="p.anchor"
        dominant-baseline="middle"
        :fill="labelColor"
        font-size="11"
        font-weight="600"
      >{{ items[i]?.name }}</text>
    </svg>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  data: { type: Array, default: () => [] },
  size: { type: Number, default: 220 },
  color: { type: String, default: '#2563eb' },
  fillColor: { type: String, default: 'rgba(37,99,235,0.16)' },
  gridColor: { type: String, default: 'rgba(37,99,235,0.18)' },
  labelColor: { type: String, default: '#4e5f7c' },
  max: { type: Number, default: 100 }
})

const items = computed(() => (props.data || []).filter(d => d && d.name))

const cx = computed(() => props.size / 2)
const cy = computed(() => props.size / 2)
// 留出标签空间：半径按比例收缩，避免中文标签被裁掉
const R = computed(() => Math.max(28, props.size / 2 - Math.max(30, props.size * 0.21)))
const n = computed(() => items.value.length || 1)

function angleOf(i) {
  return -Math.PI / 2 + (i * 2 * Math.PI) / n.value
}

const axisPoints = computed(() =>
  items.value.map((_, i) => ({
    x: cx.value + Math.cos(angleOf(i)) * R.value,
    y: cy.value + Math.sin(angleOf(i)) * R.value
  }))
)

const rings = computed(() => {
  const levels = [0.25, 0.5, 0.75, 1]
  return levels.map(f =>
    items.value
      .map((_, i) => {
        const a = angleOf(i)
        return `${(cx.value + Math.cos(a) * R.value * f).toFixed(1)},${(cy.value + Math.sin(a) * R.value * f).toFixed(1)}`
      })
      .join(' ')
  )
})

const valuePoints = computed(() =>
  items.value.map((d, i) => {
    const a = angleOf(i)
    const v = Math.max(0, Math.min(1, (Number(d.value) || 0) / props.max))
    return {
      x: cx.value + Math.cos(a) * R.value * v,
      y: cy.value + Math.sin(a) * R.value * v
    }
  })
)

const dataPolygon = computed(() => valuePoints.value.map(p => `${p.x.toFixed(1)},${p.y.toFixed(1)}`).join(' '))

const labelPoints = computed(() =>
  items.value.map((_, i) => {
    const a = angleOf(i)
    const lr = R.value + 13
    const x = cx.value + Math.cos(a) * lr
    const y = cy.value + Math.sin(a) * lr
    const cos = Math.cos(a)
    return {
      x,
      y,
      anchor: Math.abs(cos) < 0.25 ? 'middle' : cos > 0 ? 'start' : 'end'
    }
  })
)
</script>

<style scoped>
.radar-chart {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
</style>
