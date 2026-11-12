// ============================================================
// 对话引擎 · 意图识别 + 结构化讲解
// 说明：不是「关键词 → 一段死字符串」，而是意图 + 语料 + 模板拼装
// 同一问题多次提问会给出不同角度的补充，避免机械重复
// ============================================================

import { GRAMMAR_POINTS, findGrammarPoint, CONNECTIVES } from '../corpus/rules.js'
import { lookupWord, confusableOf, VOCAB } from '../corpus/vocab.js'
import { analyzeWriting } from './writing.js'
import { hash, pick, words, chineseRatio, splitSentences } from './text.js'

const INTENT_TABLE = [
  { key: 'greeting', words: ['你好', '您好', 'hello', 'hi ', '在吗', '早上好', '晚上好'], weight: 3 },
  { key: 'thanks', words: ['谢谢', '感谢', 'thank'], weight: 3 },
  { key: 'grammar', words: ['语法', 'grammar', '从句', '时态', '虚拟', '非谓语', '被动', '冠词', '主谓一致', 'tense', 'clause'], weight: 6 },
  { key: 'vocab', words: ['词汇', '单词', '背单词', 'vocabulary', '记不住', '词根', '词缀', '同义词'], weight: 6 },
  { key: 'listening', words: ['听力', 'listening', '听不懂', '精听', '泛听'], weight: 6 },
  { key: 'speaking', words: ['口语', '发音', 'speaking', 'pronunciation', '语音', '语调', '连读'], weight: 6 },
  { key: 'reading', words: ['阅读', 'reading', '长难句', '看不懂', '阅读速度', '选词填空'], weight: 6 },
  { key: 'writing', words: ['作文', '写作', 'writing', 'essay', '开头', '结尾', '模板', '论点'], weight: 6 },
  { key: 'translate', words: ['翻译', 'translate', '中译英', '英译中'], weight: 6 },
  { key: 'exam', words: ['四级', '六级', 'cet', '考试', '备考', '分值', '时间分配', '真题', '押题'], weight: 5 },
  { key: 'plan', words: ['计划', '怎么学', '学习方法', '规划', '效率', '坚持', '安排'], weight: 5 },
  { key: 'wordmeaning', words: ['是什么意思', '什么意思', '怎么用', '用法', '区别', '辨析'], weight: 4 }
]

function scoreIntent(text) {
  const s = ' ' + String(text || '').toLowerCase()
  const scores = {}
  for (const it of INTENT_TABLE) {
    let sc = 0
    for (const w of it.words) {
      if (s.includes(w)) sc += it.weight + w.length * 0.4
    }
    if (sc > 0) scores[it.key] = sc
  }
  return scores
}

function bestIntent(text) {
  const scores = scoreIntent(text)
  const entries = Object.entries(scores).sort((a, b) => b[1] - a[1])
  return entries.length ? { key: entries[0][0], score: entries[0][1], all: scores } : null
}

/* ============================================================
   各个意图的回答生成
   ============================================================ */

function answerGreeting(input) {
  const variants = [
    `我在。可以直接把问题丢给我，也可以点下面的快捷指令。

我目前能帮你处理这几类事：

| 类型 | 能做什么 |
|---|---|
| 语法讲解 | 时态、从句、非谓语、虚拟语气、冠词、主谓一致 |
| 作文诊断 | 逐句标错 + 四维评分 + 改写建议 |
| 错题解析 | 错因归类 + 知识点卡片 + 同类题 |
| 词汇速记 | 释义、搭配、例句、词根、易混词 |
| 长难句拆解 | 主干 / 修饰 / 中文翻译 |
| 备考规划 | 按剩余时间倒排日程 |

直接把句子或题目发给我也行，我能扫出语法和搭配问题。`,
    `收到。为了让回答更准，你可以按这四种方式提问：

1. **贴一段英文** —— 我做逐句语法与搭配诊断
2. **说一个语法点** —— 我给规则、例句和常见坑
3. **发一道错题** —— 我拆错因并出两道同类题
4. **说一个目标** —— 我按周排学习计划

今天想先解决哪一个？`
  ]
  return {
    content: pick(variants, input),
    suggestions: ['虚拟语气怎么用？', '帮我看看这段作文', '四级阅读时间怎么分配']
  }
}

