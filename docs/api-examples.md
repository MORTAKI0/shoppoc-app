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

2. Create order with authorized payment:

```bash
curl.exe -i -X POST "http://localhost:8080/api/v1/orders" -u "user@example.com:Password123!" -H "Content-Type: application/json" -d "{\"paymentMethodToken\":\"stub-ok\",\"lines\":[{\"productId\":\"{productIdFromCatalog}\",\"quantity\":2}]}"
```

Expected:

- HTTP 201
- `status` = `PAID`
- `paymentStatus` = `AUTHORIZED`
- `totalAmount` calculated from unit price * quantity
- lines include product `sku` and `productName`

3. Create order with rejected payment:

```bash
curl.exe -i -X POST "http://localhost:8080/api/v1/orders" -u "user@example.com:Password123!" -H "Content-Type: application/json" -d "{\"paymentMethodToken\":\"reject\",\"lines\":[{\"productId\":\"{productIdFromCatalog}\",\"quantity\":1}]}"
```

Expected:

- HTTP 201
- `status` = `PAYMENT_REJECTED`
- `paymentStatus` = `REJECTED`
- `paymentRejectionReason` present

Notes:

- Payment provider is local stub only.
- No real provider calls.
- No card numbers used/stored.
- Endpoint requires USER or ADMIN auth.

## Get My Order History (Authenticated USER/ADMIN)

```bash
curl.exe -i -u "user@example.com:Password123!" "http://localhost:8080/api/v1/orders"
```

Expected:

- HTTP 200
- JSON array contains only current user orders
- Each item includes `id`, `status`, `totalAmount`, `totalCurrency`, `lines`, and payment fields

## Get Order Detail (Authenticated USER/ADMIN)

```bash
curl.exe -i -u "user@example.com:Password123!" "http://localhost:8080/api/v1/orders/{orderId}"
```

Expected:

- HTTP 200 for own order
- Body includes `id`, `status`, `totalAmount`, `totalCurrency`, `lines`, `paymentStatus`
- Cross-user access denied (`403`)
- Anonymous access denied (`401`)

Notes:

- Users can view only own orders.
- Admin all-orders view available at `GET /api/v1/admin/orders`.

## Admin List All Orders (ADMIN Role Required)

```bash
curl.exe -i -u "admin@example.com:Admin123!" "http://localhost:8080/api/v1/admin/orders"
```

Expected `200 OK` body sample:

```json
[
  {
    "id": "order-123",
    "customerEmail": "user@example.com",
    "status": "PAID",
    "totalAmount": 1299.00,
    "totalCurrency": "EUR",
    "createdAt": "2026-05-08T12:00:00Z",
    "paymentStatus": "AUTHORIZED",
    "paymentId": "pay-1",
    "paymentReference": "ref-1"
  }
]
```

- Requires `ADMIN` role.
- `USER` gets `403 Forbidden`.
- Anonymous gets `401 Unauthorized`.
- User self-service history remains `GET /api/v1/orders`.

## In-Process Notification Recording

- Creating order with `paymentMethodToken="stub-ok"` records `ORDER_PAYMENT_AUTHORIZED` notification.
- Creating order with `paymentMethodToken="reject"` records `ORDER_PAYMENT_REJECTED` notification.
- Notifications stored in local DB only.
- No email sent.
- No Kafka/RabbitMQ used.
- No real provider credentials needed.

Optional local H2 check:

- H2 console: `/h2-console`
- JDBC URL: use current local app config
- Query:

```sql
SELECT * FROM NOTIFICATIONS;
```
