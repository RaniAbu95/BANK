import { useCallback, useEffect, useState } from 'react'
import { useOutletContext } from 'react-router-dom'
import { accounts, transactions, type Loan, type Operation, type Transaction } from '../api'
import { useAuth } from '../auth'
import { readLastAccount, type AccountSelection } from '../accountSelection'
import { ActionForm, Alert, Card, Field, formatILS, num, str } from '../components/ui'

const OPERATIONS: { value: Operation; label: string; icon: string }[] = [
  { value: 'cashDeposit', label: 'הפקדה', icon: '⬇' },
  { value: 'cashWithdrawal', label: 'משיכה', icon: '⬆' },
  { value: 'CashTransfare', label: 'העברה לחשבון אחר', icon: '⇄' },
  { value: 'Loan', label: 'בקשת הלוואה', icon: '％' },
  { value: 'DepositForeignCurrency', label: 'הפקדת מט"ח', icon: '$' },
  { value: 'WithDrawlForeignCurrency', label: 'משיכת מט"ח', icon: '€' },
]
const OP_LABEL = Object.fromEntries(OPERATIONS.map((o) => [o.value, o.label])) as Record<string, string>
const CURRENCIES = ['USD', 'EUR', 'GBP', 'JPY', 'CHF', 'CAD', 'AUD']
interface AccountData {
  balance: number | null
  transactions: Transaction[] | null
  loans: Loan[] | null
  errors: string[]
}

export default function Dashboard() {
  const { session } = useAuth()
  const { myAccounts, accountId, chooseAccount, updateBalance } = useOutletContext<AccountSelection>()
  const [accountInput, setAccountInput] = useState(readLastAccount)
  const [data, setData] = useState<AccountData | null>(null)
  const [loading, setLoading] = useState(false)
  const selected = myAccounts?.find((a) => a.accountId === accountId)

  useEffect(() => {
    if (accountId) setAccountInput(String(accountId))
  }, [accountId])

  const load = useCallback(async (id: number) => {
    setLoading(true)
    // שלוש הקריאות בלתי תלויות — כישלון של אחת לא מסתיר את האחרות
    const [b, t, l] = await Promise.allSettled([
      accounts.getBalance(id),
      accounts.getTransactions(id),
      accounts.getLoans(id),
    ])
    const errors = [b, t, l].flatMap((r) => (r.status === 'rejected' ? [String(r.reason?.message ?? r.reason)] : []))
    // רשימת החשבונות נטענה פעם אחת — מעדכנים בה את היתרה העדכנית
    if (b.status === 'fulfilled') updateBalance(id, b.value)
    setData({
      balance: b.status === 'fulfilled' ? b.value : null,
      transactions: t.status === 'fulfilled' ? t.value : null,
      loans: l.status === 'fulfilled' ? l.value : null,
      errors: [...new Set(errors)],
    })
    setLoading(false)
  }, [updateBalance])

  useEffect(() => {
    if (accountId) load(accountId)
  }, [accountId, load])

  function selectAccount(e: React.FormEvent) {
    e.preventDefault()
    chooseAccount(Number(accountInput))
  }

  return (
    <div className="stack">
      <section className="dash-hero">
        {myAccounts === null && <p className="muted small">טוען…</p>}
        {myAccounts && myAccounts.length === 0 && (
          <p className="muted small">
            {session?.isAdmin ? 'למשתמש המנהל אין חשבון בנק. ניתן להזין מספר חשבון ידנית.' : 'לא נמצא חשבון בנק למשתמש שלך.'}
          </p>
        )}
        {/* משתמש רגיל רואה רק את החשבונות שלו (לפי ההתחברות); הזנה ידנית — למנהל בלבד */}
        {session?.isAdmin && (
          <form className="inline-form" onSubmit={selectAccount}>
            <Field
              label="מספר חשבון"
              type="number"
              min={1}
              value={accountInput}
              onChange={(e) => setAccountInput(e.target.value)}
              required
            />
            <button className="btn btn-primary">הצג</button>
          </form>
        )}
      </section>

      {accountId && (
        <>
          {data?.errors.map((e) => <Alert key={e} kind="error">{e}</Alert>)}

          <div className="dash-grid">
            <Card
              className="balance-card"
              title={selected ? 'יתרה' : `יתרה — חשבון ${accountId}`}
              actions={
                <button className="btn btn-ghost btn-sm" onClick={() => load(accountId)} disabled={loading}>
                  {loading ? 'טוען…' : 'רענון'}
                </button>
              }
            >
              <p className={`balance ${data?.balance != null && data.balance < 0 ? 'negative' : ''}`}>
                {data?.balance != null ? formatILS(data.balance) : '—'}
              </p>
              {selected?.restriction != null && (
                // השרת שומר את המסגרת כמספר שלילי (למשל -10000 = מסגרת של 10,000)
                <dl className="balance-details">
                  <div>
                    <dt>מסגרת עובר ושב</dt>
                    <dd>{formatILS(Math.abs(selected.restriction))}</dd>
                  </div>
                  {data?.balance != null && (
                    <div>
                      <dt>יתרה זמינה למשיכה</dt>
                      <dd>{formatILS(Math.max(0, data.balance - selected.restriction))}</dd>
                    </div>
                  )}
                </dl>
              )}
            </Card>

            <Card className="op-card" title="ביצוע פעולה">
              <OperationForm accountId={accountId} onDone={() => load(accountId)} />
            </Card>

            <Card className="loans-card" title="הלוואות">
              <LoansList rows={data?.loans} />
            </Card>
          </div>

          <Card title="פעולות אחרונות">
            <TransactionsTable rows={data?.transactions} />
          </Card>
        </>
      )}
    </div>
  )
}

