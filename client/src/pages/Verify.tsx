import { Link, useSearchParams } from 'react-router-dom'
import { auth } from '../api'
import { ActionForm, Field, str } from '../components/ui'

export default function Verify() {
  const [params] = useSearchParams()
  return (
    <div className="auth-page">
      <div className="auth-box">
        <h1>אימות דוא"ל</h1>
        <p className="muted">הזן את הקוד שנשלח אליך במייל.</p>
        <ActionForm
          submitLabel="אימות"
          onSubmit={async (d) => {
            await auth.verify(str(d, 'email'), str(d, 'code'))
            return 'הדוא"ל אומת בהצלחה. אפשר להתחבר.'
          }}
        >
          <Field label='דוא"ל' name="email" type="email" required defaultValue={params.get('email') ?? ''} />
          <Field label="קוד אימות" name="code" required inputMode="numeric" />
        </ActionForm>
        <p className="muted">
          <Link to="/login">חזרה להתחברות</Link>
        </p>
      </div>
    </div>
  )
}
