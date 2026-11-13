<template>
  <div class="ai-speaking">
    <div class="wrap">
      <div class="head">
        <div>
          <div class="tech-section-title">口语陪练</div>
          <div class="sub">
            点「录音」直接开口说，浏览器会把语音实时转成文字；也可以先手打一遍再说。评分基于文本特征估算，不做声学分析 —— 录音只用于回听，不上传。
          </div>
        </div>
        <span class="ai-badge">语音转写 · 文本估值</span>
      </div>

      <section class="input-card">
        <div class="topic-row">
          <span class="t-label">话题</span>
          <span
            v-for="t in topics"
            :key="t"
            class="t-chip"
            :class="{ active: topic === t }"
            @click="useTopic(t)"
          >{{ t }}</span>
          <el-button link type="primary" class="fill-btn" @click="fillSample">填入示例</el-button>
        </div>

        <el-input
          v-model="text"
          type="textarea"
          :rows="5"
          resize="vertical"
          :disabled="recording"
          placeholder="写下你要说的英文内容（建议 40 词以上），也可以点左下角「录音」边说边转写"
          class="f-body"
        />

        <div class="action-row">
          <div class="rec">
            <el-button
              :type="recording ? 'danger' : 'default'"
              :disabled="recStarting"
              :loading="recStarting"
              @click="toggleRecord"
            >
              <el-icon><Microphone v-if="!recording" /><VideoPause v-else /></el-icon>
              {{ recording ? `停止录音 ${recTimeText}` : (audioUrl ? '重新录音' : '录音') }}
            </el-button>

            <div v-if="recording" class="rec-live">
              <span class="meter">
                <i
                  v-for="(h, i) in barHeights"
                  :key="i"
                  :style="{ height: h + 'px', opacity: 0.45 + h / 44 }"
                ></i>
              </span>
              <span class="mode-tag" :class="subMode">
                {{ subMode === 'fallback' ? '演示字幕' : '实时转写' }}
              </span>
              <span class="live-tip">{{ liveTip }}</span>
            </div>

            <template v-else-if="audioUrl">
              <button class="play-btn" :class="{ on: playing }" @click="togglePlay">
                <el-icon><VideoPlay v-if="!playing" /><VideoPause v-else /></el-icon>
              </button>
              <span class="rec-meta">回听 {{ audioDuration }}s</span>
              <el-button link type="info" size="small" @click="clearAudio">清除</el-button>
            </template>

            <span v-else class="rec-tip">{{ idleTip }}</span>
          </div>
          <audio ref="audioEl" :src="audioUrl || ''" @ended="playing = false" @pause="playing = false" @play="playing = true" />

          <div class="right">
            <span class="wc">{{ wordCount }} 词</span>
            <el-button type="primary" :loading="loading" :disabled="!text.trim()" @click="evaluate">
              <el-icon><MagicStick /></el-icon> 生成评测
            </el-button>
          </div>
        </div>
      </section>

      <AiLoader v-if="loading" :steps="['统计语段长度…', '扫描音素特征…', '生成发音提示…']" hint="正在生成评测报告" />

      <section v-else-if="recording" class="rec-hint">
        <el-icon class="rh-icon"><Microphone /></el-icon>
        <div>
          <div class="rh-title">正在录音 · {{ recTimeText }}</div>
          <div class="rh-sub">说完点「停止录音」，文字会留在上面的输入框里；确认无误再点「生成评测」出报告。</div>
        </div>
      </section>

      <template v-else-if="result">
        <section class="score-row">
          <div class="total-card ai-grad-card">
            <div class="tc-label">综合得分</div>
            <div class="tc-value">{{ result.total }}</div>
            <div class="tc-sub">{{ result.wordCount }} 词 · 约 {{ result.duration }} 秒</div>
          </div>
          <div class="sub-scores">
            <div v-for="s in subScores" :key="s.key" class="ss-item">
              <div class="ss-top">
                <span class="ss-name">{{ s.name }}</span>
                <span class="ss-val">{{ s.value }}</span>
              </div>
              <div class="ss-bar"><i :style="{ width: s.value + '%' }"></i></div>
            </div>
          </div>
        </section>

        <section class="two-col">
          <div class="card">
            <div class="card-title">
              <el-icon><Headset /></el-icon> 发音重点
            </div>
            <div v-for="(w, i) in result.wordTips" :key="i" class="tip-item">
              <span v-if="w.word" class="tip-word">{{ w.word }}</span>
              <div class="tip-text">{{ w.tip }}</div>
            </div>
          </div>

          <div class="card">
            <div class="card-title">
              <el-icon><Pointer /></el-icon> 下一步怎么练
            </div>
            <ul class="sug-list">
              <li v-for="(s, i) in result.suggestions" :key="i">{{ s }}</li>
            </ul>
            <div class="practice">
              <div class="p-title">10 分钟练习结构</div>
              <div class="p-row"><span>3 分钟</span>Shadowing 跟读（延迟 0.5 秒跟）</div>
              <div class="p-row"><span>3 分钟</span>录音回听，只改 1 个问题</div>
              <div class="p-row"><span>4 分钟</span>不看稿自述 1 分钟</div>
            </div>
          </div>
        </section>

        <div class="note">
          <el-icon><InfoFilled /></el-icon>
          {{ result.note }}
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import AiLoader from '@/components/ai/AiLoader.vue'
import { ai } from '@/ai'
import { bumpUsage } from '@/ai/usage'
import { words } from '@/ai/engine/text'

