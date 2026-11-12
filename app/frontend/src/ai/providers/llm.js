// ============================================================
// 真 AI 提供者 · 接口占位（当前未启用）
// ------------------------------------------------------------
// 启用步骤（接真模型时只看这一段）：
//   1. ai/config.js → mode 改成 'llm'
//   2. 填 llm.baseURL / apiKey / model
//   3. 生产环境把 viaProxy 设为 true，并在后端加一个
//      /api/ai/llm/chat 转发接口（避免 apiKey 暴露到浏览器）
//   4. 需要逐字输出时把 llm.stream 设为 true，并在 UI 层消费 streamChat()
//
// 本文件已实现：chat 的完整调用链（含 OpenAI 兼容协议解析）
// 其余方法留空并抛 NOT_IMPLEMENTED，由 ai/index.js 自动降级到本地引擎
// ============================================================

import { AI_CONFIG } from '../config.js'

const NOT_IMPLEMENTED = name => {
  const e = new Error(`llm provider 未实现 ${name}，已回退本地引擎`)
  e.code = 'NOT_IMPLEMENTED'
  return e
}

function buildHeaders() {
  const cfg = AI_CONFIG.llm
  const h = { 'Content-Type': 'application/json' }
  if (!cfg.viaProxy && cfg.apiKey) {
    h['Authorization'] = `Bearer ${cfg.apiKey}`
  }
  return h
}

function buildBody(messages, extra = {}) {
  const cfg = AI_CONFIG.llm
  return {
    model: cfg.model,
    messages,
    temperature: cfg.temperature,
    max_tokens: cfg.maxTokens,
    stream: !!cfg.stream,
    ...extra
  }
}

function target() {
  const cfg = AI_CONFIG.llm
  return cfg.viaProxy ? cfg.proxyPath : `${cfg.baseURL.replace(/\/$/, '')}/chat/completions`
}

/**
 * 底层调用：返回 { content, usage }
 */
async function callLLM(messages, extra = {}) {
  const cfg = AI_CONFIG.llm
  if (!cfg.viaProxy && (!cfg.baseURL || !cfg.model)) {
    throw new Error('LLM 未配置：请在 ai/config.js 填写 baseURL 与 model')
  }

  const controller = new AbortController()
  const timer = setTimeout(() => controller.abort(), cfg.timeout || 60000)

  try {
    const resp = await fetch(target(), {
      method: 'POST',
      headers: buildHeaders(),
      body: JSON.stringify(buildBody(messages, extra)),
      signal: controller.signal
    })
    if (!resp.ok) {
      throw new Error(`LLM 请求失败：HTTP ${resp.status}`)
    }
    const json = await resp.json()
    const content = json?.choices?.[0]?.message?.content || ''
    return { content, usage: json?.usage || null, raw: json }
  } finally {
    clearTimeout(timer)
  }
}

/**
 * 流式调用（逐字返回）—— 预留给真 AI
 * @param {Array} messages
 * @param {(chunk: string) => void} onDelta
 */
async function streamChat(messages, onDelta) {
  const cfg = AI_CONFIG.llm
  if (!cfg.stream) {
    const r = await callLLM(messages)
    onDelta && onDelta(r.content)
    return r.content
  }
  const resp = await fetch(target(), {
    method: 'POST',
    headers: buildHeaders(),
    body: JSON.stringify(buildBody(messages, { stream: true }))
  })
  if (!resp.ok || !resp.body) throw new Error(`LLM 流式请求失败：HTTP ${resp.status}`)

  const reader = resp.body.getReader()
  const decoder = new TextDecoder()
  let buffer = ''
  let full = ''

  while (true) {
    const { done, value } = await reader.read()
    if (done) break
    buffer += decoder.decode(value, { stream: true })
    const lines = buffer.split('\n')
    buffer = lines.pop() || ''
    for (const line of lines) {
      const t = line.trim()
      if (!t.startsWith('data:')) continue
      const payload = t.slice(5).trim()
      if (payload === '[DONE]') continue
      try {
        const json = JSON.parse(payload)
        const delta = json?.choices?.[0]?.delta?.content
        if (delta) {
          full += delta
          onDelta && onDelta(delta)
        }
      } catch (e) {
        // 忽略无法解析的心跳包
      }
    }
  }
  return full
}

// ---------- 对外方法 ----------

async function chat({ question = '', messages = [] } = {}) {
  const sys = {
    role: 'system',
    content:
      '你是英语学习系统的 AI 辅导老师，面向中国大学生（四六级）。回答要用简体中文讲解，英文例句需配中文解释。' +
      '输出使用 Markdown，结构清晰：先给结论，再给规则/步骤，再给例句，最后给一个自测。不要编造不确定的事实。'
  }
  const hist = (messages || []).slice(-10).map(m => ({ role: m.role, content: m.content }))
  const { content, usage } = await callLLM([sys, ...hist, { role: 'user', content: question }])
  return {
    ok: true,
    data: { content, suggestions: [], scene: 'chat' },
    meta: { provider: 'llm', model: AI_CONFIG.llm.model, usage, at: Date.now() }
  }
}

// 以下方法在接真模型时按「让模型输出 JSON → 前端渲染」的思路实现
// 目前统一抛错，由 index.js 降级到本地引擎，保证页面永远有结果
async function correctWriting() { throw NOT_IMPLEMENTED('correctWriting') }
async function grammarCheck() { throw NOT_IMPLEMENTED('grammarCheck') }
async function teacherDigest() { throw NOT_IMPLEMENTED('teacherDigest') }
async function teacherPreGrade() { throw NOT_IMPLEMENTED('teacherPreGrade') }
async function learningProfile() { throw NOT_IMPLEMENTED('learningProfile') }
async function plan() { throw NOT_IMPLEMENTED('plan') }
async function longSentence() { throw NOT_IMPLEMENTED('longSentence') }
async function speaking() { throw NOT_IMPLEMENTED('speaking') }
async function explainWrong() { throw NOT_IMPLEMENTED('explainWrong') }
async function vocabCards() { throw NOT_IMPLEMENTED('vocabCards') }

export default {
  name: 'llm',
  chat,
  correctWriting,
  grammarCheck,
  teacherDigest,
  teacherPreGrade,
  learningProfile,
  plan,
  longSentence,
  speaking,
  explainWrong,
  vocabCards,
  // 额外导出，供 UI 做逐字输出
  callLLM,
  streamChat
}
