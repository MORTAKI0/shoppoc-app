# Local Runbook

## Prerequisites

- Java 11 JDK installed
- Maven installed
- Port `8080` available

## IntelliJ Setup

1. Open project root as Maven project.
2. Set Project SDK to Java 11.
3. Ensure Maven runner uses Java 11.

## Build

```bash
mvn clean install
```

## Run Application

```bash
mvn spring-boot:run -pl shoppoc-app -Dspring-boot.run.profiles=local
```

## Verify Health Endpoint

```bash
curl http://localhost:8080/actuator/health
```

Expected:

```json
{"status":"UP"}
```

## H2 Console (local profile)

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:shoppoc`
- User: `sa`
- Password: blank

## Troubleshooting

- `invalid target release` or `class file has wrong version`:
  - active Java not 11.
  - switch `JAVA_HOME` to Java 11 and retry build.
- app does not start on `8080`:
  - stop process using port or change `server.port` in local config.
- actuator endpoint not reachable:
  - verify app started from `shoppoc-app` module with `local` profile.