function OperationForm({ accountId, onDone }: { accountId: number; onDone: () => void }) {
  const [op, setOp] = useState<Operation>('cashDeposit')
  const isTransfer = op === 'CashTransfare'
  const isForex = op === 'DepositForeignCurrency' || op === 'WithDrawlForeignCurrency'

  return (
    <ActionForm
      submitLabel="בצע"
      onSubmit={async (d) => {
        const msg = await transactions.add(accountId, {
          operation: op,
          amount: num(d, 'amount'),
          target: isTransfer ? num(d, 'target') : undefined,
          foreigCurrencyToExchange: isForex ? str(d, 'currency') : undefined,
          timeStamp: new Date().toISOString(),
        })
        onDone()
        return msg || 'הפעולה בוצעה בהצלחה'
      }}
    >
      <div className="op-tiles" role="radiogroup" aria-label="סוג פעולה">
        {OPERATIONS.map((o) => (
          <button
            key={o.value}
            type="button"
            role="radio"
            aria-checked={op === o.value}
            className={`op-tile ${op === o.value ? 'selected' : ''}`}
            onClick={() => setOp(o.value)}
          >
            <span className="op-icon" aria-hidden="true">{o.icon}</span>
            {o.label}
          </button>
        ))}
      </div>
      <Field label={isForex ? 'סכום במטבע זר' : 'סכום (₪)'} name="amount" type="number" min={1} step={1} required />
      {isTransfer && <Field label="חשבון יעד" name="target" type="number" min={1} required />}
      {isForex && (
        <label className="field">
          <span>מטבע</span>
          <select name="currency" defaultValue="USD">
            {CURRENCIES.map((c) => <option key={c}>{c}</option>)}
          </select>
        </label>
      )}
      {op === 'Loan' && (
        <p className="muted small">
          עד ₪10,000 — הלוואה בסיסית (12 תשלומים) · ₪10,000–50,000 — פלטינום (24) · ₪50,000–100,000 — זהב (36)
        </p>
      )}
    </ActionForm>
  )
}

function TransactionsTable({ rows }: { rows: Transaction[] | null | undefined }) {
  if (!rows) return <p className="muted">—</p>
  if (!rows.length) return <p className="muted">אין פעולות בחשבון</p>
  const sorted = [...rows].sort((a, b) => b.transactionId - a.transactionId)
  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr><th>#</th><th>תאריך</th><th>פעולה</th><th>סכום</th><th>יעד</th><th>מטבע</th></tr>
        </thead>
        <tbody>
          {sorted.map((t) => (
            <tr key={t.transactionId}>
              <td>{t.transactionId}</td>
              <td>{formatDate(t.timeStamp)}</td>
              <td>{OP_LABEL[t.operation] ?? t.operation}</td>
              <td className="num">{t.amount.toLocaleString('he-IL')}</td>
              <td>{t.target ?? '—'}</td>
              {/* השרת שומר מטבע רק בפעולות מט"ח (ובתשלומים את המחרוזת "null"); כל השאר בשקלים */}
              <td>{t.foreigCurrencyToExchange && t.foreigCurrencyToExchange !== 'null' ? t.foreigCurrencyToExchange : '₪'}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

function LoansList({ rows }: { rows: Loan[] | null | undefined }) {
  if (!rows) return <p className="muted">—</p>
  if (!rows.length) return <p className="muted">אין הלוואות פעילות</p>
  return (
    <ul className="loan-list">
      {rows.map((l) => (
        <li key={l.loanId}>
          <div className="loan-row">
            <strong>{l.loanType}</strong>
            <span className="num">{formatILS(l.amount)}</span>
          </div>
          <progress max={l.numberOfPayments} value={l.completedPayments} />
          <div className="loan-row muted small">
            <span>{l.completedPayments}/{l.numberOfPayments} תשלומים</span>
            <span>ריבית {l.intersetRate}%</span>
          </div>
        </li>
      ))}
    </ul>
  )
}

function formatDate(s: string) {
  const d = new Date(s)
  return isNaN(d.getTime()) ? s : d.toLocaleString('he-IL', { dateStyle: 'short', timeStyle: 'short' })
}
