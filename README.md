# URL Shortener

A Spring Boot service that creates compact links and redirects them to their original URLs. Mappings are persisted in PostgreSQL and managed with Flyway migrations.

```text
POST /shorten  ->  validate URL  ->  persist mapping  ->  return short code
GET /{code}    ->  look up mapping ->  301 Location: original URL
```

## Requirements

- Java 17
- Docker and Docker Compose

## Run locally

Start PostgreSQL:

```bash
docker compose up -d postgres
```

Start the application:

```bash
./mvnw spring-boot:run
```

On Windows, run:

```powershell
.\mvnw.cmd spring-boot:run
```

The application listens on `http://localhost:8081`. Flyway creates the required schema at startup.

## Design principles

- Keep the public API small and explicit.
- Put business rules in the service layer, not in the controller.
- Validate input before persistence.
- Use the database as the source of truth for uniqueness.
- Make retry behavior deterministic and observable in tests.
- Prefer one datastore and one responsibility per component.

## API

| Endpoint | Success response | Purpose |
| --- | --- | --- |
| `POST /shorten` | `201 Created` | Create a generated code or custom alias |
| `GET /{code}` | `301 Moved Permanently` | Redirect to the stored original URL |

### Create a generated short URL

`POST /shorten`

```bash
curl -X POST http://localhost:8081/shorten \
  -H "Content-Type: application/json" \
  -d '{"url":"https://example.com/a/very/long/path"}'
```

Example response (`201 Created`):

```json
{
  "originalUrl": "https://example.com/a/very/long/path",
  "shortCode": "Ab3dE9F",
  "shortUrl": "http://localhost:8081/Ab3dE9F"
}
```

### Create a custom alias

```bash
curl -X POST http://localhost:8081/shorten \
  -H "Content-Type: application/json" \
  -d '{"url":"https://example.com/docs","customAlias":"docs"}'
```

An alias is optional and must contain 3 to 30 letters, numbers, hyphens, or underscores. An alias that is already in use returns `409 Conflict`.

### Redirect

`GET /{code}` responds with **`301 Moved Permanently`** and a `Location` header pointing to the original URL. This is the core round-trip behavior of the service. Unknown codes return `404 Not Found`.

```bash
curl -I http://localhost:8081/docs
```

## Behavior and validation

- Only absolute `http` and `https` URLs are accepted.
- Repeating a request for the same URL without a custom alias returns its existing mapping. This makes normal client retries idempotent.
- Supplying an alias always requests that exact alias; it is never silently replaced by an existing mapping.
- Inactive codes return `404`; expired codes return `410 Gone`.

| Situation | Result |
| --- | --- |
| Missing or invalid URL | `400 Bad Request` |
| Requested alias is already taken | `409 Conflict` |
| Unknown code | `404 Not Found` |
| Expired code | `410 Gone` |

## Short-code strategy

Generated codes are seven-character Base62 values (`0-9`, `A-Z`, `a-z`) from `SecureRandom`, giving `62^7` URL-safe candidates. The service checks a candidate before persistence. PostgreSQL enforces a unique `short_code` constraint; if a concurrent request wins the same candidate, the service retries in a new transaction with another code. The database constraint is the final guarantee that duplicate codes cannot be stored.

## SDE practices used

- Schema changes are versioned with Flyway, not edited in place after release.
- Controllers stay thin and delegate behavior to services.
- The service layer owns validation, duplicate handling, and retry logic.
- Persistence rules are enforced twice: in code and in the database.
- Tests cover the main success path and the important edge cases.
- The docs describe behavior intentionally so API consumers know what to expect.

## Tests

```bash
./mvnw test
```

On Windows:

```powershell
.\mvnw.cmd test
```

The suite contains 18 automated tests covering URL validation, duplicate requests, aliases, candidate collisions, concurrent persistence collisions, `301` redirects, and unknown codes.

## Project layout

```text
src/main/java/.../controller   HTTP endpoints
src/main/java/.../service      business rules and persistence boundary
src/main/java/.../repository   JPA access
src/main/java/.../entity       persistent model
src/main/resources/db          Flyway migrations
src/test/java                  unit and MVC tests
```

See [SUBMISSION_NOTES.md](SUBMISSION_NOTES.md) for the implementation decisions and trade-offs.
