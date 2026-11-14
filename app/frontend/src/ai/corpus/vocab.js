// ============================================================
// 离线词汇库 · 词汇速记卡 / 词义查询共用
// 注意：未收录的词不会编造释义，避免给出错误信息
// ============================================================

const RAW = [
  ['abandon', 'v.', '放弃；抛弃', 'abandon a plan / abandon oneself to', 'They had to abandon the project due to lack of funding.', 'a-（去）+ bandon（控制）→ 放弃控制'],
  ['acquire', 'v.', '获得；习得', 'acquire knowledge / acquire a skill', 'Children acquire language remarkably fast.', 'ac-（加强）+ quire（寻求）→ 努力求得'],
  ['adequate', 'adj.', '足够的；适当的', 'adequate preparation / be adequate for', 'The evidence is not adequate to support the claim.', 'ad-（向）+ equ（相等）→ 与需求相等'],
  ['ambiguous', 'adj.', '模棱两可的', 'an ambiguous answer / remain ambiguous', 'His reply was deliberately ambiguous.', 'ambi-（两者）+ agere（驱动）→ 两边都能解释'],
  ['anticipate', 'v.', '预料；期待', 'anticipate problems / as anticipated', 'We anticipate a rise in demand next quarter.', 'anti-（前）+ cipere（取）→ 提前拿到'],
  ['apparent', 'adj.', '明显的；表面上的', 'it is apparent that / for no apparent reason', 'It soon became apparent that the plan would fail.', 'ap-（向）+ parere（出现）→ 显现出来'],
  ['assess', 'v.', '评估；评定', 'assess the impact / assess students\' performance', 'Teachers assess progress through both tests and projects.', 'as-（向）+ sedere（坐）→ 坐下来审议'],
  ['assume', 'v.', '假定；承担', 'assume responsibility / it is assumed that', 'We should not assume that everyone agrees.', 'as-（向）+ sumere（拿）→ 拿来当前提'],
  ['attribute', 'v./n.', '把……归因于；属性', 'attribute A to B / a key attribute', 'She attributes her success to consistent practice.', 'at-（向）+ tribuere（给予）→ 归给'],
  ['beneficial', 'adj.', '有益的', 'be beneficial to / mutually beneficial', 'Regular exercise is beneficial to mental health.', 'bene-（好）+ fic（做）→ 做好事'],
  ['comprehensive', 'adj.', '全面的；综合的', 'a comprehensive review / comprehensive coverage', 'The report offers a comprehensive analysis of the market.', 'com-（全部）+ prehendere（抓）→ 全部抓住'],
  ['consequence', 'n.', '结果；后果', 'as a consequence / serious consequences', 'Poor sleep has long-term consequences for memory.', 'con-（一起）+ sequi（跟随）→ 随之而来'],
  ['considerable', 'adj.', '相当大的', 'a considerable amount of / considerable progress', 'The reform saved a considerable amount of time.', 'consider（考虑）+ -able → 值得考虑的（量）'],
  ['contemporary', 'adj.', '当代的；同时代的', 'contemporary society / contemporary art', 'Contemporary society places a premium on efficiency.', 'con-（共同）+ tempor（时间）→ 同一时代'],
  ['crucial', 'adj.', '至关重要的', 'play a crucial role / be crucial to', 'Feedback plays a crucial role in learning.', 'cruc（十字）+ -ial → 十字路口般的决定性'],
  ['decline', 'v./n.', '下降；婉拒', 'a sharp decline / decline to comment', 'Enrollment declined by 12 percent last year.', 'de-（向下）+ clinare（倾斜）→ 向下倾'],
  ['demonstrate', 'v.', '证明；演示', 'demonstrate that / demonstrate how to', 'The data demonstrate a clear correlation.', 'de-（完全）+ monstrare（展示）→ 完整展示'],
  ['derive', 'v.', '源于；获得', 'derive from / derive benefit from', 'Many English words derive from Latin.', 'de-（从）+ rivus（溪流）→ 从河中引出'],
  ['distinguish', 'v.', '区分；辨别', 'distinguish A from B / be distinguished for', 'It is hard to distinguish fact from opinion.', 'dis-（分开）+ stinguere（刺）→ 分开标记'],
  ['efficient', 'adj.', '高效的', 'energy-efficient / an efficient method', 'Online platforms make tutoring more efficient.', 'ef-（出）+ fic（做）→ 做出成果'],
  ['eliminate', 'v.', '消除；淘汰', 'eliminate errors / be eliminated from', 'The new policy aims to eliminate redundant steps.', 'e-（出）+ limen（门槛）→ 赶出门外'],
  ['emphasize', 'v.', '强调', 'emphasize the importance of / it must be emphasized that', 'The teacher emphasized the value of reading aloud.', 'em-（使）+ phasis（显现）→ 使突出'],
  ['enhance', 'v.', '提升；增强', 'enhance efficiency / enhance the quality of', 'Group discussion can enhance understanding.', 'en-（使）+ haut（高）→ 使变高'],
  ['essential', 'adj.', '必不可少的', 'be essential to / it is essential that', 'Critical thinking is essential to academic writing.', 'essent（存在）+ -ial → 关乎本质的'],
  ['evaluate', 'v.', '评价；估计', 'evaluate the effectiveness / be evaluated on', 'We need to evaluate the program objectively.', 'e-（出）+ valere（价值）→ 算出价值'],
  ['fundamental', 'adj.', '根本的；基础的', 'a fundamental change / be fundamental to', 'Reading is fundamental to language acquisition.', 'fund（底）+ -mental → 打底的'],
  ['generate', 'v.', '产生；生成', 'generate income / generate interest', 'The campaign generated considerable interest.', 'gener（产生）+ -ate → 使产生'],
  ['inevitable', 'adj.', '不可避免的', 'it is inevitable that / an inevitable result', 'Some mistakes are inevitable in the learning process.', 'in-（不）+ evit（避开）+ -able'],
  ['maintain', 'v.', '保持；主张', 'maintain a balance / maintain that', 'It is difficult to maintain focus for three hours.', 'main（手）+ tenere（握）→ 用手握住'],
  ['obstacle', 'n.', '障碍', 'overcome an obstacle / a major obstacle to', 'Lack of confidence is the biggest obstacle.', 'ob-（反）+ stare（站）→ 挡在前面'],
  ['perceive', 'v.', '察觉；认为', 'perceive A as B / be perceived to be', 'Students often perceive grammar as boring.', 'per-（彻底）+ cipere（抓）→ 抓到'],
  ['perspective', 'n.', '视角；观点', 'from a ... perspective / put into perspective', 'From an economic perspective, the policy makes sense.', 'per-（透过）+ specere（看）→ 看过去'],
  ['phenomenon', 'n.', '现象', 'a social phenomenon / phenomena（复数）', 'This is a widespread social phenomenon.', 'phen（显现）+ -omenon → 显现之物'],
  ['priority', 'n.', '优先事项', 'give priority to / top priority', 'Safety should be given priority over speed.', 'prior（在前）+ -ity → 排在前面的'],
  ['reluctant', 'adj.', '不情愿的', 'be reluctant to do / a reluctant agreement', 'Many students are reluctant to speak in class.', 're-（反）+ luctari（挣扎）→ 反着挣扎'],
  ['significant', 'adj.', '显著的；重要的', 'a significant increase / statistically significant', 'There was a significant improvement in scores.', 'sign（标记）+ fic（做）→ 值得标记的'],
  ['substitute', 'v./n.', '替代；替代品', 'substitute A for B / a substitute for', 'Online videos cannot substitute for real practice.', 'sub-（下）+ statuere（放置）→ 放一个在下面顶上'],
  ['sustainable', 'adj.', '可持续的', 'sustainable development / environmentally sustainable', 'Sustainable habits matter more than short bursts of effort.', 'sus-（在下）+ tenere（握）→ 能撑住'],
  ['tendency', 'n.', '倾向', 'have a tendency to / a growing tendency', 'There is a tendency to overuse template sentences.', 'tend（伸展）+ -ency → 往某方向伸'],
  ['undermine', 'v.', '削弱；逐渐损害', 'undermine confidence / undermine the argument', 'Constant criticism can undermine motivation.', 'under（下面）+ mine（挖）→ 从底下挖空']
]

