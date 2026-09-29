import { Link, Navigate, useNavigate } from 'react-router-dom'
import { ApiError } from '../api'
import { useAuth } from '../auth'
import { ActionForm, Field, str } from '../components/ui'

export default function Login() {
  const { session, login } = useAuth()
  const navigate = useNavigate()
  // מנהל נשלח ישר לדף הניהול, לקוח רגיל לדשבורד
  if (session) return <Navigate to={session.isAdmin ? '/admin' : '/'} replace />

  return (
    <div className="auth-page">
      <div className="auth-box">
        <h1>כניסה לחשבון</h1>
        <ActionForm
          submitLabel="התחברות"
          onSubmit={async (d) => {
            let s
            try {
              s = await login(str(d, 'userName'), str(d, 'password'))
            } catch (err) {
              // רק 401/403 הם באמת פרטים שגויים; שגיאות אחרות (שרת למטה, 500) מוצגות כמו שהן
              if (err instanceof ApiError && err.status !== 401 && err.status !== 403) throw err
              throw new Error('שם משתמש או סיסמה שגויים')
            }
            navigate(s?.isAdmin ? '/admin' : '/', { replace: true })
          }}
        >
          <Field label="שם משתמש" name="userName" required autoComplete="username" />
          <Field label="סיסמה" name="password" type="password" required autoComplete="current-password" />
        </ActionForm>
        <p className="muted">
          אין לך משתמש? <Link to="/signup">להרשמה</Link> · <Link to="/verify">אימות דוא"ל</Link>
        </p>
      </div>
    </div>
  )
}
