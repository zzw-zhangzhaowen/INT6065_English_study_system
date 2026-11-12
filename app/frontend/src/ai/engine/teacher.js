// ============================================================
// 教师端分析引擎 · 班级提交汇总 / 共性问题 / 讲评稿
// 输入真实的提交数据，输出可讲、可复制、可追溯的简报
// ============================================================

import { analyzeWriting } from './writing.js'
import { round1, clamp, hash, pickMany } from './text.js'

const TYPE_CN = {
  grammar: '语法',
  word: '用词',
  collocation: '搭配',
  chinglish: '中式表达',
  punct: '标点',
  style: '表达'
}

/**
 * 汇总一次作业（或多次作业）的提交
 * @param {Object} opts
 * @param {Array}  opts.homeworks  [{ id, title, level, submissions: [{ studentId, studentName, content, submitTime, score }] }]
 * @param {Array}  opts.students   班级名单 [{ id, name, className }]
 */
export function buildTeacherDigest({ homeworks = [], students = [] } = {}) {
  // 展开所有提交
  const rows = []
  for (const hw of homeworks) {
    for (const sub of hw.submissions || []) {
      rows.push({
        homeworkId: hw.id,
        homeworkTitle: hw.title,
        level: hw.level || 'CET4',
        studentId: sub.studentId,
        studentName: sub.studentName,
        content: sub.content || '',
        submitTime: sub.submitTime,
        teacherScore: sub.score
      })
    }
  }

  // 只分析有正文的提交，且限制数量保证性能
  const analyzable = rows.filter(r => r.content && r.content.trim().length > 20).slice(0, 60)
  const analyzed = analyzable.map(r => {
    const res = analyzeWriting({ content: r.content, title: r.homeworkTitle, level: r.level })
    return { ...r, analysis: res }
  })

  // 提交统计
  const submittedIds = new Set(rows.map(r => String(r.studentId)))
  const unsubmitted = students.filter(s => !submittedIds.has(String(s.id)))
  const classSize = students.length || submittedIds.size
  const submitRate = classSize ? round1((submittedIds.size / classSize) * 100) : 0

  // 分数分布
  const scores = analyzed.map(a => a.analysis.scores.total)
  const avgScore = scores.length ? round1(scores.reduce((a, b) => a + b, 0) / scores.length) : 0
  const maxScore = scores.length ? Math.max(...scores) : 0
  const minScore = scores.length ? Math.min(...scores) : 0
  const buckets = [
    { range: '90-100', min: 90, max: 100 },
    { range: '80-89', min: 80, max: 89.9 },
    { range: '70-79', min: 70, max: 79.9 },
    { range: '60-69', min: 60, max: 69.9 },
    { range: '60 以下', min: 0, max: 59.9 }
  ]
  const distribution = buckets.map(b => ({
    range: b.range,
    count: scores.filter(s => s >= b.min && s <= b.max).length
  }))

  // 四维均分
  const dimAvg = ['content', 'structure', 'language', 'vocabulary'].map(k => {
    const arr = analyzed.map(a => a.analysis.scores[k])
    return {
      key: k,
      name: { content: '内容切题', structure: '结构逻辑', language: '语言准确', vocabulary: '词汇丰富' }[k],
      value: arr.length ? round1(arr.reduce((x, y) => x + y, 0) / arr.length) : 0
    }
  })

  // 共性问题聚合（按规则标签）
  const issueMap = {}
  for (const a of analyzed) {
    for (const it of a.analysis.issues) {
      if (it.type === 'punct') continue
      const key = it.label + '|' + it.type
      if (!issueMap[key]) {
        issueMap[key] = {
          label: it.label,
          type: it.type,
          typeName: TYPE_CN[it.type] || it.type,
          count: 0,
          students: new Set(),
          examples: []
        }
      }
      const g = issueMap[key]
      g.count += 1
      g.students.add(a.studentName)
      if (g.examples.length < 3) {
        g.examples.push({
          student: a.studentName,
          original: it.original.trim(),
          suggestion: it.suggestion.trim(),
          sentence: it.original
        })
      }
    }
  }
  const commonIssues = Object.values(issueMap)
    .map(g => ({ ...g, studentCount: g.students.size, students: undefined }))
    .sort((a, b) => b.studentCount - a.studentCount || b.count - a.count)
    .slice(0, 6)

  // 需关注学生：低分 / 字数不足 / 错误密度高
  const attention = analyzed
    .map(a => {
      const s = a.analysis.scores
      const reasons = []
      let level = 'low'
      if (s.total < 60) { reasons.push(`综合分仅 ${s.total}`); level = 'high' }
      if (s.language < 65) { reasons.push(`语言准确度 ${s.language}，硬错偏多`); level = level === 'high' ? 'high' : 'mid' }
      if (a.analysis.wordCount < 100 && a.level === 'CET4') { reasons.push(`仅 ${a.analysis.wordCount} 词，篇幅不足`); level = level === 'high' ? 'high' : 'mid' }
      if (s.structure < 60) { reasons.push(`结构分 ${s.structure}，段落层次不清`); level = level === 'high' ? 'high' : 'mid' }
      return {
        studentId: a.studentId,
        studentName: a.studentName,
        score: s.total,
        reasons,
        level
      }
    })
    .filter(x => x.reasons.length)
    .sort((a, b) => a.score - b.score)
    .slice(0, 8)

  // 优秀样本
  // 1) 按「正文主体」去重：不同学生常复用同一篇范文，只保留最先出现的一篇，
  //    否则会出现三名学生同分、同评语的情况。
  //    正文主体取尾部 80 字做指纹 —— 学生可能各写各的开头，但主体段落是一样的。
  // 2) 先收 ≥85 分的，篇数不够时放宽到 ≥80 分补齐，最多 3 篇。
  // 3) 评语按信息量排序，并避免多篇用同一句话。
  const fingerprint = c => hash(String(c).trim().slice(-80))
  const rankedAll = [...analyzed].sort((a, b) => b.analysis.scores.total - a.analysis.scores.total)
  const seenEssay = new Set()
  const chosen = []
  for (const min of [85, 80]) {
    for (const a of rankedAll) {
      if (chosen.length >= 3) break
      if (a.analysis.scores.total < min) continue
      const fp = fingerprint(a.content)
      if (seenEssay.has(fp)) continue
      seenEssay.add(fp)
      chosen.push(a)
    }
    if (chosen.length >= 3) break
  }
  const usedPicks = new Set()
  const excellent = chosen.map(a => {
    const hs = (a.analysis.highlights || []).filter(Boolean)
    const weight = h =>
      /连接词|分段|层次/.test(h) ? 4
        : /语法|错误/.test(h) ? 3
          : /词汇|同义|搭配/.test(h) ? 2
            : 1
    const ranked = [...hs].sort((x, y) => weight(y) - weight(x))
    const pick = ranked.find(h => !usedPicks.has(h)) || ranked[0] || ''
    if (pick) usedPicks.add(pick)
    return {
      studentId: a.studentId,
      studentName: a.studentName,
      score: a.analysis.scores.total,
      strength: pick || '整体完成度高，可作为本次范例朗读。'
    }
  })

  // 各次作业均分趋势
  const trend = homeworks
    .map(hw => {
      const arr = analyzed.filter(a => a.homeworkId === hw.id).map(a => a.analysis.scores.total)
      return {
        name: hw.title,
        value: arr.length ? round1(arr.reduce((x, y) => x + y, 0) / arr.length) : null,
        count: arr.length
      }
    })
    .filter(t => t.value !== null)

  // ---------- 一句话总评 ----------
  const weakestDim = [...dimAvg].sort((a, b) => a.value - b.value)[0]
  const strongestDim = [...dimAvg].sort((a, b) => b.value - a.value)[0]
  const topIssue = commonIssues[0]
  const summary = analyzed.length
    ? `本次共 ${submittedIds.size}/${classSize} 人提交（提交率 ${submitRate}%），已分析 ${analyzed.length} 篇，平均分 ${avgScore}。` +
      `班级最强项为「${strongestDim.name}」（${strongestDim.value}），最弱项为「${weakestDim.name}」（${weakestDim.value}）。` +
      (topIssue
        ? `共性问题集中在「${topIssue.label}」，${topIssue.studentCount} 名学生出现，共 ${topIssue.count} 处，建议作为本次讲评的第一个重点。`
        : '未发现集中性错误，可把讲评重心放在优秀样本的写法拆解上。') +
      (attention.filter(a => a.level === 'high').length
        ? `另有 ${attention.filter(a => a.level === 'high').length} 名学生需要单独跟进。`
        : '')
    : '当前没有可分析的作文提交，先让学生提交后再运行分析。'

  // ---------- 讲评稿 ----------
  const lectureScript = buildLectureScript({
    title: homeworks[0]?.title || '本次作业',
    submitRate,
    submitted: submittedIds.size,
    classSize,
    avgScore,
    maxScore,
    minScore,
    dimAvg,
    commonIssues,
    excellent,
    attention
  })

  return {
    generatedAt: Date.now(),
    classSize,
    submittedCount: submittedIds.size,
    analyzedCount: analyzed.length,
    submitRate,
    avgScore,
    maxScore,
    minScore,
    distribution,
    dimAvg,
    commonIssues,
    attention,
    excellent,
    trend,
    summary,
    lectureScript,
    passRate: analyzed.length ? round1((scores.filter(s => s >= 72).length / analyzed.length) * 100) : 0
  }
}

