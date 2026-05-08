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

## H2 Console (local only)

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:shoppoc`
- User: `sa`
- Password: blank

## Developer Docs

- Architecture and boundaries: [AGENTS.md](AGENTS.md)
- Local runbook: [docs/local-runbook.md](docs/local-runbook.md)
- API examples: [docs/api-examples.md](docs/api-examples.md)

## Demo Credentials (Local Profile Only)

- `user@example.com` / `Password123!` (`USER`)
- `admin@example.com` / `Admin123!` (`ADMIN`)

These are local demo credentials only. Not production secrets.