function answerThanks(input) {
  return {
    content: `不客气。有个小建议：刚讲过的点，最好在 24 小时内自己复述一遍或做 2 道题，否则一周后基本会忘。

需要的话我可以现在出两道小题帮你巩固。`,
    suggestions: ['出两道题巩固一下', '继续讲下一个语法点', '帮我排个复习计划']
  }
}

function answerGrammar(input) {
  const point = findGrammarPoint(input)
  if (point) {
    const exRows = point.examples.map(e => `| ${e.en} | ${e.cn} |`).join('\n')
    const ruleRows = point.rules.map((r, i) => `${i + 1}. ${r}`).join('\n')
    return {
      content: `## ${point.name}

**一句话结论**
${point.definition}

**规则拆解**
${ruleRows}

**例句对照**
| 英文 | 中文 |
|---|---|
${exRows}

**最容易踩的坑**
${point.pitfalls}

**自检**
把上面第二条例句遮住中文，自己口译一遍；如果卡在时态或关系词上，说明这个点还没内化。`,
      suggestions: [`${point.name}出两道题`, `再举几个${point.name}的例子`, '这个点在四级里怎么考']
    }
  }

  const list = GRAMMAR_POINTS.map(p => `- **${p.name}**：${p.definition.slice(0, 38)}…`).join('\n')
  return {
    content: `## 语法可以按这 8 块来补

${list}

**补语法的正确顺序**（不要从「时态总表」开始背）：

1. 先补 **主谓一致 + 冠词** —— 这两类错误在作文里扣分最密集，规则也最简单
2. 再补 **定语从句 + 非谓语** —— 决定你能写出多复杂的句子
3. 最后补 **虚拟语气** —— 只在特定题型出现，性价比最低但最容易被问到

**练习方法**：每学一个点，立刻在真题阅读里找 3 个该结构的句子抄下来，比做 20 道选择题有效。

想从哪一块开始？`,
    suggestions: ['主谓一致怎么练', '定语从句关系词怎么选', '非谓语三种形式区别']
  }
}

function answerVocab(input) {
  // 形近词/易混词查询
  const conf = VOCAB.find(v => input.toLowerCase().includes(v.word.toLowerCase()))
  return {
    content: `## 词汇：为什么「背了记不住」

绝大多数人失败的原因不是记性差，而是**只做了「认词」没做「用词」**。看到认识、写不出来，等于没背。

**四个动作，按性价比排序**

| 动作 | 做法 | 为什么有效 |
|---|---|---|
| 语境记忆 | 每个词配一个自己写的句子 | 大脑记的是场景不是字母串 |
| 词根词缀 | 先掌握 30 个高频词根 | 一个词根能解锁 10+ 个词 |
| 间隔重复 | 第 1/2/4/7/15 天各复习一次 | 对抗遗忘曲线，比一次背 100 个强 |
| 主动输出 | 当天用 3 个新词写一段话 | 从被动识别转为主动调用 |

**每天的量**：新词 20 个 + 复习旧词 40 个，20 分钟足够。贪多是最常见的死法。

**几个高频词根先拿下**
- spect（看）→ inspect / respect / perspective / prospect
- port（搬运）→ transport / export / portable / import
- duc / duct（引导）→ conduct / introduce / reduce / produce
- tract（拉）→ attract / contract / extract / distract
- mit / miss（送）→ submit / transmit / dismiss / permit

${conf ? `\n顺带提醒：**${conf.word}** 是高频词，${conf.cn}，常见搭配 ${conf.collocation}。` : ''}

要不要我用你现在正在背的词表，生成一批速记卡？`,
    suggestions: ['给我 5 个高频词根', '生成 10 张词汇速记卡', '怎么区分 affect 和 effect']
  }
}

