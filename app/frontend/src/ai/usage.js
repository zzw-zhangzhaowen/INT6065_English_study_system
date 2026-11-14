// ============================================================
// AI 使用统计（本地）· 供「功能总览」展示
// 接入后端服务后可替换为 /api/ai/stats
// ============================================================

const KEY = 'ai_usage_v1'

const EMPTY = {
  chat: 0,
  writing: 0,
  wrong: 0,
  plan: 0,
  longSentence: 0,
  vocab: 0,
  speaking: 0,
  profile: 0,
  lastAt: 0,
  recent: [] // [{ type, title, at }]
}

export function getUsage() {
  try {
    const raw = localStorage.getItem(KEY)
    if (!raw) return { ...EMPTY, recent: [] }
    const obj = JSON.parse(raw)
    return { ...EMPTY, ...obj, recent: obj.recent || [] }
  } catch (e) {
    return { ...EMPTY, recent: [] }
  }
}

export function bumpUsage(type, title = '') {
  const u = getUsage()
  if (Object.prototype.hasOwnProperty.call(u, type)) u[type] += 1
  u.lastAt = Date.now()
  u.recent = [{ type, title, at: Date.now() }, ...u.recent].slice(0, 20)
  try {
    localStorage.setItem(KEY, JSON.stringify(u))
  } catch (e) { /* 忽略容量异常 */ }
  return u
}

export function totalUsage() {
  const u = getUsage()
  return ['chat', 'writing', 'wrong', 'plan', 'longSentence', 'vocab', 'speaking', 'profile']
    .reduce((sum, k) => sum + (u[k] || 0), 0)
}

export function resetUsage() {
  try {
    localStorage.removeItem(KEY)
  } catch (e) { /* ignore */ }
}
