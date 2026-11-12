// ============================================================
// 作文分析引擎 · 逐句标注 + 四维评分 + 改写建议
// 所有分数都由可解释的指标算出，不是随机数
// ============================================================

import { RULES, UPGRADES, ALL_CONNECTIVES, CONNECTIVES } from '../corpus/rules.js'
import { splitSentences, splitParagraphs, textStats, words, clamp, round1, hash, pick } from './text.js'

const LEVEL_REQUIREMENT = {
  CET4: 120,
  CET6: 150,
  TEM4: 200,
  IELTS: 250,
  TOEFL: 300,
  DEFAULT: 120
}

const TYPE_CN = {
  grammar: '语法',
  word: '用词',
  collocation: '搭配',
  chinglish: '中式表达',
  punct: '标点',
  style: '表达'
}

/** 在文本中执行一条规则，返回命中列表（含位置） */
function scanRule(rule, text, offset = 0) {
  const flags = rule.re.flags.includes('g') ? rule.re.flags : rule.re.flags + 'g'
  const re = new RegExp(rule.re.source, flags)
  const hits = []
  let m
  while ((m = re.exec(text)) !== null) {
    const arr = Array.from(m)
    if (rule.test && !rule.test(arr)) {
      if (m.index === re.lastIndex) re.lastIndex++
      continue
    }
    let suggestion = m[0]
    try {
      suggestion = rule.fix(arr)
    } catch (e) {
      suggestion = m[0]
    }
    hits.push({
      ruleId: rule.id,
      type: rule.type,
      typeName: TYPE_CN[rule.type] || rule.type,
      label: rule.label,
      original: m[0],
      suggestion,
      explain: rule.explain,
      index: offset + m.index
    })
    if (m[0].length === 0) re.lastIndex++
  }
  return hits
}

/** 扫描整段文本的所有问题 */
export function scan(text) {
  const issues = []
  for (const rule of RULES) {
    issues.push(...scanRule(rule, text))
  }
  return issues.sort((a, b) => a.index - b.index)
}

/** 按规则修复文本 */
function repair(text) {
  let s = text
  for (const rule of RULES) {
    const flags = rule.re.flags.includes('g') ? rule.re.flags : rule.re.flags + 'g'
    const re = new RegExp(rule.re.source, flags)
    const out = []
    let last = 0
    let m
    while ((m = re.exec(s)) !== null) {
      const arr = Array.from(m)
      if (rule.test && !rule.test(arr)) {
        if (m.index === re.lastIndex) re.lastIndex++
        continue
      }
      let fixed = m[0]
      try {
        fixed = rule.fix(arr)
      } catch (e) {
        fixed = m[0]
      }
      out.push(s.slice(last, m.index), fixed)
      last = m.index + m[0].length
      if (m[0].length === 0) re.lastIndex++
    }
    out.push(s.slice(last))
    s = out.join('')
  }
  return s
}

/** 统计连接词使用情况 */
function countConnectives(text) {
  const s = text.toLowerCase()
  const used = []
  for (const c of ALL_CONNECTIVES) {
    const re = new RegExp(`\\b${c.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')}\\b`, 'g')
    const n = (s.match(re) || []).length
    if (n > 0) used.push({ word: c, count: n })
  }
  return { used, total: used.reduce((a, b) => a + b.count, 0) }
}

/** 标题关键词覆盖率（判断是否跑题） */
function titleCoverage(title, content) {
  const tw = words(title || '').filter(w => w.length > 3)
  if (!tw.length) return null
  const lower = content.toLowerCase()
  const hit = tw.filter(w => lower.includes(w.toLowerCase()))
  return { total: tw.length, hit: hit.length, words: hit }
}

/** 找出可升级的基础表达 */
function findUpgrades(content) {
  const out = []
  for (const u of UPGRADES) {
    const re = new RegExp(u.re.source, 'gi')
    const n = (content.match(re) || []).length
    if (n > 0) out.push({ word: re.source.replace(/\\b/g, ''), to: u.to, note: u.note, count: n })
  }
  return out.sort((a, b) => b.count - a.count).slice(0, 5)
}

function levelRequired(level) {
  return LEVEL_REQUIREMENT[String(level || '').toUpperCase()] || LEVEL_REQUIREMENT.DEFAULT
}

/**
 * 核心：分析一篇作文
 */
