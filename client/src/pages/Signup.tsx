import { Link, useNavigate } from 'react-router-dom'
import { auth } from '../api'
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
            await auth.signup({
              userName: str(d, 'userName'),
              password: str(d, 'password'),
              location: str(d, 'location'),
              email,
            })
            navigate(`/verify?email=${encodeURIComponent(email)}`)
          }}
        >
          <Field label="שם משתמש" name="userName" required autoComplete="username" />
          <Field label="סיסמה" name="password" type="password" required autoComplete="new-password" />
          <Field label="כתובת" name="location" required />
          <Field label='דוא"ל' name="email" type="email" required />
        </ActionForm>
        <p className="muted">
          כבר רשום? <Link to="/login">להתחברות</Link>
        </p>
      </div>
    </div>
  )
}
