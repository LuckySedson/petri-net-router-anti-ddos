export default function ControlPanel({
  state,
  simulationRunning,
  onArrival,
  onProcess,
  onDdos,
  onToggleSimulation,
  onReset
}) {
  return (
    <div className="control-panel">
      <div className="control-group">
        <h3>Trafic manuel</h3>
        <button className="btn btn-arrival" onClick={onArrival}>
          Envoyer un paquet (T1)
        </button>
        <button className="btn btn-process" onClick={onProcess}>
          Traiter un paquet (T2)
        </button>
      </div>

      <div className="control-group">
        <h3>Attaque DDoS</h3>
        <button className="btn btn-ddos" onClick={() => onDdos(15)}>
          Rafale x15
        </button>
        <button className="btn btn-ddos" onClick={() => onDdos(40)}>
          Rafale x40
        </button>
      </div>

      <div className="control-group">
        <h3>Simulation automatique</h3>
        <button
          className={`btn ${simulationRunning ? 'btn-stop' : 'btn-start'}`}
          onClick={onToggleSimulation}
        >
          {simulationRunning ? 'Mettre en pause' : 'Démarrer le trafic'}
        </button>
        <button className="btn btn-reset" onClick={onReset}>
          Réinitialiser
        </button>
      </div>

      <div className="control-group log-group">
        <h3>Journal</h3>
        <div className="log-list">
          {state?.log?.map((entry, i) => (
            <div key={i} className={`log-entry ${entry.includes('⚠') ? 'log-warn' : entry.includes('✔') ? 'log-ok' : ''}`}>
              {entry}
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}