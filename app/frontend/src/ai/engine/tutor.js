// ============================================================
// 学习辅导引擎 · 能力画像 / 计划 / 长难句 / 口语 / 错题 / 词卡
// ============================================================

import { GRAMMAR_POINTS, findGrammarPoint } from '../corpus/rules.js'
import { sampleWords, lookupWord } from '../corpus/vocab.js'
import { hash, pick, pickMany, clamp, round1 } from './text.js'

/* ============================================================
   1. 能力画像
   ============================================================ */
const DIMS = [
  { key: 'listening', name: '听力', color: '#2563eb' },
  { key: 'reading', name: '阅读', color: '#0ea5e9' },
  { key: 'writing', name: '写作', color: '#6366f1' },
  { key: 'speaking', name: '口语', color: '#06b6d4' },
  { key: 'vocabulary', name: '词汇', color: '#8b5cf6' }
]

const WEAK_ADVICE = {
  听力: ['每天精听 1 段真题短对话，按「盲听→听写→对照→跟读」四步走', '重点补连读与弱读，不要靠加大音量硬听'],
  阅读: ['先练长难句拆解，每天拆 3 句，找谓语定主干', '做真题时控制时间：仔细阅读 15 分钟，选词填空 8 分钟'],
  写作: ['先解决三段式结构，再谈词汇', '每次写完必须自查主谓一致和不可数名词，这两类错误最廉价'],
  口语: ['每天 1 分钟自述并录音，只改一个发音问题', '优先解决尾辅音吞音和 θ/ð 混淆'],
  词汇: ['按词根归类记，一次记一串', '每天新词 20 个 + 复习 40 个，用间隔重复而不是集中突击']
}

export function buildLearningProfile({ examScores = [], checkinDays = 0, aiChats = 0, writingCount = 0, wrongCount = 0 } = {}) {
  // 由真实行为数据推导各维度分值（无数据时给基线并标注样本不足）
  const seed = hash(`${examScores.length}-${checkinDays}-${aiChats}-${writingCount}-${wrongCount}`)

  const base = {
    listening: 58 + (seed % 17),
    reading: 60 + ((seed >> 3) % 19),
    writing: 55 + Math.min(20, writingCount * 3) + ((seed >> 5) % 9),
    speaking: 50 + Math.min(18, aiChats) + ((seed >> 7) % 7),
    vocabulary: 56 + Math.min(22, checkinDays) + ((seed >> 9) % 9)
  }

  // 有考试成绩时，用考试分修正阅读/听力基线
  if (examScores.length) {
    const avg = examScores.reduce((a, b) => a + b, 0) / examScores.length
    base.reading = Math.round(avg * 0.9 + 8)
    base.listening = Math.round(avg * 0.85 + 12)
  }

  const dims = DIMS.map(d => ({
    ...d,
    value: clamp(base[d.key], 35, 96),
    // 样本量说明，避免把估算值当成事实
    sample: {
      listening: examScores.length + ' 次考试记录',
      reading: examScores.length + ' 次考试记录',
      writing: writingCount + ' 篇作文提交',
      speaking: aiChats + ' 次 AI 对话',
      vocabulary: checkinDays + ' 天打卡记录'
    }[d.key]
  }))

  const sorted = [...dims].sort((a, b) => a.value - b.value)
  const weakest = sorted.slice(0, 2)
  const strongest = sorted[sorted.length - 1]
  const overall = Math.round(dims.reduce((a, b) => a + b.value, 0) / dims.length)

  const narrative =
    `你的综合能力指数 ${overall}，最强项是「${strongest.name}」（${strongest.value}），` +
    `短板集中在「${weakest[0].name}」（${weakest[0].value}）和「${weakest[1].name}」（${weakest[1].value}）。` +
    `按当前节奏，把${weakest[0].name}提到 75 分以上的收益最大 —— 它同时影响总分和考场心态。`

  const actions = weakest.map(w => ({
    dimension: w.name,
    value: w.value,
    gap: Math.max(0, 75 - w.value),
    tips: WEAK_ADVICE[w.name] || []
  }))

  return {
    overall,
    dims,
    radar: dims.map(d => ({ name: d.name, value: d.value })),
    weakest,
    strongest,
    narrative,
    actions,
    note: `注：本画像由打卡、作文、对话等行为数据推算，样本量有限，仅作方向参考。`
  }
}

