// ============================================================
// AI 全局配置 · 切换真/假 AI 只改这里
// ============================================================
// mode 说明：
//   'local'   —— 伪 AI（离线规则引擎 + 语料库），零依赖、零费用、可离线演示【当前】
//   'backend' —— 走后端 Spring /api/ai/*（后端已有一套本地算法）
//   'llm'     —— 真大模型，需在下方 llm 里填 baseURL / apiKey / model
// ============================================================

export const AI_CONFIG = {
  // 当前生效的提供者
  mode: 'local',

  // 主提供者失败时是否自动降级到本地伪 AI（演示场景建议保持 true）
  fallbackToLocal: true,

  // 模拟推理耗时（毫秒区间），让交互更像真模型；设为 [0,0] 可关闭
  latency: [280, 780],

  // ---------- 真 AI 接口占位 ----------
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

/** 运行时读取当前模式（预留给管理端开关） */
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
    local: '本地规则引擎（伪 AI）',
    backend: '后端 AI 服务',
    llm: '大语言模型'
  }[mode] || mode
}
