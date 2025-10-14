import { useState } from 'react'
import api from '../api'
import { useAuth } from '../store'
import { useNavigate } from 'react-router-dom'

export default function Login() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const { setToken } = useAuth()
  const navigate = useNavigate()

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const res = await api.post('/auth/login', { email, password })
      setToken(res.data.token)
      navigate('/')
    } catch (e: any) {
      setError('Неверный email или пароль')
    }
  }

  return (
    <div className="auth">
      <h2>Вход</h2>
      <form onSubmit={onSubmit}>
        <input placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
        <input placeholder="Пароль" type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
        {error && <div className="error">{error}</div>}
        <button type="submit">Войти</button>
      </form>
    </div>
  )
}