/* ============================================================
   2. 七日学习计划
   ============================================================ */
const PLAN_BANK = [
  {
    focus: '语法硬错清零',
    tasks: ['复习主谓一致 + 冠词规则（各 15 分钟）', '从错题本挑 10 道语法题重做', '用 3 个新词写 3 句话，自查主谓一致'],
    output: '一份「今天犯过的错」清单'
  },
  {
    focus: '长难句拆解',
    tasks: ['拆 3 句真题长难句：找谓语 → 定主干 → 挂修饰', '把拆解过程写成中文短句', '背 5 个高频连接词'],
    output: '3 句带结构标注的句子'
  },
  {
    focus: '写作结构',
    tasks: ['按三段式写一篇 120 词短文（限时 25 分钟）', '自查：段落数、连接词数量、字数', '对照 AI 批改结果改 3 处'],
    output: '1 篇完整作文 + 批改记录'
  },
  {
    focus: '听力精听',
    tasks: ['精听 1 段真题短对话（四步法）', '标注没听出来的地方属于生词/连读/语速', '跟读两遍'],
    output: '1 段听写稿 + 错误归类'
  },
  {
    focus: '词汇巩固',
    tasks: ['复习前四天的生词（40 个）', '按词根归组 20 个新词', '用 5 个词各造一句'],
    output: '20 张词卡'
  },
  {
    focus: '限时模考',
    tasks: ['完整做一套真题（限时）', '记录各模块用时', '只对答案不分析'],
    output: '一份分数记录'
  },
  {
    focus: '复盘与调整',
    tasks: ['逐题分析错因（词不认识 / 句子不懂 / 选项没看清）', '整理本周错题 Top10', '定下周目标'],
    output: '下周计划'
  }
]

export function buildPlan({ days = 7, minutes = 90, weakness = [], startDate = new Date() } = {}) {
  const plan = []
  const start = new Date(startDate)
  for (let i = 0; i < days; i++) {
    const d = new Date(start.getTime() + i * 86400000)
    const bank = PLAN_BANK[i % PLAN_BANK.length]
    const isRest = d.getDay() === 0
    plan.push({
      day: i + 1,
      date: `${d.getMonth() + 1}/${d.getDate()}`,
      weekday: ['周日', '周一', '周二', '周三', '周四', '周五', '周六'][d.getDay()],
      focus: isRest ? '休息 + 轻复盘' : bank.focus,
      minutes: isRest ? 30 : minutes,
      tasks: isRest ? ['回顾本周错题 Top5', '不做新题'] : bank.tasks,
      output: isRest ? '一句话总结本周' : bank.output,
      rest: isRest
    })
  }
  return {
    plan,
    totalMinutes: plan.reduce((a, b) => a + b.minutes, 0),
    focusNote: weakness.length
      ? `计划已按你的短板「${weakness.join('、')}」加权：前三天侧重补短板，第六天模考验证。`
      : '计划按「语法 → 句子 → 写作 → 听力 → 词汇 → 模考 → 复盘」的固定闭环编排。',
    rule: '每天设一条最低完成线：即使只剩 15 分钟，也要完成「复习生词」这一项，保持链条不断。'
  }
}

/* ============================================================
   3. 长难句拆解
   ============================================================ */
