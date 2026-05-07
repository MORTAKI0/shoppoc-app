# API Examples

Base URL: `http://localhost:8080`

## Start App (Local Profile)

```bash
mvn spring-boot:run -pl shoppoc-app -Dspring-boot.run.profiles=local
```

## Health Check

```bash
curl.exe http://localhost:8080/actuator/health
```

## User Registration (Public)

```bash
curl.exe -i -X POST http://localhost:8080/api/v1/users/register \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"user@example.com\",\"password\":\"Password123!\"}"
```

## Login (Public)

```bash
curl.exe -i -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"user@example.com\",\"password\":\"Password123!\"}"
```

## Catalog Browsing (Public Endpoint)

```bash
curl.exe http://localhost:8080/api/v1/products
```

```bash
curl.exe http://localhost:8080/api/v1/products/{productId}
```

## Current User Profile (Authenticated)

```bash
curl.exe -i http://localhost:8080/api/v1/users/me ^
  -u user@example.com:Password123!
```

Expected `200 OK` body:

```json
{
  "id": "f4967a97-4df0-4981-a4f7-0e19385fd43a",
  "email": "user@example.com",
  "roles": ["USER"],
  "status": "ACTIVE"
}
```

## Admin Product Creation (ADMIN Role Required)

```bash
curl.exe -i -X POST http://localhost:8080/api/v1/admin/products \
  -u admin@example.com:Admin123! \
  -H "Content-Type: application/json" \
  -d "{\"sku\":\"SKU-MOUSE-001\",\"name\":\"Wireless Mouse\",\"description\":\"Wireless mouse for daily work\",\"priceAmount\":79.00,\"priceCurrency\":\"EUR\",\"stockQuantity\":40}"
```

Notes:

- Protected endpoints use HTTP Basic auth for now.
- `GET /api/v1/users/me` requires authentication.
- Profile response never includes `password` or `passwordHash`.
- Demo credentials only for local verification.
- Seeded products are local demo data only.
- No real credentials/secrets used.

## Payment Authorization (Authenticated USER/ADMIN)

```bash
curl.exe -i -X POST "http://localhost:8080/api/v1/payments/authorize" -u "user@example.com:Password123!" -H "Content-Type: application/json" -d "{\"amount\":99.99,\"currency\":\"EUR\",\"orderReference\":\"ORDER-DEMO-001\",\"paymentMethodToken\":\"stub-ok\"}"
```

## Payment Authorization Rejected (Local Stub)

```bash
curl.exe -i -X POST "http://localhost:8080/api/v1/payments/authorize" -u "user@example.com:Password123!" -H "Content-Type: application/json" -d "{\"amount\":99.99,\"currency\":\"EUR\",\"orderReference\":\"ORDER-DEMO-002\",\"paymentMethodToken\":\"reject\"}"
```

## Get Payment Status (Authenticated USER/ADMIN)

```bash
curl.exe -i -u "user@example.com:Password123!" "http://localhost:8080/api/v1/payments/{paymentId}"
```

- Payment provider is local stub only.
- No real payment provider calls.
- No real card numbers stored or transmitted.

## Create Order (Authenticated USER/ADMIN)

1. Get product id:

```bash
curl.exe http://localhost:8080/api/v1/products
```

2. Create order:

```bash
curl.exe -i -X POST "http://localhost:8080/api/v1/orders" -u "user@example.com:Password123!" -H "Content-Type: application/json" -d "{\"lines\":[{\"productId\":\"{productIdFromCatalog}\",\"quantity\":2}]}"
```

Expected:

- HTTP 201
- `status` = `CREATED`
- `totalAmount` calculated from unit price * quantity
- lines include product `sku` and `productName`

Notes:

- Payment not connected yet in EGA-337.
- Payment connection comes in EGA-338.
- Endpoint requires USER or ADMIN auth.
