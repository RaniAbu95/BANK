import { Link, Navigate, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth'
import { ActionForm, Field, str } from '../components/ui'

export default function Login() {
  const { session, login } = useAuth()
  const navigate = useNavigate()
  if (session) return <Navigate to="/" replace />

  return (
    <div className="auth-page">
      <div className="auth-box">
        <h1>כניסה לחשבון</h1>
        <ActionForm
          submitLabel="התחברות"
          onSubmit={async (d) => {
            try {
              await login(str(d, 'userName'), str(d, 'password'))
            } catch {
              throw new Error('שם משתמש או סיסמה שגויים')
            }
            navigate('/')
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
