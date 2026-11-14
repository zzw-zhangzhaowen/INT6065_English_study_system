// ============================================================
// 常见错误规则库 · 中国学生英语写作高频问题
// 说明：判断逻辑全部来自这里，规则可随时增删
// 语法规则库 · 与引擎解耦，可独立扩充与版本管理
// ============================================================

const VOWEL_EXCEPTIONS = ['university', 'user', 'useful', 'unique', 'uniform', 'union', 'unit', 'european', 'one', 'usual', 'usual', 'uk', 'us']
const H_EXCEPTIONS = ['hour', 'honest', 'honor', 'honour', 'heir']

const IRREGULAR_THIRD = {
  have: 'has', do: 'does', go: 'goes', be: 'is', was: 'is',
  study: 'studies', try: 'tries', carry: 'carries', fly: 'flies',
  watch: 'watches', teach: 'watches', finish: 'finishes', pass: 'passes',
  fix: 'fixes', miss: 'misses', guess: 'guesses', wash: 'washes'
}

function thirdPerson(base) {
  const b = base.toLowerCase()
  if (IRREGULAR_THIRD[b]) return IRREGULAR_THIRD[b]
  if (/[^aeiou]y$/.test(b)) return b.slice(0, -1) + 'ies'
  if (/(s|x|z|ch|sh)$/.test(b)) return b + 'es'
  return b + 's'
}

