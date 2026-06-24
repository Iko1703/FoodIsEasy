import { useCallback, useEffect, useState } from 'react'
import api from '../api'

type GroupMember = { id: number; group: { id: number; name: string }; role: string }
type GroupBrowse = { id: number; name: string; role: string | null }
type GroupDetail = {
  id: number
  name: string
  ownerName: string
  myRole: string
  members: { userId: number; firstName: string; lastName: string; email: string; role: string }[]
}
type Delishies = { id: number; title: string; mealRole?: string }
type VoteOption = {
  optionId: number
  delishiesId: number
  delishiesTitle: string
  voteCount: number
}
type Vote = {
  id: number
  title: string
  status: string
  options: VoteOption[]
  userVoted: boolean
  winnerDelishiesId?: number | null
  winnerDelishiesTitle?: string | null
}

export default function Groups() {
  const [memberships, setMemberships] = useState<GroupMember[]>([])
  const [browse, setBrowse] = useState<GroupBrowse[]>([])
  const [groupDetail, setGroupDetail] = useState<GroupDetail | null>(null)
  const [groupId, setGroupId] = useState('')
  const [votes, setVotes] = useState<Vote[]>([])
  const [dishes, setDishes] = useState<Delishies[]>([])
  const [voteTitle, setVoteTitle] = useState('Что готовим на ужин?')
  const [selectedDishes, setSelectedDishes] = useState<number[]>([])
  const [newGroupName, setNewGroupName] = useState('')
  const [joinGroupId, setJoinGroupId] = useState('')
  const [message, setMessage] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)

  const loadMemberships = useCallback(async () => {
    const res = await api.get<GroupMember[]>('/me/groups')
    const list = res.data ?? []
    setMemberships(list)
    if (list.length > 0 && !groupId) {
      setGroupId(String(list[0].group.id))
    }
    return list
  }, [groupId])

  const loadBrowse = useCallback(async () => {
    const res = await api.get<GroupBrowse[]>('/me/groups/browse')
    setBrowse(res.data ?? [])
  }, [])

  const loadGroupDetail = useCallback(async (id: string) => {
    if (!id) return
    const res = await api.get<GroupDetail>(`/me/groups/${id}`)
    setGroupDetail(res.data)
  }, [])

  const loadVotes = useCallback(async (id: string) => {
    if (!id) return
    const res = await api.get<Vote[]>(`/groups/${id}/votes`)
    setVotes(res.data ?? [])
  }, [])

  useEffect(() => {
    Promise.all([
      loadMemberships(),
      loadBrowse(),
      api.get<Delishies[]>('/delishies').then(r => setDishes(r.data)),
    ]).finally(() => setLoading(false))
  }, [loadMemberships, loadBrowse])

  useEffect(() => {
    if (!groupId) return
    loadGroupDetail(groupId)
    loadVotes(groupId)
  }, [groupId, loadGroupDetail, loadVotes])

  const createGroup = async () => {
    if (!newGroupName.trim()) return
    try {
      const res = await api.post<GroupDetail>('/me/groups', { name: newGroupName.trim() })
      setMessage(`Группа «${res.data.name}» создана (ID: ${res.data.id})`)
      setNewGroupName('')
      await loadMemberships()
      await loadBrowse()
      setGroupId(String(res.data.id))
    } catch {
      setMessage('Не удалось создать группу (возможно, имя занято)')
    }
  }

  const joinGroup = async () => {
    const id = parseInt(joinGroupId, 10)
    if (!id) return
    try {
      const res = await api.post<GroupDetail>('/me/groups/join', { groupId: id })
      setMessage(`Вы вступили в «${res.data.name}»`)
      setJoinGroupId('')
      await loadMemberships()
      await loadBrowse()
      setGroupId(String(res.data.id))
    } catch {
      setMessage('Не удалось вступить (уже в группе или неверный ID)')
    }
  }

  const toggleDish = (id: number) => {
    setSelectedDishes(prev =>
      prev.includes(id) ? prev.filter(x => x !== id) : prev.length < 5 ? [...prev, id] : prev
    )
  }

  const createVote = async () => {
    if (selectedDishes.length < 2 || !groupId) {
      setMessage('Выберите минимум 2 блюда для голосования')
      return
    }
    try {
      await api.post(`/groups/${groupId}/votes`, { title: voteTitle, delishiesIds: selectedDishes })
      setSelectedDishes([])
      setMessage('Голосование создано')
      await loadVotes(groupId)
    } catch {
      setMessage('Не удалось создать голосование. Проверьте, что вы в группе и выбраны разные блюда.')
    }
  }

  const castVote = async (voteId: number, optionId: number) => {
    await api.post(`/groups/${groupId}/votes/${voteId}/ballot`, { optionId })
    await loadVotes(groupId)
    setMessage('Голос учтён')
  }

  const closeVote = async (voteId: number) => {
    try {
      const res = await api.post<Vote>(`/groups/${groupId}/votes/${voteId}/close`)
      await loadVotes(groupId)
      setMessage(res.data.winnerDelishiesTitle
        ? `Голосование закрыто. Победитель: ${res.data.winnerDelishiesTitle}`
        : 'Голосование закрыто')
    } catch {
      setMessage('Закрыть может создатель или администратор группы')
    }
  }

  const myRole = memberships.find(m => String(m.group.id) === groupId)?.role
  const openVotes = votes.filter(v => v.status === 'OPEN')
  const closedVotes = votes.filter(v => v.status === 'CLOSED')

  if (loading) return <div className="loading">Загрузка...</div>

  return (
    <section className="planner-page">
      <h1 className="page-title">Группы и голосования</h1>
      {message && <div className="toast success">{message}</div>}

      <section className="profile-card">
        <h2 className="section-title">Создать группу</h2>
        <div className="form-grid">
          <label>
            Название
            <input
              value={newGroupName}
              onChange={e => setNewGroupName(e.target.value)}
              placeholder="Семья, офис, друзья..."
            />
          </label>
        </div>
        <button type="button" className="btn-primary" onClick={createGroup}>
          Создать
        </button>
      </section>

      <section className="profile-card">
        <h2 className="section-title">Вступить в группу</h2>
        <p className="hint">Укажите ID группы из списка ниже</p>
        <div className="form-grid">
          <label>
            ID группы
            <input
              value={joinGroupId}
              onChange={e => setJoinGroupId(e.target.value)}
              placeholder="Например, 1"
            />
          </label>
        </div>
        <button type="button" className="btn-secondary" onClick={joinGroup}>
          Вступить
        </button>
        {browse.length > 0 && (
          <ul className="browse-list">
            {browse.map(g => (
              <li key={g.id}>
                <span className="browse-name">#{g.id} — {g.name}</span>
                {g.role ? (
                  <span className="browse-badge">вы в группе ({g.role})</span>
                ) : (
                  <button
                    type="button"
                    className="btn-link"
                    onClick={() => setJoinGroupId(String(g.id))}
                  >
                    выбрать
                  </button>
                )}
              </li>
            ))}
          </ul>
        )}
      </section>

      {memberships.length === 0 ? (
        <p className="hint">Вы пока не в группах — создайте или вступите по ID</p>
      ) : (
        <>
          <label className="group-select">
            Моя группа:
            <select value={groupId} onChange={e => setGroupId(e.target.value)}>
              {memberships.map(m => (
                <option key={m.id} value={m.group.id}>{m.group.name}</option>
              ))}
            </select>
          </label>

          {groupDetail && (
            <section className="profile-card">
              <h2 className="section-title">Участники — {groupDetail.name}</h2>
              <p className="hint">Владелец: {groupDetail.ownerName} · ваша роль: {groupDetail.myRole}</p>
              <ul className="member-list">
                {groupDetail.members.map(m => (
                  <li key={m.userId}>
                    {m.firstName || m.email} {m.lastName ?? ''}
                    <span className="member-role">{m.role}</span>
                  </li>
                ))}
              </ul>
            </section>
          )}

          <section className="profile-card">
            <h2 className="section-title">Новое голосование</h2>
            <input value={voteTitle} onChange={e => setVoteTitle(e.target.value)} placeholder="Тема голосования" />
            <p className="hint">Выберите 2–5 блюд-кандидатов</p>
            <div className="chip-grid chip-grid-scroll">
              {dishes.map(d => (
                <button
                  key={d.id}
                  type="button"
                  className={`chip-btn ${selectedDishes.includes(d.id) ? 'chip-active' : ''}`}
                  onClick={() => toggleDish(d.id)}
                >
                  {d.title}
                </button>
              ))}
            </div>
            <button type="button" className="btn-primary" style={{ marginTop: '1rem' }} onClick={createVote}>
              Создать голосование
            </button>
          </section>

          <section className="profile-card">
            <h2 className="section-title">Активные голосования</h2>
            {openVotes.length === 0 && <p className="hint">Нет открытых голосований</p>}
            {openVotes.map(v => (
              <article key={v.id} className="vote-block">
                <h3>{v.title}</h3>
                <ul className="vote-options">
                  {v.options.map(o => (
                    <li key={o.optionId}>
                      <span>{o.delishiesTitle}</span>
                      <span className="vote-count">{o.voteCount} голосов</span>
                      {!v.userVoted && (
                        <button type="button" className="btn-secondary" onClick={() => castVote(v.id, o.optionId)}>
                          Голосовать
                        </button>
                      )}
                    </li>
                  ))}
                </ul>
                {myRole === 'ADMIN' && (
                  <button type="button" className="btn-secondary" onClick={() => closeVote(v.id)}>
                    Закрыть голосование
                  </button>
                )}
              </article>
            ))}
          </section>

          {closedVotes.length > 0 && (
            <section className="profile-card">
              <h2 className="section-title">Завершённые голосования</h2>
              {closedVotes.map(v => (
                <article key={v.id} className="vote-block vote-closed">
                  <h3>{v.title}</h3>
                  {v.winnerDelishiesTitle && (
                    <p className="vote-winner">Победитель: <strong>{v.winnerDelishiesTitle}</strong></p>
                  )}
                  <ul className="vote-options">
                    {v.options.map(o => (
                      <li
                        key={o.optionId}
                        className={v.winnerDelishiesId === o.delishiesId ? 'vote-option-winner' : ''}
                      >
                        <span>{o.delishiesTitle}</span>
                        <span className="vote-count">{o.voteCount} голосов</span>
                      </li>
                    ))}
                  </ul>
                </article>
              ))}
            </section>
          )}
        </>
      )}
    </section>
  )
}
