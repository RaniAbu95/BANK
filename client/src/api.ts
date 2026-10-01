// שכבת גישה ל-REST API של שרת הבנק (Spring Boot).
// רוב נקודות הקצה בשרת מקבלות פרמטרים כ-query string (@RequestParam),
// וחלקן גוף JSON (@RequestBody) — הפונקציות כאן משקפות את זה אחד לאחד.

const BASE_URL = import.meta.env.VITE_API_URL ?? '/api'
const TOKEN_KEY = 'bank.token'

export function getToken(): string | null {
  try {
    return localStorage.getItem(TOKEN_KEY)
  } catch {
    return null
  }
}

export function setToken(token: string | null) {
  try {
    if (token) localStorage.setItem(TOKEN_KEY, token)
    else localStorage.removeItem(TOKEN_KEY)
  } catch {
    /* אחסון לא זמין — ממשיכים בלי לשמור */
  }
}

export class ApiError extends Error {
  status: number
  constructor(status: number, message: string) {
    super(message)
    this.status = status
  }
}

// השרת במסלול החינמי של Render נרדם כשאין שימוש, והבקשה הראשונה אחרי זה לוקחת עד דקה.
// בקשה שמתעכבת יותר מ-SLOW_MS מציגה הודעה במקום המתנה שקטה — הודעת "השרת מתעורר",
// או הודעה משלה כשידוע מראש למה היא איטית (למשל שליחת מייל).
const SLOW_MS = 3000
export const WAKING_MESSAGE = 'השרת מתעורר אחרי זמן ללא שימוש — זה יכול לקחת עד דקה. אין צורך לרענן את הדף.'
const slowMessages: string[] = []
const slowListeners = new Set<(message: string | null) => void>()

function notifySlow() {
  const message = slowMessages.at(-1) ?? null
  slowListeners.forEach((listener) => listener(message))
}

/** ההודעה של הבקשה האיטית האחרונה שעדיין פתוחה, או null כשאין כזו */
export function onSlowRequest(listener: (message: string | null) => void): () => void {
  slowListeners.add(listener)
  return () => slowListeners.delete(listener)
}

async function trackedFetch(url: string, init?: RequestInit, slowMessage = WAKING_MESSAGE): Promise<Response> {
  let slow = false
  const timer = setTimeout(() => {
    slow = true
    slowMessages.push(slowMessage)
    notifySlow()
  }, SLOW_MS)
  try {
    return await fetch(url, init)
  } finally {
    clearTimeout(timer)
    if (slow) {
      slowMessages.splice(slowMessages.indexOf(slowMessage), 1)
      notifySlow()
    }
  }
}

/** מעיר את השרת כבר בטעינת האתר, עוד לפני שהמשתמש מתחבר */
export function wakeServer() {
  trackedFetch(`${BASE_URL}/health`).catch(() => {
    /* השגיאה תופיע בבקשה האמיתית הבאה */
  })
}

type Params = Record<string, string | number | undefined>

function messageFor(status: number, body: string): string {
  // שגיאה בפורמט של Spring / GlobalExceptionHandler: { status, error, message, path }
  let json: { message?: string; error?: string } | null = null
  try {
    json = JSON.parse(body)
  } catch {
    /* לא JSON */
  }
  if (json?.message) return json.message
  if (status === 401 || status === 403) return 'אין הרשאה לפעולה זו. יש להתחבר מחדש או להשתמש במשתמש מנהל.'
  if (json?.error) return `שגיאת שרת (${status}): ${json.error}`
  if (!json && body && body.length < 300) return body
  return `שגיאת שרת (${status})`
}

async function request<T>(method: string, path: string, opts: { params?: Params; body?: unknown; slowMessage?: string } = {}): Promise<T> {
  const query = new URLSearchParams()
  for (const [k, v] of Object.entries(opts.params ?? {})) {
    if (v !== undefined && v !== '') query.set(k, String(v))
  }
  const url = `${BASE_URL}${path}${query.size ? `?${query}` : ''}`

  const headers: Record<string, string> = {}
  const token = getToken()
  if (token) headers.Authorization = `Bearer ${token}`
  if (opts.body !== undefined) headers['Content-Type'] = 'application/json'

  let res: Response
  try {
    res = await trackedFetch(url, {
      method,
      headers,
      body: opts.body !== undefined ? JSON.stringify(opts.body) : undefined,
    }, opts.slowMessage)
  } catch {
    throw new ApiError(0, 'לא ניתן להתחבר לשרת. בדוק את החיבור לאינטרנט ונסה שוב בעוד רגע.')
  }

  const text = await res.text()
  if (!res.ok) throw new ApiError(res.status, messageFor(res.status, text))
  try {
    return JSON.parse(text) as T
  } catch {
    return text as T
  }
}

