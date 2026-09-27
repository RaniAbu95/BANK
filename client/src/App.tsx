import { BrowserRouter, Navigate, NavLink, Outlet, Route, Routes } from 'react-router-dom'
import { AuthProvider, useAuth } from './auth'
import Login from './pages/Login'
import Signup from './pages/Signup'
import Verify from './pages/Verify'
import Dashboard from './pages/Dashboard'
import Admin from './pages/admin/Admin'

function Layout() {
  const { session, logout } = useAuth()
  if (!session) return <Navigate to="/login" replace />
  return (
    <div className="app">
      <header className="topbar">
        <span className="brand">🏦 הבנק שלי</span>
        <nav>
          <NavLink to="/" end>החשבון שלי</NavLink>
          {session.isAdmin && <NavLink to="/admin">ניהול</NavLink>}
        </nav>
        <span className="user">
          {session.userName}
          {session.isAdmin && <span className="badge badge-ok">מנהל</span>}
          <button className="btn btn-ghost btn-sm" onClick={logout}>יציאה</button>
        </span>
      </header>
      <main className="container">
        <Outlet />
      </main>
    </div>
  )
}

function AdminOnly() {
  const { session } = useAuth()
  return session?.isAdmin ? <Admin /> : <Navigate to="/" replace />
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          <Route path="/verify" element={<Verify />} />
          <Route element={<Layout />}>
            <Route path="/" element={<Dashboard />} />
            <Route path="/admin" element={<AdminOnly />} />
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}
