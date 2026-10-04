import { Link, useNavigate } from 'react-router-dom'
import { auth } from '../api'
import GoogleLoginButton from '../components/GoogleLoginButton'
import { ActionForm, Field, str } from '../components/ui'

export default function Signup() {
  const navigate = useNavigate()
  return (
    <div className="auth-page">
      <div className="auth-box">
        <h1>הרשמה</h1>
        <p className="muted">לאחר ההרשמה יישלח קוד אימות לכתובת הדוא"ל שהזנת.</p>
        <ActionForm
          submitLabel="הרשמה"
          onSubmit={async (d) => {
            const email = str(d, 'email')
            const { accountNumber } = await auth.signup({
              userName: str(d, 'userName'),
              password: str(d, 'password'),
              location: str(d, 'location'),
              email,
            })
            const account = accountNumber ? `&account=${accountNumber}` : ''
            navigate(`/verify?email=${encodeURIComponent(email)}${account}`)
          }}
        >
          <Field label="שם משתמש" name="userName" required autoComplete="username" />
          <Field label="סיסמה" name="password" type="password" required autoComplete="new-password" />
          <Field label="כתובת" name="location" required />
          <Field label='דוא"ל' name="email" type="email" required />
        </ActionForm>
        <GoogleLoginButton />
        <p className="muted">
          כבר רשום? <Link to="/login">להתחברות</Link>
        </p>
        <p className="muted small">
          <Link to="/privacy">מדיניות פרטיות</Link>
        </p>
      </div>
    </div>
  )
}
