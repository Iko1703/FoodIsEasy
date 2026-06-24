import { useCallback, useEffect, useState } from 'react'
import api from '../api'

type GroupSummary = { id: number; name: string; role: string }
type Profile = {
  id: number
  email: string
  firstName?: string
  lastName?: string
  age?: number
  gender?: 'MALE' | 'FEMALE'
  groups: GroupSummary[]
}
type Cuisine = { id: number; name: string }
type Product = { id: number; name: string }
type CuisinePref = { cuisineId: number; cuisineName: string; weight: number }
type ProductPref = { productId: number; productName: string; prefType: PrefType }
type PrefType = 'FAVORITE' | 'DISLIKED' | 'ALLERGY' | 'SOFT_DISLIKE'

const PREF_LABELS: Record<PrefType, string> = {
  FAVORITE: 'Любимые',
  DISLIKED: 'Нежелательные',
  ALLERGY: 'Аллергии',
  SOFT_DISLIKE: 'Не люблю, но допустимо',
}

const PREF_TYPES: PrefType[] = ['FAVORITE', 'DISLIKED', 'ALLERGY', 'SOFT_DISLIKE']

export default function Profile() {
  const [profile, setProfile] = useState<Profile | null>(null)
  const [cuisines, setCuisines] = useState<Cuisine[]>([])
  const [products, setProducts] = useState<Product[]>([])
  const [cuisinePrefs, setCuisinePrefs] = useState<CuisinePref[]>([])
  const [productPrefs, setProductPrefs] = useState<ProductPref[]>([])

  const [firstName, setFirstName] = useState('')
  const [lastName, setLastName] = useState('')
  const [age, setAge] = useState('')
  const [gender, setGender] = useState<'MALE' | 'FEMALE' | ''>('')

  const [selectedProductId, setSelectedProductId] = useState('')
  const [selectedPrefType, setSelectedPrefType] = useState<PrefType>('FAVORITE')

  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [message, setMessage] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)

  const load = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const [profileRes, prefRes, cuisinesRes, productsRes] = await Promise.all([
        api.get<Profile>('/me/profile'),
        api.get<{ cuisines: CuisinePref[]; products: ProductPref[] }>('/me/preferences'),
        api.get<Cuisine[]>('/cuisines'),
        api.get<Product[]>('/products'),
      ])
      const p = profileRes.data
      setProfile(p)
      setFirstName(p.firstName ?? '')
      setLastName(p.lastName ?? '')
      setAge(p.age != null ? String(p.age) : '')
      setGender(p.gender ?? '')
      setCuisinePrefs(prefRes.data.cuisines ?? [])
      setProductPrefs(prefRes.data.products ?? [])
      setCuisines(cuisinesRes.data)
      setProducts(productsRes.data)
    } catch {
      setError('Не удалось загрузить профиль')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    load()
  }, [load])

  const saveProfile = async () => {
    setSaving(true)
    setMessage(null)
    setError(null)
    try {
      const res = await api.put<Profile>('/me/profile', {
        firstName: firstName || null,
        lastName: lastName || null,
        age: age ? parseInt(age, 10) : null,
        gender: gender || null,
      })
      setProfile(res.data)
      setMessage('Профиль сохранён')
    } catch {
      setError('Ошибка сохранения профиля')
    } finally {
      setSaving(false)
    }
  }

  const savePreferences = async () => {
    setSaving(true)
    setMessage(null)
    setError(null)
    try {
      const res = await api.put<{ cuisines: CuisinePref[]; products: ProductPref[] }>('/me/preferences', {
        cuisines: cuisinePrefs.map(c => ({ cuisineId: c.cuisineId, weight: c.weight })),
        products: productPrefs.map(p => ({ productId: p.productId, prefType: p.prefType })),
      })
      setCuisinePrefs(res.data.cuisines)
      setProductPrefs(res.data.products)
      setMessage('Предпочтения сохранены')
    } catch (err: unknown) {
      const status = (err as { response?: { status?: number } })?.response?.status
      if (status === 401) setError('Войдите в аккаунт')
      else if (status === 400) setError('Проверьте выбранные кухни и продукты')
      else setError('Ошибка сохранения предпочтений')
    } finally {
      setSaving(false)
    }
  }

  const toggleCuisine = (cuisine: Cuisine) => {
    const exists = cuisinePrefs.find(c => c.cuisineId === cuisine.id)
    if (exists) {
      setCuisinePrefs(prev => prev.filter(c => c.cuisineId !== cuisine.id))
    } else {
      setCuisinePrefs(prev => [
        ...prev,
        { cuisineId: cuisine.id, cuisineName: cuisine.name, weight: 3 },
      ])
    }
  }

  const setCuisineWeight = (cuisineId: number, weight: number) => {
    setCuisinePrefs(prev =>
      prev.map(c => (c.cuisineId === cuisineId ? { ...c, weight } : c))
    )
  }

  const addProductPref = () => {
    const product = products.find(p => String(p.id) === selectedProductId)
    if (!product) return
    if (productPrefs.some(p => p.productId === product.id && p.prefType === selectedPrefType)) return
    setProductPrefs(prev => [
      ...prev,
      { productId: product.id, productName: product.name, prefType: selectedPrefType },
    ])
    setSelectedProductId('')
  }

  const removeProductPref = (productId: number, prefType: PrefType) => {
    setProductPrefs(prev =>
      prev.filter(p => !(p.productId === productId && p.prefType === prefType))
    )
  }

  if (loading) return <div className="loading">Загрузка профиля...</div>
  if (!profile) return <div className="error">{error ?? 'Профиль недоступен'}</div>

  return (
    <div className="profile-page">
      <h1 className="page-title">Мой профиль</h1>

      {message && <div className="toast success">{message}</div>}
      {error && <div className="toast error">{error}</div>}

      <section className="profile-card">
        <h2 className="section-title">Основные данные</h2>
        <p className="profile-email">{profile.email}</p>
        <div className="form-grid">
          <label>
            Имя
            <input value={firstName} onChange={e => setFirstName(e.target.value)} placeholder="Имя" />
          </label>
          <label>
            Фамилия
            <input value={lastName} onChange={e => setLastName(e.target.value)} placeholder="Фамилия" />
          </label>
          <label>
            Возраст
            <input type="number" min={1} max={120} value={age} onChange={e => setAge(e.target.value)} placeholder="Возраст" />
          </label>
          <label>
            Пол
            <select value={gender} onChange={e => setGender(e.target.value as 'MALE' | 'FEMALE' | '')}>
              <option value="">Не указан</option>
              <option value="MALE">Мужской</option>
              <option value="FEMALE">Женский</option>
            </select>
          </label>
        </div>
        <button className="btn-primary" onClick={saveProfile} disabled={saving}>
          Сохранить профиль
        </button>
      </section>

      <section className="profile-card">
        <h2 className="section-title">Любимые кухни</h2>
        <p className="hint">Выберите кухни и укажите, насколько они вам нравятся (1–5)</p>
        <div className="chip-grid">
          {cuisines.map(c => {
            const pref = cuisinePrefs.find(p => p.cuisineId === c.id)
            const active = !!pref
            return (
              <div key={c.id} className={`chip ${active ? 'chip-active' : ''}`}>
                <button type="button" className="chip-btn" onClick={() => toggleCuisine(c)}>
                  {c.name}
                </button>
                {active && pref && (
                  <input
                    type="range"
                    min={1}
                    max={5}
                    value={pref.weight}
                    onChange={e => setCuisineWeight(c.id, parseInt(e.target.value, 10))}
                    title={`Важность: ${pref.weight}`}
                  />
                )}
              </div>
            )
          })}
        </div>
      </section>

      <section className="profile-card">
        <h2 className="section-title">Продукты и ограничения</h2>
        <p className="hint">
          Жёсткие ограничения (аллергии) полностью исключают блюда. Мягкие — только понижают рейтинг.
        </p>
        <div className="add-product-row">
          <select value={selectedProductId} onChange={e => setSelectedProductId(e.target.value)}>
            <option value="">Выберите продукт</option>
            {products.map(p => (
              <option key={p.id} value={p.id}>{p.name}</option>
            ))}
          </select>
          <select value={selectedPrefType} onChange={e => setSelectedPrefType(e.target.value as PrefType)}>
            {PREF_TYPES.map(t => (
              <option key={t} value={t}>{PREF_LABELS[t]}</option>
            ))}
          </select>
          <button type="button" className="btn-secondary" onClick={addProductPref}>Добавить</button>
        </div>

        {PREF_TYPES.map(type => {
          const items = productPrefs.filter(p => p.prefType === type)
          if (items.length === 0) return null
          return (
            <div key={type} className="pref-group">
              <h3>{PREF_LABELS[type]}</h3>
              <ul className="pref-list">
                {items.map(p => (
                  <li key={`${p.productId}-${p.prefType}`}>
                    <span>{p.productName}</span>
                    <button type="button" className="btn-remove" onClick={() => removeProductPref(p.productId, p.prefType)}>×</button>
                  </li>
                ))}
              </ul>
            </div>
          )
        })}
      </section>

      <section className="profile-card">
        <button className="btn-primary btn-large" onClick={savePreferences} disabled={saving}>
          Сохранить предпочтения
        </button>
      </section>

      {profile.groups.length > 0 && (
        <section className="profile-card">
          <h2 className="section-title">Мои группы</h2>
          <ul className="groups-list">
            {profile.groups.map(g => (
              <li key={g.id}>
                <strong>{g.name}</strong>
                <span className="role-badge">{g.role === 'ADMIN' ? 'Админ' : 'Участник'}</span>
              </li>
            ))}
          </ul>
        </section>
      )}
    </div>
  )
}
