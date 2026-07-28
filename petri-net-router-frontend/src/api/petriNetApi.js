import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

const BASE_URL = '/api/petrinet'
const WS_URL = '/ws'

export async function fetchState() {
  const res = await fetch(`${BASE_URL}/state`)
  return res.json()
}

export async function sendArrival() {
  const res = await fetch(`${BASE_URL}/arrival`, { method: 'POST' })
  return res.json()
}

export async function sendProcess() {
  const res = await fetch(`${BASE_URL}/process`, { method: 'POST' })
  return res.json()
}

export async function sendDdos(count = 20) {
  const res = await fetch(`${BASE_URL}/ddos?count=${count}`, { method: 'POST' })
  return res.json()
}

export async function sendReset() {
  const res = await fetch(`${BASE_URL}/reset`, { method: 'POST' })
  return res.json()
}

export async function startSimulation() {
  const res = await fetch(`${BASE_URL}/simulation/start`, { method: 'POST' })
  return res.json()
}

export async function stopSimulation() {
  const res = await fetch(`${BASE_URL}/simulation/stop`, { method: 'POST' })
  return res.json()
}

export async function fetchSimulationStatus() {
  const res = await fetch(`${BASE_URL}/simulation/status`)
  return res.json()
}

export async function sendResetCounter() {
  const res = await fetch(`${BASE_URL}/reset-counter`, { method: 'POST' })
  return res.json()
}

export function connectWebSocket(onStateUpdate, onConnected) {
  const client = new Client({
    webSocketFactory: () => new SockJS(WS_URL),
    reconnectDelay: 3000,
    onConnect: () => {
      console.log('STOMP connecté')
      if (onConnected) onConnected()
      client.subscribe('/topic/petrinet-state', (message) => {
        const state = JSON.parse(message.body)
        onStateUpdate(state)
      })
    },
    onStompError: (frame) => {
      console.error('Erreur STOMP:', frame)
    },
    onWebSocketError: (event) => {
      console.error('Erreur WebSocket:', event)
    }
  })
  client.activate()
  return client
}