function buildLectureScript(d) {
  const lines = []
  lines.push(`# ${d.title} · 讲评稿`)
  lines.push('')
  lines.push(`**提交情况**：${d.submitted}/${d.classSize} 人，提交率 ${d.submitRate}%；已分析 ${d.submitted} 篇，平均 ${d.avgScore} 分（最高 ${d.maxScore}，最低 ${d.minScore}）。`)
  lines.push('')
  lines.push('## 一、整体表现（2 分钟）')
  const best = [...d.dimAvg].sort((a, b) => b.value - a.value)[0]
  const worst = [...d.dimAvg].sort((a, b) => a.value - b.value)[0]
  lines.push(`这次班级在「${best.name}」上表现最好，均分 ${best.value}；「${worst.name}」是短板，均分只有 ${worst.value}，今天我们重点解决这一块。`)
  lines.push('')
  if (d.commonIssues.length) {
    lines.push('## 二、三个共性问题（10 分钟）')
    d.commonIssues.slice(0, 3).forEach((it, i) => {
      lines.push('')
      lines.push(`**问题 ${i + 1}：${it.label}**（${it.studentCount} 人出现，共 ${it.count} 处）`)
      if (it.examples[0]) {
        lines.push(`- 典型错句：\`${it.examples[0].original}\``)
        lines.push(`- 改为：\`${it.examples[0].suggestion}\``)
      }
      lines.push(`- 讲解要点：${explainOf(it)}`)
    })
    lines.push('')
  }
  if (d.excellent.length) {
    lines.push('## 三、写得好的地方（5 分钟）')
    d.excellent.forEach(e => {
      lines.push(`- **${e.studentName}**（${e.score} 分）：${e.strength}`)
    })
    lines.push('')
  }
  if (d.attention.length) {
    lines.push('## 四、需要单独沟通的同学')
    d.attention.slice(0, 5).forEach(a => {
      lines.push(`- ${a.studentName}（${a.score} 分）：${a.reasons.join('；')}`)
    })
    lines.push('')
  }
  lines.push('## 五、下次作业的三条硬要求')
  lines.push('1. 至少分三段，第一段亮明立场。')
  lines.push(d.commonIssues[0] ? `2. 不许再出现「${d.commonIssues[0].label}」这类问题。` : '2. 每个论点后面必须跟一句解释。')
  lines.push('3. 全文至少使用 4 个不同的连接词。')
  return lines.join('\n')
}

