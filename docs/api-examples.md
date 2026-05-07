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

## Protected Endpoint with Basic Auth

```bash
curl.exe -i http://localhost:8080/api/v1/users/me -u user@example.com:Password123!
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
- Demo credentials only for local verification.
- Seeded products are local demo data only.
- No real credentials/secrets used.
