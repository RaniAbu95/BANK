import { useCallback, useEffect, useState } from 'react'
import { accounts, type Account } from './api'
import type { Session } from './auth'

const LAST_ACCOUNT_KEY = 'bank.lastAccount'
// "Buisness" — כך השרת שומר את הקטגוריה (ראו AccountBL.setRestrictionAmount)
export const CATEGORY_LABEL: Record<string, string> = { Regular: 'רגיל', Saving: 'חיסכון', Buisness: 'עסקי', Business: 'עסקי', Student: 'סטודנט' }

export function readLastAccount(): string {
  try {
    return localStorage.getItem(LAST_ACCOUNT_KEY) ?? ''
  } catch {
    return ''
  }
}

export interface AccountSelection {
  myAccounts: Account[] | null
  accountId: number | null
  chooseAccount: (id: number) => void
  updateBalance: (id: number, balance: number) => void
}

// החשבון הנבחר משותף לתפריט המשתמש בסרגל העליון ולדשבורד
export function useAccountSelection(session: Session | null): AccountSelection {
  // נקבע רק אחרי שרשימת החשבונות של המשתמש נטענה — כדי לא לטעון חשבון שנשמר ע"י משתמש אחר
  const [accountId, setAccountId] = useState<number | null>(null)
  const [myAccounts, setMyAccounts] = useState<Account[] | null>(null)

  const chooseAccount = useCallback((id: number) => {
    if (!id) return
    try {
      localStorage.setItem(LAST_ACCOUNT_KEY, String(id))
    } catch {
      /* ignore */
    }
    setAccountId(id)
  }, [])

  // החשבונות של המשתמש המחובר; אם החשבון השמור לא שלו (או שאין) — בוחרים את הראשון
  useEffect(() => {
    if (!session) return
    accounts.mine().then(
      (list) => {
        setMyAccounts(list)
        const saved = Number(readLastAccount())
        if (list.some((a) => a.accountId === saved)) {
          chooseAccount(saved)
        } else if (list.length) {
          chooseAccount(list[0].accountId)
        } else if (session.isAdmin && saved) {
          chooseAccount(saved)
        } else {
          // החשבון השמור (אם יש) שייך למשתמש אחר שהתחבר קודם מאותו דפדפן — לא מציגים אותו
          setAccountId(null)
          try {
            localStorage.removeItem(LAST_ACCOUNT_KEY)
          } catch {
            /* ignore */
          }
        }
      },
      () => setMyAccounts([]),
    )
  }, [session?.userName])

  const updateBalance = useCallback((id: number, balance: number) => {
    setMyAccounts((list) => list?.map((a) => (a.accountId === id ? { ...a, balance } : a)) ?? list)
  }, [])

  return { myAccounts, accountId, chooseAccount, updateBalance }
}
