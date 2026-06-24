import { useCallback, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../api'

type MealType = 'BREAKFAST' | 'LUNCH' | 'DINNER' | 'SNACK'
type MealRole = 'BREAKFAST' | 'SOUP' | 'MAIN' | 'SALAD' | 'SIDE' | 'DESSERT' | 'SNACK'
type Delishies = { id: number; title: string; imageUrl?: string; mealRole?: MealRole }
type MealPlanSummary = {
  id: number
  name: string
  startDate: string
  endDate: string
  scope: string
  groupName?: string
  entriesCount: number
}
type MealPlanEntry = {
  id: number
  planDate: string
  mealType: MealType
  delishiesId: number
  delishiesTitle: string
  imageUrl?: string
  mealRole?: MealRole
}

const MEAL_ROLE_LABELS: Record<MealRole, string> = {
  BREAKFAST: 'Завтрак',
  SOUP: 'Суп',
  MAIN: 'Основное',
  SALAD: 'Салат',
  SIDE: 'Гарнир',
  DESSERT: 'Десерт',
  SNACK: 'Перекус',
}
type MealPlanDetail = MealPlanSummary & { entries: MealPlanEntry[] }
type MealHistoryItem = {
  id: number
  delishiesId: number
  delishiesTitle: string
  mealType: MealType
  eatenAt: string
}
type ShoppingItem = {
  id: number
  productId: number
  productName: string
  quantityGrams: number
  checked: boolean
}
type ShoppingList = {
  id: number
  status: string
  orderNote?: string
  items: ShoppingItem[]
}
type GroupMember = { group: { id: number; name: string } }

const MEAL_LABELS: Record<MealType, string> = {
  BREAKFAST: 'Завтрак',
  LUNCH: 'Обед',
  DINNER: 'Ужин',
  SNACK: 'Перекус',
}

const MEAL_SLOTS: MealType[] = ['BREAKFAST', 'LUNCH', 'DINNER']

function weekStart(date = new Date()): string {
  const d = new Date(date)
  const day = d.getDay()
  const diff = d.getDate() - day + (day === 0 ? -6 : 1)
  d.setDate(diff)
  return d.toISOString().slice(0, 10)
}

function addDays(iso: string, days: number): string {
  const d = new Date(iso)
  d.setDate(d.getDate() + days)
  return d.toISOString().slice(0, 10)
}

function datesBetween(start: string, end: string): string[] {
  const out: string[] = []
  let cur = start
  while (cur <= end) {
    out.push(cur)
    cur = addDays(cur, 1)
  }
  return out
}

function formatDate(iso: string) {
  return new Date(iso + 'T12:00:00').toLocaleDateString('ru-RU', { weekday: 'short', day: 'numeric', month: 'short' })
}

export default function MealPlanner() {
  const [tab, setTab] = useState<'plan' | 'history'>('plan')
  const [plans, setPlans] = useState<MealPlanSummary[]>([])
  const [selectedPlanId, setSelectedPlanId] = useState<number | null>(null)
  const [planDetail, setPlanDetail] = useState<MealPlanDetail | null>(null)
  const [dishes, setDishes] = useState<Delishies[]>([])
  const [groups, setGroups] = useState<GroupMember[]>([])
  const [history, setHistory] = useState<MealHistoryItem[]>([])

  const [planName, setPlanName] = useState('')
  const [startDate, setStartDate] = useState(weekStart())
  const [endDate, setEndDate] = useState(addDays(weekStart(), 6))
  const [groupId, setGroupId] = useState('')

  const [editSlot, setEditSlot] = useState<{ date: string; mealType: MealType } | null>(null)
  const [pickDishId, setPickDishId] = useState('')

  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [message, setMessage] = useState<string | null>(null)
  const [cart, setCart] = useState<ShoppingList | null>(null)

  const loadPlans = useCallback(async () => {
    const res = await api.get<MealPlanSummary[]>('/me/meal-plans')
    setPlans(res.data)
    return res.data
  }, [])

  const loadCart = useCallback(async (planId: number) => {
    try {
      const res = await api.get<ShoppingList>(`/me/shopping-lists/by-meal-plan/${planId}`)
      setCart(res.data)
    } catch {
      setCart(null)
    }
  }, [])

  const loadPlanDetail = useCallback(async (id: number) => {
    const res = await api.get<MealPlanDetail>(`/me/meal-plans/${id}`)
    setPlanDetail(res.data)
    setSelectedPlanId(id)
    await loadCart(id)
  }, [loadCart])

  const loadHistory = useCallback(async () => {
    const res = await api.get<MealHistoryItem[]>('/me/meal-history')
    setHistory(res.data)
  }, [])

  useEffect(() => {
    Promise.all([
      loadPlans(),
      api.get<Delishies[]>('/delishies').then(r => setDishes(r.data)),
      api.get<GroupMember[]>('/me/groups').then(r => setGroups(r.data ?? [])),
      loadHistory(),
    ])
      .then(([p]) => {
        if (p.length > 0 && !selectedPlanId) loadPlanDetail(p[0].id)
      })
      .finally(() => setLoading(false))
  }, [loadPlans, loadPlanDetail, loadHistory])

  const createPlan = async () => {
    setSaving(true)
    setMessage(null)
    try {
      const res = await api.post<MealPlanDetail>('/me/meal-plans', {
        name: planName || undefined,
        startDate,
        endDate,
        groupId: groupId ? parseInt(groupId, 10) : null,
      })
      await loadPlans()
      setPlanDetail(res.data)
      setSelectedPlanId(res.data.id)
      setMessage('План создан')
    } catch {
      setMessage('Не удалось создать план')
    } finally {
      setSaving(false)
    }
  }

  const buildCart = async () => {
    if (!selectedPlanId) return
    setSaving(true)
    try {
      const res = await api.post<ShoppingList>(`/me/shopping-lists/from-meal-plan/${selectedPlanId}`)
      setCart(res.data)
      setMessage('Список покупок собран из меню (продукты агрегированы по всем блюдам)')
    } catch {
      setMessage('Сначала заполните меню, затем соберите корзину')
    } finally {
      setSaving(false)
    }
  }

  const toggleCartItem = async (itemId: number, checked: boolean) => {
    if (!cart) return
    const res = await api.patch<ShoppingList>(`/me/shopping-lists/${cart.id}/items/${itemId}?checked=${checked}`)
    setCart(res.data)
  }

  const orderCart = async () => {
    if (!cart) return
    const res = await api.post<ShoppingList>(`/me/shopping-lists/${cart.id}/order`, {
      orderNote: 'Заказ через FoodIsEasy (демо)',
    })
    setCart(res.data)
    setMessage('Заявка на заказ отправлена!')
  }

  const generatePlan = async () => {
    if (!selectedPlanId) return
    setSaving(true)
    try {
      const res = await api.post<MealPlanDetail>(`/me/meal-plans/${selectedPlanId}/generate`)
      setPlanDetail(res.data)
      setMessage('Меню сгенерировано по рекомендациям (балл, аллергии, история)')
      await loadPlans()
    } catch {
      setMessage('Ошибка генерации')
    } finally {
      setSaving(false)
    }
  }

  const assignDish = async () => {
    if (!selectedPlanId || !editSlot || !pickDishId) return
    setSaving(true)
    try {
      const res = await api.put<MealPlanDetail>(`/me/meal-plans/${selectedPlanId}/entries`, {
        planDate: editSlot.date,
        mealType: editSlot.mealType,
        delishiesId: parseInt(pickDishId, 10),
      })
      setPlanDetail(res.data)
      setPickDishId('')
      setMessage('Блюдо добавлено в приём пищи')
      await loadPlans()
    } catch {
      setMessage('Не удалось добавить: блюдо уже есть или роль (суп/основное/салат) занята')
    } finally {
      setSaving(false)
    }
  }

  const removeEntry = async (entryId: number) => {
    if (!selectedPlanId) return
    setSaving(true)
    try {
      await api.delete(`/me/meal-plans/${selectedPlanId}/entries/${entryId}`)
      await loadPlanDetail(selectedPlanId)
      await loadPlans()
      setMessage('Блюдо убрано из плана')
    } catch {
      setMessage('Не удалось удалить позицию')
    } finally {
      setSaving(false)
    }
  }

  const deleteHistory = async (id: number) => {
    await api.delete(`/me/meal-history/${id}`)
    await loadHistory()
  }

  const getEntries = (date: string, mealType: MealType) =>
    planDetail?.entries.filter(e => e.planDate === date && e.mealType === mealType) ?? []

  if (loading) return <div className="loading">Загрузка...</div>

  return (
    <div className="planner-page">
      <h1 className="page-title">Питание</h1>

      <div className="tabs">
        <button type="button" className={tab === 'plan' ? 'tab active' : 'tab'} onClick={() => setTab('plan')}>
          Планировщик
        </button>
        <button type="button" className={tab === 'history' ? 'tab active' : 'tab'} onClick={() => setTab('history')}>
          История
        </button>
      </div>

      {message && <div className="toast success">{message}</div>}

      {tab === 'plan' && (
        <>
          <section className="profile-card">
            <h2 className="section-title">Новый план</h2>
            <div className="form-grid">
              <label>
                Название
                <input value={planName} onChange={e => setPlanName(e.target.value)} placeholder="Меню на неделю" />
              </label>
              <label>
                С
                <input type="date" value={startDate} onChange={e => setStartDate(e.target.value)} />
              </label>
              <label>
                По
                <input type="date" value={endDate} onChange={e => setEndDate(e.target.value)} />
              </label>
              <label>
                Группа (необязательно)
                <select value={groupId} onChange={e => setGroupId(e.target.value)}>
                  <option value="">Личный план</option>
                  {groups.map(g => (
                    <option key={g.group.id} value={g.group.id}>{g.group.name}</option>
                  ))}
                </select>
              </label>
            </div>
            <button type="button" className="btn-primary" onClick={createPlan} disabled={saving}>
              Создать план
            </button>
          </section>

          {plans.length > 0 && (
            <section className="profile-card">
              <h2 className="section-title">Мои планы</h2>
              <div className="plan-chips">
                {plans.map(p => (
                  <button
                    key={p.id}
                    type="button"
                    className={`plan-chip ${selectedPlanId === p.id ? 'chip-active' : ''}`}
                    onClick={() => loadPlanDetail(p.id)}
                  >
                    {p.name}
                    {p.groupName && <span className="plan-group"> · {p.groupName}</span>}
                  </button>
                ))}
              </div>
            </section>
          )}

          {planDetail && (
            <section className="profile-card planner-grid-section">
              <div className="planner-header">
                <h2>{planDetail.name}</h2>
                <p className="hint">
                  {planDetail.startDate} — {planDetail.endDate}
                  {planDetail.scope === 'GROUP' && planDetail.groupName && ` · ${planDetail.groupName}`}
                </p>
                <div className="planner-actions">
                  <button type="button" className="btn-secondary" onClick={generatePlan} disabled={saving}>
                    Сгенерировать меню
                  </button>
                  <button type="button" className="btn-secondary" onClick={buildCart} disabled={saving}>
                    Собрать корзину
                  </button>
                </div>

              </div>

              <div className="meal-grid-wrapper">
                <table className="meal-grid">
                  <thead>
                    <tr>
                      <th>День</th>
                      {MEAL_SLOTS.map(m => (
                        <th key={m}>{MEAL_LABELS[m]}</th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {datesBetween(planDetail.startDate, planDetail.endDate).map(date => (
                      <tr key={date}>
                        <td className="day-cell">{formatDate(date)}</td>
                        {MEAL_SLOTS.map(mealType => {
                          const entries = getEntries(date, mealType)
                          return (
                            <td key={mealType} className="meal-cell">
                              <div className="meal-slot-list">
                                {entries.map(entry => (
                                  <div key={entry.id} className="meal-slot-item">
                                    {entry.imageUrl && (
                                      <img src={entry.imageUrl} alt="" className="meal-thumb" />
                                    )}
                                    <div className="meal-slot-text">
                                      {entry.mealRole && (
                                        <span className="meal-role-badge">{MEAL_ROLE_LABELS[entry.mealRole]}</span>
                                      )}
                                      <Link to={`/delishies/${entry.delishiesId}`} className="meal-slot-title">
                                        {entry.delishiesTitle}
                                      </Link>
                                    </div>
                                    <button
                                      type="button"
                                      className="btn-remove meal-remove"
                                      title="Убрать"
                                      onClick={() => removeEntry(entry.id)}
                                    >
                                      ×
                                    </button>
                                  </div>
                                ))}
                                <button
                                  type="button"
                                  className="meal-slot-add"
                                  onClick={() => {
                                    setEditSlot({ date, mealType })
                                    setPickDishId('')
                                  }}
                                >
                                  + Добавить блюдо
                                </button>
                              </div>
                            </td>
                          )
                        })}
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </section>
          )}

          {cart && cart.items.length > 0 && (
            <section className="profile-card cart-section">
              <h2 className="section-title">Общая корзина</h2>
              <p className="hint">Статус: {cart.status === 'ORDERED' ? 'Заказано' : 'Активна'}</p>
              <ul className="cart-list">
                {cart.items.map(item => (
                  <li key={item.id} className={item.checked ? 'checked' : ''}>
                    <label>
                      <input
                        type="checkbox"
                        checked={item.checked}
                        onChange={e => toggleCartItem(item.id, e.target.checked)}
                      />
                      {item.productName} — {item.quantityGrams} г
                    </label>
                  </li>
                ))}
              </ul>
              {cart.status !== 'ORDERED' && (
                <button type="button" className="btn-primary btn-large" onClick={orderCart}>
                  Заказать продукты
                </button>
              )}
              {cart.orderNote && <p className="hint">{cart.orderNote}</p>}
            </section>
          )}

          {editSlot && (
            <div className="modal-overlay" onClick={() => setEditSlot(null)}>
              <div className="modal" onClick={e => e.stopPropagation()}>
                <h3>{formatDate(editSlot.date)} — {MEAL_LABELS[editSlot.mealType]}</h3>
                <p className="hint">Можно добавить несколько блюд на один приём (например, суп и салат)</p>
                <select value={pickDishId} onChange={e => setPickDishId(e.target.value)}>
                  <option value="">Выберите блюдо</option>
                  {dishes
                    .filter(d => {
                      if (!editSlot || !planDetail) return true
                      const inSlot = planDetail.entries.filter(
                        e => e.planDate === editSlot.date && e.mealType === editSlot.mealType
                      )
                      const takenRoles = new Set(inSlot.map(e => e.mealRole).filter(Boolean))
                      const takenIds = new Set(inSlot.map(e => e.delishiesId))
                      if (takenIds.has(d.id)) return false
                      if (d.mealRole && takenRoles.has(d.mealRole)) return false
                      return true
                    })
                    .map(d => (
                      <option key={d.id} value={d.id}>
                        {d.mealRole ? `[${MEAL_ROLE_LABELS[d.mealRole]}] ` : ''}{d.title}
                      </option>
                    ))}
                </select>
                <div className="modal-actions">
                  <button type="button" className="btn-primary" onClick={assignDish} disabled={!pickDishId || saving}>
                    Добавить
                  </button>
                  <button type="button" className="btn-secondary" onClick={() => setEditSlot(null)}>
                    Готово
                  </button>
                </div>
              </div>
            </div>
          )}
        </>
      )}

      {tab === 'history' && (
        <section className="profile-card">
          <h2 className="section-title">Что вы ели</h2>
          <p className="hint">История используется для рекомендаций и избегания повторов</p>
          {history.length === 0 ? (
            <p className="hint">Записей пока нет. Отметьте блюдо на странице рецепта.</p>
          ) : (
            <ul className="history-list">
              {history.map(h => (
                <li key={h.id}>
                  <div>
                    <Link to={`/delishies/${h.delishiesId}`}>{h.delishiesTitle}</Link>
                    <span className="history-meta">
                      {MEAL_LABELS[h.mealType]} · {new Date(h.eatenAt).toLocaleString('ru-RU')}
                    </span>
                  </div>
                  <button type="button" className="btn-remove" onClick={() => deleteHistory(h.id)}>×</button>
                </li>
              ))}
            </ul>
          )}
        </section>
      )}
    </div>
  )
}
