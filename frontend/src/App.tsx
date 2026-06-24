import './App.css'
import { BrowserRouter, Routes, Route, Link, NavLink, Navigate } from 'react-router-dom'
import Login from './pages/Login'
import Register from './pages/Register'
import DelishiesList from './pages/DelishiesList'
import DelishiesDetails from './pages/DelishiesDetails'
import Profile from './pages/Profile'
import MealPlanner from './pages/MealPlanner'
import Recommendations from './pages/Recommendations'
import Analytics from './pages/Analytics'
import Groups from './pages/Groups'
import ApiDocs from './pages/ApiDocs'
import { useAuth } from './store'

function NavItem({ to, children, end }: { to: string; children: React.ReactNode; end?: boolean }) {
  return (
    <NavLink
      to={to}
      end={end}
      className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
    >
      {children}
    </NavLink>
  )
}

function App() {
  const { token, setToken } = useAuth()

  return (
    <BrowserRouter>
      <nav className="nav">
        <div className="container">
          <Link to="/" className="nav-brand">FoodIsEasy</Link>
          <div className="nav-links">
            <NavItem to="/" end>Блюда</NavItem>
            {token ? (
              <>
                <NavItem to="/recommendations">Рекомендации</NavItem>
                <NavItem to="/planner">Питание</NavItem>
                <NavItem to="/analytics">Аналитика</NavItem>
                <NavItem to="/groups">Группы</NavItem>
                <NavItem to="/profile">Профиль</NavItem>
                <button type="button" onClick={() => setToken(null)}>Выйти</button>
              </>
            ) : (
              <>
                <NavItem to="/login">Войти</NavItem>
                <NavItem to="/register">Регистрация</NavItem>
              </>
            )}
            <NavItem to="/api">API</NavItem>
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
            <Route path="/recommendations" element={token ? <Recommendations /> : <Navigate to="/login" />} />
            <Route path="/planner" element={token ? <MealPlanner /> : <Navigate to="/login" />} />
            <Route path="/analytics" element={token ? <Analytics /> : <Navigate to="/login" />} />
            <Route path="/groups" element={token ? <Groups /> : <Navigate to="/login" />} />
            <Route path="/profile" element={token ? <Profile /> : <Navigate to="/login" />} />
            <Route path="/api" element={<ApiDocs />} />
          </Routes>
        </div>
      </main>
      <footer className="app-footer">
        <div className="container footer-inner">
          <span>FoodIsEasy — дипломный проект</span>
          <Link to="/api">REST API</Link>
        </div>
      </footer>
    </BrowserRouter>
  )
}

export default App
