import { useEffect, useState } from 'react'
import api from '../api'

type Group = { id: number; name: string }
type GroupMember = { id: number; role: string; group: Group }
type User = { id: number; email: string; firstName?: string; lastName?: string }

export default function Profile() {
  const [me, setMe] = useState<User | null>(null)
  const [groups, setGroups] = useState<GroupMember[]>([])

  useEffect(() => {
    api.get('/me').then(res => setMe(res.data))
    api.get('/me/groups').then(res => setGroups(res.data ?? []))
  }, [])

  if (!me) return <div>Загрузка...</div>

  return (
    <div>
      <h1 className="page-title">Профиль</h1>
      <div className="profile-card">
        <div className="profile-info">
          <h3>{me.firstName} {me.lastName}</h3>
          <p>Email: {me.email}</p>
        </div>
      </div>
      
      <h2 className="section-title">Мои группы</h2>
      <ul className="groups-list">
        {groups.map(gm => (
          <li key={gm.id}>{gm.group?.name} — {gm.role}</li>
        ))}
        {groups.length === 0 && (
          <li>Вы пока не состоите ни в одной группе</li>
        )}
      </ul>
    </div>
  )
}


