<template>
  <div class="whiteboard">
    <!-- 工具栏 -->
    <div class="wb-toolbar" v-if="!readOnly">
      <div class="tool-group">
        <el-tooltip content="画笔" placement="top">
          <el-button :type="currentTool === 'pen' ? 'primary' : 'default'" size="small" @click="currentTool = 'pen'" circle>
            <el-icon><EditPen /></el-icon>
          </el-button>
        </el-tooltip>
        <el-tooltip content="橡皮擦" placement="top">
          <el-button :type="currentTool === 'eraser' ? 'primary' : 'default'" size="small" @click="currentTool = 'eraser'" circle>
            <el-icon><Delete /></el-icon>
          </el-button>
        </el-tooltip>
        <el-tooltip content="文字" placement="top">
          <el-button :type="currentTool === 'text' ? 'primary' : 'default'" size="small" @click="currentTool = 'text'" circle>
            <el-icon><EditPen /></el-icon>
          </el-button>
        </el-tooltip>
        <el-tooltip content="矩形" placement="top">
          <el-button :type="currentTool === 'rect' ? 'primary' : 'default'" size="small" @click="currentTool = 'rect'" circle>
            <el-icon><FullScreen /></el-icon>
          </el-button>
        </el-tooltip>
      </div>

      <div class="tool-group">
        <el-color-picker v-model="strokeColor" size="small" :predefine="predefineColors" />
        <el-select v-model="lineWidth" size="small" style="width: 70px">
          <el-option :value="2" label="细" />
          <el-option :value="4" label="中" />
          <el-option :value="8" label="粗" />
          <el-option :value="12" label="特粗" />
        </el-select>
      </div>

      <div class="tool-group">
        <el-tooltip content="撤销" placement="top">
          <el-button size="small" @click="undo" :disabled="historyIndex <= 0" circle>
            <el-icon><RefreshLeft /></el-icon>
          </el-button>
        </el-tooltip>
        <el-tooltip content="重做" placement="top">
          <el-button size="small" @click="redo" :disabled="historyIndex >= history.length - 1" circle>
            <el-icon><RefreshRight /></el-icon>
          </el-button>
        </el-tooltip>
        <el-tooltip content="清屏" placement="top">
          <el-button size="small" type="danger" @click="clearCanvas" circle>
            <el-icon><Delete /></el-icon>
          </el-button>
        </el-tooltip>
      </div>
    </div>

    <!-- 画布区域 -->
    <div class="canvas-wrapper" ref="canvasWrapper">
      <canvas
        ref="canvasRef"
        @mousedown="onMouseDown"
        @mousemove="onMouseMove"
        @mouseup="onMouseUp"
        @mouseleave="onMouseUp"
        :style="{ cursor: cursorStyle }"
      ></canvas>
      <!-- 文字输入框 -->
      <input
        v-if="showTextInput"
        ref="textInputRef"
        class="text-input"
        v-model="textContent"
        :style="textInputStyle"
        @keyup.enter="confirmText"
        @blur="confirmText"
        autofocus
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { EditPen, Delete, FullScreen, RefreshLeft, RefreshRight } from '@element-plus/icons-vue'

const props = defineProps({
  stompClient: { type: Object, default: null },
  classroomId: { type: [String, Number], required: true },
  wsConnected: { type: Boolean, default: false },
  readOnly: { type: Boolean, default: false }
})

const canvasRef = ref(null)
const canvasWrapper = ref(null)
const textInputRef = ref(null)

const currentTool = ref('pen')
const strokeColor = ref('#333333')
const lineWidth = ref(4)
const isDrawing = ref(false)
const lastPoint = ref(null)
const showTextInput = ref(false)
const textContent = ref('')
const textPosition = ref({ x: 0, y: 0 })

// 矩形绘制状态
const rectStart = ref(null)
const tempCanvas = ref(null)

// 历史记录
const history = ref([])
const historyIndex = ref(-1)

const predefineColors = ['#333333', '#E74C3C', '#3498DB', '#2ECC71', '#F39C12', '#9B59B6', '#1ABC9C', '#FFFFFF']

let ctx = null
let resizeObserver = null

const cursorStyle = computed(() => {
  switch (currentTool.value) {
    case 'pen': return 'crosshair'
    case 'eraser': return 'cell'
    case 'text': return 'text'
    case 'rect': return 'crosshair'
    default: return 'default'
  }
})

