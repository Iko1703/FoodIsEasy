import { useEffect, useMemo, useState } from 'react'
import api from '../api'

type DailyStat = {
  date: string
  meals: number
  kcal: number
  protein: number
  fat: number
  carbs: number
}

type MealTypeStat = { mealType: string; count: number }

type Analytics = {
  periodDays: number
  totalMeals: number
  uniqueDishes: number
  diversityIndex: number
  avgKcalPerMeal: number
  avgProteinPerMeal: number
  avgFatPerMeal: number
  avgCarbPerMeal: number
  topRepeated: { title: string; count: number }[]
  insights: string[]
  dailyStats: DailyStat[]
  mealTypeStats: MealTypeStat[]
}

const MEAL_TYPE_LABELS: Record<string, string> = {
  BREAKFAST: 'Завтрак',
  LUNCH: 'Обед',
  DINNER: 'Ужин',
  SNACK: 'Перекус',
}

function formatDay(iso: string): string {
  const d = new Date(iso)
  return d.toLocaleDateString('ru-RU', { day: 'numeric', month: 'short' })
}

export default function Analytics() {
  const [data, setData] = useState<Analytics | null>(null)
  const [days, setDays] = useState(30)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    setLoading(true)
    api.get<Analytics>(`/me/analytics?days=${days}`)
      .then(r => setData(r.data))
      .finally(() => setLoading(false))
  }, [days])

  const maxKcal = useMemo(() => {
    if (!data?.dailyStats?.length) return 1
    return Math.max(1, ...data.dailyStats.map(d => d.kcal))
  }, [data])

  const maxMealType = useMemo(() => {
    if (!data?.mealTypeStats?.length) return 1
    return Math.max(1, ...data.mealTypeStats.map(m => m.count))
  }, [data])

  const macroTotal = useMemo(() => {
    if (!data) return 1
    return Math.max(1, data.avgProteinPerMeal + data.avgFatPerMeal + data.avgCarbPerMeal)
  }, [data])

  if (loading) return <p className="loading">Загрузка аналитики...</p>
  if (!data) return <p className="hint">Не удалось загрузить данные</p>

  return (
    <section className="planner-page">
      <h1 className="page-title">Аналитика питания</h1>

      <label className="analytics-period">
        Период:
        <select value={days} onChange={e => setDays(parseInt(e.target.value, 10))}>
          <option value={7}>7 дней</option>
          <option value={14}>14 дней</option>
          <option value={30}>30 дней</option>
          <option value={90}>90 дней</option>
        </select>
      </label>

      <section className="stats-grid">
        <article className="stat-card">
          <span className="stat-value">{data.totalMeals}</span>
          <span className="stat-label">Приёмов пищи</span>
        </article>
        <article className="stat-card">
          <span className="stat-value">{data.uniqueDishes}</span>
          <span className="stat-label">Уникальных блюд</span>
        </article>
        <article className="stat-card highlight">
          <span className="stat-value">{data.diversityIndex}%</span>
          <span className="stat-label">Индекс разнообразия</span>
        </article>
        <article className="stat-card">
          <span className="stat-value">{data.avgKcalPerMeal}</span>
          <span className="stat-label">Сред. ккал</span>
        </article>
        <article className="stat-card">
          <span className="stat-value">{data.avgProteinPerMeal}г</span>
          <span className="stat-label">Белки</span>
        </article>
        <article className="stat-card">
          <span className="stat-value">{data.avgFatPerMeal}г</span>
          <span className="stat-label">Жиры</span>
        </article>
        <article className="stat-card">
          <span className="stat-value">{data.avgCarbPerMeal}г</span>
          <span className="stat-label">Углеводы</span>
        </article>
      </section>

      {data.totalMeals > 0 && (
        <>
          <section className="profile-card chart-card">
            <h2 className="section-title">Калории по дням</h2>
            <p className="hint">Сумма ккал из записанных приёмов пищи</p>
            <div className="chart-scroll">
              <div className="bar-chart" style={{ minWidth: `${Math.max(data.dailyStats.length * 28, 280)}px` }}>
                {data.dailyStats.map(d => (
                  <div key={d.date} className="bar-chart-col" title={`${formatDay(d.date)}: ${d.kcal} ккал, ${d.meals} приёмов`}>
                    <div className="bar-chart-bar-wrap">
                      <div
                        className="bar-chart-bar"
                        style={{ height: `${(d.kcal / maxKcal) * 100}%` }}
                      />
                    </div>
                    <span className="bar-chart-value">{d.kcal > 0 ? Math.round(d.kcal) : ''}</span>
                    <span className="bar-chart-label">{formatDay(d.date)}</span>
                  </div>
                ))}
              </div>
            </div>
          </section>

          <div className="chart-row">
            <section className="profile-card chart-card">
              <h2 className="section-title">Приёмы пищи</h2>
              <ul className="hbar-chart">
                {data.mealTypeStats.map(m => (
                  <li key={m.mealType}>
                    <span className="hbar-label">{MEAL_TYPE_LABELS[m.mealType] ?? m.mealType}</span>
                    <div className="hbar-track">
                      <div
                        className="hbar-fill"
                        style={{ width: `${(m.count / maxMealType) * 100}%` }}
                      />
                    </div>
                    <span className="hbar-count">{m.count}</span>
                  </li>
                ))}
              </ul>
            </section>

            <section className="profile-card chart-card">
              <h2 className="section-title">Средний БЖУ на приём</h2>
              <div className="macro-stack">
                <div
                  className="macro-seg macro-protein"
                  style={{ flex: data.avgProteinPerMeal / macroTotal }}
                  title={`Белки ${data.avgProteinPerMeal}г`}
                />
                <div
                  className="macro-seg macro-fat"
                  style={{ flex: data.avgFatPerMeal / macroTotal }}
                  title={`Жиры ${data.avgFatPerMeal}г`}
                />
                <div
                  className="macro-seg macro-carb"
                  style={{ flex: data.avgCarbPerMeal / macroTotal }}
                  title={`Углеводы ${data.avgCarbPerMeal}г`}
                />
              </div>
              <ul className="macro-legend">
                <li><span className="macro-dot macro-protein" /> Белки {data.avgProteinPerMeal}г</li>
                <li><span className="macro-dot macro-fat" /> Жиры {data.avgFatPerMeal}г</li>
                <li><span className="macro-dot macro-carb" /> Углеводы {data.avgCarbPerMeal}г</li>
              </ul>
            </section>
          </div>
        </>
      )}

      {data.topRepeated.length > 0 && (
        <section className="profile-card">
          <h2 className="section-title">Частые повторы</h2>
          <ul className="pref-list">
            {data.topRepeated.map(r => (
              <li key={r.title}>
                <span>{r.title}</span>
                <span>{r.count} раз</span>
              </li>
            ))}
          </ul>
        </section>
      )}

      <section className="profile-card">
        <h2 className="section-title">Выводы</h2>
        <ul className="insights-list">
          {data.insights.map((text, i) => (
            <li key={i}>{text}</li>
          ))}
        </ul>
      </section>
    </section>
  )
}