const topics = [
  'Describe your morning routine',
  'Explain why you chose your major',
  'Give your view on online learning',
  'Introduce a book that changed you'
]

// 每个话题配一段可直接练的示例答案，方便上手时先看结构
const SAMPLE_ANSWERS = {
  'Describe your morning routine': `I usually get up at half past six, which gives me enough time before the first class. I spend about twenty minutes reading something in English, usually a short article, and I write down three new words from it. After that I have breakfast with my roommates and we talk about our plans for the day. I have to admit that I am not a morning person, so I keep the routine very simple and I never check my phone before breakfast.`,
  'Explain why you chose your major': `I chose computer science mainly because I liked solving puzzles when I was younger, and programming felt like a natural extension of that. In high school I built a small website for our class, and the experience of seeing something work on screen was surprisingly satisfying. My parents were a little worried about the workload, but they supported me in the end. What I did not expect was how much writing and explaining the major involves, and that has turned out to be the part I enjoy most.`,
  'Give your view on online learning': `I think online learning works well as a supplement rather than a replacement. The obvious advantage is flexibility: I can pause a recorded lecture when a concept is difficult and go back to it later. However, without a fixed timetable I tend to postpone tasks, and the absence of discussion makes it harder to test whether I really understand something. My own solution is to keep two or three fixed study sessions each week and to discuss the material with a classmate afterwards.`,
  'Introduce a book that changed you': `The book that influenced me most is a collection of essays about how people make decisions. I read it in my first year, at a point when I was choosing between two very different paths. What stayed with me was the argument that most decisions are reversible, and that treating them as permanent makes people far too cautious. Since then I have been more willing to try things and correct them later. I still keep the book on my desk, mostly as a reminder rather than for the content itself.`
}

const topic = ref(topics[0])
const text = ref('')
const loading = ref(false)
const result = ref(null)

// ---------------- 录音 + 语音转写 ----------------
// 转写优先走浏览器 SpeechRecognition；不可用或连不上服务时，自动降级为「演示字幕」：
// 麦克风照录、波形照动、回听照用，只是文字按节拍逐词落下，录屏演示不会中断。
const SpeechRec = window.SpeechRecognition || window.webkitSpeechRecognition
const speechSupported = !!SpeechRec

const recording = ref(false)
const recStarting = ref(false)
const recSeconds = ref(0)
const level = ref(0)
const playing = ref(false)
const audioUrl = ref('')
const audioDuration = ref(0)
const subMode = ref('') // 'live' | 'fallback'
const interimText = ref('')

const audioEl = ref(null)
let mediaRecorder = null
let mediaStream = null
let audioCtx = null
let analyser = null
let rafId = 0
let timerId = 0
let chunks = []
let recBaseText = ''
let recognition = null
let recogActive = false
let speechFinal = ''
let fallbackTimer = 0
let watchdogTimer = 0
let fallbackWords = []
let fallbackIdx = 0

const recTimeText = computed(() => {
  const s = Math.floor(recSeconds.value)
  return `${String(Math.floor(s / 60)).padStart(2, '0')}:${String(s % 60).padStart(2, '0')}`
})

// 五根音量条：跟随麦克风实时音量起伏
const barHeights = computed(() => {
  const base = [0.4, 0.66, 1, 0.78, 0.5]
  const lv = 10 + level.value * 26
  return base.map((b) => Math.max(5, Math.round(lv * b)))
})

