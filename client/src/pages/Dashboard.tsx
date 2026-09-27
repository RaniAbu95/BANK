import { useCallback, useEffect, useState } from 'react'
import { accounts, transactions, type Account, type Loan, type Operation, type Transaction } from '../api'
import { ActionForm, Alert, Card, Field, formatILS, num, str } from '../components/ui'

const OPERATIONS: { value: Operation; label: string }[] = [
  { value: 'cashDeposit', label: 'הפקדה' },
  { value: 'cashWithdrawal', label: 'משיכה' },
  { value: 'CashTransfare', label: 'העברה לחשבון אחר' },
  { value: 'Loan', label: 'בקשת הלוואה' },
  { value: 'DepositForeignCurrency', label: 'הפקדת מט"ח' },
  { value: 'WithDrawlForeignCurrency', label: 'משיכת מט"ח' },
]
const OP_LABEL = Object.fromEntries(OPERATIONS.map((o) => [o.value, o.label])) as Record<string, string>
const CURRENCIES = ['USD', 'EUR', 'GBP', 'JPY', 'CHF', 'CAD', 'AUD']
const LAST_ACCOUNT_KEY = 'bank.lastAccount'

function readLastAccount(): string {
  try {
    return localStorage.getItem(LAST_ACCOUNT_KEY) ?? ''
  } catch {
    return ''
  }
}

interface AccountData {
  balance: number | null
  transactions: Transaction[] | null
  loans: Loan[] | null
  errors: string[]
}

export default function Dashboard() {
  const [accountInput, setAccountInput] = useState(readLastAccount)
  const [accountId, setAccountId] = useState<number | null>(() => Number(readLastAccount()) || null)
  const [data, setData] = useState<AccountData | null>(null)
  const [loading, setLoading] = useState(false)
  const [myAccounts, setMyAccounts] = useState<Account[] | null>(null)

  // החשבונות של המשתמש המחובר; אם אין חשבון שמור — בוחרים את הראשון
  useEffect(() => {
    accounts.mine().then(
      (list) => {
        setMyAccounts(list)
        if (list.length) setAccountId((cur) => cur ?? list[0].accountId)
      },
      () => setMyAccounts([]),
    )
  }, [])

  const load = useCallback(async (id: number) => {
    setLoading(true)
    // שלוש הקריאות בלתי תלויות — כישלון של אחת לא מסתיר את האחרות
    const [b, t, l] = await Promise.allSettled([
      accounts.getBalance(id),
      accounts.getTransactions(id),
      accounts.getLoans(id),
    ])
    const errors = [b, t, l].flatMap((r) => (r.status === 'rejected' ? [String(r.reason?.message ?? r.reason)] : []))
    setData({
      balance: b.status === 'fulfilled' ? b.value : null,
      transactions: t.status === 'fulfilled' ? t.value : null,
      loans: l.status === 'fulfilled' ? l.value : null,
      errors: [...new Set(errors)],
    })
    setLoading(false)
  }, [])

  useEffect(() => {
    if (accountId) load(accountId)
  }, [accountId, load])

  function selectAccount(e: React.FormEvent) {
    e.preventDefault()
    chooseAccount(Number(accountInput))
  }

  function chooseAccount(id: number) {
    if (!id) return
    setAccountInput(String(id))
    try {
      localStorage.setItem(LAST_ACCOUNT_KEY, String(id))
    } catch {
      /* ignore */
    }
    setAccountId(id)
  }

  return (
    <div className="stack">
      <Card title="בחירת חשבון">
        {myAccounts && myAccounts.length > 0 && (
          <div className="account-list">
            {myAccounts.map((a) => (
              <button
                key={a.accountId}
                className={`account-chip ${a.accountId === accountId ? 'selected' : ''}`}
                onClick={() => chooseAccount(a.accountId)}
              >
                <strong>חשבון {a.accountId}</strong>
                <span className="muted small">{a.status === 'Active' ? formatILS(a.balance) : 'מושהה'}</span>
              </button>
            ))}
          </div>
        )}
        {myAccounts && myAccounts.length === 0 && (
          <p className="muted small">לא נמצאו חשבונות המקושרים למשתמש שלך. ניתן להזין מספר חשבון ידנית.</p>
        )}
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
      </Card>

      {accountId && (
        <>
          {data?.errors.map((e) => <Alert key={e} kind="error">{e}</Alert>)}

          <div className="grid-2">
            <Card
              title={`יתרה — חשבון ${accountId}`}
              actions={
                <button className="btn btn-ghost" onClick={() => load(accountId)} disabled={loading}>
                  {loading ? 'טוען…' : 'רענון'}
                </button>
              }
            >
              <p className={`balance ${data?.balance != null && data.balance < 0 ? 'negative' : ''}`}>
                {data?.balance != null ? formatILS(data.balance) : '—'}
              </p>
            </Card>

            <Card title="ביצוע פעולה">
              <OperationForm accountId={accountId} onDone={() => load(accountId)} />
            </Card>
          </div>

          <Card title="פעולות אחרונות">
            <TransactionsTable rows={data?.transactions} />
          </Card>

          <Card title="הלוואות">
            <LoansTable rows={data?.loans} />
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
      <label className="field">
        <span>סוג פעולה</span>
        <select value={op} onChange={(e) => setOp(e.target.value as Operation)}>
          {OPERATIONS.map((o) => (
            <option key={o.value} value={o.value}>{o.label}</option>
          ))}
        </select>
      </label>
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
              <td>{t.foreigCurrencyToExchange ?? '—'}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

function LoansTable({ rows }: { rows: Loan[] | null | undefined }) {
  if (!rows) return <p className="muted">—</p>
  if (!rows.length) return <p className="muted">אין הלוואות פעילות</p>
  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr><th>#</th><th>סוג</th><th>סכום</th><th>ריבית</th><th>תשלומים</th></tr>
        </thead>
        <tbody>
          {rows.map((l) => (
            <tr key={l.loanId}>
              <td>{l.loanId}</td>
              <td>{l.loanType}</td>
              <td className="num">{formatILS(l.amount)}</td>
              <td>{l.intersetRate}%</td>
              <td>
                <progress max={l.numberOfPayments} value={l.completedPayments} />{' '}
                {l.completedPayments}/{l.numberOfPayments}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

function formatDate(s: string) {
  const d = new Date(s)
  return isNaN(d.getTime()) ? s : d.toLocaleString('he-IL', { dateStyle: 'short', timeStyle: 'short' })
}
