/**
 * 获取文件的完整访问URL
 * 处理后端返回的相对路径，拼接正确的访问地址
 */
export const getFileUrl = (url) => {
  if (!url) return ''
  // 已经是完整URL，直接返回
  if (url.startsWith('http://') || url.startsWith('https://')) return url
  // data: / blob: 是自包含资源，不能加前缀，否则会失效
  if (url.startsWith('data:') || url.startsWith('blob:')) return url
  // 相对路径：开发环境通过 vite proxy 转发，生产环境拼接后端地址
  if (url.startsWith('/')) {
    return url
  }
  return `/${url}`
}

/** 默认封面图片：内联 SVG，避免依赖后端静态资源（离线演示也不会 404） */
export const DEFAULT_COVER = `data:image/svg+xml;charset=utf-8,${encodeURIComponent(
  '<svg xmlns="http://www.w3.org/2000/svg" width="320" height="180"><defs><linearGradient id="g" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="#e2e8f0"/><stop offset="1" stop-color="#cbd5e1"/></linearGradient></defs><rect width="320" height="180" fill="url(#g)"/><circle cx="250" cy="40" r="58" fill="#ffffff" opacity="0.35"/><circle cx="60" cy="150" r="66" fill="#ffffff" opacity="0.25"/></svg>'
)}`