export function analyzeWriting({ content = '', title = '', level = 'CET4' } = {}) {
  const text = String(content || '').trim()
  const stats = textStats(text)
  const sentences = splitSentences(text)
  const paragraphs = splitParagraphs(text)
  const allIssues = scan(text).filter(it => it.ruleId !== 'lowercase-after-period')
  const connectives = countConnectives(text)
  const coverage = titleCoverage(title, text)
  const upgrades = findUpgrades(text)

  // ---------- 逐句标注 ----------
  let cursor = 0
  const extraIssues = []
  const sentenceDetail = sentences.map((s, i) => {
    const pos = text.indexOf(s, cursor)
    const start = pos >= 0 ? pos : cursor
    cursor = start + s.length
    // 只归属「完整落在本句内」的问题，跨句匹配归到后一句处理
    const sIssues = allIssues
      .filter(it => it.index >= start && it.index + it.original.length <= start + s.length)
      .map(it => ({ ...it }))

    // 句首小写：按句处理，避免跨句错配
    if (/^[a-z]/.test(s)) {
      const issue = {
        ruleId: 'sentence-start-case',
        type: 'punct',
        typeName: '标点',
        label: '句首大写',
        original: s.charAt(0),
        suggestion: s.charAt(0).toUpperCase(),
        explain: '每个句子的第一个字母必须大写，这是卷面分最容易被扣的细节之一。',
        index: start
      }
      sIssues.unshift(issue)
      extraIssues.push(issue)
    }

    const fixed = sIssues.length ? repair(s) : s
    return {
      index: i,
      text: s,
      issues: sIssues.map(it => ({
        type: it.type,
        typeName: it.typeName,
        label: it.label,
        original: it.original,
        suggestion: it.suggestion,
        explain: it.explain
      })),
      upgraded: fixed !== s ? fixed : null,
      wordCount: words(s).length
    }
  })

  const mergedIssues = [...allIssues, ...extraIssues].sort((a, b) => a.index - b.index)

  // ---------- 分类计数 ----------
  const byType = { grammar: 0, word: 0, collocation: 0, chinglish: 0, punct: 0, style: 0 }
  mergedIssues.forEach(it => { byType[it.type] = (byType[it.type] || 0) + 1 })
  const realErrors = byType.grammar + byType.word + byType.collocation + byType.chinglish

  // ---------- 四维评分 ----------
  const required = levelRequired(level)

  // 内容：字数达标度 + 标题覆盖 + 段落完整
  let contentScore = 62
  const ratio = stats.wordCount / required
  if (ratio >= 1) contentScore += Math.min(16, 8 + (ratio - 1) * 12)
  else contentScore -= Math.round((1 - ratio) * 34)
  if (coverage) contentScore += Math.round((coverage.hit / coverage.total) * 12) - 4
  if (stats.paragraphCount >= 2) contentScore += 4
  if (stats.paragraphCount >= 3) contentScore += 3
  contentScore = clamp(Math.round(contentScore), 35, 97)

  // 结构：段落 + 连接词 + 句长变化
  let structureScore = 52
  structureScore += Math.min(stats.paragraphCount, 4) * 5
  structureScore += Math.min(connectives.total, 8) * 4
  if (stats.avgSentenceLength >= 12 && stats.avgSentenceLength <= 22) structureScore += 6
  if (stats.avgSentenceLength > 26) structureScore -= 6
  if (stats.longestSentence > 45) structureScore -= 4
  structureScore = clamp(Math.round(structureScore), 35, 97)

  // 语言：语法错误密度
  let languageScore = 96
  languageScore -= byType.grammar * 5
  languageScore -= byType.word * 4
  languageScore -= byType.collocation * 4
  languageScore -= byType.chinglish * 4
  languageScore -= byType.punct * 1.5
  if (stats.sentenceCount > 0) {
    const density = realErrors / stats.sentenceCount
    if (density > 0.8) languageScore -= 8
    else if (density > 0.4) languageScore -= 3
  }
  languageScore = clamp(Math.round(languageScore), 32, 98)

  // 词汇：多样度 + 长词比 + 高级表达
  let vocabularyScore = 58
  if (stats.uniqueRatio >= 0.62) vocabularyScore += 22
  else if (stats.uniqueRatio >= 0.52) vocabularyScore += 16
  else if (stats.uniqueRatio >= 0.42) vocabularyScore += 9
  else vocabularyScore += 3
  vocabularyScore += Math.min(14, Math.round(stats.longWordRatio * 160))
  vocabularyScore += Math.min(6, upgrades.length * 2)
  vocabularyScore -= byType.style * 2
  // 篇幅过短时，多样度指标天然偏高，需要修正，否则短作文词汇分虚高
  if (stats.wordCount < 100) vocabularyScore -= 10
  if (stats.wordCount < 60) vocabularyScore -= 8
  vocabularyScore = clamp(Math.round(vocabularyScore), 32, 98)

  const totalScore = round1(
    contentScore * 0.25 + structureScore * 0.25 + languageScore * 0.30 + vocabularyScore * 0.20
  )

  // ---------- 评分依据 ----------
  const dimensions = [
    {
      key: 'content',
      name: '内容切题',
      score: contentScore,
      weight: '25%',
      basis: [
        `字数 ${stats.wordCount} 词，${level} 要求约 ${required} 词（${ratio >= 1 ? '已达标' : `还差 ${Math.round(required - stats.wordCount)} 词`}）`,
        coverage ? `标题关键词命中 ${coverage.hit}/${coverage.total}${coverage.hit ? '：' + coverage.words.join(', ') : ''}` : '未填写标题，无法判断切题度',
        `共 ${stats.paragraphCount} 个段落`
      ]
    },
    {
      key: 'structure',
      name: '结构逻辑',
      score: structureScore,
      weight: '25%',
      basis: [
        `使用连接词 ${connectives.total} 处${connectives.used.length ? '（' + connectives.used.map(u => u.word).slice(0, 4).join('、') + '）' : '，几乎全靠简单句并列'}`,
        `平均句长 ${stats.avgSentenceLength} 词，最长 ${stats.longestSentence} 词`,
        stats.paragraphCount <= 1 ? '全文只有一个段落，缺少「引入—分析—结论」层次' : '段落数量合理'
      ]
    },
    {
      key: 'language',
      name: '语言准确',
      score: languageScore,
      weight: '30%',
      basis: [
        `语法错误 ${byType.grammar} 处，用词/搭配问题 ${byType.word + byType.collocation} 处，中式表达 ${byType.chinglish} 处`,
        `标点格式问题 ${byType.punct} 处`,
        `平均每句 ${stats.sentenceCount ? round1(realErrors / stats.sentenceCount) : 0} 个错误`
      ]
    },
    {
      key: 'vocabulary',
      name: '词汇丰富',
      score: vocabularyScore,
      weight: '20%',
      basis: [
        `词形多样性 ${round1(stats.uniqueRatio * 100)}%（去重 ${stats.uniqueCount}/${stats.wordCount}）`,
        `8 字母以上长词占比 ${round1(stats.longWordRatio * 100)}%`,
        upgrades.length ? `可升级的基础表达 ${upgrades.length} 组` : '未发现明显低阶表达堆积'
      ]
    }
  ]

  // ---------- 亮点 ----------
  const highlights = []
  if (stats.wordCount >= required) highlights.push(`字数 ${stats.wordCount} 词，达到 ${level} 写作要求，篇幅控制到位。`)
  if (stats.paragraphCount >= 3) highlights.push(`分段清晰（${stats.paragraphCount} 段），符合三段式结构。`)
  if (connectives.total >= 4) highlights.push(`使用了 ${connectives.total} 处连接词，句间逻辑衔接意识较好。`)
  if (stats.uniqueRatio >= 0.58) highlights.push('词汇重复率低，说明有一定的同义替换能力。')
  if (byType.grammar === 0) highlights.push('通篇未检出主谓一致、冠词等硬性语法错误。')
  if (!highlights.length) highlights.push('已具备完整表达意图，接下来重点补语法准确度与段落层次。')

  // ---------- 改进清单 ----------
  const checklist = []
  if (ratio < 1) {
    checklist.push({
      priority: '高',
      title: '补足字数',
      detail: `还差约 ${Math.round(required - stats.wordCount)} 词。建议在第二段每个论点后补一句「解释 + 例子」，比硬凑形容词更自然。`
    })
  }
  if (stats.paragraphCount <= 1) {
    checklist.push({
      priority: '高',
      title: '拆成三段',
      detail: '第一段交代现象，第二段给 2 个分论点，第三段表明立场并提一条建议。段落是最容易拿分也最容易丢分的地方。'
    })
  }
  if (byType.grammar > 0) {
    const top = findTopLabel(mergedIssues, 'grammar')
    checklist.push({
      priority: '高',
      title: `优先修掉「${top}」`,
      detail: `本文有 ${byType.grammar} 处语法硬错${top ? '，集中在「' + top + '」' : ''}。这类错误阅卷时直接扣语言分，性价比最高的动作是先清零。`
    })
  }
  if (byType.chinglish + byType.collocation > 0) {
    checklist.push({
      priority: '中',
      title: '替换中式搭配',
      detail: `检出 ${byType.chinglish + byType.collocation} 处中式表达或搭配错误，建议整块记搭配（如 acquire knowledge），不要逐字翻译。`
    })
  }
  if (connectives.total < 3) {
    checklist.push({
      priority: '中',
      title: '补连接词',
      detail: '目前句间关系基本靠并列。至少补 3 个：开头用 To begin with，转折用 However，收尾用 Therefore / In conclusion。'
    })
  }
  if (upgrades.length) {
    checklist.push({
      priority: '中',
      title: '升级基础词',
      detail: `可用更精确的词替换：${upgrades.slice(0, 3).map(u => `${u.word} → ${u.to}`).join('；')}。`
    })
  }
  if (stats.avgSentenceLength < 9) {
    checklist.push({
      priority: '低',
      title: '拉开句长变化',
      detail: `平均句长仅 ${stats.avgSentenceLength} 词，全是短句。试着把两句合成一个含定语从句的长句，再跟一个短句收尾，读起来更有节奏。`
    })
  }
  if (!checklist.length) {
    checklist.push({
      priority: '低',
      title: '冲高分',
      detail: '基础层面已无明显问题，下一步可以练「观点句 + 数据/例子支撑」，让论证更具体。'
    })
  }

  // ---------- 修正全文 ----------
  const correctedContent = paragraphs.map(p => repair(p)).join('\n\n')

  // ---------- 总评 ----------
  const band = totalScore >= 85 ? '优秀' : totalScore >= 75 ? '良好' : totalScore >= 60 ? '及格' : '待加强'
  const weakest = [...dimensions].sort((a, b) => a.score - b.score)[0]
  const strongest = [...dimensions].sort((a, b) => b.score - a.score)[0]
  const summary = [
    `本篇 ${stats.wordCount} 词，综合得分 ${totalScore}（${band}）。`,
    `最强项是「${strongest.name}」${strongest.score} 分，最弱项是「${weakest.name}」${weakest.score} 分。`,
    realErrors > 0
      ? `全文检出 ${realErrors} 处语言硬错，其中${findTopLabel(mergedIssues, null) || '语法与搭配'}占比最高，这是当前最该先修的部分。`
      : '未检出明显语言硬错，问题主要集中在表达层次与论证深度上。',
    ratio < 1 ? `字数距 ${level} 要求还差 ${Math.round(required - stats.wordCount)} 词，篇幅不足会直接影响内容分。` : ''
  ].filter(Boolean).join('')

  return {
    wordCount: stats.wordCount,
    sentenceCount: stats.sentenceCount,
    paragraphCount: stats.paragraphCount,
    avgSentenceLength: stats.avgSentenceLength,
    uniqueRatio: round1(stats.uniqueRatio * 100),
    scores: {
      total: totalScore,
      content: contentScore,
      structure: structureScore,
      language: languageScore,
      vocabulary: vocabularyScore
    },
    dimensions,
    radar: dimensions.map(d => ({ name: d.name, value: d.score })),
    issues: mergedIssues.map((it, i) => ({ ...it, id: i + 1 })),
    issueCount: realErrors,
    issueSummary: Object.entries(byType)
      .filter(([, n]) => n > 0)
      .map(([k, n]) => ({ type: k, name: TYPE_CN[k], count: n })),
    sentences: sentenceDetail,
    highlights,
    checklist,
    upgrades,
    connectives: connectives.used,
    correctedContent,
    summary,
    band
  }
}

function findTopLabel(issues, type) {
  const map = {}
  issues
    .filter(it => (type ? it.type === type : it.type !== 'punct' && it.type !== 'style'))
    .forEach(it => { map[it.label] = (map[it.label] || 0) + 1 })
  const arr = Object.entries(map).sort((a, b) => b[1] - a[1])
  return arr.length ? arr[0][0] : null
}

export { TYPE_CN, repair, levelRequired }
