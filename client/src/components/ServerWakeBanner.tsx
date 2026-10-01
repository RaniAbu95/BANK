import { useEffect, useState } from 'react'
import { onSlowRequest } from '../api'

export default function ServerWakeBanner() {
  const [message, setMessage] = useState<string | null>(null)
  useEffect(() => onSlowRequest(setMessage), [])
  if (!message) return null
  return (
    <div className="wake-banner" role="status">
      <span className="wake-spinner" aria-hidden="true" />
      {message}
    </div>
  )
}
