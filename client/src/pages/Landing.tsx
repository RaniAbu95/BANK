import { Link } from 'react-router-dom'

const FEATURES = [
  { icon: '💳', title: 'חשבונות וכרטיסים', text: 'פתיחת חשבון, הנפקת כרטיס אשראי ומעקב אחרי היתרה והמסגרת בזמן אמת.' },
  { icon: '🔁', title: 'העברות ותשלומים', text: 'העברה בין חשבונות, הפקדה, משיכה ועסקאות בתשלומים — הכול ממסך אחד.' },
  { icon: '🏠', title: 'הלוואות', text: 'בקשת הלוואה ומעקב אחרי יתרת ההחזר של כל הלוואה פתוחה.' },
  { icon: '🛡️', title: 'ניהול ואבטחה', text: 'אימות דוא"ל בהרשמה, התחברות עם JWT ודף ניהול לבנקאים וללקוחות.' },
]

// דף הנחיתה הציבורי — מוצג בכתובת הראשית למי שלא מחובר
export default function Landing() {
  return (
    <div className="app">
      <header className="topbar">
        <div className="topbar-inner">
          <span className="brand">
            <span className="brand-mark" aria-hidden="true">🏦</span>
            הבנק שלי
          </span>
          <nav />
          <Link to="/login" className="btn btn-ghost btn-sm">כניסה</Link>
        </div>
      </header>
      <main className="container landing">
        <section className="landing-hero">
          <h1>הבנק שלך, בדפדפן</h1>
          <p className="muted">
            מערכת בנקאות מלאה: חשבונות, כרטיסים, העברות והלוואות, עם ממשק ניהול לבנקאים.
          </p>
          <div className="landing-actions">
            <Link to="/signup" className="btn btn-primary">פתיחת משתמש</Link>
            <Link to="/login" className="btn btn-ghost">יש לי כבר משתמש</Link>
          </div>
        </section>
        <section className="landing-grid">
          {FEATURES.map((f) => (
            <div key={f.title} className="card">
              <span className="op-icon" aria-hidden="true">{f.icon}</span>
              <h2>{f.title}</h2>
              <p className="muted small">{f.text}</p>
            </div>
          ))}
        </section>
      </main>
      <footer className="site-footer">
        <Link to="/privacy">מדיניות פרטיות</Link>
      </footer>
    </div>
  )
}