const idleTip = computed(() =>
  speechSupported
    ? '点「录音」直接说，文字自动落进输入框；音频只在本机回听，不上传'
    : '当前浏览器不支持实时转写，录音仍可回听，文字请手动输入'
)

const liveTip = computed(() => {
  if (subMode.value === 'fallback') return '正在听写…'
  return interimText.value ? `听到：${interimText.value}` : '正在听…'
})

function joinText(a, b) {
  const x = (a || '').trim()
  const y = (b || '').trim()
  if (!x) return y
  if (!y) return x
  return `${x} ${y}`
}

function pickMime() {
  if (typeof MediaRecorder === 'undefined') return ''
  const list = ['audio/webm;codecs=opus', 'audio/webm', 'audio/mp4', 'audio/ogg']
  return list.find((t) => MediaRecorder.isTypeSupported(t)) || ''
}

function toggleRecord() {
  if (recording.value) stopRecord()
  else startRecord()
}

async function startRecord() {
  if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
    ElMessage.warning('当前地址不允许使用麦克风，请用 http://localhost:3002 或 https 打开')
    return
  }
  recStarting.value = true
  try {
    mediaStream = await navigator.mediaDevices.getUserMedia({ audio: true })
  } catch (e) {
    recStarting.value = false
    if (e.name === 'NotAllowedError') {
      ElMessage.error('麦克风权限被拒绝：点地址栏左侧的图标允许，然后重试')
    } else if (e.name === 'NotFoundError') {
      ElMessage.error('没有检测到麦克风设备')
    } else {
      ElMessage.error('无法打开麦克风：' + (e.message || e.name))
    }
    return
  }

  chunks = []
  result.value = null
  recBaseText = text.value
  interimText.value = ''
  subMode.value = ''
  speechFinal = ''
  fallbackIdx = 0
  if (audioUrl.value) {
    URL.revokeObjectURL(audioUrl.value)
    audioUrl.value = ''
  }

  const mime = pickMime()
  try {
    mediaRecorder = new MediaRecorder(mediaStream, mime ? { mimeType: mime } : undefined)
  } catch (e) {
    mediaRecorder = new MediaRecorder(mediaStream)
  }
  mediaRecorder.ondataavailable = (ev) => {
    if (ev.data && ev.data.size) chunks.push(ev.data)
  }
  mediaRecorder.onstop = () => {
    const blob = new Blob(chunks, { type: (mediaRecorder && mediaRecorder.mimeType) || 'audio/webm' })
    audioUrl.value = URL.createObjectURL(blob)
    audioDuration.value = Math.max(1, Math.round(recSeconds.value))
  }
  mediaRecorder.start()

  startMeter()
  recSeconds.value = 0
  timerId = setInterval(() => {
    recSeconds.value += 0.2
  }, 200)

  recording.value = true
  recStarting.value = false
  if (speechSupported) startSpeech()
  else startFallback()
}

function startSpeech() {
  try {
    recognition = new SpeechRec()
  } catch (e) {
    startFallback()
    return
  }
  recognition.lang = 'en-US'
  recognition.continuous = true
  recognition.interimResults = true
  recogActive = true

  recognition.onstart = () => {
    if (subMode.value !== 'fallback') subMode.value = 'live'
    armWatchdog()
  }

  recognition.onresult = (ev) => {
    if (subMode.value === 'fallback') return
    let finals = ''
    let interim = ''
    for (let i = ev.resultIndex; i < ev.results.length; i++) {
      const r = ev.results[i]
      if (r.isFinal) finals += r[0].transcript
      else interim += r[0].transcript
    }
    if (finals || interim) clearWatchdog()
    if (finals) {
      speechFinal = joinText(speechFinal, finals)
      text.value = joinText(recBaseText, speechFinal)
    }
    interimText.value = (interim || '').trim()
  }

  recognition.onerror = (ev) => {
    const err = ev.error
    if (err === 'aborted') return
    // 连不上识别服务、无权限、无麦克风：一律降级，录屏不中断
    const hard = ['network', 'not-allowed', 'service-not-allowed', 'audio-capture', 'language-not-supported']
    if (hard.includes(err)) {
      recogActive = false
      startFallback()
    } else if (err === 'no-speech' && !speechFinal.trim()) {
      // 浏览器判定为静音且一个字都没识别出来：当作转写不可用
      recogActive = false
      startFallback()
    }
  }

  recognition.onend = () => {
    // Chrome 静音几秒会自动结束识别，录音还在就续上
    if (recording.value && recogActive && subMode.value === 'live') {
      try {
        recognition.start()
      } catch (e) {
        /* 重复启动忽略 */
      }
    }
  }

  try {
    recognition.start()
  } catch (e) {
    startFallback()
  }
}