export const RULES = [
  // ---------------- 冠词 ----------------
  {
    id: 'article-a-vowel',
    type: 'grammar',
    label: '冠词误用',
    re: /\ba\s+([aeiouAEIOU][a-zA-Z]*)\b/g,
    test: m => !VOWEL_EXCEPTIONS.includes(m[1].toLowerCase()),
    fix: m => `an ${m[1]}`,
    explain: '元音音素开头的单词前应用 an，而不是 a。注意 university / user 这类虽以元音字母开头、但读音以辅音 /j/ 开头的词仍用 a。'
  },
  {
    id: 'article-an-consonant',
    type: 'grammar',
    label: '冠词误用',
    re: /\ban\s+([b-df-hj-np-tv-z][a-zA-Z]*)\b/gi,
    test: m => !H_EXCEPTIONS.includes(m[1].toLowerCase()),
    fix: m => `a ${m[1]}`,
    explain: '辅音音素开头的单词前用 a。hour / honest / honor 的 h 不发音，仍用 an。'
  },
  {
    id: 'missing-article-singular',
    type: 'grammar',
    label: '可数名词缺冠词',
    re: /\b(go to|attend|enter)\s+(school|university|college|class)\b/gi,
    test: m => m[0].toLowerCase().includes('go to school') === false,
    fix: m => `${m[1]} the ${m[2]}`,
    explain: '表示「去某所学校/参加某场课」这类具体指代时，可数单数名词前需要限定词。'
  },

  // ---------------- 主谓一致 ----------------
  {
    id: 'sv-third-person',
    type: 'grammar',
    label: '主谓一致',
    re: /\b(he|she|it)\s+(go|come|have|do|make|take|like|want|think|say|know|study|write|read|play|need|feel|look|get|help|find|seem)\b/gi,
    fix: m => `${m[1]} ${thirdPerson(m[2])}`,
    explain: '一般现在时中，第三人称单数主语后的动词要加 -s/-es（have→has，go→goes）。这是四级作文扣分最密集的一类。'
  },
  {
    id: 'sv-people',
    type: 'grammar',
    label: '主谓一致',
    re: /\bpeople\s+(is|was|has)\b/gi,
    fix: m => `people ${m[1].toLowerCase() === 'has' ? 'have' : m[1].toLowerCase() === 'is' ? 'are' : 'were'}`,
    explain: 'people 是复数概念（= persons），谓语用 are / were / have。'
  },
  {
    id: 'sv-everyone',
    type: 'grammar',
    label: '主谓一致',
    re: /\b(everyone|everybody|someone|somebody|nobody|each of us|each student)\s+(have|are|were|do)\b/gi,
    fix: m => {
      const map = { have: 'has', are: 'is', were: 'was', do: 'does' }
      return `${m[1]} ${map[m[2].toLowerCase()]}`
    },
    explain: 'everyone / everybody / someone 等不定代词作主语时视为单数，谓语用 has / is / does。'
  },
  {
    id: 'sv-there-is-plural',
    type: 'grammar',
    label: 'there be 句型',
    re: /\bthere\s+(is|was)\s+(many|several|a lot of|lots of|numerous|two|three|four|five|some)\b/gi,
    fix: m => `there ${m[1].toLowerCase() === 'is' ? 'are' : 'were'} ${m[2]}`,
    explain: 'there be 句型的 be 动词与后面真正的主语一致，复数主语用 are / were。'
  },

  // ---------------- 常见语法点 ----------------
  {
    id: 'pronoun-i-lower',
    type: 'grammar',
    label: '代词大小写',
    re: /(^|[^A-Za-z])i([^A-Za-z]|$)/g,
    fix: m => `${m[1]}I${m[2]}`,
    explain: '英语中第一人称代词 I 无论出现在句首还是句中都必须大写。'
  },
  {
    id: 'double-conjunction',
    type: 'grammar',
    label: '连词冗余',
    re: /\b(because|although|though|since)\b[^.!?]{0,60}?\b(so|but)\b/gi,
    fix: m => m[0].replace(/\b(so|but)\b\s*/i, ''),
    explain: 'because 与 so、although 与 but 不能在同一个句子里同时出现，中文的「因为…所以…」「虽然…但是…」不能直译。'
  },
  {
    id: 'one-of-singular',
    type: 'grammar',
    label: 'one of 结构',
    re: /\bone of the ([a-zA-Z]+)\b/gi,
    test: m => !m[1].toLowerCase().endsWith('s'),
    fix: m => `one of the ${m[1]}s`,
    explain: 'one of the + 复数名词，表示「……之一」，后面名词必须用复数。'
  },
  {
    id: 'double-negative',
    type: 'grammar',
    label: '双重否定',
    re: /\b(don't|doesn't|didn't|can't|cannot|won't|isn't|aren't)\b[^.!?]{0,24}?\b(nothing|nobody|none|never)\b/gi,
    fix: m => m[0],
    explain: '英语一个句子通常只保留一个否定词，双重否定会变成肯定含义。'
  },

  // ---------------- 词形与搭配 ----------------
  {
    id: 'uncountable-plural',
    type: 'word',
    label: '不可数名词',
    re: /\b(informations|advices|equipments|furnitures|knowledges|musics|homeworks|newses|progresses)\b/gi,
    fix: m => {
      const map = {
        informations: 'information', advices: 'advice', equipments: 'equipment',
        furnitures: 'furniture', knowledges: 'knowledge', musics: 'music',
        homeworks: 'homework', newses: 'news', progresses: 'progress'
      }
      return map[m[0].toLowerCase()] || m[0]
    },
    explain: 'information / advice / equipment / knowledge / homework 都是不可数名词，没有复数形式，计量要用 a piece of。'
  },
  {
    id: 'peoples',
    type: 'word',
    label: '词形错误',
    re: /\bpeoples\b/gi,
    fix: () => 'people',
    explain: 'people 本身即复数（person 的复数），加 s 只在表示「多个民族」时使用，写作中基本用不到。'
  },
  {
    id: 'alot',
    type: 'word',
    label: '拼写错误',
    re: /\balot\b/gi,
    fix: () => 'a lot of',
    explain: 'a lot 是两个词，且后面接名词时要加 of。'
  },
  {
    id: 'cannot-split',
    type: 'word',
    label: '拼写规范',
    re: /\bcan\s+not\b/gi,
    fix: () => 'cannot',
    explain: 'cannot 通常写成一个词；只有强调对比时才分开写（can not only...）。'
  },
  {
    id: 'advice-verb',
    type: 'word',
    label: '词性混淆',
    re: /\b(advice)\s+(you|him|her|them|us|me|students)\b/gi,
    fix: m => `advise ${m[2]}`,
    explain: 'advice 是名词「建议」，advise 是动词「建议某人做某事」，考试常考这一组。'
  },
  {
    id: 'affect-effect',
    type: 'word',
    label: '形近词混淆',
    re: /\b(effect)\s+(on\s+)?(our|my|their|the)\s+(\w+)\b/gi,
    test: m => false,
    fix: m => m[0],
    explain: 'affect 是动词「影响」，effect 是名词「影响、效果」（have an effect on）。写作中用动词时别写成 effect。'
  },

  // ---------------- 中式英语 ----------------
  {
    id: 'chinglish-very-like',
    type: 'chinglish',
    label: '中式表达',
    re: /\bI\s+very\s+(like|love|enjoy|want)\b/gi,
    fix: m => `I really ${m[1]}`,
    explain: 'very 不能直接修饰动词。要用 really / greatly，或换成 I like ... very much。'
  },
  {
    id: 'chinglish-learn-knowledge',
    type: 'collocation',
    label: '动宾搭配',
    re: /\b(learn|study)\s+(a lot of\s+)?knowledge\b/gi,
    fix: m => `acquire ${m[2] || ''}knowledge`,
    explain: 'knowledge 与动词的固定搭配是 acquire / gain / obtain knowledge，learn 后面接的是技能或科目。'
  },
  {
    id: 'chinglish-open-light',
    type: 'collocation',
    label: '动宾搭配',
    re: /\b(open|close)\s+(the\s+)?(light|lights|computer|TV|air conditioner)\b/gi,
    fix: m => `${m[1].toLowerCase() === 'open' ? 'turn on' : 'turn off'} ${m[2] || ''}${m[3]}`,
    explain: '电器的「开/关」用 turn on / turn off，open / close 只用于门窗、书本这类物理开合。'
  },
  {
    id: 'chinglish-play-computer',
    type: 'collocation',
    label: '动宾搭配',
    re: /\bplay\s+(the\s+)?(computer|phone|mobile)\b/gi,
    fix: m => `use ${m[2]}`,
    explain: 'play 后面接的是游戏或球类，设备本身要用 use。'
  },
  {
    id: 'chinglish-according-me',
    type: 'chinglish',
    label: '中式表达',
    re: /\baccording to me\b/gi,
    fix: () => 'in my view',
    explain: 'according to 用于引用他人或资料，表达自己的观点要用 in my view / from my perspective。'
  },
  {
    id: 'chinglish-how-to-say',
    type: 'chinglish',
    label: '疑问句结构',
    re: /\bhow to say\b/gi,
    fix: () => 'how do you say',
    explain: '特殊疑问句要有助动词：How do you say ... in English?'
  },
  {
    id: 'chinglish-in-nowadays',
    type: 'chinglish',
    label: '介词冗余',
    re: /\bin\s+nowadays\b/gi,
    fix: () => 'nowadays',
    explain: 'nowadays 本身就是副词，前面不加介词。'
  },
  {
    id: 'cliche-development',
    type: 'style',
    label: '套话模板',
    re: /\bwith the (rapid )?development of\b/gi,
    fix: () => 'Driven by',
    explain: 'With the rapid development of... 是阅卷老师最疲劳的模板句，全考场可能有一半人这么开头。可改成「Driven by X, ...」或直接用一个具体现象、数据切入。'
  },
  {
    id: 'cliche-more-and-more',
    type: 'style',
    label: '表达单一',
    re: /\bmore and more\b/gi,
    fix: () => 'an increasing number of',
    explain: 'more and more 反复出现会显得词汇量不足，可替换为 an increasing number of / a growing proportion of。'
  },
  {
    id: 'cliche-last-but-not-least',
    type: 'style',
    label: '套话模板',
    re: /\blast but not least\b/gi,
    fix: () => 'finally',
    explain: 'last but not least 属于高频模板，除非全文其他部分很出彩，否则建议用 Finally / Most importantly 替代。'
  },
  {
    id: 'redundant-in-my-opinion',
    type: 'style',
    label: '语义重复',
    re: /\bin my opinion,?\s*I\s+think\b/gi,
    fix: () => 'In my opinion',
    explain: 'in my opinion 和 I think 语义重叠，二选一即可。'
  },
  {
    id: 'informal-gonna',
    type: 'style',
    label: '语域不当',
    re: /\b(gonna|wanna|gotta|kinda|sorta|ain't)\b/gi,
    fix: m => ({ gonna: 'going to', wanna: 'want to', gotta: 'have to', kinda: 'kind of', sorta: 'sort of', "ain't": 'is not' }[m[0].toLowerCase()] || m[0]),
    explain: '这些是口语缩写，正式作文里不能出现。'
  },
  {
    id: 'very-overuse',
    type: 'style',
    label: '程度副词单一',
    re: /\bvery\s+(good|bad|big|small|important|happy|angry|tired|nice|beautiful)\b/gi,
    fix: m => {
      const map = {
        good: 'excellent', bad: 'unfavorable', big: 'substantial', small: 'minor',
        important: 'crucial', happy: 'delighted', angry: 'furious',
        tired: 'exhausted', nice: 'delightful', beautiful: 'stunning'
      }
      return map[m[1].toLowerCase()] || m[0]
    },
    explain: 'very + 基础形容词是最容易暴露词汇量不足的组合，直接换成一个更精确的单词即可。'
  },

  // ---------------- 标点与格式 ----------------
  {
    id: 'space-before-punct',
    type: 'punct',
    label: '标点格式',
    re: /\s+([,.!?;:])/g,
    fix: m => m[1],
    explain: '英文标点紧贴前一个单词，标点前不加空格。'
  },
  {
    id: 'missing-space-after-punct',
    type: 'punct',
    label: '标点格式',
    re: /([,;:])([A-Za-z])/g,
    fix: m => `${m[1]} ${m[2]}`,
    explain: '英文逗号、分号、冒号后需要一个空格再接下一个单词。'
  },
  {
    id: 'double-space',
    type: 'punct',
    label: '格式冗余',
    re: /[ \t]{2,}/g,
    fix: () => ' ',
    explain: '连续多个空格属于格式问题，会影响卷面分。'
  },
  {
    id: 'lowercase-after-period',
    type: 'punct',
    label: '句首大写',
    re: /([.!?]\s+)([a-z])/g,
    fix: m => `${m[1]}${m[2].toUpperCase()}`,
    explain: '句号、问号、感叹号后的第一个字母必须大写。'
  },
  {
    id: 'repeated-word',
    type: 'word',
    label: '重复用词',
    re: /\b([A-Za-z]{2,})\s+\1\b/gi,
    fix: m => m[1],
    explain: '同一个词连续出现两次，通常是输入或修改时残留。'
  }
]

// ============================================================
// 高级表达替换建议（不计为错误，只作为提分建议）
// ============================================================
export const UPGRADES = [
  { re: /\bthink\b/gi, to: 'maintain', note: 'think 换成 maintain / argue 更学术' },
  { re: /\bget\b/gi, to: 'obtain', note: 'get 在书面语里可换成 obtain / acquire' },
  { re: /\bhelp\b/gi, to: 'facilitate', note: 'help 换成 facilitate / contribute to 更正式' },
  { re: /\bmany\b/gi, to: 'numerous', note: 'many 可换成 numerous / a considerable number of' },
  { re: /\bshow\b/gi, to: 'demonstrate', note: 'show 可换成 demonstrate / reveal' },
  { re: /\bimportant\b/gi, to: 'crucial', note: 'important 可换成 crucial / vital / pivotal' },
  { re: /\bdifficult\b/gi, to: 'challenging', note: 'difficult 可换成 challenging / demanding' },
  { re: /\bproblem\b/gi, to: 'issue', note: 'problem 可换成 issue / challenge' },
  { re: /\bpeople\b/gi, to: 'individuals', note: 'people 可换成 individuals / the public' },
  { re: /\bbecause\b/gi, to: 'owing to the fact that', note: 'because 可换成 owing to / due to the fact that' }
]

// ============================================================
// 连接词库（结构分依据）
// ============================================================
export const CONNECTIVES = {
  addition: ['furthermore', 'moreover', 'in addition', 'besides', 'additionally', 'what is more'],
  contrast: ['however', 'nevertheless', 'nonetheless', 'on the contrary', 'in contrast', 'whereas', 'conversely'],
  cause: ['therefore', 'consequently', 'as a result', 'thus', 'hence', 'accordingly'],
  example: ['for instance', 'for example', 'to illustrate', 'a case in point'],
  conclusion: ['in conclusion', 'to sum up', 'on the whole', 'all things considered', 'ultimately'],
  sequence: ['firstly', 'secondly', 'finally', 'meanwhile', 'subsequently']
}

export const ALL_CONNECTIVES = Object.values(CONNECTIVES).flat()

// ============================================================
// 语法知识点库（对话 / 错题讲解 / 知识点卡片共用）
// ============================================================
export const GRAMMAR_POINTS = [
  {
    key: 'subjunctive',
    name: '虚拟语气',
    keywords: ['虚拟语气', 'subjunctive', 'if i were', 'would have'],
    definition: '虚拟语气用于表达与事实相反的假设、愿望、建议或要求，核心是「时态往过去退一步」。',
    rules: [
      '与现在事实相反：If + 主语 + 过去式（be 用 were）, 主语 + would/could/might + 动词原形',
      '与过去事实相反：If + 主语 + had done, 主语 + would/could/might + have done',
      '与将来事实相反：If + 主语 + were to do / should do, 主语 + would + 动词原形',
      '宾语从句中 suggest / demand / insist / require 后接 (should) + 动词原形'
    ],
    examples: [
      { en: 'If I were you, I would apply for the scholarship.', cn: '如果我是你，我会申请这个奖学金。（与现在事实相反）' },
      { en: 'If she had left earlier, she would have caught the train.', cn: '如果她早点出发，就赶上火车了。（与过去事实相反）' },
      { en: 'The professor suggested that every student (should) submit the draft by Friday.', cn: '教授建议每位学生在周五前提交初稿。' }
    ],
    pitfalls: '最常见错误是把 if 从句里的 were 写成 was，以及在 suggest 从句里加 to do。'
  },
  {
    key: 'attributive',
    name: '定语从句',
    keywords: ['定语从句', 'relative clause', 'which', 'that', 'whose', 'whom'],
    definition: '定语从句用关系词把一句话变成形容词，去修饰前面的名词（先行词）。',
    rules: [
      '先行词是人：who（主格）/ whom（宾格）/ whose（所有格）',
      '先行词是物：which / that；先行词是地点用 where，时间用 when，原因用 why',
      '非限制性定语从句只能用 which，不能用 that，且用逗号隔开',
      '介词 + which/whom 结构里介词不能丢：the city in which I grew up'
    ],
    examples: [
      { en: 'The book that you lent me last week is extremely inspiring.', cn: '你上周借给我的那本书非常有启发性。' },
      { en: 'Students whose parents support them tend to perform better.', cn: '父母给予支持的学生通常表现更好。' },
      { en: 'He failed the exam, which surprised nobody.', cn: '他考试没过，这一点没人感到意外。' }
    ],
    pitfalls: '关系代词在从句中作宾语时容易多写一个宾语（the book which I read it），这是最典型的错。'
  },
  {
    key: 'tense',
    name: '时态体系',
    keywords: ['时态', 'tense', 'present perfect', '过去完成', '现在完成'],
    definition: '英语时态 = 时间（现在/过去/将来）+ 体（一般/进行/完成/完成进行），共 16 种，四级作文常用 6 种。',
    rules: [
      '一般现在时：客观事实、普遍规律、反复发生的动作',
      '现在完成时：have/has + done，强调对现在的影响，常与 already / yet / since / so far 连用',
      '过去完成时：had + done，表示「过去的过去」，用于两个过去动作的先后',
      '主将从现：时间/条件状语从句中，主句将来时，从句用一般现在时'
    ],
    examples: [
      { en: 'Over the past decade, online learning has reshaped higher education.', cn: '过去十年，在线学习重塑了高等教育。' },
      { en: 'By the time I arrived, the lecture had already begun.', cn: '我到的时候，讲座已经开始了。' },
      { en: 'If it rains tomorrow, the sports meeting will be postponed.', cn: '如果明天下雨，运动会将延期。' }
    ],
    pitfalls: 'since 后面必须接时间点且主句用完成时；for + 时间段要和完成时搭配，别写成 I studied here for three years 表达「到目前为止」。'
  },
  {
    key: 'nonfinite',
    name: '非谓语动词',
    keywords: ['非谓语', 'non-finite', '不定式', '动名词', '分词'],
    definition: '一个句子里已经有谓语动词，其他动词必须变成不定式、动名词或分词形式。',
    rules: [
      '不定式 to do：表目的、将来、或将要做的事',
      '动名词 doing：作主语/宾语，表习惯性动作；介词后必须用 doing',
      '现在分词 doing：主动、进行；过去分词 done：被动、完成',
      'remember/forget/stop 后接 to do 与 doing 意思完全不同'
    ],
    examples: [
      { en: 'Reading extensively is the most effective way to enlarge vocabulary.', cn: '大量阅读是扩大词汇量最有效的方式。' },
      { en: 'Faced with fierce competition, graduates tend to pursue further study.', cn: '面对激烈竞争，毕业生倾向于继续深造。' },
      { en: 'He stopped smoking last year.', cn: '他去年戒烟了。（stop doing = 停止做）' }
    ],
    pitfalls: '两个谓语并列却没连词（I like reading, he likes playing）是最常见错误，需要改成并列句或从句。'
  },
  {
    key: 'passive',
    name: '被动语态',
    keywords: ['被动', 'passive voice', 'be done'],
    definition: '当动作的承受者比执行者更重要，或执行者不明时使用被动语态：be + 过去分词。',
    rules: [
      '一般现在时被动：am/is/are + done',
      '一般过去时被动：was/were + done',
      '情态动词被动：can/must/should + be + done',
      '只有及物动词才有被动语态，happen / appear / belong 没有被动'
    ],
    examples: [
      { en: 'English is spoken by more than 1.5 billion people worldwide.', cn: '全世界有超过 15 亿人说英语。' },
      { en: 'The issue should be addressed before it gets worse.', cn: '这个问题应该在恶化之前得到处理。' }
    ],
    pitfalls: 'happen / occur / take place 本身不及物，不能写成 was happened。'
  },
  {
    key: 'subject-verb',
    name: '主谓一致',
    keywords: ['主谓一致', 'subject-verb', 'agreement'],
    definition: '谓语动词的单复数必须与主语保持一致，这是英语句子的硬性规则。',
    rules: [
      '第三人称单数主语 + 动词加 -s（一般现在时）',
      'people / police / cattle 视为复数',
      'everyone / each / neither 视为单数',
      'there be 句型与后面真正的主语一致',
      'either...or / neither...nor 遵循就近原则'
    ],
    examples: [
      { en: 'Each of the participants has received a certificate.', cn: '每位参与者都收到了一份证书。' },
      { en: 'There are several reasons why I support this view.', cn: '我支持这个观点有几个原因。' }
    ],
    pitfalls: '主语后面跟了长定语时容易忘记一致：The number of students who study abroad is increasing.（主语是 number，不是 students）'
  },
  {
    key: 'article',
    name: '冠词用法',
    keywords: ['冠词', 'article', 'a an the'],
    definition: '冠词是中文里没有的语法范畴，分不定冠词 a/an（泛指）和定冠词 the（特指）。',
    rules: [
      '首次提到用 a/an，再次提到用 the',
      '世界上独一无二的、最高级、序数词前用 the',
      '不可数名词和复数名词泛指时不加冠词',
      '固定搭配要整块记：in the end / at present / go to school'
    ],
    examples: [
      { en: 'A student asked a question, and the teacher answered it patiently.', cn: '一个学生提了个问题，老师耐心地解答了。' },
      { en: 'The Yangtze River is the longest river in China.', cn: '长江是中国最长的河流。' }
    ],
    pitfalls: 'a/an 的选择看的是读音而不是字母：an hour、a university、an MBA。'
  },
  {
    key: 'writing-structure',
    name: '四级作文结构',
    keywords: ['作文', 'writing', '结构', '模板', 'essay'],
    definition: '四级作文 120–180 词，三段式最稳：引入现象 → 分析原因/利弊 → 给出观点与建议。',
    rules: [
      '第一段：2–3 句交代现象与争议，不要背模板句开头',
      '第二段：2–3 个分论点，每点「观点 + 解释 + 例子」',
      '第三段：明确立场 + 一条可执行的建议',
      '全文至少 4 个不同的连接词，避免 more and more / very good 这类低阶表达'
    ],
    examples: [
      { en: 'A growing number of undergraduates are turning to online courses, a shift that has both widened access and raised concerns about self-discipline.', cn: '越来越多的本科生转向在线课程，这一变化既扩大了受教育机会，也引发了关于自律的担忧。' }
    ],
    pitfalls: '字数不足、段落只有一段、通篇简单句，是四级写作最致命的三件事。'
  }
]

export function findGrammarPoint(text) {
  const s = String(text || '').toLowerCase()
  if (!s) return null
  let best = null
  let bestScore = 0
  for (const p of GRAMMAR_POINTS) {
    let score = 0
    for (const k of p.keywords) {
      if (s.includes(k.toLowerCase())) score += k.length
    }
    if (score > bestScore) {
      bestScore = score
      best = p
    }
  }
  return best
}
