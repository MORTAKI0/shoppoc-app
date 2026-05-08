# API Examples

Base URL: `http://localhost:8080`

Demo credentials below are local-only and must never be reused outside local profile.

## 1) Health

```powershell
curl.exe -i "http://localhost:8080/actuator/health"
```

Expected: `200` and `{"status":"UP"}`.

## 2) Public catalog browse

```powershell
curl.exe -i "http://localhost:8080/api/v1/products"
```

Copy one product id into `PRODUCT_ID_HERE` for next calls.

## 3) User profile

```powershell
curl.exe -i -u "user@example.com:Password123!" "http://localhost:8080/api/v1/users/me"
```

Expected: `200`.

## 4) Login

```powershell
curl.exe -i -X POST "http://localhost:8080/api/v1/auth/login" -H "Content-Type: application/json" -d "{\"email\":\"user@example.com\",\"password\":\"Password123!\"}"
```

Expected: `200`.

## 4.1) Registration (repeatable with unique email)

```powershell
$ts = Get-Date -Format "yyyyMMddHHmmss"
$body = @{ email = "accept-$ts@example.com"; password = "Password123!" } | ConvertTo-Json -Compress
Invoke-WebRequest -Uri "http://localhost:8080/api/v1/users/register" -Method POST -ContentType "application/json" -Body $body
```

Expected: `201` and user payload without password/hash fields.

## 5) Admin create product

```powershell
curl.exe -i -X POST "http://localhost:8080/api/v1/admin/products" -u "admin@example.com:Admin123!" -H "Content-Type: application/json" -d "{\"sku\":\"SKU-MOUSE-LOCAL\",\"name\":\"Local Demo Mouse\",\"description\":\"Mouse created from admin demo flow\",\"priceAmount\":79.00,\"priceCurrency\":\"EUR\",\"stockQuantity\":40}"
```

Expected: `201`. If SKU exists, use different SKU.

## 6) Create authorized order

```powershell
curl.exe -i -X POST "http://localhost:8080/api/v1/orders" -u "user@example.com:Password123!" -H "Content-Type: application/json" -d "{\"paymentMethodToken\":\"stub-ok\",\"lines\":[{\"productId\":\"PRODUCT_ID_HERE\",\"quantity\":2}]}"
```

Expected `201` with `status=PAID`, `paymentStatus=AUTHORIZED`, total fields calculated.

## 7) Create rejected order

```powershell
curl.exe -i -X POST "http://localhost:8080/api/v1/orders" -u "user@example.com:Password123!" -H "Content-Type: application/json" -d "{\"paymentMethodToken\":\"reject\",\"lines\":[{\"productId\":\"PRODUCT_ID_HERE\",\"quantity\":1}]}"
```

Expected `201` with `status=PAYMENT_REJECTED`, `paymentStatus=REJECTED`, rejection reason present.

## 7.1) Direct payment authorize (stub-ok)

```powershell
curl.exe -i -X POST "http://localhost:8080/api/v1/payments/authorize" -u "user@example.com:Password123!" -H "Content-Type: application/json" -d "{\"amount\":99.99,\"currency\":\"EUR\",\"orderReference\":\"LOCAL-PAY-OK\",\"paymentMethodToken\":\"stub-ok\"}"
```

Expected: `201` and `status=AUTHORIZED`, `provider=LOCAL_STUB`.

## 7.2) Direct payment reject (reject token)

```powershell
curl.exe -i -X POST "http://localhost:8080/api/v1/payments/authorize" -u "user@example.com:Password123!" -H "Content-Type: application/json" -d "{\"amount\":99.99,\"currency\":\"EUR\",\"orderReference\":\"LOCAL-PAY-REJECT\",\"paymentMethodToken\":\"reject\"}"
```

Expected: `201` and `status=REJECTED` with rejection reason.

## 8) User order history

```powershell
curl.exe -i -u "user@example.com:Password123!" "http://localhost:8080/api/v1/orders"
```

Expected: `200`.

## 9) User order detail

```powershell
curl.exe -i -u "user@example.com:Password123!" "http://localhost:8080/api/v1/orders/ORDER_ID_HERE"
```

Expected: `200` for owned order.

## 10) Admin order list

```powershell
curl.exe -i -u "admin@example.com:Admin123!" "http://localhost:8080/api/v1/admin/orders"
```

Expected: `200`.

## 11) Security behavior checks

- USER calling admin orders:

```powershell
curl.exe -i -u "user@example.com:Password123!" "http://localhost:8080/api/v1/admin/orders"
```

Expected: `403`.

- Anonymous calling orders:

```powershell
curl.exe -i "http://localhost:8080/api/v1/orders"
```

Expected: `401`.

- Anonymous calling public products:

```powershell
curl.exe -i "http://localhost:8080/api/v1/products"
```

Expected: `200`.

## 12) Notification recording proof

Order payment outcomes create notification rows in local DB.

If H2 console enabled, run:

```sql
SELECT * FROM NOTIFICATIONS;
```

No email sent. No Kafka/RabbitMQ.
