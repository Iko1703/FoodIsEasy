import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../api'
import DishImage from '../components/DishImage'

type Rec = {
  delishiesId: number
  title: string
  description: string
  imageUrl?: string
  score: number
  reasons: string[]
}

type GroupMember = { group: { id: number; name: string } }

export default function Recommendations() {
  const [recs, setRecs] = useState<Rec[]>([])
  const [groups, setGroups] = useState<GroupMember[]>([])
  const [groupId, setGroupId] = useState('')
  const [loading, setLoading] = useState(true)

  const loadPersonal = () => {
    setLoading(true)
    api.get<Rec[]>('/me/recommendations?limit=50')
      .then(r => setRecs(r.data))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    loadPersonal()
    api.get<GroupMember[]>('/me/groups').then(r => setGroups(r.data ?? []))
  }, [])

  const loadGroup = () => {
    if (!groupId) return loadPersonal()
    setLoading(true)
    api.get<Rec[]>(`/groups/${groupId}/recommendations?limit=50`)
      .then(r => setRecs(r.data))
      .finally(() => setLoading(false))
  }

  if (loading) return <p className="loading">Подбираем блюда...</p>

  return (
    <section className="planner-page">
      <h1 className="page-title">Рекомендации для вас</h1>
      <p className="hint">Подбор с учётом предпочтений, истории питания и ограничений</p>

      {groups.length > 0 && (
        <div className="add-product-row" style={{ marginBottom: '1.5rem' }}>
          <select value={groupId} onChange={e => setGroupId(e.target.value)}>
            <option value="">Личные рекомендации</option>
            {groups.map(g => (
              <option key={g.group.id} value={g.group.id}>{g.group.name}</option>
            ))}
          </select>
          <button type="button" className="btn-secondary" onClick={loadGroup}>Показать</button>
        </div>
      )}

      <div className="grid">
        {recs.map(r => (
          <Link key={r.delishiesId} to={`/delishies/${r.delishiesId}`} className="card rec-card">
            {r.imageUrl ? (
              <DishImage src={r.imageUrl} alt={r.title} className="card-image" />
            ) : (
              <div className="card-image placeholder-img">🍽️</div>
            )}
            <div className="card-content">
              <span className="score-badge">{r.score}</span>
              <h3 className="card-title">{r.title}</h3>
              <p className="card-description">{r.description}</p>
              {r.reasons?.length > 0 && (
                <ul className="rec-reasons">
                  {r.reasons.slice(0, 2).map((reason, i) => (
                    <li key={i}>{reason}</li>
                  ))}
                </ul>
              )}
            </div>
          </Link>
        ))}
      </div>
      {recs.length === 0 && <p className="hint">Нет подходящих блюд. Заполните профиль и предпочтения.</p>}
    </section>
  )
}