function answerListening(input) {
  return {
    content: `## 听力：从「听不懂」到「跟得上」

**先判断你属于哪一种听不懂**
1. 单词都认识但连起来听不出 → **连读弱读**问题（占 70%）
2. 关键词能抓到但句子意思断 → **短时记忆**问题
3. 完全跟不上语速 → **辨音基础**问题

对应解法完全不同，别都用「多听」硬扛。

**精听四步法（每天 15 分钟，一段材料听 4 遍）**
1. 盲听一遍，只记能听懂的词
2. 逐句听写，写不出的地方留空
3. 对照原文，把没听出来的地方标出来，归类是「生词 / 连读 / 弱读 / 语速」
4. 跟读两遍，模仿语音语调

**材料分级**
- 起步：VOA Learning English（语速慢、用词受限）
- 进阶：CET4/6 真题听力（一定要用真题，不要用美剧）
- 冲刺：TED-Ed（语速正常、逻辑清晰）

**真题的正确用法**：一套听力听 3 遍比三套各听 1 遍强。第一遍做题，第二遍精听，第三遍裸听确认。

**30 天节奏**：前 10 天每天精听 1 段短对话 + 跟读；中间 10 天精听 1 篇长对话；最后 10 天整套模考。

要不要我按你每天能投入的时间排一张听力计划表？`,
    suggestions: ['排一张 30 天听力计划', '连读弱读有哪些规律', '长对话怎么记笔记']
  }
}

function answerSpeaking(input) {
  return {
    content: `## 口语：先解决「敢说」，再解决「说对」

**中式发音的 4 个高频问题**
1. **尾辅音吞掉**：把 good 读成 goo，把 like 读成 lie —— 英语尾辅音必须发出来
2. **θ / ð 读成 s / z**：think 读成 sink，this 读成 zis
3. **v / w 不分**：very 读成 wery
4. **没有重音节奏**：中文是音节计时，英语是重音计时，句子要「重读实词、弱读虚词」

**每天 10 分钟的练习结构**
| 时间 | 动作 |
|---|---|
| 3 分钟 | Shadowing 跟读（延迟 0.5 秒跟，不是听完再读） |
| 3 分钟 | 录音回听，只找 1 个问题改（不要一次改 5 个） |
| 4 分钟 | 就一个话题自述 1 分钟，不看稿 |

**自述话题库（由易到难）**
- 描述今天做的一件事（练一般过去时）
- 解释一个你熟悉的概念（练定义句：X refers to...）
- 表达一个观点并给理由（练 argue + support）

**关键心态**：流利度优先于准确度。先能连续说 60 秒不卡，再回头修语法。

要不要我用「发音评测」帮你测一段？`,
    suggestions: ['帮我练一段跟读', '怎么练重音节奏', '口语考试常见话题']
  }
}

function answerReading(input) {
  return {
    content: `## 阅读：不是读得慢，是读得没有优先级

**四级阅读 35 分钟的时间分配**
- 选词填空 10 题：8 分钟（性价比最低，别纠缠）
- 长篇匹配 10 题：12 分钟（先看题干关键词，再扫段落首末句）
- 仔细阅读 10 题：15 分钟（分值最高，必须留足）

**长难句拆解三步**
1. **找谓语**：一句话里有多少个谓语动词，就有多少个分句
2. **定主干**：主语 + 谓语 + 宾语，其余全是修饰
3. **挂修饰**：从句挂到它修饰的名词上，介词短语挂到最近的动词或名词上

例：
> The assumption **that** online learning inevitably lowers academic standards, **which** many educators still hold, **has been** challenged by recent studies.

- 谓语：has been challenged（主句）
- 主干：The assumption has been challenged
- 修饰：that 从句解释 assumption；which 从句修饰 assumption

**判断题的坑**：选项里有原文原词 ≠ 正确；正确选项通常是同义改写。看到「和原文几乎一字不差」的选项，先怀疑它是陷阱。

**做题顺序建议**：仔细阅读 → 长篇匹配 → 选词填空。把最值钱的放在精力最好的时候。

要不要我拿一段真题长难句带你拆？`,
    suggestions: ['拆一段四级长难句', '选词填空怎么提分', '匹配题定位技巧']
  }
}