// ---------- טיפוסים (לפי ה-beans בשרת) ----------

export interface Customer {
  customerId: number
  username: string
  email: string
  location: string
  status: string
  emailVerify: string
}

export interface Account {
  accountId: number
  /** מספר חשבון בנק בן 7 ספרות — זה המספר שמוצג ללקוח */
  accountNumber: number
  balance: number
  category: string
  restriction: number | null
  status: string
  customer?: Customer
}

export interface Transaction {
  transactionId: number
  timeStamp: string
  operation: string
  target: number | null
  amount: number
  foreigCurrencyToExchange: string | null
}

export interface Loan {
  loanId: number
  loanType: string
  amount: number
  intersetRate: number
  completedPayments: number
  numberOfPayments: number
}

export interface Banker {
  bankerId: number
  name: string
  email: string
  numberOfAccounts: number
}

export type Operation =
  | 'cashDeposit'
  | 'cashWithdrawal'
  | 'CashTransfare'
  | 'Loan'
  | 'DepositForeignCurrency'
  | 'WithDrawlForeignCurrency'

export interface OperationRequest {
  operation: Operation
  amount: number
  target?: number
  timeStamp: string
  foreigCurrencyToExchange?: string
}

// ---------- משתמשים והתחברות ----------

export const auth = {
  login: (userName: string, password: string) =>
    request<string>('POST', '/login', { body: { userName, password } }),
  signup: (user: { userName: string; password: string; location: string; email: string }) =>
    request<{ message: string; accountNumber: number }>('POST', '/signup', {
      body: user,
      slowMessage: 'שולחים קוד אימות לדוא"ל שהזנת — אם השרת היה רדום זה יכול לקחת עד דקה. אין צורך לרענן את הדף.',
    }),
  verify: (email: string, code: string) => request<string>('POST', '/verify', { params: { email, code } }),
}

// ---------- חשבונות ----------

export const accounts = {
  mine: () => request<Account[]>('GET', '/accounts/my'),
  getBalance: (accountId: number) => request<number>('GET', `/accounts/getBalance/${accountId}`),
  getTransactions: (accountId: number) => request<Transaction[]>('GET', `/accounts/getAllTransactions/${accountId}`),
  getLoans: (accountId: number) => request<Loan[]>('GET', `/accounts/getAllLoans/${accountId}`),
  get: (accountId: number) => request<Account>('GET', `/accounts/getId/${accountId}`),
  getAll: () => request<Account[]>('GET', '/accounts/getAll'),
  add: (customerId: number, category: string, password: string) =>
    request<Account>('POST', '/accounts/add', { params: { customerId, category, password } }),
  suspend: (accountId: number) => request<Account>('PUT', `/accounts/suspend/${accountId}`),
}

// ---------- פעולות בחשבון ----------

export const transactions = {
  add: (accountId: number, op: OperationRequest) =>
    request<string>('POST', '/transactions/add', { params: { accountId }, body: op }),
}

// ---------- לקוחות (מנהל) ----------

export const customers = {
  getAll: () => request<Customer[]>('GET', '/customers/getAll'),
  get: (customerId: number) => request<Customer>('GET', '/customers/get', { params: { customerId } }),
  add: (c: { location: string; username: string; email: string; password: string }) =>
    request<string>('POST', '/customers/add', { params: c }),
  updateEmail: (customerId: number, newEmail: string) =>
    request<string>('PUT', '/customers/updateEmail', { params: { customerId, newEmail } }),
  updateLocation: (customerId: number, newLocation: string) =>
    request<string>('PUT', '/customers/updateLocation', { params: { customerId, newLocation } }),
  remove: (customerId: number) => request<string>('POST', '/customers/delete', { params: { customerId } }),
  verify: (email: string, code: string, customerId: number) =>
    request<string>('POST', '/customers/verify', { params: { email, code, customerId } }),
}

// ---------- בנקאים (מנהל) ----------

export const bankers = {
  getAll: () => request<Banker[]>('GET', '/bankers/getAll'),
  add: (name: string, email: string) => request<string>('POST', '/bankers/add', { params: { name, email } }),
}

// ---------- כרטיסי אשראי (מנהל) ----------

export const visaCards = {
  add: (c: {
    accountId: number
    status: string
    company: string
    cvv: number
    expiredDate: string
    visaCardNumber: string
    limit: number
  }) => request<string>('POST', '/visaCards/add', { params: c }),
  addInstallments: (numberOfInstallments: number, valueOfInstallments: number, visaCardInstallmentsId: number) =>
    request<string>('POST', '/visaCards/addInstallments', {
      params: { numberOfInstallments, valueOfInstallments, visaCardInstallmentsId },
    }),
}
