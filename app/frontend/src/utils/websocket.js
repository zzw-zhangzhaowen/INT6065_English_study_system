import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client/dist/sockjs'

// 独立运行模式：课堂消息由本地数据层同步，不建立实时通道
const LOCAL_ONLY = String(import.meta.env.VITE_MOCK_ONLY || '') === 'true'

let stompClient = null

export function connectWebSocket(token, onConnected, onError) {
  if (LOCAL_ONLY) {
    console.info('[课堂] 本地运行模式：课堂消息经本地数据通道同步')
    return null
  }

  const client = new Client({
    webSocketFactory: () => new SockJS('/ws'),
    connectHeaders: {
      Authorization: `Bearer ${token}`
    },
    reconnectDelay: 5000,
    heartbeatIncoming: 10000,
    heartbeatOutgoing: 10000,
    onConnect: () => {
      stompClient = client
      if (onConnected) onConnected(client)
    },
    onStompError: (frame) => {
      console.warn('STOMP 通道异常:', frame?.headers?.message || frame)
      if (onError) onError(frame)
    },
    onDisconnect: () => {
      console.info('课堂实时通道已断开')
    }
  })

  client.activate()
  return client
}

export function disconnectWebSocket() {
  if (stompClient && stompClient.connected) {
    stompClient.deactivate()
    stompClient = null
  }
}

export function getStompClient() {
  return stompClient
}
