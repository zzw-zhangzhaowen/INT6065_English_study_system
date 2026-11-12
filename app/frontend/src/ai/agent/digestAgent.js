// ============================================================
// 教师端后台分析 Agent
// 登录后静默运行：定时拉取提交 → 本地汇总 → 缓存结果
// 教师打开页面即见「上次分析」结果，无需手动等待
// ============================================================

import { ai } from '../index.js'
import { AI_CONFIG } from '../config.js'
import { digestInput } from '@/mock/db'

const CACHE_KEY = 'teacher_digest_v1'

/**
 * 取分析输入。
 * TODO(接后端)：把这里换成 homeworkApi.getTeacherHomeworks + getHomeworkSubmissions，
 * 返回结构保持 { homeworks:[{id,title,level,submissions:[...]}], students:[{id,name}] } 即可，
 * 引擎无需改动。
 */
export function loadDigestInput(homeworkIds = [501]) {
  return digestInput(homeworkIds)
}

export function readCache() {
  try {
    const raw = localStorage.getItem(CACHE_KEY)
    return raw ? JSON.parse(raw) : null
  } catch (e) {
    return null
  }
}

function writeCache(digest, homeworkIds) {
  if (!AI_CONFIG.teacher.persist) return
  try {
    localStorage.setItem(CACHE_KEY, JSON.stringify({ digest, homeworkIds, at: Date.now() }))
  } catch (e) { /* 忽略容量异常 */ }
}

/**
 * 执行一次完整汇总
 * @returns {Promise<Object>} digest
 */
export async function runDigest(homeworkIds = [501]) {
  const input = loadDigestInput(homeworkIds)
  const res = await ai.teacherDigest(input)
  writeCache(res.data, homeworkIds)
  return res.data
}

/**
 * 启动后台定时 Agent
 * @param {Object} opts
 * @param {number} opts.intervalMinutes 间隔分钟数
 * @param {Function} opts.onUpdate 每次完成后的回调 (digest) => void
 * @param {boolean} opts.immediate 是否立刻跑一次
 * @returns {Function} stop 停止函数
 */
export function startDigestAgent({ intervalMinutes = AI_CONFIG.teacher.digestIntervalMinutes, onUpdate, immediate = true } = {}) {
  let timer = null
  let running = false

  const tick = async () => {
    if (running) return
    running = true
    try {
      const digest = await runDigest()
      onUpdate && onUpdate(digest)
    } catch (e) {
      console.warn('[digest-agent] 分析失败', e)
    } finally {
      running = false
    }
  }

  if (immediate) tick()
  timer = setInterval(tick, Math.max(1, intervalMinutes) * 60 * 1000)

  return () => {
    if (timer) clearInterval(timer)
    timer = null
  }
}

export function timeAgo(at) {
  if (!at) return '尚未分析'
  const diff = Date.now() - at
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)} 分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)} 小时前`
  return `${Math.floor(diff / 86400000)} 天前`
}
