// ============================================================
// 后端 AI 提供者 · 复用现有 Spring /api/ai/* 接口
// 后端 AIService 里已有一套本地算法，此处只做协议适配
// ============================================================

import request from '@/utils/request'
import { AI_CONFIG } from '../config.js'

const EP = AI_CONFIG.backend.endpoints

function notImplemented(name) {
  const e = new Error(`后端 provider 未实现 ${name}`)
  e.code = 'NOT_IMPLEMENTED'
  return e
}

async function chat({ question = '', sessionId } = {}) {
  const res = await request({
    url: EP.chat,
    method: 'post',
    data: { message: question, sessionId },
    silent: true
  })
  const d = res?.data || {}
  return {
    ok: true,
    data: {
      content: d.answer || d.content || '',
      suggestions: [],
      sessionId: d.sessionId
    },
    meta: { provider: 'backend', model: 'spring-local-ai', at: Date.now() }
  }
}

async function correctWriting({ content = '', title = '', level = 'CET4' } = {}) {
  const res = await request({
    url: EP.correctWriting,
    method: 'post',
    data: { content, title, level },
    silent: true
  })
  return {
    ok: true,
    data: res?.data || {},
    meta: { provider: 'backend', model: 'spring-local-ai', at: Date.now() }
  }
}

async function grammarCheck({ text = '' } = {}) {
  const res = await request({ url: EP.grammarCheck, method: 'post', data: { text }, silent: true })
  return { ok: true, data: res?.data || {}, meta: { provider: 'backend', at: Date.now() } }
}

// 后端尚未提供的能力，直接抛错由上层降级到本地引擎
async function teacherDigest() { throw notImplemented('teacherDigest') }
async function teacherPreGrade() { throw notImplemented('teacherPreGrade') }
async function learningProfile() { throw notImplemented('learningProfile') }
async function plan() { throw notImplemented('plan') }
async function longSentence() { throw notImplemented('longSentence') }
async function speaking() { throw notImplemented('speaking') }
async function explainWrong() { throw notImplemented('explainWrong') }
async function vocabCards() { throw notImplemented('vocabCards') }

export default {
  name: 'backend',
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
  vocabCards
}
