import { useCallback, useEffect, useState } from 'react'
import { accounts, bankers, customers, visaCards, type Account, type Banker, type Customer } from '../../api'
import { ActionForm, Alert, Card, Field, formatILS, num, str } from '../../components/ui'

const TABS = [
  { id: 'customers', label: 'לקוחות' },
  { id: 'accounts', label: 'חשבונות' },
  { id: 'bankers', label: 'בנקאים' },
  { id: 'visa', label: 'כרטיסי אשראי' },
] as const
type Tab = (typeof TABS)[number]['id']

// ערכי הקטגוריה חייבים להתאים בדיוק לערכים שהשרת בודק ב-AccountBL
const CATEGORIES = [
  { value: 'Regular', label: 'רגיל' },
  { value: 'Saving', label: 'חיסכון' },
  { value: 'Buisness', label: 'עסקי' },
  { value: 'Student', label: 'סטודנט' },
]

export default function Admin() {
  const [tab, setTab] = useState<Tab>('customers')
  return (
    <div className="stack">
      <nav className="tabs" role="tablist">
        {TABS.map((t) => (
          <button key={t.id} role="tab" aria-selected={tab === t.id} className="tab" onClick={() => setTab(t.id)}>
            {t.label}
          </button>
        ))}
      </nav>
      {tab === 'customers' && <CustomersTab />}
      {tab === 'accounts' && <AccountsTab />}
      {tab === 'bankers' && <BankersTab />}
      {tab === 'visa' && <VisaTab />}
    </div>
  )
}

// טוען רשימה מהשרת ומחזיר גם פונקציית רענון
function useList<T>(fetcher: () => Promise<T[]>) {
  const [rows, setRows] = useState<T[] | null>(null)
  const [error, setError] = useState<string | null>(null)
  const reload = useCallback(() => {
    setError(null)
    fetcher().then(setRows, (e) => setError(e.message))
  }, [fetcher])
  useEffect(reload, [reload])
  return { rows, error, reload }
}

function RefreshButton({ onClick }: { onClick: () => void }) {
  return <button className="btn btn-ghost" onClick={onClick}>רענון</button>
}

// ---------------- לקוחות ----------------

