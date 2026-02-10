# cURL examples (port 8069)

Base URL: `http://localhost:8069`

## 1. Get token (login)

**Admin:**
```bash
curl -s -X POST http://localhost:8069/api/public/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"admin\",\"password\":\"admin123\"}"
```

**Regular user:**
```bash
curl -s -X POST http://localhost:8069/api/public/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"user\",\"password\":\"user123\"}"
```

Save the token from the response (`"token":"eyJ..."`) and use it below as `YOUR_TOKEN`.

---

## 2. Public (no token)

```bash
curl -s http://localhost:8069/api/public/health
```

---

## 3. Protected – current user (any authenticated user)

```bash
curl -s http://localhost:8069/api/user/me \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 4. Admin only – list all users

```bash
curl -s http://localhost:8069/api/admin/users \
  -H "Authorization: Bearer YOUR_TOKEN"
```

(Use the **admin** token; **user** will get 403.)

---

## One-liner: login and then call /api/user/me

```bash
TOKEN=$(curl -s -X POST http://localhost:8069/api/public/auth/login -H "Content-Type: application/json" -d '{"username":"admin","password":"admin123"}' | jq -r .token)
curl -s http://localhost:8069/api/user/me -H "Authorization: Bearer $TOKEN"
```

(Requires `jq`. On Windows PowerShell you can use the Postman collection or do login and copy the token manually.)
