// ============================================================
// 本地伪 AI 提供者 · 真正的「思考」全在 ai/engine 里
// 这一层只负责：参数校验 → 调引擎 → 统一返回结构 → 模拟耗时
// ============================================================

import { AI_CONFIG } from '../config.js'
import { generateReply } from '../engine/chat.js'
import { analyzeWriting, scan } from '../engine/writing.js'
import { buildTeacherDigest, preGrade } from '../engine/teacher.js'
import {
  buildLearningProfile,
  buildPlan,
  analyzeLongSentence,
  evaluateSpeaking,
  explainWrongQuestion,
  buildVocabCards,
  buildUpgradePairs
} from '../engine/tutor.js'
import { sleep } from '../engine/text.js'

const MODEL_TAG = 'local-rule-engine-v1'

function randLatency() {
  const [a, b] = AI_CONFIG.latency || [0, 0]
  if (!b) return 0
  return Math.round(a + Math.random() * (b - a))
}

async function wrap(fn) {
  const t0 = Date.now()
  await sleep(randLatency())
  const data = fn()
  return {
    ok: true,
    data,
    meta: {
      provider: 'local',
      model: MODEL_TAG,
      latency: Date.now() - t0,
      at: Date.now()
    }
  }
}

/* ---------------- 对话 ---------------- */
async function chat({ question = '', messages = [] } = {}) {
  if (!question || !String(question).trim()) {
    const err = new Error('请输入问题内容')
    err.code = 'EMPTY_INPUT'
    throw err
  }
  const history = (messages || []).map(m => ({ role: m.role, content: m.content }))
  return wrap(() => {
    const res = generateReply({ question, history })
    return {
      content: res.content,
      suggestions: res.suggestions || [],
      scene: 'chat'
    }
  })
}

/* ---------------- 作文批改 ---------------- */
async function correctWriting({ content = '', title = '', level = 'CET4' } = {}) {
  if (!content || String(content).trim().length < 10) {
    const err = new Error('作文内容太短，至少输入一句完整的话')
    err.code = 'EMPTY_INPUT'
    throw err
  }
  return wrap(() => {
    const analysis = analyzeWriting({ content, title, level })
    return {
      ...analysis,
      upgradePairs: buildUpgradePairs(analysis)
    }
  })
}

/* ---------------- 语法检查 ---------------- */
async function grammarCheck({ text = '' } = {}) {
  return wrap(() => {
    const issues = scan(String(text || ''))
    return {
      count: issues.length,
      issues,
      score: Math.max(32, 98 - issues.length * 4)
    }
  })
}

/* ---------------- 教师：班级汇总 ---------------- */
async function teacherDigest({ homeworks = [], students = [] } = {}) {
  return wrap(() => buildTeacherDigest({ homeworks, students }))
}

/* ---------------- 教师：单篇预批改 ---------------- */
async function teacherPreGrade(payload = {}) {
  return wrap(() => preGrade(payload))
}

/* ---------------- 学习画像 ---------------- */
async function learningProfile(payload = {}) {
  return wrap(() => buildLearningProfile(payload))
}

/* ---------------- 学习计划 ---------------- */
async function plan(payload = {}) {
  return wrap(() => buildPlan(payload))
}

/* ---------------- 长难句 ---------------- */
async function longSentence({ text = '' } = {}) {
  if (!text || String(text).trim().split(/\s+/).length < 5) {
    const err = new Error('请粘贴一句完整的英文长句（至少 5 个单词）')
    err.code = 'EMPTY_INPUT'
    throw err
  }
  return wrap(() => analyzeLongSentence({ text }))
}

/* ---------------- 口语评测 ---------------- */
async function speaking(payload = {}) {
  if (!payload.text) {
    const err = new Error('请先输入要评测的文本')
    err.code = 'EMPTY_INPUT'
    throw err
  }
  return wrap(() => evaluateSpeaking(payload))
}

/* ---------------- 错题讲解 ---------------- */
async function explainWrong(payload = {}) {
  return wrap(() => explainWrongQuestion(payload))
}

/* ---------------- 词汇卡 ---------------- */
async function vocabCards(payload = {}) {
  return wrap(() => buildVocabCards(payload))
}

export default {
  name: 'local',
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