function answerWriting(input) {
  const conn = CONNECTIVES
  return {
    content: `## 作文：阅卷老师 40 秒看什么

按重要性排序：**结构清晰 > 语言准确 > 词汇丰富 > 思想深刻**。很多人反过来用力，所以分数上不去。

**三段式骨架（120–180 词）**
| 段落 | 作用 | 句数 | 要点 |
|---|---|---|---|
| 第一段 | 引出话题 + 亮明立场 | 2–3 | 用具体现象切入，别背 With the development of |
| 第二段 | 2 个分论点 | 5–6 | 每点「观点 + 解释 + 例子」三件套 |
| 第三段 | 重申 + 建议 | 2–3 | 给一条可执行建议，不要空喊口号 |

**连接词按功能记，不要按字母背**
- 递进：${conn.addition.slice(0, 4).join(' / ')}
- 转折：${conn.contrast.slice(0, 4).join(' / ')}
- 因果：${conn.cause.slice(0, 3).join(' / ')}
- 举例：${conn.example.slice(0, 3).join(' / ')}
- 总结：${conn.conclusion.slice(0, 3).join(' / ')}

**四个扣分重灾区**
1. 主谓一致（people is / he go）
2. 可数不可数（informations / advices）
3. 中式搭配（learn knowledge / open the light）
4. 全篇一个段落

**高分句式的正确用法**：不是塞模板句，而是让句子承载信息。
> 平庸：With the development of society, more and more people pay attention to education.
> 有效：A growing number of undergraduates now take online courses, a shift that has widened access while raising concerns about self-discipline.

第二句包含了「现象 + 影响 + 争议」三层信息，一句话就完成了引入。

把作文贴给我，我可以逐句标错并给出改写版。`,
    suggestions: ['帮我批改一篇作文', '第二段论点怎么写', '高级替换词有哪些']
  }
}

function answerTranslate(input) {
  return {
    content: `## 翻译：得分靠「说人话」，不靠逐字对译

**三步法**

1. **断句**：中文一个长句往往要拆成 2–3 个英文句子。先按语义断开。
2. **抓主干**：每段找出「谁 + 做了什么」，先写主谓宾。
3. **挂修饰 + 润色**：定语、状语后置；数字、单位、专有名词核对。

**例**
> 原文：随着互联网的普及，越来越多的年轻人选择在网上购物，这不仅节省了时间，也改变了他们的消费习惯。

断句后 →
1. 互联网普及 → With the popularity of the Internet
2. 年轻人网上购物 → an increasing number of young people shop online
3. 两个影响 → which saves time and reshapes their spending habits

成句：
> With the growing popularity of the Internet, an increasing number of young people do their shopping online, which not only saves time but also reshapes their spending habits.

**三个保命技巧**
- 不会的词换说法，别硬译（"非遗" → traditional cultural heritage）
- 中文的「四字词」先翻译成白话再译
- 时态统一：描述普遍现象用一般现在时，描述过去的事用过去时

**常见失分点**：主谓不一致、单复数、漏译后半句。

发一段中文给我，我按三步拆给你看。`,
    suggestions: ['翻译这段中文', '四字词怎么处理', '被动句怎么译']
  }
}

function answerExam(input) {
  return {
    content: `## 四六级：先搞清楚分值，再谈复习

**四级分值结构（总分 710）**
| 模块 | 分值 | 占比 | 建议用时 |
|---|---|---|---|
| 写作 | 106.5 | 15% | 30 分钟 |
| 听力 | 248.5 | 35% | 25 分钟 |
| 阅读 | 248.5 | 35% | 40 分钟 |
| 翻译 | 106.5 | 15% | 30 分钟 |

听力和阅读合计 70%，这两块决定过不过；写作翻译决定分数好不好看。

**425 分及格线的现实拆解**
按 70% 得分率算：听力 174 + 阅读 174 + 写作 74 + 翻译 74 ≈ 496。也就是说，**听力阅读各对 60% 就稳过**，不需要样样精通。

**备考节奏（按剩余时间）**
- 还有 8 周以上：先补词汇 + 语法，再做真题
- 还有 4–8 周：真题为主，一周 2 套，错题必须复盘
- 还有 2 周：只做真题 + 背作文框架，不要再学新东西
- 最后 3 天：重做错题、调整作息

**真题的正确用法**
第一遍限时做 → 第二遍逐题分析错因（是词不认识？句子读不懂？还是选项没看清？）→ 第三遍只做错题。三遍下来一套题的价值才榨干。

告诉我你还有几周、目前大概多少分，我给你排具体日程。`,
    suggestions: ['我还有 4 周怎么安排', '听力怎么提分最快', '作文怎么稳在 12 分']
  }
}

