// ============================================================
// 文本处理工具 · 伪 AI 引擎的基础设施
// ============================================================

/** 按句末标点切句，保留标点 */
export function splitSentences(text) {
  if (!text) return []
  const cleaned = String(text).replace(/\r/g, '').trim()
  const parts = cleaned.match(/[^.!?。！？\n]+[.!?。！？]*/g) || []
  return parts.map(s => s.trim()).filter(Boolean)
}

/** 按段落切分 */
export function splitParagraphs(text) {
  if (!text) return []
  return String(text)
    .split(/\n+/)
    .map(p => p.trim())
    .filter(Boolean)
}

/** 取英文单词（小写） */
export function words(text) {
  const m = String(text || '').match(/[A-Za-z][A-Za-z'-]*/g)
  return m || []
}

/** 稳定的字符串哈希，用于确定性选样 */
export function hash(str) {
  let h = 2166136261
  const s = String(str || '')
  for (let i = 0; i < s.length; i++) {
    h ^= s.charCodeAt(i)
    h = Math.imul(h, 16777619)
  }
  return Math.abs(h)
}

/** 按种子从数组中确定性地取一个 */
export function pick(arr, seed) {
  if (!arr || !arr.length) return null
  return arr[hash(seed) % arr.length]
}

/** 按种子取 n 个不重复元素 */
export function pickMany(arr, n, seed) {
  const pool = [...arr]
  const out = []
  let s = hash(seed)
  while (pool.length && out.length < n) {
    s = (s * 1103515245 + 12345) & 0x7fffffff
    out.push(pool.splice(s % pool.length, 1)[0])
  }
  return out
}

/** 数值夹紧 */
export function clamp(v, min, max) {
  return Math.min(max, Math.max(min, v))
}

/** 保留一位小数 */
export function round1(v) {
  return Math.round(v * 10) / 10
}

/** 延时（模拟推理耗时，让交互更像真 AI） */
export function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms))
}

/** 基础文本统计（供评分使用） */
export function textStats(text) {
  const raw = String(text || '')
  const ws = words(raw)
  const unique = new Set(ws.map(w => w.toLowerCase()))
  const sents = splitSentences(raw)
  const paragraphs = splitParagraphs(raw)
  const longWords = ws.filter(w => w.length >= 8)
  const lens = sents.map(s => words(s).length).filter(n => n > 0)
  const avgLen = lens.length ? lens.reduce((a, b) => a + b, 0) / lens.length : 0
  return {
    wordCount: ws.length,
    uniqueCount: unique.size,
    uniqueRatio: ws.length ? unique.size / ws.length : 0,
    sentenceCount: sents.length,
    paragraphCount: paragraphs.length,
    longWordRatio: ws.length ? longWords.length / ws.length : 0,
    avgSentenceLength: round1(avgLen),
    longestSentence: lens.length ? Math.max(...lens) : 0,
    shortestSentence: lens.length ? Math.min(...lens) : 0
  }
}

/** 检测中文字符占比（判断是否在问中文问题） */
export function chineseRatio(text) {
  const s = String(text || '')
  if (!s.length) return 0
  const cn = (s.match(/[\u4e00-\u9fa5]/g) || []).length
  return cn / s.length
}

/** 极简 Markdown → HTML（AI 输出渲染用，够用即可） */
export function mdToHtml(md) {
  if (!md) return ''
  let s = String(md).replace(/\r/g, '')

  // 代码块先占位，避免被后续规则破坏
  const blocks = []
  s = s.replace(/```([\s\S]*?)```/g, (_, code) => {
    blocks.push(code.replace(/^\w*\n/, ''))
    return `\u0000CODE${blocks.length - 1}\u0000`
  })

  const esc = t => t.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
  s = esc(s)

  // 表格
  s = s.replace(/((?:^\|.*\|[ \t]*\n?)+)/gm, table => {
    const rows = table.trim().split('\n').map(r => r.trim()).filter(Boolean)
    if (rows.length < 2) return table
    const cells = r => r.replace(/^\||\|$/g, '').split('|').map(c => c.trim())
    const head = cells(rows[0])
    const body = rows.slice(rows[1].match(/^[\s|:-]+$/) ? 2 : 1).map(cells)
    let html = '<table><thead><tr>' + head.map(h => `<th>${h}</th>`).join('') + '</tr></thead><tbody>'
    body.forEach(r => {
      html += '<tr>' + r.map(c => `<td>${c}</td>`).join('') + '</tr>'
    })
    return html + '</tbody></table>'
  })

  s = s.replace(/^######\s+(.*)$/gm, '<h4>$1</h4>')
  s = s.replace(/^#####\s+(.*)$/gm, '<h4>$1</h4>')
  s = s.replace(/^####\s+(.*)$/gm, '<h4>$1</h4>')
  s = s.replace(/^###\s+(.*)$/gm, '<h4>$1</h4>')
  s = s.replace(/^##\s+(.*)$/gm, '<h3>$1</h3>')
  s = s.replace(/^#\s+(.*)$/gm, '<h2>$1</h2>')
  s = s.replace(/^&gt;\s?(.*)$/gm, '<blockquote>$1</blockquote>')
  s = s.replace(/^\s*---+\s*$/gm, '<hr/>')

  s = s.replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')
  s = s.replace(/(^|[^*])\*([^*\n]+)\*/g, '$1<em>$2</em>')
  s = s.replace(/`([^`]+)`/g, '<code>$1</code>')

  // 列表
  const lines = s.split('\n')
  const out = []
  let listType = null
  const close = () => {
    if (listType) {
      out.push(listType === 'ul' ? '</ul>' : '</ol>')
      listType = null
    }
  }
  for (const line of lines) {
    const ul = line.match(/^\s*[-*+]\s+(.*)$/)
    const ol = line.match(/^\s*\d+[.)]\s+(.*)$/)
    if (ul) {
      if (listType !== 'ul') { close(); out.push('<ul>'); listType = 'ul' }
      out.push(`<li>${ul[1]}</li>`)
    } else if (ol) {
      if (listType !== 'ol') { close(); out.push('<ol>'); listType = 'ol' }
      out.push(`<li>${ol[1]}</li>`)
    } else {
      close()
      out.push(line)
    }
  }
  close()
  s = out.join('\n')

  // 段落
  s = s
    .split(/\n{2,}/)
    .map(p => {
      const t = p.trim()
      if (!t) return ''
      if (/^<(h\d|ul|ol|table|blockquote|hr|pre)/.test(t)) return t
      if (t.startsWith('\u0000CODE')) return t
      return `<p>${t.replace(/\n/g, '<br/>')}</p>`
    })
    .join('\n')

  s = s.replace(/\u0000CODE(\d+)\u0000/g, (_, i) => `<pre><code>${esc(blocks[Number(i)])}</code></pre>`)
  return s
}
