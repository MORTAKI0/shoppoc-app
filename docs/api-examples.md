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

## Catalog Browsing (Public Endpoint)

List products:

```bash
curl.exe http://localhost:8080/api/v1/products
```

Get product by ID:

1. Call list endpoint and copy one product `id` from response.
2. Replace `{productId}` and call:

```bash
curl.exe http://localhost:8080/api/v1/products/{productId}
```

Expected sample local-demo SKUs:

- `SKU-LAPTOP-001`
- `SKU-HEADSET-001`
- `SKU-KEYBOARD-001`

Notes:

- Seeded products are local demo data only.
- No real credentials are required.
