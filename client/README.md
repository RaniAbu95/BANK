# Bank client

React + Vite + TypeScript web client for the bank server's REST API. The UI is in Hebrew (RTL).

## Running

1. Start the Spring Boot server (port 8081, needs MySQL).
2. In this folder:

```bash
npm install
npm run dev
```

3. Open http://localhost:5173

In dev mode, every request to `/api/*` is proxied to `http://localhost:8081` with the `/api` prefix removed, so the server needs no CORS config.
To point at another server: `BANK_SERVER_URL=http://host:port npm run dev`.

## Screens

- **Login / signup / email verification**: `/login`, `/signup`, `/verify`
- **My account**: balance, recent transactions, loans, and actions (deposit, withdrawal, transfer, loan, foreign-currency deposit/withdrawal)
- **Admin** (only for the `ADMIN` user): customers, accounts, bankers, credit cards

## Production build

`npm run build` writes to `dist/`. If the client is served from a different origin than the server, set `VITE_API_URL` to the server's address and add CORS support on the server.
