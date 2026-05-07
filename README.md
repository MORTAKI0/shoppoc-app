# shopPoc Modular Monolith App

shopPoc multi-module modular monolith for e-commerce use cases.

## Tech Constraints

- Java 11
- Spring Boot 2.7.18 (Spring Framework 5.3.x)
- Maven multi-module
- H2 for local and test runtime
- `javax.*` APIs where applicable (no `jakarta.*`)

## Modules

- `shoppoc-app` (only executable Spring Boot module; bootstrapping/config/global web support only)
- `shoppoc-shared` (shared primitives, ids, errors, events)
- `shoppoc-user`
- `shoppoc-catalog`
- `shoppoc-order`
- `shoppoc-payment`

Architecture note: business logic must not live in `shoppoc-app`.

## Build

```bash
mvn clean install
```

## Run (local profile)

```bash
mvn spring-boot:run -pl shoppoc-app -Dspring-boot.run.profiles=local
```

## Health Check

```bash
curl http://localhost:8080/actuator/health
```

Expected response:

```json
{"status":"UP"}
```