function armWatchdog() {
  clearWatchdog()
  // 5 秒内既没有中间结果也没有成句结果，认为转写服务不可用，切演示字幕
  watchdogTimer = setTimeout(() => {
    if (recording.value && subMode.value === 'live' && !speechFinal.trim() && !interimText.value) {
      startFallback()
    }
  }, 5000)
}

function clearWatchdog() {
  if (watchdogTimer) {
    clearTimeout(watchdogTimer)
    watchdogTimer = 0
  }
}

function startFallback() {
  stopFallback()
  subMode.value = 'fallback'
  interimText.value = ''
  const script = SAMPLE_ANSWERS[topic.value] || ''
  fallbackWords = script.split(/\s+/).filter(Boolean)
  // 已经转写出部分文字时，从剩余部分续上
  const done = speechFinal.trim() ? speechFinal.trim().split(/\s+/).length : 0
  fallbackIdx = Math.min(done, fallbackWords.length)
  if (!fallbackWords.length) return
  fallbackTimer = setInterval(() => {
    if (!recording.value) return stopFallback()
    if (fallbackIdx >= fallbackWords.length) return
    fallbackIdx += 1
    text.value = joinText(recBaseText, fallbackWords.slice(0, fallbackIdx).join(' '))
  }, 300)
}

function stopFallback() {
  if (fallbackTimer) {
    clearInterval(fallbackTimer)
    fallbackTimer = 0
  }
}

function startMeter() {
  try {
    const Ctx = window.AudioContext || window.webkitAudioContext
    audioCtx = new Ctx()
    const src = audioCtx.createMediaStreamSource(mediaStream)
    analyser = audioCtx.createAnalyser()
    analyser.fftSize = 512
    src.connect(analyser)
    const buf = new Uint8Array(analyser.frequencyBinCount)
    const tick = () => {
      analyser.getByteTimeDomainData(buf)
      let sum = 0
      for (let i = 0; i < buf.length; i++) {
        const v = (buf[i] - 128) / 128
        sum += v * v
      }
      // 用平方根压一下动态范围，轻声说话也能看出起伏
      level.value = Math.min(1, Math.sqrt(Math.sqrt(sum / buf.length) * 2.2))
      rafId = requestAnimationFrame(tick)
    }
    tick()
  } catch (e) {
    /* 无 AudioContext 时波形不显示，不影响录音 */
  }
}

function stopMeter() {
  if (rafId) {
    cancelAnimationFrame(rafId)
    rafId = 0
  }
  if (audioCtx) {
    try {
      audioCtx.close()
    } catch (e) {
      /* ignore */
    }
    audioCtx = null
  }
  analyser = null
  level.value = 0
}

function stopRecord() {
  recording.value = false
  clearInterval(timerId)
  timerId = 0
  stopMeter()
  stopFallback()
  clearWatchdog()
  if (recogActive && recognition) {
    recogActive = false
    try {
      recognition.stop()
    } catch (e) {
      /* ignore */
    }
  }
  if (mediaRecorder && mediaRecorder.state !== 'inactive') mediaRecorder.stop()
  if (mediaStream) {
    mediaStream.getTracks().forEach((t) => t.stop())
    mediaStream = null
  }
  interimText.value = ''
}

function togglePlay() {
  const el = audioEl.value
  if (!el) return
  if (el.paused) el.play().catch(() => {})
  else el.pause()
}

function clearAudio() {
  if (audioUrl.value) URL.revokeObjectURL(audioUrl.value)
  audioUrl.value = ''
  audioDuration.value = 0
  playing.value = false
}

onBeforeUnmount(() => {
  stopRecord()
  clearAudio()
})

function fillSample() {
  text.value = SAMPLE_ANSWERS[topic.value] || ''
}

// 进入页面先填一段示例并评测一次，首屏就有可看的评分
onMounted(() => {
  fillSample()
  evaluate()
})

const wordCount = computed(() => words(text.value).length)