function answerPlan(input) {
  return {
    content: `## 学习计划：能坚持的才叫计划

**先砍掉三个错误做法**
1. 每天排 5 小时 —— 三天后必崩
2. 计划里只有「学英语」三个字 —— 无法执行
3. 只排输入（背单词、看视频），不排输出（做题、写作）—— 到考场还是不会

**一个可执行的日模板（60–90 分钟）**
| 时段 | 内容 | 时长 |
|---|---|---|
| 早上 | 复习昨日生词 + 新词 20 个 | 15 分钟 |
| 午间 | 精听 1 段真题听力 | 15 分钟 |
| 晚上 | 阅读 1 篇 + 逐题分析错因 | 25 分钟 |
| 睡前 | 写 3 句话总结今天学到什么 | 5 分钟 |

关键在「睡前 3 句话」—— 这是把当天输入转成长期记忆的开关。

**周节奏**
- 周一至周五：按上面模板走
- 周六：整套真题限时
- 周日：只复盘错题，不学新内容

**如何防止崩盘**
- 设一个「最低完成线」：实在没时间就只做单词复习，别断链
- 用打卡记录连续性，连续 7 天后给自己一个具体奖励
- 计划留 20% 空档，排满的计划一定会失败

告诉我你每天能投入多久、目标分数多少，我给你排一张 7 天表。`,
    suggestions: ['按 90 分钟排 7 天计划', '单词总是断怎么办', '怎么复盘错题']
  }
}

function answerWordMeaning(input) {
  // 尝试从输入中捞出英文单词
  const ws = words(input)
  const candidates = ws
    .map(w => w.toLowerCase())
    .filter(w => w.length >= 3)
  for (const c of candidates) {
    const v = lookupWord(c)
    if (v) {
      const conf = confusableOf(c)
      return {
        content: `## ${v.word}

**词性与释义** ${v.pos} ${v.cn}

**常见搭配**
- ${v.collocation}

**例句**
> ${v.example}

**记忆线索**
${v.root}

${conf ? `**易混提醒**\n${conf.a} vs ${conf.b}：${conf.note}` : ''}

**自测**：不看上面，用它造一个和例句不同的句子。造得出来才算记住。`,
        suggestions: [`${v.word} 的派生词有哪些`, '再给我一张词卡', '这个词在四级里怎么考']
      }
    }
  }

  const conf = VOCAB.find(v => input.toLowerCase().includes(v.word.toLowerCase()))
  if (conf) {
    return {
      content: `## ${conf.word}\n\n**释义** ${conf.pos} ${conf.cn}\n\n**搭配** ${conf.collocation}\n\n**例句**\n> ${conf.example}\n\n**记忆线索**\n${conf.root}`,
      suggestions: ['再给我一张词卡', '容易混的词有哪些', '这批词怎么复习']
    }
  }

  const sample = VOCAB.slice(hash(input) % 10, hash(input) % 10 + 3)
  return {
    content: `## 这个词不在我的离线词库里

当前是**伪 AI 模式**，内置词库只覆盖约 ${VOCAB.length} 个四级高频词，没有联网词典接口，所以我不会硬编一个释义给你。

**你可以这样快速判断它的用法**
1. 看它在句子里的位置：冠词/形容词后面 → 大概率是名词；主语后面 → 谓语
2. 看词尾：-tion/-ment/-ness 名词，-able/-ive 形容词，-ize/-ify 动词
3. 查搭配而不是查释义：搜「word + collocation」，比背中文意思有用

**顺便给你几张高频词卡**

${sample.map(v => `- **${v.word}** ${v.pos} ${v.cn} —— ${v.example}`).join('\n')}

接上真 AI 后可以直接查任意词，接口位置已经预留好了。`,
    suggestions: ['给我 10 张词汇卡', '词根词缀怎么学', '四级高频词有哪些']
  }
}

