import { useEffect, useRef, useState } from 'react'
import {
  connectWebSocket,
  fetchSimulationStatus,
  fetchState,
  sendArrival,
  sendDdos,
  sendProcess,
  sendReset,
  startSimulation,
  stopSimulation
} from './api/petriNetApi.js'
import ControlPanel from './components/ControlPanel.jsx'
import PetriNetDiagram from './components/PetriNetDiagram.jsx'

export default function App() {
  const [state, setState] = useState(null)
  const [simulationRunning, setSimulationRunning] = useState(false)
  const [connected, setConnected] = useState(false)
  const wsClientRef = useRef(null)

  useEffect(() => {
    fetchState().then(setState).catch(() => {})
    fetchSimulationStatus().then((s) => setSimulationRunning(s.running)).catch(() => {})

    const client = connectWebSocket((newState) => {
      setState(newState)
      setConnected(true)
    })
    wsClientRef.current = client

    return () => {
      client.deactivate()
    }
  }, [])

  async function handleArrival() {
    const res = await sendArrival()
    setState(res.state)
  }

  async function handleProcess() {
    const res = await sendProcess()
    setState(res.state)
  }

  async function handleDdos(count) {
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

  return (
    <div className="app">
      <header className="app-header">
        <h1>Simulateur Routeur Anti-DDoS</h1>
        <p className="app-subtitle">Réseau de Petri à arc inhibiteur — protection par seuil de saturation</p>
        <div className={`connection-badge ${connected ? 'connected' : 'disconnected'}`}>
          <span className="connection-dot" />
          {connected ? 'Temps réel actif' : 'Connexion…'}
        </div>
      </header>

      <main className="app-main">
        <section className="diagram-section">
          <PetriNetDiagram state={state} />
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
          />
        </aside>
      </main>
    </div>
  )
}