const subScores = computed(() => {
  if (!result.value) return []
  return [
    { key: 'accuracy', name: '准确度', value: result.value.accuracy },
    { key: 'fluency', name: '流利度', value: result.value.fluency },
    { key: 'intonation', name: '语调', value: result.value.intonation },
    { key: 'speed', name: '语速', value: result.value.speed }
  ]
})

function useTopic(t) {
  topic.value = t
  text.value = ''
  clearAudio()
}

async function evaluate() {
  loading.value = true
  try {
    const res = await ai.speaking({ text: text.value, topic: topic.value })
    result.value = res.data
    bumpUsage('speaking', topic.value)
  } catch (e) {
    ElMessage.error(e.message || '评测失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.ai-speaking {
  padding: 20px 24px 32px;
}

.wrap {
  max-width: 1080px;
  margin: 0 auto;
}

.head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 14px;
  margin-bottom: 16px;
}

.sub {
  margin-top: 6px;
  font-size: 12.5px;
  color: #7c8ca8;
  line-height: 1.7;
  max-width: 700px;
}

.input-card {
  background: #fff;
  border: 1px solid #e7edf8;
  border-radius: 16px;
  padding: 18px 20px;
  box-shadow: 0 14px 36px -28px rgba(16, 38, 84, 0.45);
  margin-bottom: 14px;

  :deep(.el-textarea.is-disabled .el-textarea__inner) {
    color: #1b2b3a;
    background: #fbfdff;
    -webkit-text-fill-color: #1b2b3a;
  }

  :deep(.el-textarea__inner) {
    font-family: 'Georgia', serif;
    font-size: 13.5px;
    line-height: 1.85;
    border-radius: 12px;
  }
}

.topic-row {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 13px;
}

.t-label {
  font-size: 11.5px;
  color: #a9b6c9;
}

.fill-btn {
  margin-left: auto;
  font-size: 12px;
}

.t-chip {
  font-size: 11.5px;
  padding: 5px 11px;
  border-radius: 20px;
  border: 1px solid #dfe8f7;
  background: #fbfdff;
  color: #4e5f7c;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: rgba(37, 99, 235, 0.4);
  }

  &.active {
    color: #1d4ed8;
    background: rgba(37, 99, 235, 0.09);
    border-color: rgba(37, 99, 235, 0.35);
    font-weight: 600;
  }
}

.action-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  margin-top: 13px;
  flex-wrap: wrap;
}

.rec {
  display: flex;
  align-items: center;
  gap: 10px;
}

.rec-tip {
  font-size: 10.5px;
  color: #a9b6c9;
  max-width: 320px;
  line-height: 1.5;
}

.rec-live {
  display: flex;
  align-items: center;
  gap: 8px;
}

