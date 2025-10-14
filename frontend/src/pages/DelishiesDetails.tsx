import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import api from '../api'

type Product = { id: number; name: string; fatPer100g: number; proteinPer100g: number; carbPer100g: number; kcalPer100g: number }
type DelishiesProduct = { id: number; quantityGrams: number; product: Product }
type Feedback = { id: number; message: string }
type Delishies = { 
  id: number; 
  title: string; 
  description: string; 
  imageUrl?: string
  products: DelishiesProduct[]; 
  feedbacks: Feedback[] 
}

export default function DelishiesDetails() {
  const { id } = useParams()
  const [item, setItem] = useState<Delishies | null>(null)

  useEffect(() => {
    api.get(`/delishies/${id}`).then(res => setItem(res.data))
  }, [id])

  if (!item) return <div className="loading">Загрузка...</div>

  return (
    <div className="details-container">
      {item.imageUrl ? (
        <img src={item.imageUrl} alt={item.title} className="details-image" />
      ) : (
        <div className="details-image" style={{ 
          background: 'linear-gradient(45deg, #3d3d3d, #4d4d4d)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: '#888',
          fontSize: '3rem'
        }}>
          🍽️
        </div>
      )}
      <h1 className="details-title">{item.title}</h1>
      <p className="details-description">{item.description}</p>
      
      <h2 className="section-title">Компоненты</h2>
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
      <ul className="groups-list">
        {item.feedbacks?.map(f => (
          <li key={f.id}>{f.message}</li>
        ))}
      </ul>
    </div>
  )
}


