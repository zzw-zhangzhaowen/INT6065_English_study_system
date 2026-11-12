// ============================================================
// AI 统一入口 · 页面只 import 这里，永远不直接碰 provider
// 能力：模式分发 + 失败自动降级 + 统一返回结构
// ============================================================

import { AI_CONFIG, getMode, setMode, modeLabel } from './config.js'
import local from './providers/local.js'
import backend from './providers/backend.js'
import llm from './providers/llm.js'

const PROVIDERS = { local, backend, llm }

/**
 * 按优先级调用，失败自动降级
 * 返回统一结构 { ok, data, meta }
 */
async function invoke(method, payload = {}) {
  const primary = getMode()
  const chain = [primary]
  if (AI_CONFIG.fallbackToLocal && primary !== 'local') chain.push('local')

  let lastError = null
  for (const name of chain) {
    const provider = PROVIDERS[name]
    if (!provider || typeof provider[method] !== 'function') continue
    try {
      const res = await provider[method](payload)
      if (res && res.ok) {
        if (name !== primary) {
          res.meta = { ...(res.meta || {}), degradedFrom: primary }
        }
        return res
      }
      lastError = new Error(`${name}.${method} 返回异常`)
    } catch (e) {
      lastError = e
      // 用户输入类错误不降级，直接抛给页面
      if (e && (e.code === 'EMPTY_INPUT' || e.code === 'INVALID_INPUT')) throw e
    }
  }
  throw lastError || new Error(`AI 能力 ${method} 暂不可用`)
}

/* ---------------- 对外能力 ---------------- */
export const ai = {
  // 学生端
  chat: payload => invoke('chat', payload),
  correctWriting: payload => invoke('correctWriting', payload),
  grammarCheck: payload => invoke('grammarCheck', payload),
  explainWrong: payload => invoke('explainWrong', payload),
  learningProfile: payload => invoke('learningProfile', payload),
  plan: payload => invoke('plan', payload),
  longSentence: payload => invoke('longSentence', payload),
  speaking: payload => invoke('speaking', payload),
  vocabCards: payload => invoke('vocabCards', payload),

  // 教师端
  teacherDigest: payload => invoke('teacherDigest', payload),
  teacherPreGrade: payload => invoke('teacherPreGrade', payload)
}

export { getMode, setMode, modeLabel, AI_CONFIG }
export default ai