const textInputStyle = computed(() => ({
  left: textPosition.value.x + 'px',
  top: textPosition.value.y + 'px',
  color: strokeColor.value,
  fontSize: (lineWidth.value * 4) + 'px'
}))

const initCanvas = () => {
  const canvas = canvasRef.value
  const wrapper = canvasWrapper.value
  if (!canvas || !wrapper) return

  canvas.width = wrapper.clientWidth
  canvas.height = wrapper.clientHeight

  ctx = canvas.getContext('2d')
  ctx.lineCap = 'round'
  ctx.lineJoin = 'round'
  ctx.fillStyle = '#ffffff'
  ctx.fillRect(0, 0, canvas.width, canvas.height)

  saveHistory()
}

const resizeCanvas = () => {
  const canvas = canvasRef.value
  const wrapper = canvasWrapper.value
  if (!canvas || !wrapper || !ctx) return

  const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height)
  canvas.width = wrapper.clientWidth
  canvas.height = wrapper.clientHeight
  ctx.fillStyle = '#ffffff'
  ctx.fillRect(0, 0, canvas.width, canvas.height)
  ctx.putImageData(imageData, 0, 0)
}

const getCanvasPoint = (e) => {
  const canvas = canvasRef.value
  const rect = canvas.getBoundingClientRect()
  return {
    x: (e.clientX - rect.left) * (canvas.width / rect.width),
    y: (e.clientY - rect.top) * (canvas.height / rect.height)
  }
}

const onMouseDown = (e) => {
  if (props.readOnly) return
  const point = getCanvasPoint(e)

  if (currentTool.value === 'text') {
    textPosition.value = { x: e.offsetX, y: e.offsetY }
    textContent.value = ''
    showTextInput.value = true
    nextTick(() => textInputRef.value?.focus())
    return
  }

  isDrawing.value = true
  lastPoint.value = point

  if (currentTool.value === 'rect') {
    rectStart.value = point
    // 保存当前画布状态用于矩形预览
    tempCanvas.value = ctx.getImageData(0, 0, canvasRef.value.width, canvasRef.value.height)
  }
}

const onMouseMove = (e) => {
  if (!isDrawing.value || !ctx) return
  const point = getCanvasPoint(e)

  if (currentTool.value === 'pen') {
    drawLine(lastPoint.value, point, strokeColor.value, lineWidth.value)
    broadcastDraw('line', lastPoint.value, point, strokeColor.value, lineWidth.value)
    lastPoint.value = point
  } else if (currentTool.value === 'eraser') {
    drawLine(lastPoint.value, point, '#ffffff', lineWidth.value * 4)
    broadcastDraw('erase', lastPoint.value, point, '#ffffff', lineWidth.value * 4)
    lastPoint.value = point
  } else if (currentTool.value === 'rect' && rectStart.value) {
    // 矩形预览
    ctx.putImageData(tempCanvas.value, 0, 0)
    drawRect(rectStart.value, point, strokeColor.value, lineWidth.value)
  }
}

const onMouseUp = () => {
  if (!isDrawing.value) return

  if (currentTool.value === 'rect' && rectStart.value) {
    const point = lastPoint.value
    broadcastDraw('rect', rectStart.value, getCanvasPoint({ clientX: 0, clientY: 0 }), strokeColor.value, lineWidth.value)
    rectStart.value = null
    tempCanvas.value = null
  }

  isDrawing.value = false
  lastPoint.value = null
  saveHistory()
}

const drawLine = (from, to, color, width) => {
  if (!ctx) return
  ctx.beginPath()
  ctx.strokeStyle = color
  ctx.lineWidth = width
  ctx.lineCap = 'round'
  ctx.moveTo(from.x, from.y)
  ctx.lineTo(to.x, to.y)
  ctx.stroke()
}

const drawRect = (from, to, color, width) => {
  if (!ctx) return
  ctx.beginPath()
  ctx.strokeStyle = color
  ctx.lineWidth = width
  ctx.strokeRect(from.x, from.y, to.x - from.x, to.y - from.y)
}

