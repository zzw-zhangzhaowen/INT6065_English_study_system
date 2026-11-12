import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client/dist/sockjs'

let stompClient = null

export function connectWebSocket(token, onConnected, onError) {
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
      console.error('STOMP error:', frame)
      if (onError) onError(frame)
    },
    onDisconnect: () => {
      console.log('WebSocket disconnected')
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
