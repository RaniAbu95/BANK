import { useEffect, useRef, useState } from 'react'
import { BrowserRouter, Link, Navigate, NavLink, Outlet, Route, Routes, useNavigate } from 'react-router-dom'
import { CATEGORY_LABEL, useAccountSelection, type AccountSelection } from './accountSelection'
import { AuthProvider, useAuth } from './auth'
import Login from './pages/Login'
import Signup from './pages/Signup'
import Verify from './pages/Verify'
import Dashboard from './pages/Dashboard'
import Admin from './pages/admin/Admin'

function Layout() {
  const { session, logout } = useAuth()
  const selection = useAccountSelection(session)
  if (!session) return <Navigate to="/login" replace />
  return (
    <div className="app">
      <header className="topbar">
        <div className="topbar-inner">
          {/* הלוגו מחזיר לדשבורד (למשל מדף הניהול) */}
          <Link to="/" className="brand">
            <span className="brand-mark" aria-hidden="true">🏦</span>
            הבנק שלי
          </Link>
          <nav>
            {session.isAdmin && <NavLink to="/admin">ניהול</NavLink>}
          </nav>
          <div className="user">
            <UserMenu userName={session.userName} isAdmin={session.isAdmin} selection={selection} />
            <button className="btn btn-ghost btn-sm" onClick={logout}>יציאה</button>
          </div>
        </div>
      </header>
      <main className="container">
        <Outlet context={selection} />
      </main>
    </div>
  )
}

function UserMenu({ userName, isAdmin, selection }: { userName: string; isAdmin: boolean; selection: AccountSelection }) {
  const [open, setOpen] = useState(false)
  const ref = useRef<HTMLDivElement>(null)
  const navigate = useNavigate()
  const { myAccounts, accountId, chooseAccount } = selection

  // סגירה בלחיצה מחוץ לתפריט או ב-Escape
  useEffect(() => {
    if (!open) return
    const onClick = (e: MouseEvent) => {
      if (!ref.current?.contains(e.target as Node)) setOpen(false)
    }
    const onKey = (e: KeyboardEvent) => e.key === 'Escape' && setOpen(false)
    document.addEventListener('mousedown', onClick)
    document.addEventListener('keydown', onKey)
    return () => {
      document.removeEventListener('mousedown', onClick)
      document.removeEventListener('keydown', onKey)
    }
  }, [open])

  return (
    <div className="user-menu" ref={ref}>
      <button className="user-trigger" aria-haspopup="true" aria-expanded={open} onClick={() => setOpen((o) => !o)}>
        <span className="avatar" aria-hidden="true">{userName.charAt(0).toUpperCase()}</span>
        <span className="user-name">{userName}</span>
        {isAdmin && <span className="badge badge-ok">מנהל</span>}
        <span className="chevron" aria-hidden="true">▾</span>
      </button>
      {open && (
        <div className="user-dropdown">
          <p className="dropdown-label">{myAccounts && myAccounts.length > 1 ? 'החשבונות שלי' : 'החשבון שלי'}</p>
          {myAccounts === null && <p className="muted small">טוען…</p>}
          {myAccounts?.length === 0 && <p className="muted small">אין חשבון בנק</p>}
          {myAccounts?.map((a) => (
            <button
              key={a.accountId}
              className={`dropdown-account ${a.accountId === accountId ? 'selected' : ''}`}
              aria-current={a.accountId === accountId}
              onClick={() => {
                chooseAccount(a.accountId)
                setOpen(false)
                navigate('/')
              }}
            >
              <span>
                מספר חשבון: <strong>{a.accountNumber ?? a.accountId}</strong>
              </span>
              <span>
                סוג חשבון: <strong>{CATEGORY_LABEL[a.category] ?? a.category}</strong>
              </span>
            </button>
          ))}
        </div>
      )}
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