function CustomersTab() {
  const { rows, error, reload } = useList(customers.getAll)
  const [actionError, setActionError] = useState<string | null>(null)

  async function remove(c: Customer) {
    if (!confirm(`להשהות את הלקוח ${c.username}?`)) return
    setActionError(null)
    try {
      await customers.remove(c.customerId)
      reload()
    } catch (e) {
      setActionError((e as Error).message)
    }
  }

  return (
    <>
      <Card title="כל הלקוחות" actions={<RefreshButton onClick={reload} />}>
        {error && <Alert kind="error">{error}</Alert>}
        {actionError && <Alert kind="error">{actionError}</Alert>}
        {rows && (
          <div className="table-wrap">
            <table>
              <thead>
                <tr><th>#</th><th>שם משתמש</th><th>דוא"ל</th><th>כתובת</th><th>סטטוס</th><th>אימות</th><th /></tr>
              </thead>
              <tbody>
                {rows.map((c) => (
                  <tr key={c.customerId}>
                    <td>{c.customerId}</td>
                    <td>{c.username}</td>
                    <td>{c.email}</td>
                    <td>{c.location}</td>
                    <td><StatusBadge status={c.status} /></td>
                    <td>{c.emailVerify === 'EmailVerfiyed' ? '✓ מאומת' : 'לא מאומת'}</td>
                    <td>
                      <button className="btn btn-danger btn-sm" onClick={() => remove(c)}>השהיה</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Card>

      <div className="grid-2">
        <Card title="לקוח חדש">
          <ActionForm
            submitLabel="הוספה"
            onSubmit={async (d) => {
              const msg = await customers.add({
                username: str(d, 'username'),
                password: str(d, 'password'),
                email: str(d, 'email'),
                location: str(d, 'location'),
              })
              reload()
              return `${msg}. נשלח קוד אימות לדוא"ל.`
            }}
          >
            <Field label="שם משתמש" name="username" required />
            <Field label="סיסמה" name="password" type="password" required />
            <Field label='דוא"ל' name="email" type="email" required />
            <Field label="כתובת" name="location" required />
          </ActionForm>
        </Card>

        <Card title='אימות דוא"ל של לקוח'>
          <ActionForm
            submitLabel="אימות"
            onSubmit={async (d) => {
              const msg = await customers.verify(str(d, 'email'), str(d, 'code'), num(d, 'customerId'))
              reload()
              return msg
            }}
          >
            <Field label="מספר לקוח" name="customerId" type="number" min={1} required />
            <Field label='דוא"ל' name="email" type="email" required />
            <Field label="קוד אימות" name="code" required />
          </ActionForm>
        </Card>

        <Card title='עדכון דוא"ל'>
          <ActionForm
            submitLabel="עדכון"
            onSubmit={async (d) => {
              const msg = await customers.updateEmail(num(d, 'customerId'), str(d, 'newEmail'))
              reload()
              return msg
            }}
          >
            <Field label="מספר לקוח" name="customerId" type="number" min={1} required />
            <Field label='דוא"ל חדש' name="newEmail" type="email" required />
          </ActionForm>
        </Card>

        <Card title="עדכון כתובת">
          <ActionForm
            submitLabel="עדכון"
            onSubmit={async (d) => {
              const msg = await customers.updateLocation(num(d, 'customerId'), str(d, 'newLocation'))
              reload()
              return msg
            }}
          >
            <Field label="מספר לקוח" name="customerId" type="number" min={1} required />
            <Field label="כתובת חדשה" name="newLocation" required />
          </ActionForm>
        </Card>
      </div>
    </>
  )
}

// ---------------- חשבונות ----------------

function AccountsTab() {
  const { rows, error, reload } = useList(accounts.getAll)
  const [actionError, setActionError] = useState<string | null>(null)

  async function suspend(a: Account) {
    if (!confirm(`להשהות את חשבון ${a.accountNumber ?? a.accountId}?`)) return
    setActionError(null)
    try {
      await accounts.suspend(a.accountId)
      reload()
    } catch (e) {
      setActionError((e as Error).message)
    }
  }

  return (
    <>
      <Card title="כל החשבונות" actions={<RefreshButton onClick={reload} />}>
        {error && <Alert kind="error">{error}</Alert>}
        {actionError && <Alert kind="error">{actionError}</Alert>}
        {rows && (
          <div className="table-wrap">
            <table>
              <thead>
                <tr><th>#</th><th>מספר חשבון</th><th>לקוח</th><th>קטגוריה</th><th>יתרה</th><th>מסגרת</th><th>סטטוס</th><th /></tr>
              </thead>
              <tbody>
                {rows.map((a) => (
                  <tr key={a.accountId}>
                    <td>{a.accountId}</td>
                    <td>{a.accountNumber ?? '—'}</td>
                    <td>{a.customer ? `${a.customer.username} (${a.customer.customerId})` : '—'}</td>
                    <td>{CATEGORIES.find((c) => c.value === a.category)?.label ?? a.category}</td>
                    <td className={`num ${a.balance < 0 ? 'negative' : ''}`}>{formatILS(a.balance)}</td>
                    <td className="num">{a.restriction != null ? formatILS(a.restriction) : '—'}</td>
                    <td><StatusBadge status={a.status} /></td>
                    <td>
                      {a.status === 'Active' && (
                        <button className="btn btn-danger btn-sm" onClick={() => suspend(a)}>השהיה</button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Card>

      <Card title="פתיחת חשבון">
        <p className="muted small">ניתן לפתוח חשבון רק ללקוח שאימת את כתובת הדוא"ל שלו.</p>
        <ActionForm
          submitLabel="פתיחה"
          onSubmit={async (d) => {
            const acc = await accounts.add(num(d, 'customerId'), str(d, 'category'), str(d, 'password'))
            reload()
            return `נפתח חשבון מספר ${acc.accountNumber}`
          }}
        >
          <Field label="מספר לקוח" name="customerId" type="number" min={1} required />
          <label className="field">
            <span>קטגוריה</span>
            <select name="category" defaultValue="Saving">
              {CATEGORIES.map((c) => <option key={c.value} value={c.value}>{c.label}</option>)}
            </select>
          </label>
          <Field label="סיסמת חשבון" name="password" type="password" required />
        </ActionForm>
      </Card>
    </>
  )
}

// ---------------- בנקאים ----------------

function BankersTab() {
  const { rows, error, reload } = useList<Banker>(bankers.getAll)
  return (
    <div className="grid-2">
      <Card title="בנקאים" actions={<RefreshButton onClick={reload} />}>
        {error && <Alert kind="error">{error}</Alert>}
        {rows && (
          <div className="table-wrap">
            <table>
              <thead><tr><th>#</th><th>שם</th><th>דוא"ל</th><th>חשבונות</th></tr></thead>
              <tbody>
                {rows.map((b) => (
                  <tr key={b.bankerId}>
                    <td>{b.bankerId}</td><td>{b.name}</td><td>{b.email}</td><td>{b.numberOfAccounts}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Card>
      <Card title="בנקאי חדש">
        <ActionForm
          submitLabel="הוספה"
          onSubmit={async (d) => {
            const msg = await bankers.add(str(d, 'name'), str(d, 'email'))
            reload()
            return msg
          }}
        >
          <Field label="שם" name="name" required />
          <Field label='דוא"ל' name="email" type="email" required />
        </ActionForm>
      </Card>
    </div>
  )
}

// ---------------- כרטיסי אשראי ----------------

function VisaTab() {
  return (
    <div className="grid-2">
      <Card title="הנפקת כרטיס">
        <ActionForm
          submitLabel="הנפקה"
          onSubmit={async (d) => {
            await visaCards.add({
              accountId: num(d, 'accountId'),
              company: str(d, 'company'),
              status: 'Active',
              visaCardNumber: str(d, 'visaCardNumber'),
              cvv: num(d, 'cvv'),
              expiredDate: str(d, 'expiredDate'),
              limit: num(d, 'limit'),
            })
            return 'הכרטיס הונפק בהצלחה'
          }}
        >
          <Field label="מספר חשבון" name="accountId" type="number" min={1} required />
          <label className="field">
            <span>חברה</span>
            <select name="company" defaultValue="Visa">
              <option>Visa</option><option>Mastercard</option><option>Isracard</option><option>AmericanExpress</option>
            </select>
          </label>
          <Field label="מספר כרטיס" name="visaCardNumber" inputMode="numeric" pattern="\d{8,19}" required />
          <Field label="CVV" name="cvv" inputMode="numeric" pattern="\d{3,4}" required />
          <Field label="תוקף" name="expiredDate" type="month" required />
          <Field label="מסגרת (₪)" name="limit" type="number" min={0} required />
        </ActionForm>
      </Card>

      <Card title="עסקת תשלומים">
        <ActionForm
          submitLabel="הוספה"
          onSubmit={async (d) => {
            await visaCards.addInstallments(num(d, 'count'), num(d, 'value'), num(d, 'cardId'))
            return 'התשלומים נוספו'
          }}
        >
          <Field label="מזהה כרטיס" name="cardId" type="number" min={1} required />
          <Field label="מספר תשלומים" name="count" type="number" min={1} required />
          <Field label="סכום לתשלום (₪)" name="value" type="number" min={1} required />
        </ActionForm>
      </Card>
    </div>
  )
}

function StatusBadge({ status }: { status: string }) {
  const active = status === 'Active'
  return <span className={`badge ${active ? 'badge-ok' : 'badge-warn'}`}>{active ? 'פעיל' : status.startsWith('Suspend') ? 'מושהה' : status}</span>
}
