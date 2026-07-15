# URL Shortener

A small Spring Boot service that creates compact links and redirects them to their original URLs. Mappings are stored in PostgreSQL and created through a minimal HTTP API.

## Requirements

- Java 17
- Docker and Docker Compose

## Run the application

Start PostgreSQL:

```bash
docker compose up -d postgres
```

Start the service:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

The service listens on `http://localhost:8081`. Flyway creates the database schema on startup.

## API

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

`customAlias` is optional. When supplied, it must contain 3 to 30 URL-safe characters: letters, numbers, hyphens, or underscores. A requested alias that is already in use returns `409 Conflict`.

### Redirect

`GET /{code}` returns `301 Moved Permanently` and sets the `Location` header to the original URL.

```bash
curl -I http://localhost:8081/docs
```

An unknown, inactive, or expired code returns `404 Not Found` (expired links return `410 Gone`).

## Validation and behavior

- Only absolute `http` and `https` URLs are accepted.
- A request for a URL that already has a mapping, without a custom alias, returns the existing mapping. This makes ordinary retries idempotent.
- A request with a custom alias always attempts to create that exact alias. It does not silently reuse a prior mapping for the same target URL.
- A mapping is persisted in PostgreSQL. The `short_code` column is unique, and the database is the final authority preventing duplicate codes.

## Short-code generation

Generated codes contain seven Base62 characters (`0-9`, `A-Z`, `a-z`) selected using `SecureRandom`. They are URL-safe and provide `62^7` possible values. Before persistence, the service checks whether a candidate is already present and generates another candidate if necessary. PostgreSQL also enforces a unique constraint on `short_code`, so two mappings cannot be stored with the same code.

## Test

Run the automated tests:

```bash
./mvnw test
```

On Windows:

```powershell
.\mvnw.cmd test
```

The suite covers URL validation, generated-code retries, idempotent duplicate requests, custom aliases, redirects, and unknown codes.

## Project structure

```text
src/main/java/.../controller   HTTP endpoints
src/main/java/.../service      business rules
src/main/java/.../repository   JPA persistence access
src/main/java/.../entity       database entities
src/main/resources/db          Flyway migrations
src/test/java                  unit and MVC tests
```

Further implementation notes are in [SUBMISSION_NOTES.md](SUBMISSION_NOTES.md).