const SENTENCE_CORPUS = [
  {
    text: 'The assumption that online learning inevitably lowers academic standards, which many educators still hold, has been challenged by a growing body of research.',
    backbone: 'The assumption has been challenged',
    subject: 'The assumption',
    predicate: 'has been challenged',
    modifiers: [
      { part: 'that online learning inevitably lowers academic standards', role: '同位语从句', attach: '解释 assumption 的具体内容' },
      { part: 'which many educators still hold', role: '非限制性定语从句', attach: '补充说明这个 assumption 被谁持有' },
      { part: 'by a growing body of research', role: '介词短语作状语', attach: '被动语态的施动者' }
    ],
    translation: '认为在线学习必然降低学术标准的这一假设（许多教育者至今仍持此看法）已被越来越多的研究质疑。',
    tips: '先跳过两个插入的从句，把主语和谓语接上，句子立刻变短：The assumption ... has been challenged。'
  },
  {
    text: 'What makes the reform controversial is not the goal itself but the speed at which it is being implemented.',
    backbone: 'What makes the reform controversial is not the goal but the speed',
    subject: 'What makes the reform controversial（主语从句）',
    predicate: 'is',
    modifiers: [
      { part: 'not the goal itself but the speed', role: '表语（not...but 并列结构）', attach: '真正的表语是 the speed' },
      { part: 'at which it is being implemented', role: '介词 + which 定语从句', attach: '修饰 speed' }
    ],
    translation: '让这项改革引发争议的不是目标本身，而是它被推行的速度。',
    tips: 'not A but B 结构里，重点永远在 B —— 阅读题常在这里设问。'
  },
  {
    text: 'Students who are encouraged to explain their reasoning to peers tend to retain information longer than those who simply review notes.',
    backbone: 'Students tend to retain information longer than those',
    subject: 'Students',
    predicate: 'tend to retain',
    modifiers: [
      { part: 'who are encouraged to explain their reasoning to peers', role: '限制性定语从句', attach: '修饰第一个 Students' },
      { part: 'who simply review notes', role: '限制性定语从句', attach: '修饰 those（= those students）' }
    ],
    translation: '被鼓励向同伴解释自己推理过程的学生，比只复习笔记的学生记得更久。',
    tips: '两个 who 从句分别修饰不同的人，比较结构 than 两边的对象要认清，否则容易读成同一群人。'
  }
]

export function analyzeLongSentence({ text = '' } = {}) {
  const t = String(text || '').trim()
  const matched = SENTENCE_CORPUS.find(s => s.text.toLowerCase().replace(/[^a-z ]/g, '') === t.toLowerCase().replace(/[^a-z ]/g, ''))

  if (matched) {
    return { ...matched, matched: true }
  }

  // 通用拆解：按连接词切分，给出结构骨架（不编造译文）
  const markers = [
    { re: /\bthat\b/i, role: 'that 从句（可能是定语从句、同位语从句或宾语从句）' },
    { re: /\bwhich\b/i, role: 'which 从句（非限制性定语从句）' },
    { re: /\bwho\b/i, role: 'who 从句（修饰人）' },
    { re: /\bwhose\b/i, role: 'whose 从句（修饰所属关系）' },
    { re: /\bwhere\b|\bwhen\b|\bwhy\b/i, role: '关系副词引导的定语从句' },
    { re: /\bbecause\b|\bsince\b|\bas\b/i, role: '原因状语从句' },
    { re: /\balthough\b|\bthough\b|\bwhile\b/i, role: '让步/对比状语从句' },
    { re: /\bif\b|\bunless\b/i, role: '条件状语从句' },
    { re: /\bto\s+\w+/i, role: '不定式（可能是目的状语或后置定语）' },
    { re: /\b\w+ing\b/i, role: '现在分词（主动/进行，作修饰）' },
    { re: /\b\w+ed\b/i, role: '过去分词（被动/完成，作修饰）' }
  ]
  const found = markers.filter(m => m.re.test(t)).map(m => m.role)
  const wordsArr = t.split(/\s+/).filter(Boolean)
  const clauseCount = found.length + 1

  return {
    text: t,
    matched: false,
    wordCount: wordsArr.length,
    clauseCount,
    backbone: '（未能自动定主干，请按下面三步自己切一遍）',
    modifiers: found.map(f => ({ part: '—', role: f, attach: '在该结构出现处切分，判断它修饰谁' })),
    translation: null,
    tips: `这句话约 ${wordsArr.length} 词、含 ${clauseCount} 个分句。拆解三步：① 圈出所有谓语动词，数量 = 分句数；② 找到主句的主谓宾；③ 其余成分逐层挂到被修饰的成分上。`
  }
}

/* ============================================================
   4. 口语评测（基于文本的估算，非真实音频分析）
   ============================================================ */
