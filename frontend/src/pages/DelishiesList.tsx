import { useEffect, useState } from 'react'
import api from '../api'
import { Link } from 'react-router-dom'

type Delishies = {
  id: number
  title: string
  description: string
  imageUrl?: string
  createdAt?: string
}

export default function DelishiesList() {
  const [items, setItems] = useState<Delishies[]>([])

  useEffect(() => {
    api.get('/delishies').then(res => setItems(res.data ?? []))
  }, [])

  return (
    <div>
      <h1 className="page-title">Блюда</h1>
      <div className="grid">
        {items.map(i => (
          <Link key={i.id} to={`/delishies/${i.id}`} className="card">
            {i.imageUrl ? (
              <img src={i.imageUrl} alt={i.title} className="card-image" />
            ) : (
              <div className="card-image" style={{ 
                background: 'linear-gradient(45deg, #3d3d3d, #4d4d4d)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                color: '#888',
                fontSize: '1.2rem'
              }}>
                🍽️
              </div>
            )}
            <div className="card-content">
              <h3 className="card-title">{i.title}</h3>
              <p className="card-description">{i.description}</p>
              {i.createdAt && (
                <div className="card-date">
                  {new Date(i.createdAt).toLocaleDateString('ru-RU')}
                </div>
              )}
            </div>
          </Link>
        ))}
      </div>
    </div>
  )
}