export const VOCAB = RAW.map(([word, pos, cn, collocation, example, root]) => ({
  word, pos, cn, collocation, example, root
}))

export const VOCAB_MAP = VOCAB.reduce((acc, v) => {
  acc[v.word.toLowerCase()] = v
  return acc
}, {})

/** 易混词对 */
export const CONFUSABLES = [
  { a: 'affect', b: 'effect', note: 'affect 动词「影响」；effect 名词「效果」，固定搭配 have an effect on。' },
  { a: 'advice', b: 'advise', note: 'advice 名词不可数；advise 动词，advise sb to do sth。' },
  { a: 'adapt', b: 'adopt', note: 'adapt 适应/改编；adopt 采纳/收养。' },
  { a: 'principle', b: 'principal', note: 'principle 原则；principal 主要的/校长。' },
  { a: 'economic', b: 'economical', note: 'economic 经济的；economical 节省的。' },
  { a: 'respectable', b: 'respectful', note: 'respectable 值得尊敬的；respectful 恭敬的。' },
  { a: 'complement', b: 'compliment', note: 'complement 补充；compliment 赞美。' },
  { a: 'assure', b: 'ensure', note: 'assure 使某人确信（宾语是人）；ensure 确保（宾语是事）。' }
]

export function lookupWord(w) {
  return VOCAB_MAP[String(w || '').toLowerCase().trim()] || null
}

export function confusableOf(w) {
  const s = String(w || '').toLowerCase()
  return CONFUSABLES.find(c => c.a === s || c.b === s) || null
}

/** 按种子取 n 张词卡 */
export function sampleWords(n, seed = 0) {
  const pool = [...VOCAB]
  const out = []
  let s = seed || 7
  while (pool.length && out.length < n) {
    s = (s * 1103515245 + 12345) & 0x7fffffff
    out.push(pool.splice(s % pool.length, 1)[0])
  }
  return out
}