const confirmText = () => {
  if (!showTextInput.value) return
  if (textContent.value.trim()) {
    const canvas = canvasRef.value
    const rect = canvas.getBoundingClientRect()
    const scaleX = canvas.width / rect.width
    const scaleY = canvas.height / rect.height
    const x = textPosition.value.x * scaleX
    const y = textPosition.value.y * scaleY
    const fontSize = lineWidth.value * 4

    ctx.font = `${fontSize * scaleY}px sans-serif`
    ctx.fillStyle = strokeColor.value
    ctx.fillText(textContent.value, x, y + fontSize * scaleY)

    broadcastDraw('text', { x, y }, null, strokeColor.value, fontSize, textContent.value)
    saveHistory()
  }
  showTextInput.value = false
  textContent.value = ''
}

const saveHistory = () => {
  if (!ctx || !canvasRef.value) return
  // 删除当前位置之后的历史
  history.value = history.value.slice(0, historyIndex.value + 1)
  history.value.push(ctx.getImageData(0, 0, canvasRef.value.width, canvasRef.value.height))
  historyIndex.value = history.value.length - 1
  // 限制历史记录数量
  if (history.value.length > 50) {
    history.value.shift()
    historyIndex.value--
  }
}

const undo = () => {
  if (historyIndex.value <= 0) return
  historyIndex.value--
  ctx.putImageData(history.value[historyIndex.value], 0, 0)
  broadcastDraw('snapshot', null, null, null, null, null, canvasRef.value.toDataURL('image/png', 0.5))
}

const redo = () => {
  if (historyIndex.value >= history.value.length - 1) return
  historyIndex.value++
  ctx.putImageData(history.value[historyIndex.value], 0, 0)
  broadcastDraw('snapshot', null, null, null, null, null, canvasRef.value.toDataURL('image/png', 0.5))
}

const clearCanvas = () => {
  if (!ctx || !canvasRef.value) return
  ctx.fillStyle = '#ffffff'
  ctx.fillRect(0, 0, canvasRef.value.width, canvasRef.value.height)
  saveHistory()
  broadcastDraw('clear')
}

// WebSocket 广播白板操作
const broadcastDraw = (type, from, to, color, width, text, snapshot) => {
  if (!props.stompClient || !props.wsConnected) return
  try {
    props.stompClient.publish({
      destination: `/app/classroom/${props.classroomId}/whiteboard`,
      body: JSON.stringify({ type, from, to, color, width, text, snapshot })
    })
  } catch (e) {
    console.error('白板广播失败:', e)
  }
}

// 处理远程白板操作
const handleRemoteWhiteboard = (data) => {
  if (!ctx || !canvasRef.value) return
  switch (data.type) {
    case 'line':
    case 'erase':
      drawLine(data.from, data.to, data.color, data.width)
      break
    case 'rect':
      drawRect(data.from, data.to, data.color, data.width)
      break
    case 'text':
      if (data.text) {
        ctx.font = `${data.width}px sans-serif`
        ctx.fillStyle = data.color
        ctx.fillText(data.text, data.from.x, data.from.y + data.width)
      }
      break
    case 'clear':
      ctx.fillStyle = '#ffffff'
      ctx.fillRect(0, 0, canvasRef.value.width, canvasRef.value.height)
      break
    case 'snapshot':
      if (data.snapshot) {
        const img = new Image()
        img.onload = () => {
          ctx.drawImage(img, 0, 0)
        }
        img.src = data.snapshot
      }
      break
  }
}

defineExpose({ handleRemoteWhiteboard })

onMounted(() => {
  nextTick(() => {
    initCanvas()
    resizeObserver = new ResizeObserver(() => resizeCanvas())
    if (canvasWrapper.value) {
      resizeObserver.observe(canvasWrapper.value)
    }
  })
})

onUnmounted(() => {
  if (resizeObserver) {
    resizeObserver.disconnect()
  }
})
</script>

<style lang="scss" scoped>
.whiteboard {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.wb-toolbar {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px 12px;
  background: #fafafa;
  border-bottom: 1px solid #eee;
  flex-wrap: wrap;

  .tool-group {
    display: flex;
    align-items: center;
    gap: 4px;
  }
}

.canvas-wrapper {
  flex: 1;
  position: relative;
  overflow: hidden;
  min-height: 0;

  canvas {
    display: block;
    width: 100%;
    height: 100%;
  }

  .text-input {
    position: absolute;
    background: transparent;
    border: 1px dashed #409eff;
    outline: none;
    padding: 2px 4px;
    min-width: 100px;
    font-family: sans-serif;
  }
}
</style>
