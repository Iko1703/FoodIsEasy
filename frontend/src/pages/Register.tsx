import { useState } from 'react'
import api from '../api'
import { useAuth } from '../store'
import { useNavigate } from 'react-router-dom'

export default function Register() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [firstName, setFirstName] = useState('')
  const [lastName, setLastName] = useState('')
  const [error, setError] = useState<string | null>(null)
  const { setToken } = useAuth()
  const navigate = useNavigate()

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const res = await api.post('/auth/register', { email, password, firstName, lastName })
      setToken(res.data.token)
      navigate('/')
    } catch (e: unknown) {
      const err = e as { response?: { data?: { error?: string } }; message?: string }
      if (!err.response) {
        setError('Сервер недоступен. Запустите: docker compose up --build')
      } else {
        setError(err.response?.data?.error ?? 'Ошибка регистрации (пароль от 6 символов, корректный email)')
      }
    }
  }

  return (
    <div className="auth">
      <h2>Регистрация</h2>
      <form onSubmit={onSubmit}>
        <input placeholder="Имя" value={firstName} onChange={(e) => setFirstName(e.target.value)} />
        <input placeholder="Фамилия" value={lastName} onChange={(e) => setLastName(e.target.value)} />
        <input placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
        <input placeholder="Пароль" type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
        {error && <div className="error">{error}</div>}
        <button type="submit">Создать аккаунт</button>
      </form>
    </div>
  )
}


