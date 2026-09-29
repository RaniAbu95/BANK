import { useEffect, useState } from 'react'
import { onServerWaking } from '../api'

export default function ServerWakeBanner() {
  const [waking, setWaking] = useState(false)
  useEffect(() => onServerWaking(setWaking), [])
  if (!waking) return null
  return (
    <div className="wake-banner" role="status">
      <span className="wake-spinner" aria-hidden="true" />
      השרת מתעורר אחרי זמן ללא שימוש — זה יכול לקחת עד דקה. אין צורך לרענן את הדף.
    </div>
  )
}