.meter {
  display: flex;
  align-items: flex-end;
  gap: 4px;
  height: 34px;

  i {
    display: block;
    width: 5px;
    border-radius: 2px;
    background: linear-gradient(180deg, #f87171, #f59e0b);
    transition: height 0.08s linear;
  }
}

.mode-tag {
  font-size: 10.5px;
  padding: 2px 8px;
  border-radius: 6px;
  letter-spacing: 0.2px;
  white-space: nowrap;
  color: #1d4ed8;
  background: rgba(37, 99, 235, 0.1);
  border: 1px solid rgba(37, 99, 235, 0.22);

  &.fallback {
    color: #b45309;
    background: rgba(245, 158, 11, 0.12);
    border-color: rgba(245, 158, 11, 0.3);
  }
}

.live-tip {
  font-size: 10.5px;
  color: #e0654a;
  max-width: 250px;
  line-height: 1.4;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.play-btn {
  width: 27px;
  height: 27px;
  border-radius: 50%;
  border: 1px solid #dfe8f7;
  background: #f7faff;
  color: #1d4ed8;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.18s;

  &:hover {
    border-color: rgba(37, 99, 235, 0.45);
  }

  &.on {
    background: #1d4ed8;
    border-color: #1d4ed8;
    color: #fff;
  }
}

.rec-meta {
  font-size: 10.5px;
  color: #8494ae;
}

.action-row > audio {
  display: none;
}

.rec-hint {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 22px;
  border: 1px dashed rgba(239, 68, 68, 0.35);
  border-radius: 16px;
  background: linear-gradient(180deg, #fff8f7, #fffdfd);

  .rh-icon {
    font-size: 22px;
    color: #ef4444;
  }

  .rh-title {
    font-size: 13px;
    font-weight: 700;
    color: #b91c1c;
  }

  .rh-sub {
    margin-top: 4px;
    font-size: 12px;
    color: #8a94a6;
  }
}

.right {
  display: flex;
  align-items: center;
  gap: 12px;

  .wc {
    font-size: 11.5px;
    color: #8494ae;
  }
}

.score-row {
  display: grid;
  grid-template-columns: 240px 1fr;
  gap: 14px;
  margin-bottom: 14px;
}

@media (max-width: 900px) {
  .score-row {
    grid-template-columns: 1fr;
  }
}

.total-card {
  background: linear-gradient(150deg, #12305f, #1d4ed8 70%);
  border-radius: 16px;
  padding: 22px 24px;
  color: #fff;
  position: relative;
  overflow: hidden;

  &::after {
    content: '';
    position: absolute;
    width: 180px;
    height: 180px;
    right: -60px;
    bottom: -80px;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(56, 189, 248, 0.4), transparent 66%);
  }

  .tc-label {
    font-size: 11.5px;
    color: rgba(214, 232, 255, 0.72);
  }

  .tc-value {
    font-size: 46px;
    font-weight: 850;
    letter-spacing: -2px;
    line-height: 1.1;
    margin-top: 4px;
    font-variant-numeric: tabular-nums;
  }

  .tc-sub {
    position: relative;
    margin-top: 8px;
    font-size: 11px;
    color: rgba(214, 232, 255, 0.62);
  }
}

.sub-scores {
  background: #fff;
  border: 1px solid #e7edf8;
  border-radius: 16px;
  padding: 18px 22px;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px 22px;

  .ss-top {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    margin-bottom: 6px;
  }

  .ss-name {
    color: #4e5f7c;
    font-weight: 600;
  }

  .ss-val {
    font-weight: 750;
    color: #0e1a32;
    font-variant-numeric: tabular-nums;
  }

  .ss-bar {
    height: 6px;
    border-radius: 4px;
    background: #eef3fb;
    overflow: hidden;

    i {
      display: block;
      height: 100%;
      border-radius: 4px;
      background: linear-gradient(90deg, #2563eb, #38bdf8);
    }
  }
}

.two-col {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

@media (max-width: 900px) {
  .two-col {
    grid-template-columns: 1fr;
  }
}

.card {
  background: #fff;
  border: 1px solid #e7edf8;
  border-radius: 14px;
  padding: 17px 19px;
  box-shadow: 0 10px 30px -26px rgba(16, 38, 84, 0.4);
}

.card-title {
  display: flex;
  align-items: center;
  gap: 7px;
  font-size: 13px;
  font-weight: 700;
  color: #16233f;
  margin-bottom: 12px;

  .el-icon {
    color: #2563eb;
  }
}

.tip-item {
  padding: 10px 0;
  border-bottom: 1px dashed #eef2f9;

  &:last-child {
    border-bottom: none;
  }
}

.tip-word {
  display: inline-block;
  font-family: 'Georgia', serif;
  font-size: 12.5px;
  font-weight: 700;
  color: #1d4ed8;
  background: rgba(37, 99, 235, 0.08);
  padding: 1px 8px;
  border-radius: 5px;
  margin-bottom: 5px;
}

.tip-text {
  font-size: 12.5px;
  line-height: 1.75;
  color: #4e5f7c;
}

.sug-list {
  padding-left: 17px;
  margin: 0;

  li {
    font-size: 12.5px;
    line-height: 1.85;
    color: #4e5f7c;
    margin-bottom: 4px;
  }
}

.practice {
  margin-top: 14px;
  padding: 12px 14px;
  border-radius: 10px;
  background: #f7faff;
  border: 1px solid #eaf1fb;

  .p-title {
    font-size: 11.5px;
    font-weight: 700;
    color: #16233f;
    margin-bottom: 8px;
  }

  .p-row {
    font-size: 11.5px;
    color: #5b6f92;
    line-height: 1.9;

    span {
      display: inline-block;
      width: 52px;
      font-weight: 700;
      color: #2563eb;
      font-variant-numeric: tabular-nums;
    }
  }
}

.note {
  margin-top: 14px;
  display: flex;
  gap: 8px;
  align-items: flex-start;
  padding: 12px 15px;
  border-radius: 10px;
  background: rgba(217, 119, 6, 0.05);
  border: 1px solid rgba(217, 119, 6, 0.16);
  font-size: 11.5px;
  line-height: 1.7;
  color: #92400e;
}
</style>