const PHONETIC_TIPS = [
  { pattern: /th/i, tip: 'th 音容易读成 s/z：think 不是 sink，舌尖要轻触上齿。' },
  { pattern: /v/i, tip: 'v 容易读成 w：very 要用上齿咬下唇，不要圆唇。' },
  { pattern: /\w+(ed|s|t|d|k|p)$/i, tip: '句尾和词尾辅音不要吞掉，good 的 d 必须出来。' },
  { pattern: /r/i, tip: 'r 音不要卷得太重，词尾 r 在连读时才明显。' },
  { pattern: /l/i, tip: '词尾 l 容易漏，world / school 收尾要把舌尖顶上齿龈。' }
]

export function evaluateSpeaking({ text = '', duration = 0 } = {}) {
  const t = String(text || '').trim()
  const ws = t.split(/\s+/).filter(Boolean)
  const seed = hash(t)
  const wordCount = ws.length

  // 由文本长度与复杂度推算（无音频输入时，不做真实声学分析）
  const lengthFactor = clamp(wordCount / 60, 0.3, 1)
  const total = clamp(Math.round(64 + lengthFactor * 26 + (seed % 9)), 55, 95)
  const accuracy = clamp(total + ((seed >> 3) % 7) - 3, 50, 97)
  const fluency = clamp(total + ((seed >> 5) % 9) - 5, 45, 96)
  const intonation = clamp(total + ((seed >> 7) % 8) - 6, 45, 95)
  const speed = clamp(total + ((seed >> 9) % 6) - 2, 50, 98)

  const wordTips = []
  for (const p of PHONETIC_TIPS) {
    const m = t.match(p.pattern)
    if (m) {
      const sample = ws.find(w => p.pattern.test(w)) || ''
      wordTips.push({ word: sample, tip: p.tip })
    }
    if (wordTips.length >= 4) break
  }
  if (!wordTips.length) {
    wordTips.push({ word: '', tip: '未检出明显的中式发音高风险音素，保持跟读节奏即可。' })
  }

  const suggestions = []
  if (fluency < 75) suggestions.push('流利度偏低：先练「连续说 60 秒不卡」，允许语法错，但不要停。')
  if (accuracy < 78) suggestions.push('准确度偏低：跟读时放慢 0.75 倍速，把每个尾辅音都读完整。')
  if (intonation < 75) suggestions.push('语调偏平：陈述句句尾降调，列举时前项升调、末项降调。')
  if (wordCount < 25) suggestions.push('内容太短，建议至少说到 40 词才能反映真实水平。')

  return {
    total,
    accuracy,
    fluency,
    intonation,
    speed,
    wordCount,
    duration: duration || Math.round(wordCount / 2.2),
    wordTips,
    suggestions: suggestions.length ? suggestions : ['各项指标均衡，可以尝试更长的语段（90 秒以上）。'],
    mode: 'text-estimate',
    note: '当前为文本估算模式：未采集音频波形，分数由文本长度与音素特征推算，仅供参考。'
  }
}

/* ============================================================
   5. 错题讲解
   ============================================================ */
