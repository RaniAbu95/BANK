import { createContext, useContext, useState, type ReactNode } from 'react'
import { auth as authApi, getToken, setToken } from './api'

export interface Session {
  userName: string
  isAdmin: boolean
}

interface AuthContextValue {
  session: Session | null
  login: (userName: string, password: string) => Promise<Session | null>
  logout: () => void
}

const AuthContext = createContext<AuthContextValue | null>(null)

// פענוח ה-payload של ה-JWT (ללא אימות חתימה — האימות נעשה בשרת).
// השרת שם את שם המשתמש ב-sub ואת ההרשאות בשדה roles.
function decode(token: string | null): Session | null {
  if (!token) return null
  try {
    const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')))
    if (payload.exp && payload.exp * 1000 < Date.now()) return null
    const roles: string[] = payload.roles ?? []
    return { userName: payload.sub, isAdmin: roles.includes('ROLE_ADMIN') }
  } catch {
    return null
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<Session | null>(() => {
    const s = decode(getToken())
    // טוקן שפג תוקפו נשאר ב-localStorage ונשלח עם כל בקשה (כולל /login) — מנקים אותו
    if (!s) setToken(null)
    return s
  })

  async function login(userName: string, password: string) {
    const token = await authApi.login(userName, password)
    setToken(token)
    const s = decode(token)
    setSession(s)
    return s
  }

  function logout() {
    setToken(null)
    // החשבון האחרון שנבחר (Dashboard) שייך למשתמש הזה — לא להשאיר אותו למשתמש הבא
    try {
      localStorage.removeItem('bank.lastAccount')
    } catch {
      /* ignore */
    }
    setSession(null)
  }

  return <AuthContext.Provider value={{ session, login, logout }}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider')
  return ctx
}
