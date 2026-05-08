# Final Acceptance Report — EGA-344

## 1. Summary
Final acceptance verification completed for `shopPoc` on 2026-05-08. Scope was HITL verification only. No business logic changed.

## 2. Stack Verified
- Java runtime:
  - `java -version` (default shell) showed Java 21.0.10, so acceptance commands pinned Java 11 explicitly.
  - Maven runtime pinned to Java 11: `11.0.31` (Temurin).
- Spring Boot version from root pom: `2.7.18`.
- Maven version: `3.9.12`.
- `javax` compatibility:
  - No `jakarta.*` imports found in source modules.
  - No Lombok usage in source modules.
  - No Java `record` usage in source modules.

## 3. Build Result
- Command:
  - `$env:JAVA_HOME="C:\Users\abmor\.jdks\temurin-11.0.31"`
  - `C:\Users\abmor\scoop\apps\maven\3.9.12\bin\mvn.cmd clean install`
- Result: `BUILD SUCCESS`
- Reactor summary: all 7 modules `SUCCESS`.
- Aggregate tests summary from Maven output:
  - `Tests run: 81`
  - `Failures: 0`
  - `Errors: 0`
  - `Skipped: 0`
- Build finished at: `2026-05-08T11:17:02+01:00`.

## 4. Runtime Result
- Start command:
  - `$env:JAVA_HOME="C:\Users\abmor\.jdks\temurin-11.0.31"`
  - `C:\Users\abmor\scoop\apps\maven\3.9.12\bin\mvn.cmd spring-boot:run -pl shoppoc-app -Dspring-boot.run.profiles=local`
- Active profile: `local` (from startup log).
- Port: `8080`.
- Health check:
  - `curl.exe -i "http://localhost:8080/actuator/health"`
  - HTTP `200`
  - Body: `{"status":"UP"}`

## 5. Core Flow Evidence
Evidence run timestamp: `20260508111849`.

- Registration:
  - Email: `accept-20260508111849@example.com`
  - Status: `201`
- Login:
  - Status: `200`
- Product listing:
  - Status: `200`
  - Seed SKUs present: `SKU-LAPTOP-001`, `SKU-HEADSET-001`, `SKU-KEYBOARD-001`
  - Product count at verification time: `4`
  - Selected product:
    - id: `fd60ea96-6959-4ba5-ab41-27f9dc880c51`
    - price: `1299.00 EUR`
    - stock: `10`
- Admin product creation:
  - Created SKU: `SKU-FINAL-20260508111849`
  - Status: `201`
- Direct payment authorize:
  - Status: `201`
  - payment id: `781f55c9-dc10-4e14-8977-61828a25e4e7`
  - status field: `AUTHORIZED`
  - provider: `LOCAL_STUB`
- Direct payment reject:
  - Status: `201`
  - payment id: `ddd7c52e-abac-4c04-9afc-801fb5917cc6`
  - status field: `REJECTED`
  - rejection reason present
- Order create (authorized path):
  - Status: `201`
  - order id: `e4945df3-9ee9-460b-8116-cfd9223aaab3`
  - order status: `PAID`
  - payment status: `AUTHORIZED`
  - total: `2598.00 EUR` (2 x 1299.00)
  - lines: `1`
- Order create (rejected path):
  - Status: `201`
  - order id: `6c964a0d-9bbb-4303-a7f9-310b3053973c`
  - order status: `PAYMENT_REJECTED`
  - payment status: `REJECTED`
  - rejection reason present
- Order detail:
  - `GET /api/v1/orders/e4945df3-9ee9-460b-8116-cfd9223aaab3`
  - Status: `200`
  - Status value remained `PAID`
- User order history:
  - `GET /api/v1/orders`
  - Status: `200`
  - Contains both authorized/rejected orders for current user
- Admin order list:
  - `GET /api/v1/admin/orders`
  - Status: `200`
  - Contains `id`, `customerEmail`, `totalAmount`, `totalCurrency`, `status`, `createdAt`, payment fields

## 6. Security Evidence
- Anonymous denied:
  - `GET /api/v1/orders` -> `401`
  - `GET /api/v1/admin/orders` -> `401`
- USER role denied for admin endpoint:
  - `GET /api/v1/admin/orders` as `user@example.com` -> `403`
- Public browse allowed:
  - `GET /api/v1/products` anonymous -> `200`

## 7. Notification Evidence
Runtime DB query was not automated in this acceptance pass. Notification proof validated via passing integration test evidence:
- `com.shoppoc.app.security.NotificationRecordingIntegrationTest`
- Surefire result: `Tests run: 3, Failures: 0, Errors: 0, Skipped: 0`

## 8. Architecture and Quality Gate
- `mvn clean install` passed with Java 11.
- Architecture tests passed, including:
  - `ControllerRulesTest`
  - `ModuleDependencyRulesTest`
  - `PackageBoundaryRulesTest`
  - `QualityRulesTest`
  - `SharedAndAppRulesTest`
  - `ForbiddenSourcePatternsTest`

## 9. Documentation Review
Reviewed:
- `README.md`
- `AGENTS.md`
- `docs/local-runbook.md`
- `docs/api-examples.md`

Outcome:
- Docs sufficient for local run and core flow.
- Small gap found in `docs/api-examples.md` (explicit registration + direct payment authorize/reject examples). Gap fixed in docs only.

## 10. Dirty Tree / Artifact Handling
- `target/**` and runtime logs created during verification as expected.
- No `target/**` staged.
- No runtime logs staged (`app-local.out.log`, `app-local.err.log`).

## 11. Final Decision
`PASS`

Blocking gates satisfied:
- Build success
- App start success (local profile)
- Health `UP`
- Core flows verified
- Architecture tests passing
- Docs sufficient for developer execution