function explainOf(issue) {
  // 先按具体问题标签给针对性讲法，没有再按大类兜底
  const byLabel = {
    拼写规范: '这类是「固定写法」问题（cannot / a lot of / because），属于必须清零的送分点，讲评时直接列清单全班齐读一遍。',
    表达单一: '同一表达反复出现会被判定词汇量不足。备 3 个同义替换轮换使用，比背新词见效更快。',
    套话模板: '套话堆砌会让阅卷老师疲劳，换成具体现象或数据切入更容易得分。',
    代词大小写: '英语第一人称代词 I 在任何位置都必须大写，属于卷面印象分，扣得很冤。',
    主谓一致: '先找主语单复数，再定时态；集体名词、the number of 这类高频陷阱单独拎出来讲。',
    冠词: '冠词错多半是没判断「可数/不可数」和「特指/泛指」，讲评时用 5 个句子做对比训练即可。',
    中式搭配: '先想英文里怎么说这件事，再想中文怎么翻，直译几乎是必错路径。',
    动宾搭配: '动宾要成对记：acquire knowledge、turn on the light，不要单独背动词。'
  }
  const map = {
    语法: '这类错误属于硬伤，阅卷时直接影响语言分，规则固定、必须清零。',
    用词: '词形和词性错误多来自「按中文意思硬凑」，建议整块记搭配而不是单记单词。',
    搭配: '动宾搭配要成对记，如 acquire knowledge、turn on the light，直译必错。',
    中式表达: '这是最容易被识别为「非母语写作」的部分。先想英文里怎么说这件事，再想中文怎么翻。',
    表达: '套话堆砌会让阅卷老师疲劳，换成具体现象切入更容易得分。'
  }
  return byLabel[issue.label] || map[issue.typeName] || '先归类，再逐条改，比整篇重写有效。'
}

/**
 * 单篇 AI 预批改（教师视角：给建议分 + 需人工确认项）
 */
export function preGrade({ content, title = '', level = 'CET4', studentName = '' } = {}) {
  const a = analyzeWriting({ content, title, level })
  const needsReview = []
  if (a.scores.total >= 82 && a.issueCount <= 2) {
    needsReview.push('分数较高且错误少，建议人工确认是否有「套模板」痕迹')
  }
  if (a.issueCount >= 8) {
    needsReview.push('硬错较多，建议确认是否存在偏题或抄写问题')
  }
  if (a.wordCount < 80) {
    needsReview.push('篇幅明显不足，需确认是否为刻意简写')
  }
  if (!needsReview.length) needsReview.push('无明显争议，可直接采用 AI 建议分')

  return {
    studentName,
    suggestedScore: a.scores.total,
    band: a.band,
    analysis: a,
    needsReview,
    teacherActions: [
      `建议分 ${a.scores.total}，可在 ±3 分内调整`,
      a.checklist[0] ? `首要讲评点：${a.checklist[0].title} —— ${a.checklist[0].detail}` : '无突出问题',
      a.highlights[0] ? `可公开表扬：${a.highlights[0]}` : ''
    ].filter(Boolean)
  }
}

export { TYPE_CN }
