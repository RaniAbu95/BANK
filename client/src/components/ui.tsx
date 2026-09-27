import { useState, type ReactNode, type InputHTMLAttributes, type FormEvent } from 'react'

export function Field({ label, ...props }: { label: string } & InputHTMLAttributes<HTMLInputElement>) {
  return (
    <label className="field">
      <span>{label}</span>
      <input {...props} />
    </label>
  )
}

export function Alert({ kind, children }: { kind: 'error' | 'success'; children: ReactNode }) {
  return <div className={`alert alert-${kind}`} role={kind === 'error' ? 'alert' : 'status'}>{children}</div>
}

export function Card({ title, children, actions }: { title?: string; children: ReactNode; actions?: ReactNode }) {
  return (
    <section className="card">
      {(title || actions) && (
        <header className="card-header">
          {title && <h2>{title}</h2>}
          {actions}
        </header>
      )}
      {children}
    </section>
  )
}

// טופס עם ניהול מצב טעינה/שגיאה/הצלחה אחיד.
// onSubmit מחזיר הודעת הצלחה (או כלום) וזורק שגיאה במקרה כישלון.
export function ActionForm({
  onSubmit,
  submitLabel,
  children,
}: {
  onSubmit: (data: FormData) => Promise<string | void>
  submitLabel: string
  children: ReactNode
}) {
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState<string | null>(null)

  async function handle(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()
    const form = e.currentTarget
    setBusy(true)
    setError(null)
    setSuccess(null)
    try {
      const msg = await onSubmit(new FormData(form))
      if (msg) setSuccess(msg)
      form.reset()
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err))
    } finally {
      setBusy(false)
    }
  }

  return (
    <form className="form" onSubmit={handle}>
      {children}
      {error && <Alert kind="error">{error}</Alert>}
      {success && <Alert kind="success">{success}</Alert>}
      <button className="btn btn-primary" disabled={busy}>
        {busy ? 'שולח…' : submitLabel}
      </button>
    </form>
  )
}

export const formatILS = (n: number) =>
  new Intl.NumberFormat('he-IL', { style: 'currency', currency: 'ILS' }).format(n)

export const str = (d: FormData, k: string) => String(d.get(k) ?? '').trim()
export const num = (d: FormData, k: string) => Number(d.get(k))
