import { useCallback, useEffect, useState } from 'react'
import api from '../api'
import { Link } from 'react-router-dom'
import { useAuth } from '../store'
import DishImage from '../components/DishImage'

const QUICK_LINKS = [
  { to: '/recommendations', label: 'Рекомендации', icon: '✨' },
  { to: '/planner', label: 'Планировщик', icon: '📅' },
  { to: '/analytics', label: 'Аналитика', icon: '📊' },
  { to: '/groups', label: 'Группы', icon: '👥' },
  { to: '/profile', label: 'Профиль', icon: '⚙️' },
]

type ProductRef = { name: string }
type DelishiesProduct = { quantityGrams: number; product: ProductRef }
type Cuisine = { id: number; name: string }
type DishCategory = { id: number; name: string }
type MealRole = 'BREAKFAST' | 'SOUP' | 'MAIN' | 'SALAD' | 'SIDE' | 'DESSERT' | 'SNACK'
type Delishies = {
  id: number
  title: string
  description: string
  imageUrl?: string
  createdAt?: string
  kcalTotal?: number
  cookTimeMinutes?: number
  mealRole?: MealRole
  avgRating?: number
  products?: DelishiesProduct[]
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

const MEAL_ROLES: MealRole[] = ['BREAKFAST', 'SOUP', 'MAIN', 'SALAD', 'SIDE', 'DESSERT', 'SNACK']

export default function DelishiesList() {
  const { token } = useAuth()
  const [items, setItems] = useState<Delishies[]>([])
  const [cuisines, setCuisines] = useState<Cuisine[]>([])
  const [categories, setCategories] = useState<DishCategory[]>([])
  const [favoriteIds, setFavoriteIds] = useState<Set<number>>(new Set())
  const [loading, setLoading] = useState(true)

  const [search, setSearch] = useState('')
  const [cuisineId, setCuisineId] = useState('')
  const [categoryId, setCategoryId] = useState('')
  const [mealRole, setMealRole] = useState('')
  const [maxKcal, setMaxKcal] = useState('')
  const [favoritesOnly, setFavoritesOnly] = useState(false)

  const loadFavorites = useCallback(async () => {
    if (!token) {
      setFavoriteIds(new Set())
      return
    }
    try {
      const res = await api.get<number[]>('/me/favorites/ids')
      setFavoriteIds(new Set(res.data ?? []))
    } catch {
      setFavoriteIds(new Set())
    }
  }, [token])

  const loadDishes = useCallback(async () => {
    setLoading(true)
    try {
      const params: Record<string, string | boolean> = {}
      if (search.trim()) params.q = search.trim()
      if (cuisineId) params.cuisineId = cuisineId
      if (categoryId) params.categoryId = categoryId
      if (mealRole) params.mealRole = mealRole
      if (maxKcal) params.maxKcal = maxKcal
      if (favoritesOnly) params.favoritesOnly = true
      const res = await api.get<Delishies[]>('/delishies', { params })
      setItems(res.data ?? [])
    } catch {
      setItems([])
    } finally {
      setLoading(false)
    }
  }, [search, cuisineId, categoryId, mealRole, maxKcal, favoritesOnly])

  useEffect(() => {
    Promise.all([
      api.get<Cuisine[]>('/cuisines').then(r => setCuisines(r.data ?? [])),
      api.get<DishCategory[]>('/dish-categories').then(r => setCategories(r.data ?? [])),
      loadFavorites(),
    ])
  }, [loadFavorites])

  useEffect(() => {
    loadDishes()
  }, [loadDishes])

  const toggleFavorite = async (e: React.MouseEvent, dishId: number) => {
    e.preventDefault()
    e.stopPropagation()
    if (!token) return
    try {
      if (favoriteIds.has(dishId)) {
        await api.delete(`/me/favorites/${dishId}`)
        setFavoriteIds(prev => {
          const next = new Set(prev)
          next.delete(dishId)
          return next
        })
        if (favoritesOnly) await loadDishes()
      } else {
        await api.post('/me/favorites', { delishiesId: dishId })
        setFavoriteIds(prev => new Set(prev).add(dishId))
      }
    } catch {
      /* ignore */
    }
  }

  const resetFilters = () => {
    setSearch('')
    setCuisineId('')
    setCategoryId('')
    setMealRole('')
    setMaxKcal('')
    setFavoritesOnly(false)
  }

  return (
    <div className="catalog-page">
      <header className="page-hero">
        <h1 className="page-title">Блюда</h1>
        <p className="page-subtitle">
          Каталог с фильтрами, составом из БД и персональными рекомендациями
        </p>
      </header>

      {token && (
        <section className="dashboard-strip">
          {QUICK_LINKS.map(l => (
            <Link key={l.to} to={l.to} className="dashboard-tile">
              <span className="dashboard-icon">{l.icon}</span>
              <span>{l.label}</span>
            </Link>
          ))}
        </section>
      )}

      {!token && (
        <p className="hint catalog-guest-hint">
          <Link to="/login">Войдите</Link>, чтобы сохранять избранное, планировать питание и видеть аналитику.
        </p>
      )}

      <section className="profile-card catalog-filters">
        <div className="form-grid">
          <label>
            Поиск
            <input
              value={search}
              onChange={e => setSearch(e.target.value)}
              placeholder="Название или описание"
            />
          </label>
          <label>
            Кухня
            <select value={cuisineId} onChange={e => setCuisineId(e.target.value)}>
              <option value="">Все</option>
              {cuisines.map(c => (
                <option key={c.id} value={c.id}>{c.name}</option>
              ))}
            </select>
          </label>
          <label>
            Категория
            <select value={categoryId} onChange={e => setCategoryId(e.target.value)}>
              <option value="">Все</option>
              {categories.map(c => (
                <option key={c.id} value={c.id}>{c.name}</option>
              ))}
            </select>
          </label>
          <label>
            Роль в приёме пищи
            <select value={mealRole} onChange={e => setMealRole(e.target.value)}>
              <option value="">Любая</option>
              {MEAL_ROLES.map(r => (
                <option key={r} value={r}>{MEAL_ROLE_LABELS[r]}</option>
              ))}
            </select>
          </label>
          <label>
            Макс. ккал
            <input
              type="number"
              min={0}
              value={maxKcal}
              onChange={e => setMaxKcal(e.target.value)}
              placeholder="например 400"
            />
          </label>
        </div>
        <div className="filter-actions">
          {token && (
            <label className="filter-check">
              <input
                type="checkbox"
                checked={favoritesOnly}
                onChange={e => setFavoritesOnly(e.target.checked)}
              />
              Только избранное
            </label>
          )}
          <button type="button" className="btn-link" onClick={resetFilters}>
            Сбросить фильтры
          </button>
        </div>
        <p className="hint">Найдено: {items.length}</p>
      </section>

      {loading ? (
        <div className="loading">Загрузка...</div>
      ) : items.length === 0 ? (
        <p className="hint">Нет блюд по выбранным фильтрам</p>
      ) : (
        <div className="grid">
          {items.map(i => (
            <div key={i.id} className="card card-with-fav">
              {token && (
                <button
                  type="button"
                  className={`fav-btn ${favoriteIds.has(i.id) ? 'fav-active' : ''}`}
                  title={favoriteIds.has(i.id) ? 'Убрать из избранного' : 'В избранное'}
                  onClick={e => toggleFavorite(e, i.id)}
                >
                  {favoriteIds.has(i.id) ? '♥' : '♡'}
                </button>
              )}
              <Link to={`/delishies/${i.id}`} className="card-link">
                <DishImage src={i.imageUrl} alt={i.title} className="card-image" />
                <div className="card-content">
                  <h3 className="card-title">{i.title}</h3>
                  {i.mealRole && (
                    <span className="meal-role-badge">{MEAL_ROLE_LABELS[i.mealRole]}</span>
                  )}
                  {i.products && i.products.length > 0 ? (
                    <p className="card-composition">
                      {i.products.map(dp => dp.product?.name).filter(Boolean).join(', ')}
                    </p>
                  ) : (
                    <p className="card-description">{i.description}</p>
                  )}
                  <p className="card-meta">
                    {i.cookTimeMinutes && `${i.cookTimeMinutes} мин`}
                    {i.cookTimeMinutes && i.kcalTotal && ' · '}
                    {i.kcalTotal && `${i.kcalTotal} ккал`}
                    {i.avgRating != null && ` · ★ ${i.avgRating.toFixed(1)}`}
                  </p>
                </div>
              </Link>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
