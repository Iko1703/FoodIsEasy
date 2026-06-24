import { useCallback, useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import api from '../api'
import { useAuth } from '../store'
import DishImage from '../components/DishImage'
import { formatRecipe } from '../utils/formatRecipe'

type Product = { id: number; name: string; fatPer100g: number; proteinPer100g: number; carbPer100g: number; kcalPer100g: number }
type DelishiesProduct = { id: number; quantityGrams: number; product: Product }
type Author = { firstName?: string; lastName?: string }
type Feedback = { id: number; message: string; rating?: number; author?: Author; createdAt?: string }
type Delishies = {
  id: number
  title: string
  description: string
  recipe?: string
  imageUrl?: string
  cookTimeMinutes?: number
  kcalTotal?: number
  proteinTotal?: number
  fatTotal?: number
  carbTotal?: number
  avgRating?: number
  products: DelishiesProduct[]
  feedbacks: Feedback[]
}

type MealType = 'BREAKFAST' | 'LUNCH' | 'DINNER' | 'SNACK'

const MEAL_LABELS: Record<MealType, string> = {
  BREAKFAST: 'Завтрак',
  LUNCH: 'Обед',
  DINNER: 'Ужин',
  SNACK: 'Перекус',
}

export default function DelishiesDetails() {
  const { id } = useParams()
  const { token } = useAuth()
  const [item, setItem] = useState<Delishies | null>(null)
  const [isFavorite, setIsFavorite] = useState(false)
  const [mealType, setMealType] = useState<MealType>('LUNCH')
  const [logMsg, setLogMsg] = useState<string | null>(null)
  const [feedbackMsg, setFeedbackMsg] = useState('')
  const [feedbackRating, setFeedbackRating] = useState(5)
  const [feedbackStatus, setFeedbackStatus] = useState<string | null>(null)

  const loadDish = useCallback(async () => {
    const res = await api.get<Delishies>(`/delishies/${id}`)
    setItem(res.data)
  }, [id])

  const loadFavorite = useCallback(async () => {
    if (!token || !id) {
      setIsFavorite(false)
      return
    }
    try {
      const res = await api.get<number[]>('/me/favorites/ids')
      setIsFavorite((res.data ?? []).includes(Number(id)))
    } catch {
      setIsFavorite(false)
    }
  }, [token, id])

  useEffect(() => {
    loadDish()
    loadFavorite()
  }, [loadDish, loadFavorite])

  const toggleFavorite = async () => {
    if (!item || !token) return
    try {
      if (isFavorite) {
        await api.delete(`/me/favorites/${item.id}`)
        setIsFavorite(false)
      } else {
        await api.post('/me/favorites', { delishiesId: item.id })
        setIsFavorite(true)
      }
    } catch {
      /* ignore */
    }
  }

  const logMeal = async () => {
    if (!item) return
    try {
      await api.post('/me/meal-history', { delishiesId: item.id, mealType })
      setLogMsg('Добавлено в историю питания')
    } catch {
      setLogMsg('Войдите в аккаунт, чтобы записать приём пищи')
    }
  }

  const submitFeedback = async () => {
    if (!item || !feedbackMsg.trim()) return
    setFeedbackStatus(null)
    try {
      await api.post(`/me/delishies/${item.id}/feedbacks`, {
        message: feedbackMsg.trim(),
        rating: feedbackRating,
      })
      setFeedbackMsg('')
      setFeedbackRating(5)
      setFeedbackStatus('Отзыв добавлен')
      await loadDish()
    } catch {
      setFeedbackStatus('Не удалось отправить отзыв')
    }
  }

  if (!item) return <div className="loading">Загрузка...</div>

  return (
    <div className="details-container">
      {item.imageUrl ? (
        <DishImage src={item.imageUrl} alt={item.title} className="details-image" />
      ) : (
        <DishImage src={`/images/dishes/${(Number(id) % 12) || 12}.jpg`} alt={item.title} className="details-image" />
      )}

      <div className="details-header-row">
        <h1 className="details-title">{item.title}</h1>
        {token && (
          <button
            type="button"
            className={`fav-btn fav-btn-lg ${isFavorite ? 'fav-active' : ''}`}
            onClick={toggleFavorite}
          >
            {isFavorite ? '♥ В избранном' : '♡ В избранное'}
          </button>
        )}
      </div>

      <p className="details-description">{item.description}</p>

      {(item.kcalTotal || item.cookTimeMinutes || item.avgRating != null) && (
        <p className="nutrition-brief">
          {item.cookTimeMinutes && `${item.cookTimeMinutes} мин · `}
          {item.kcalTotal && `${item.kcalTotal} ккал`}
          {item.proteinTotal != null && ` · Б ${item.proteinTotal}г Ж ${item.fatTotal}г У ${item.carbTotal}г`}
          {item.avgRating != null && ` · ★ ${item.avgRating.toFixed(1)}`}
        </p>
      )}

      {item.recipe && (
        <>
          <h2 className="section-title">Рецепт</h2>
          <pre className="recipe-text">{formatRecipe(item.recipe)}</pre>
        </>
      )}

      {token && (
        <div className="log-meal-box">
          <h2 className="section-title">Записать в историю</h2>
          <div className="add-product-row">
            <select value={mealType} onChange={e => setMealType(e.target.value as MealType)}>
              {(Object.keys(MEAL_LABELS) as MealType[]).map(m => (
                <option key={m} value={m}>{MEAL_LABELS[m]}</option>
              ))}
            </select>
            <button type="button" className="btn-primary" onClick={logMeal}>
              Я ел это
            </button>
          </div>
          {logMsg && <p className="hint">{logMsg}</p>}
        </div>
      )}

      <h2 className="section-title">Ингредиенты</h2>
      <table>
        <thead>
          <tr>
            <th>Продукт</th>
            <th>Кол-во (г)</th>
            <th>Ккал/100г</th>
            <th>Белки</th>
            <th>Жиры</th>
            <th>Углеводы</th>
          </tr>
        </thead>
        <tbody>
          {item.products?.map(dp => (
            <tr key={dp.id}>
              <td>{dp.product?.name}</td>
              <td>{dp.quantityGrams}</td>
              <td>{dp.product?.kcalPer100g}</td>
              <td>{dp.product?.proteinPer100g}g</td>
              <td>{dp.product?.fatPer100g}g</td>
              <td>{dp.product?.carbPer100g}g</td>
            </tr>
          ))}
        </tbody>
      </table>

      <h2 className="section-title">Отзывы</h2>
      {token && (
        <div className="feedback-form profile-card">
          <label>
            Ваш отзыв
            <textarea
              value={feedbackMsg}
              onChange={e => setFeedbackMsg(e.target.value)}
              rows={3}
              placeholder="Что понравилось или не понравилось?"
            />
          </label>
          <label>
            Оценка
            <select value={feedbackRating} onChange={e => setFeedbackRating(Number(e.target.value))}>
              {[5, 4, 3, 2, 1].map(n => (
                <option key={n} value={n}>{'★'.repeat(n)} ({n})</option>
              ))}
            </select>
          </label>
          <button type="button" className="btn-primary" onClick={submitFeedback}>
            Оставить отзыв
          </button>
          {feedbackStatus && <p className="hint">{feedbackStatus}</p>}
        </div>
      )}
      <ul className="feedback-list">
        {item.feedbacks?.length ? item.feedbacks.map(f => (
          <li key={f.id} className="feedback-item">
            <div className="feedback-head">
              <span className="feedback-author">
                {f.author?.firstName ?? 'Пользователь'} {f.author?.lastName ?? ''}
              </span>
              {f.rating != null && <span className="feedback-rating">{'★'.repeat(f.rating)}</span>}
            </div>
            <p>{f.message}</p>
            {f.createdAt && (
              <span className="feedback-date">
                {new Date(f.createdAt).toLocaleDateString('ru-RU')}
              </span>
            )}
          </li>
        )) : (
          <li className="hint">Пока нет отзывов</li>
        )}
      </ul>
    </div>
  )
}
