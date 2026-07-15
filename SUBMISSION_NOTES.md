# Design Notes

## Scope and approach

The service has two public responsibilities: create a stored mapping from an original URL to a compact code, and redirect a visitor from that code to the original URL. The implementation uses Spring Boot for the HTTP layer, JPA for persistence, PostgreSQL as the datastore, and Flyway for repeatable schema migrations.

The public API intentionally stays small:

- `POST /shorten` creates or returns a short mapping.
- `GET /{code}` returns a permanent redirect.

The response and error handling make the important outcomes explicit: creation is `201`, an unavailable custom alias is `409`, invalid input is `400`, an unknown code is `404`, and an expired link is `410`.

## Key decisions and trade-offs

### Random Base62 codes with a database uniqueness constraint

Generated codes are seven-character Base62 values from `SecureRandom`. Base62 keeps links compact and URL-safe without encoding punctuation. The service checks whether a candidate already exists and retries if it does. The unique database constraint on `short_code` is the final invariant: no two mappings can be persisted with the same code.

An alternative was Base62 encoding a database sequence or record ID. That makes collisions impossible without retries, but exposes approximate insertion order and requires the identifier before the final code can be stored. Random codes better fit the small service's public-facing behavior. In a larger system, a save-time unique-constraint conflict caused by concurrent requests would be retried explicitly.

### Idempotent duplicate URL requests

For a request without a custom alias, shortening the same URL again returns the existing mapping. This is useful when a client retries after a timeout: it receives a stable result rather than a new short link each time.

Custom aliases follow a different rule. They are an explicit request for a particular code, so the alias must be unused even if the original URL already has another mapping. This prevents a caller from accidentally believing it created a requested alias when it did not.

### PostgreSQL only

PostgreSQL is the only runtime datastore. It offers durable mappings, indexes for lookups, and declarative uniqueness constraints. A cache would improve read latency at higher traffic, but it introduces invalidation behavior and another operational dependency. That complexity is not necessary for this scope.

## Testing

Tests focus on observable behavior and core business rules:

- valid and invalid URL schemes;
- generated candidate collision and retry;
- duplicate URL reuse;
- accepted and rejected aliases;
- `301` redirect behavior;
- `404` for a missing code.

## Future work

With more time, the next changes would be:

- retry persistence after a concurrent unique-constraint conflict;
- integration tests against PostgreSQL using Testcontainers;
- rate limiting and abuse protection;
- structured logs and metrics;
- an explicit retention policy for expired mappings;
- a decision on whether redirect status should stay permanent for links that can later expire or be disabled.
