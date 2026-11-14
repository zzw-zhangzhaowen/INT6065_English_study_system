// ============================================================
// AI 全局配置 · 切换 AI 提供者只改这里
// ============================================================
// mode 说明：
//   'local'   —— 内置智能引擎，零依赖、零费用、可离线运行【当前】
//   'backend' —— 走后端 Spring /api/ai/*
//   'llm'     —— 大语言模型，需在下方 llm 里填 baseURL / apiKey / model
// ============================================================

export const AI_CONFIG = {
  // 当前生效的提供者
  mode: 'local',

  // 主提供者不可用时自动切换到内置引擎，保证功能连续可用
  fallbackToLocal: true,

  // 推理耗时区间（毫秒），用于让交互节奏贴近真实推理
  latency: [280, 780],

  // ---------- 大语言模型接口配置 ----------
  // 接入时只需：mode 改成 'llm' + 填下面三项
  // 兼容任何 OpenAI Chat Completions 协议的服务
  //（OpenAI / DeepSeek / 通义 / Moonshot / 本地 vLLM / Ollama 的 /v1 接口）
  llm: {
    baseURL: '', // 例：'https://api.deepseek.com/v1'
    apiKey: '', // 例：'sk-xxxxxxxx'
    model: '', // 例：'deepseek-chat'
    temperature: 0.6,
    maxTokens: 1600,
    stream: false, // 置 true 需要提供方支持 SSE 流式
    timeout: 60000,
    // 生产环境请勿把 apiKey 放前端：应改为请求自己的 /api/ai/llm 网关
    viaProxy: true,
    proxyPath: '/api/ai/llm/chat'
  },

  // ---------- 后端提供者 ----------
  backend: {
    enabled: false,
    baseURL: '/api/ai',
    // 后端已有的接口（见 backend/.../AiController.java）
    endpoints: {
      chat: '/chat',
      correctWriting: '/writing/correct',
      grammarCheck: '/grammar/check',
      stats: '/stats'
    }
  },

  // ---------- 教师端后台任务 ----------
  teacher: {
    // 自动汇总间隔（分钟）
    digestIntervalMinutes: 5,
    // 登录后是否自动运行一次
    autoRun: true,
    // 是否把结果缓存到 localStorage（刷新不丢）
    persist: true
  }
}

/** 运行时读取当前模式（管理端可切换） */
export function getMode() {
  return AI_CONFIG.mode
}

/** 运行时切换模式 */
export function setMode(mode) {
  if (['local', 'backend', 'llm'].includes(mode)) {
    AI_CONFIG.mode = mode
    return true
  }
  return false
}

/** 当前模式的展示文案 */
export function modeLabel(mode = AI_CONFIG.mode) {
  return {
    local: 'AI 智能引擎',
    backend: '后端 AI 服务',
    llm: '大语言模型'
  }[mode] || mode
}