/** 英文句子诊断（用户直接贴英文） */
function answerSentenceCheck(input) {
  const res = analyzeWriting({ content: input, level: 'CET4' })
  const issues = res.issues.filter(i => i.type !== 'punct').slice(0, 6)
  if (!issues.length) {
    return {
      content: `## 诊断结果：未检出明显错误

这句话在语法层面是站得住的。给两点表达上的建议：

**1. 语域**：书面写作里尽量避免 very / a lot of / thing 这类词，换成更精确的搭配。

**2. 信息密度**：如果这是作文中的论点句，建议补一个「为什么」或「例如」，让句子承载更多信息。

把整段作文发给我，我能看句间逻辑，单句判断有限。`,
      suggestions: ['帮我看看整篇作文', '这句怎么改得更高级', '再检查一句']
    }
  }
  const rows = issues
    .map(i => `| ${i.typeName} | ${i.original.trim()} | ${i.suggestion.trim()} |`)
    .join('\n')
  return {
    content: `## 诊断结果：检出 ${res.issues.length} 处问题

| 类型 | 原文 | 建议 |
|---|---|---|
${rows}

**逐条说明**

${issues.map((i, idx) => `**${idx + 1}. ${i.label}**（${i.typeName}）
> ${i.original.trim()} → ${i.suggestion.trim()}

${i.explain}`).join('\n\n')}

**修正版**
> ${res.correctedContent}

语言准确度评分：${res.scores.language}/100。改完这些，这一项能提到 90 以上。`,
    suggestions: ['再看一段', '帮我批改整篇作文', '这类错误怎么系统练']
  }
}

function answerFallback(input) {
  const variants = [
    `我理解你想问的是「${String(input).slice(0, 24)}」相关的内容，但当前的伪 AI 引擎还没覆盖到这个话题的专门知识。

我这几个方向回答质量最好：

1. **语法点讲解** —— 说出具体名称，比如「虚拟语气」「定语从句」
2. **作文诊断** —— 直接贴英文段落，我逐句标错
3. **错题解析** —— 发题目 + 你的答案
4. **长难句拆解** —— 贴一句读不懂的英文
5. **备考规划** —— 说清剩余时间和目标分

换个问法再试一次，或者点下面的快捷指令。`,
    `这个问题涉及的信息我暂时没有可靠来源，不想给你编一个看起来像那么回事的答案。

能确定的是：**英语学习里 80% 的困惑都落在「语法结构没吃透」和「练习量不够」这两件事上**。如果方便，把你的具体句子或题目发给我，我基于实际文本给判断，比空对空讨论有用得多。

当前模式说明：伪 AI（离线规则引擎），知识范围限于内置语料；接真模型后可以开放问答。`
  ]
  return {
    content: pick(variants, input),
    suggestions: ['虚拟语气怎么用', '帮我诊断一段英文', '四级怎么备考']
  }
}

/* ============================================================
   对外入口
   ============================================================ */

const HANDLERS = {
  greeting: answerGreeting,
  thanks: answerThanks,
  grammar: answerGrammar,
  vocab: answerVocab,
  listening: answerListening,
  speaking: answerSpeaking,
  reading: answerReading,
  writing: answerWriting,
  translate: answerTranslate,
  exam: answerExam,
  plan: answerPlan,
  wordmeaning: answerWordMeaning
}

/**
 * 生成对话回答
 * @param {Object} opts
 * @param {string} opts.question 当前问题
 * @param {Array}  opts.history  历史消息 [{role, content}]
 */
export function generateReply({ question = '', history = [] } = {}) {
  const q = String(question || '').trim()

  const enWords = words(q)
  const looksEnglish = chineseRatio(q) < 0.15 && enWords.length >= 4
  const intent = bestIntent(q)

  // 先看意图：英文提问（how to improve listening）也属于「问问题」而不是「待诊断的句子」
  if (intent && HANDLERS[intent.key] && intent.score >= 5) {
    return HANDLERS[intent.key](q)
  }

  // 纯英文且有一定长度 → 视为句子诊断
  if (looksEnglish) return answerSentenceCheck(q)

  // 短追问：沿用上一轮意图
  if (q.length <= 8 && history.length) {
    const lastUser = [...history].reverse().find(m => m.role === 'user' && m.content !== q)
    if (lastUser) {
      const prev = bestIntent(lastUser.content)
      if (prev && HANDLERS[prev.key]) {
        return HANDLERS[prev.key](lastUser.content + ' ' + q)
      }
    }
  }

  return answerFallback(q)
}
