<#
    serve-dist.ps1 - 零依赖静态文件服务器
    用途：在没有安装 Node.js 的机器上，直接用 PowerShell 把前端构建产物 dist/ 跑起来，
          供 start-all.bat 的离线兜底分支调用。

    用法：
      powershell -NoProfile -ExecutionPolicy Bypass -File serve-dist.ps1 -Port 3002 -Root "D:\path\dist"

    说明：这是本地演示用服务器，不做任何鉴权，请勿对外暴露端口。
#>
param(
    [int]$Port = 3002,
    [string]$Root = "."
)

$ErrorActionPreference = 'Stop'

try {
    $rootPath = (Resolve-Path -LiteralPath $Root).Path
} catch {
    Write-Host "[错误] 找不到目录: $Root"
    exit 1
}

$listener = New-Object System.Net.HttpListener
# 同时注册 localhost 与 127.0.0.1，否则浏览器用 IP 访问时会因 Host 不匹配返回 400
$listener.Prefixes.Add("http://localhost:$Port/")
$listener.Prefixes.Add("http://127.0.0.1:$Port/")
try {
    $listener.Start()
} catch {
    Write-Host "[错误] 端口 $Port 监听失败: $($_.Exception.Message)"
    exit 1
}

$mime = @{
    '.html' = 'text/html; charset=utf-8'
    '.js'   = 'application/javascript'
    '.mjs'  = 'application/javascript'
    '.css'  = 'text/css'
    '.json' = 'application/json'
    '.svg'  = 'image/svg+xml'
    '.png'  = 'image/png'
    '.jpg'  = 'image/jpeg'
    '.jpeg' = 'image/jpeg'
    '.gif'  = 'image/gif'
    '.webp' = 'image/webp'
    '.ico'  = 'image/x-icon'
    '.woff' = 'font/woff'
    '.woff2'= 'font/woff2'
    '.ttf'  = 'font/ttf'
    '.map'  = 'application/json'
}

Write-Host "[静态服务器] 已启动 http://localhost:$Port/  (根目录: $rootPath)"
Write-Host "[静态服务器] 按 Ctrl+C 停止"

while ($listener.IsListening) {
    $ctx = $listener.GetContext()
    try {
        $rel = [System.Uri]::UnescapeDataString($ctx.Request.Url.LocalPath).TrimStart('/')
        if ($rel -eq '') { $rel = 'index.html' }
        $file = Join-Path $rootPath $rel
        if (-not (Test-Path -LiteralPath $file -PathType Leaf)) {
            # 单页应用回退：找不到的路径一律返回 index.html
            $file = Join-Path $rootPath 'index.html'
        }
        $ext = [System.IO.Path]::GetExtension($file).ToLowerInvariant()
        if ($mime.ContainsKey($ext)) { $ctx.Response.ContentType = $mime[$ext] }
        else { $ctx.Response.ContentType = 'application/octet-stream' }
        $bytes = [System.IO.File]::ReadAllBytes($file)
        $ctx.Response.ContentLength64 = $bytes.Length
        $ctx.Response.OutputStream.Write($bytes, 0, $bytes.Length)
    } catch {
        try { $ctx.Response.StatusCode = 500 } catch {}
    } finally {
        try { $ctx.Response.Close() } catch {}
    }
}
