const PLACE_POS = {
  P1: { x: 110, y: 110 },
  P2: { x: 400, y: 110 },
  P3: { x: 400, y: 340 },
  P4: { x: 670, y: 340 }
}

const TRANSITION_POS = {
  T1: { x: 250, y: 110 },
  T2: { x: 540, y: 110 },
  T3: { x: 400, y: 225 },
  T4: { x: 250, y: 340 }
}

const PLACE_LABELS = {
  P1: 'Entrée_Ouverte',
  P2: 'File_Attente',
  P3: 'Routeur_Saturé',
  P4: 'Filtre_Actif'
}

const TRANSITION_LABELS = {
  T1: 'Arrivée_Paquet',
  T2: 'Traiter_Paquet',
  T3: 'Déclencher_Protection',
  T4: 'Réinitialiser_Sécurité'
}

function Tokens({ cx, cy, count }) {
  if (count === 0) return null
  if (count <= 5) {
    return Array.from({ length: count }).map((_, i) => {
      const angle = (i / count) * 2 * Math.PI - Math.PI / 2
      const tx = cx + (count === 1 ? 0 : Math.cos(angle) * 12)
      const ty = cy + (count === 1 ? 0 : Math.sin(angle) * 12)
      return <circle key={i} cx={tx} cy={ty} r="5" className="token-dot" />
    })
  }
  return (
    <text x={cx} y={cy + 6} textAnchor="middle" className="token-count">
      {count}
    </text>
  )
}

function PlaceNode({ id, tokens, isAlert }) {
  const { x, y } = PLACE_POS[id]
  return (
    <g className={`place-node ${isAlert ? 'place-alert' : ''}`}>
      <circle cx={x} cy={y} r="36" className="place-circle" />
      <Tokens cx={x} cy={y} count={tokens} />
      <text x={x} y={y - 50} textAnchor="middle" className="node-id">{id}</text>
      <text x={x} y={y + 56} textAnchor="middle" className="node-label">{PLACE_LABELS[id]}</text>
    </g>
  )
}

function TransitionNode({ id, fireable }) {
  const { x, y } = TRANSITION_POS[id]
  return (
    <g className={`transition-node ${fireable ? 'transition-fireable' : ''}`}>
      <rect x={x - 14} y={y - 14} width="28" height="28" className="transition-rect" />
      <text x={x} y={y - 26} textAnchor="middle" className="node-id">{id}</text>
      <text x={x} y={y + 42} textAnchor="middle" className="node-label">{TRANSITION_LABELS[id]}</text>
    </g>
  )
}

function Arc({ from, to, dashed = false, curve = 0, waypoints = null }) {
  let path
  if (waypoints) {
    const points = [from, ...waypoints, to]
    path = `M ${points[0].x} ${points[0].y} ` + points.slice(1).map((p) => `L ${p.x} ${p.y}`).join(' ')
  } else {
    const midX = (from.x + to.x) / 2
    const midY = (from.y + to.y) / 2 + curve
    path = `M ${from.x} ${from.y} Q ${midX} ${midY} ${to.x} ${to.y}`
  }
  return (
    <path
      d={path}
      className={dashed ? 'arc arc-inhibitor' : 'arc'}
      markerEnd={dashed ? 'url(#inhibitor-end)' : 'url(#arrow-end)'}
    />
  )
}

export default function PetriNetDiagram({ state }) {
  if (!state) return <div className="diagram-loading">Connexion au routeur…</div>

  const marking = state.marking || {}
  const fireable = new Set(state.fireableTransitions || [])

  return (
    <div className="diagram-wrapper">
      <svg viewBox="0 0 760 450" className="petri-svg">
        <defs>
          <marker id="arrow-end" markerWidth="10" markerHeight="10" refX="8" refY="3" orient="auto">
            <path d="M0,0 L0,6 L9,3 z" className="arrow-head" />
          </marker>
          <marker id="inhibitor-end" markerWidth="10" markerHeight="10" refX="5" refY="5" orient="auto">
            <circle cx="5" cy="5" r="4" className="inhibitor-head" />
          </marker>
        </defs>

        <Arc from={{ x: 166, y: 110 }} to={{ x: 234, y: 110 }} />
        <Arc from={{ x: 266, y: 110 }} to={{ x: 364, y: 110 }} />

        <Arc from={{ x: 436, y: 110 }} to={{ x: 526, y: 110 }} />

        <Arc from={{ x: 400, y: 146 }} to={{ x: 400, y: 211 }} />
        <Arc from={{ x: 400, y: 239 }} to={{ x: 400, y: 304 }} />
        <Arc from={{ x: 414, y: 232 }} to={{ x: 656, y: 326 }} curve={10} />

        <Arc from={{ x: 374, y: 340 }} to={{ x: 264, y: 340 }} />
        <Arc from={{ x: 670, y: 326 }} to={{ x: 270, y: 348 }} curve={26} />
        <Arc from={{ x: 240, y: 326 }} to={{ x: 128, y: 144 }} curve={-30} />

        <Arc
          from={{ x: 420, y: 326 }}
          to={{ x: 232, y: 104 }}
          dashed
          waypoints={[
            { x: 730, y: 326 },
            { x: 730, y: 20 },
            { x: 220, y: 20 },
            { x: 220, y: 104 }
          ]}
        />

        <Arc
          from={{ x: 400, y: 146 }}
          to={{ x: 250, y: 358 }}
          dashed
          waypoints={[
            { x: 20, y: 146 },
            { x: 20, y: 410 },
            { x: 250, y: 410 }
          ]}
        />

        {['P1', 'P2', 'P3', 'P4'].map((id) => (
          <PlaceNode
            key={id}
            id={id}
            tokens={marking[id] ?? 0}
            isAlert={id === 'P3' && (marking[id] ?? 0) > 0}
          />
        ))}

        {['T1', 'T2', 'T3', 'T4'].map((id) => (
          <TransitionNode key={id} id={id} fireable={fireable.has(id)} />
        ))}
      </svg>

      <div className="diagram-legend">
        <span><span className="legend-swatch legend-arc" /> arc normal</span>
        <span><span className="legend-swatch legend-inhibitor" /> arc inhibiteur</span>
        <span><span className="legend-swatch legend-fireable" /> transition franchissable</span>
      </div>
    </div>
  )
}