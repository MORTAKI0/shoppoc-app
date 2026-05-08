# Local Runbook

## Prerequisites

- Java 11 required
- Maven required
- Spring Boot 2.7.18 app
- Run commands from repository root

## Build

PowerShell:

```powershell
$env:JAVA_HOME="C:\Users\abmor\.jdks\temurin-11.0.31"
C:\Users\abmor\scoop\apps\maven\3.9.12\bin\mvn.cmd clean install
```

Generic:

```bash
mvn clean install
```

## Run Local Profile

```bash
mvn spring-boot:run -pl shoppoc-app -Dspring-boot.run.profiles=local
```

Known Maven path:

```powershell
C:\Users\abmor\scoop\apps\maven\3.9.12\bin\mvn.cmd spring-boot:run -pl shoppoc-app -Dspring-boot.run.profiles=local
```

## Health

```powershell
curl.exe -i "http://localhost:8080/actuator/health"
```

Expected:

```json
{"status":"UP"}
```

## Demo Users (Local Only)

- `user@example.com` / `Password123!` / `USER`
- `admin@example.com` / `Admin123!` / `ADMIN`

Warning: these are local demo credentials only, not production secrets.

## Local DB

- H2 console URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:shoppoc;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
- Username: `sa`
- Password: blank

Query examples:

```sql
SELECT * FROM USERS;
SELECT * FROM PRODUCTS;
SELECT * FROM ORDERS;
SELECT * FROM NOTIFICATIONS;
```

## Troubleshooting

- Port `8080` in use: stop other process or change port in local profile.
- Stale app process: terminate existing Java process before restart.
- PowerShell curl: use `curl.exe` one-line commands to avoid alias/line-continuation issues.
- Never commit `target/**` artifacts.
