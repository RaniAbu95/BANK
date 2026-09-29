import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// בפיתוח: כל קריאה ל-/api מועברת לשרת ה-Spring Boot (פורט 8081) בלי הקידומת,
// כך שאין צורך בהגדרות CORS בצד השרת.
export default defineConfig({
  // נתיב שממנו האתר מוגש, למשל /bank/ כש-rani-support.com/bank מפנה ל-Cloudflare Pages
  base: process.env.BASE_PATH ?? '/',
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: process.env.BANK_SERVER_URL ?? 'http://localhost:8081',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },
})
