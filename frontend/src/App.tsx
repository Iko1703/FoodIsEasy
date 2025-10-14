import './App.css'
import { BrowserRouter, Routes, Route, Link, Navigate } from 'react-router-dom'
import Login from './pages/Login'
import Register from './pages/Register'
import DelishiesList from './pages/DelishiesList'
import DelishiesDetails from './pages/DelishiesDetails'
import Profile from './pages/Profile'
import { useAuth } from './store'

function App() {
  const { token, setToken } = useAuth()

  return (
    <BrowserRouter>
      <nav className="nav">
        <div className="container">
          <Link to="/" className="nav-brand">FoodIsEasy</Link>
          <div className="nav-links">
            <Link to="/">Блюда</Link>
            {token ? (
              <>
                <Link to="/profile">Профиль</Link>
                <button onClick={() => setToken(null)}>Выйти</button>
              </>
            ) : (
              <>
                <Link to="/login">Войти</Link>
                <Link to="/register">Регистрация</Link>
              </>
            )}
          </div>
        </div>
      </nav>
      <main>
        <div className="container">
          <Routes>
            <Route path="/" element={<DelishiesList />} />
            <Route path="/delishies/:id" element={<DelishiesDetails />} />
            <Route path="/login" element={token ? <Navigate to="/" /> : <Login />} />
            <Route path="/register" element={token ? <Navigate to="/" /> : <Register />} />
            <Route path="/profile" element={token ? <Profile /> : <Navigate to="/login" />} />
          </Routes>
        </div>
      </main>
    </BrowserRouter>
  )
}

export default App