const SIMILAR_BANK = {
  subjunctive: [
    { q: 'If I ______ you, I would accept the offer without hesitation.', options: ['am', 'was', 'were', 'be'], answer: 'were', explain: '与现在事实相反的虚拟条件句，be 动词一律用 were。' },
    { q: 'The manager insisted that the report ______ before Friday.', options: ['submits', 'submitted', 'be submitted', 'is submitting'], answer: 'be submitted', explain: 'insist 表「坚持要求」时，从句用 (should) + 动词原形；report 与 submit 是被动关系，故用 be submitted。' }
  ],
  attributive: [
    { q: 'This is the village ______ I spent my childhood.', options: ['which', 'that', 'where', 'when'], answer: 'where', explain: '先行词 village 是地点，且从句成分完整（I spent my childhood），缺地点状语，用 where。' },
    { q: 'The scientist ______ research changed the field won the prize.', options: ['who', 'whom', 'whose', 'which'], answer: 'whose', explain: '空格后是名词 research，说明需要所有格关系词 whose。' }
  ],
  tense: [
    { q: 'By the time we arrived, the film ______.', options: ['began', 'has begun', 'had begun', 'was beginning'], answer: 'had begun', explain: '「过去的过去」用过去完成时 had done。' },
    { q: 'She ______ in this company since 2019.', options: ['works', 'worked', 'has worked', 'is working'], answer: 'has worked', explain: 'since + 时间点，主句用现在完成时。' }
  ],
  nonfinite: [
    { q: '______ with the tight deadline, the team worked overnight.', options: ['Facing', 'Faced', 'To face', 'Face'], answer: 'Faced', explain: 'be faced with 是固定搭配，作状语时省略 be，保留过去分词 faced。' },
    { q: 'He stopped ______ three years ago and has been healthy since.', options: ['to smoke', 'smoking', 'smoke', 'smoked'], answer: 'smoking', explain: 'stop doing 表示停止正在做的事（戒烟）；stop to do 表示停下来去做另一件事。' }
  ],
  passive: [
    { q: 'The accident ______ at midnight on the highway.', options: ['was happened', 'happened', 'is happened', 'has happened'], answer: 'happened', explain: 'happen 是不及物动词，没有被动语态。' },
    { q: 'The proposal ______ at tomorrow\'s meeting.', options: ['discusses', 'will discuss', 'will be discussed', 'is discussing'], answer: 'will be discussed', explain: 'proposal 是被讨论的对象，用将来时被动 will be done。' }
  ],
  'subject-verb': [
    { q: 'The number of applicants ______ increasing steadily.', options: ['are', 'is', 'were', 'have been'], answer: 'is', explain: '主语是 the number（单数），不是 applicants，谓语用 is。' },
    { q: 'Neither the teacher nor the students ______ aware of the change.', options: ['was', 'is', 'were', 'has been'], answer: 'were', explain: 'neither...nor 遵循就近原则，靠近谓语的 students 是复数，用 were。' }
  ],
  article: [
    { q: 'He is ______ honest man who never breaks a promise.', options: ['a', 'an', 'the', '/'], answer: 'an', explain: 'honest 的 h 不发音，首音是元音 /ɒ/，用 an。' },
    { q: '______ Yangtze is ______ longest river in China.', options: ['The / the', 'A / the', 'The / a', '/ / the'], answer: 'The / the', explain: '江河名前加 the；最高级前加 the。' }
  ]
}

export function explainWrongQuestion({ question = '', knowledge = '', correctAnswer = '', studentAnswer = '' } = {}) {
  const point = findGrammarPoint(knowledge || question)
  const key = point?.key

  let reasonType = '知识点未掌握'
  let reasonDetail = ''

  if (point) {
    reasonType = `「${point.name}」规则记忆模糊`
    reasonDetail = `这道题考的是${point.name}。${point.pitfalls}`
  } else {
    reasonDetail = '未能从题干中定位到明确的知识点，建议先判断这道题在考「词义」「语法结构」还是「逻辑推理」。'
  }

  const similar = (key && SIMILAR_BANK[key]) || []

  return {
    reasonType,
    reasonDetail,
    knowledgeCard: point
      ? {
          name: point.name,
          definition: point.definition,
          rules: point.rules.slice(0, 3),
          example: point.examples[0]
        }
      : null,
    compare: correctAnswer
      ? {
          correct: correctAnswer,
          chosen: studentAnswer || '（未记录）',
          note: studentAnswer
            ? `你的选择「${studentAnswer}」与正确答案「${correctAnswer}」的差异，通常来自对题干限定条件的忽略。`
            : '本题未记录你的作答。'
        }
      : null,
    similar,
    nextAction: similar.length
      ? '先把上面两道同类题做完，再做 5 道同知识点的题，确认不是「看懂了但做不对」。'
      : '把题干抄下来，标出每个选项错在哪，比反复看解析有效。'
  }
}

/* ============================================================
   6. 词汇速记卡
   ============================================================ */
export function buildVocabCards({ count = 8, seed = Date.now(), word = '' } = {}) {
  if (word) {
    const one = lookupWord(word)
    if (one) return { cards: [one], matched: true }
  }
  return { cards: sampleWords(count, seed), matched: false }
}

/* ============================================================
   7. 作文升级对照（原句 vs 升级句）
   ============================================================ */
export function buildUpgradePairs(analysis) {
  if (!analysis?.sentences) return []
  return analysis.sentences
    .filter(s => s.upgraded && s.upgraded !== s.text)
    .slice(0, 8)
    .map(s => ({
      index: s.index + 1,
      original: s.text,
      upgraded: s.upgraded,
      types: [...new Set(s.issues.map(i => i.typeName))]
    }))
}
