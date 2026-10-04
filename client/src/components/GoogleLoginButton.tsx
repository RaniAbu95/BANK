import { useEffect, useRef, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../auth'
import { Alert } from './ui'

// Client ID של אפליקציית OAuth ב-Google Cloud. בלי הגדרה הכפתור לא מוצג בכלל.
const CLIENT_ID: string | undefined = import.meta.env.VITE_GOOGLE_CLIENT_ID

// החלק שבו אנחנו משתמשים מ-Google Identity Services (https://accounts.google.com/gsi/client)
interface GoogleIdentity {
  accounts: {
    id: {
      initialize(config: { client_id: string; callback: (response: { credential: string }) => void }): void
      renderButton(parent: HTMLElement, options: Record<string, string | number>): void
    }
  }
}

declare global {
  interface Window {
    google?: GoogleIdentity
  }
}

// הסקריפט נטען ומאותחל פעם אחת לכל האתר; הכפתור שמוצג כרגע מקבל את הטוקן דרך onCredential
let googleReady: Promise<GoogleIdentity> | null = null
let onCredential: ((credential: string) => void) | null = null

function loadGoogle(clientId: string): Promise<GoogleIdentity> {
  googleReady ??= new Promise((resolve, reject) => {
    const script = document.createElement('script')
    script.src = 'https://accounts.google.com/gsi/client'
    script.async = true
    script.onload = () => {
      const google = window.google!
      google.accounts.id.initialize({ client_id: clientId, callback: (r) => onCredential?.(r.credential) })
      resolve(google)
    }
    script.onerror = () => {
      script.remove()
      googleReady = null
      reject(new Error('google script failed to load'))
    }
    document.head.appendChild(script)
  })
  return googleReady
}

/** כפתור "המשך עם Google". משתמש חדש נרשם אוטומטית עם חשבון בנק */
export default function GoogleLoginButton() {
  const { loginWithGoogle } = useAuth()
  const navigate = useNavigate()
  const container = useRef<HTMLDivElement>(null)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState<string | null>(null)

  async function handle(credential: string) {
    setBusy(true)
    setError(null)
    try {
      const s = await loginWithGoogle(credential)
      navigate(s?.isAdmin ? '/admin' : '/', { replace: true })
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err))
    } finally {
      setBusy(false)
    }
  }
  const handleRef = useRef(handle)
  handleRef.current = handle

  useEffect(() => {
    if (!CLIENT_ID) return
    let active = true
    onCredential = (credential) => handleRef.current(credential)
    loadGoogle(CLIENT_ID)
      .then((google) => {
        const el = container.current
        if (!active || !el) return
        google.accounts.id.renderButton(el, {
          theme: 'outline',
          size: 'large',
          text: 'continue_with',
          locale: 'he',
          // Google מקבלת רוחב בין 200 ל-400 פיקסלים
          width: Math.min(400, Math.max(200, el.clientWidth)),
        })
      })
      .catch(() => active && setError('לא ניתן לטעון את ההתחברות עם Google. בדקו את החיבור ונסו לרענן את הדף.'))
    return () => {
      active = false
      onCredential = null
    }
  }, [])

  if (!CLIENT_ID) return null

  return (
    <div className="google-login">
      <p className="auth-divider">או</p>
      <div ref={container} className="google-button" />
      {busy && <p className="muted small">מתחברים…</p>}
      {error && <Alert kind="error">{error}</Alert>}
    </div>
  )
}
