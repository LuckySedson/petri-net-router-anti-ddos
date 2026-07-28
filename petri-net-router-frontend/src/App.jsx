import { useEffect, useRef, useState } from 'react'
import {
  connectWebSocket,
  fetchSimulationStatus,
  fetchState,
  sendArrival,
  sendDdos,
  sendProcess,
  sendReset,
  sendResetCounter,
  startSimulation,
  stopSimulation
} from './api/petriNetApi.js'
import ControlPanel from './components/ControlPanel.jsx'
import PetriNetDiagram from './components/PetriNetDiagram.jsx'

export default function App() {
  const [state, setState] = useState(null)
  const [simulationRunning, setSimulationRunning] = useState(false)
  const [connected, setConnected] = useState(false)
  const [theme, setTheme] = useState('dark')
  const [activeArc, setActiveArc] = useState(null)
  const wsClientRef = useRef(null)

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme === 'light' ? 'light' : '')
  }, [theme])

  useEffect(() => {
    fetchState().then(setState).catch(() => {})
    fetchSimulationStatus().then((s) => setSimulationRunning(s.running)).catch(() => {})
    const client = connectWebSocket(
      (newState) => setState(newState),
      () => setConnected(true)
    )
    wsClientRef.current = client
    return () => client.deactivate()
  }, [])

  function flashArc(arcId) {
    setActiveArc(arcId)
    setTimeout(() => setActiveArc(null), 600)
  }

  async function handleArrival() {
    flashArc('T1-P2')
    const res = await sendArrival()
    setState(res.state)
  }

  async function handleProcess() {
    flashArc('T2-P5')
    const res = await sendProcess()
    setState(res.state)
  }

  async function handleDdos(count) {
    flashArc('T1-P2')
    const res = await sendDdos(count)
    setState(res.state)
  }

  async function handleToggleSimulation() {
    if (simulationRunning) {
      await stopSimulation()
      setSimulationRunning(false)
    } else {
      await startSimulation()
      setSimulationRunning(true)
    }
  }

  async function handleReset() {
    const newState = await sendReset()
    setState(newState)
  }

  async function handleResetCounter() {
    flashArc('T5-P2')
    const res = await sendResetCounter()
    setState(res.state)
  }

  return (
    <div className="app">
      <header className="app-header">
        <h1>Simulateur Routeur Anti-DDoS</h1>
        <p className="app-subtitle">Réseau de Petri à arc inhibiteur — protection par seuil de saturation</p>
        <div className="header-right">
          <div className={`connection-badge ${connected ? 'connected' : 'disconnected'}`}>
            <span className="connection-dot" />
            {connected ? 'Temps réel actif' : 'Connexion…'}
          </div>
          <button
            className="theme-toggle"
            onClick={() => setTheme(t => t === 'dark' ? 'light' : 'dark')}
            title="Basculer mode clair/sombre"
          >
            {theme === 'dark' ? '☀️' : '🌙'}
          </button>
        </div>
      </header>

      <main className="app-main">
        <section className="diagram-section">
          <PetriNetDiagram state={state} activeArc={activeArc} />
          <div className="status-bar">
            <div className={`status-pill ${state?.entryOpen ? 'status-ok' : 'status-blocked'}`}>
              Entrée {state?.entryOpen ? 'ouverte' : 'fermée'}
            </div>
            <div className={`status-pill ${state?.saturated ? 'status-alert' : 'status-ok'}`}>
              {state?.saturated ? 'Routeur saturé' : 'Routeur stable'}
            </div>
            <div className="status-pill status-neutral">
              File : {state?.queueLength ?? 0} / {state?.threshold ?? '—'}
            </div>
          </div>
        </section>

        <aside className="panel-section">
          <ControlPanel
            state={state}
            simulationRunning={simulationRunning}
            onArrival={handleArrival}
            onProcess={handleProcess}
            onDdos={handleDdos}
            onToggleSimulation={handleToggleSimulation}
            onReset={handleReset}
            onResetCounter={handleResetCounter}
          />
        </aside>
      </main>
    </div>
  )
}