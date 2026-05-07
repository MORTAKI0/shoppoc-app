

> Read this file before touching code. This is the application only — no migration pipeline code.

---

## Constraints

```yaml
java: "11" | spring_boot: "2.7.18" | imports: javax.* | db: H2
```

**Forbidden:** Java 17+ · Boot 3 / jakarta.* · Lombok · microservices · Kafka/RabbitMQ · real secrets · JPA entities outside infrastructure · business logic in controllers · cross-module access to another module's `domain`, `application`, or `infrastructure`.

---

## Modules & dependencies

```text
shoppoc-app/      # boot + wiring only
shoppoc-shared/   # value objects, errors, ids, events — no workflows
shoppoc-user/     # registration, auth, profile, roles
shoppoc-catalog/  # products, search, price, stock
shoppoc-order/    # lifecycle, totals, history
shoppoc-payment/  # auth stub, status, events
```

```text
app → all
order → shared + catalog.api + payment.api
user, catalog, payment → shared only
```

**Forbidden:** `user/catalog/payment → order` · `shared → any business module` · circular deps · any module → another's `domain.*` / `application.*` / `infrastructure.*`

---

## Internal package layout

```text
com.shoppoc.<module>/
  api/            # public: facades, DTOs, ports, events — no controllers, no JPA
  application/    # use cases, commands, queries, tx — no JPA, no cross-module private access
  domain/         # aggregates, value objects, repo interfaces — no Spring/HTTP/DB
  infrastructure/ # controllers, JPA entities, Spring Data, adapters, config
```

Cross-module import: `com.shoppoc.<module>.api.*` **only**.

---

## Coding rules

- Constructor injection only — no `@Autowired` field injection.
- Validate request DTOs with `javax.validation`.
- Controllers: `request DTO → application service → response DTO`. Nothing else.
- Map JPA entities ↔ domain objects inside infrastructure adapters only.
- Passwords hashed with BCrypt. Plain text forbidden.
- Global exception handler; never expose stack traces.
- Error shape: `{ timestamp, status, error, message, path, details }`.
- `400 / 401 / 403 / 404 / 409-422 / 500` used correctly.
- Spring Security 5 style — no Boot 3 / Security 6 patterns.

---

## Naming

`*Controller` · `*ApplicationService` · `*UseCase` · `*Command` / `*Query` · `*Request` / `*Response` · `*Event` · `*Repository` (domain iface) · `SpringData*Repository` · `Jpa*RepositoryAdapter` · `Jpa*Entity` · `*Id`

---

## REST — base `/api/v1`

| Scope | Endpoints |
|---|---|
| Public | `POST /users/register` · `POST /auth/login` · `GET /products` · `GET /products/{id}` · `GET /actuator/health` |
| User | `GET /users/me` · `POST /orders` · `GET /orders` · `GET /orders/{id}` · `POST /payments/authorize` |
| Admin | `POST /admin/products` · `GET /admin/orders` · `GET /payments/{id}` |

---

## Domain events (in-process only)

`ApplicationEventPublisher` · no Kafka/RabbitMQ · no outbox · events must not expose JPA entities.

Required: `UserRegisteredEvent` · `ProductCreatedEvent` · `OrderCreatedEvent` · `PaymentAuthorizedEvent` · `PaymentRejectedEvent` · `OrderPaidEvent` · `NotificationCreatedEvent`

---

## Testing

Required types: unit (domain + app services) · `@WebMvcTest` + MockMvc (controllers, security) · `@DataJpaTest` + H2 (repos) · `@SpringBootTest` (context) · ArchUnit (boundaries, no cycles, controllers never call repos directly).

Rules: Java 11 · H2 only · no external services · no secrets · never skip/disable failing tests.

Run before done: `mvn clean install`

---

## Work order

```
325 foundation       330 catalog listing   335 user profile
326 shared           331 seed data         336 payment stub
327 error handler    332 admin products    337 order creation
328 profiles         333 user registration 338 order + payment
329 arch tests       334 security + roles  339-340 order retrieval + admin
                                           341-344 notifications → acceptance
```

Branch: `feature/EGA-<n>-short-title` · Commit: `EGA-<n>: imperative summary`

---

## Definition of done

- Scope matches Linear issue · Java 11 · Boot 2.7.18 · `javax.*` · no `jakarta.*` · no Java 17
- Tests added/updated · `mvn clean install` passes · app still starts
- No boundary violations · no secrets

---

## Agent rules

**Do:** read this file first · scope to the issue · constructor injection · add tests · preserve boundaries · explicit code over clever abstractions.

**Do not:** use `jakarta.*` · use Java 17 · copy Boot 3 examples · add frameworks/modules without approval · skip or disable tests · touch unrelated modules · add migration concepts.

---

## Commands

```bash
mvn clean install
mvn spring-boot:run -pl shoppoc-app -Dspring-boot.run.profiles=local
curl http://localhost:8080/actuator/health   # expect {"status":"UP"}
```
