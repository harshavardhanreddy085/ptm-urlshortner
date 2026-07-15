# Submission Notes

## Development approach

AI was used for initial project structure and prototyping. I made the implementation decisions, defined the API behavior and validation rules, chose the persistence model, and performed the final review. The initial broader structure included caching and analytics; I intentionally removed them from the request path to keep the submission focused on the assignment's core behavior and one required datastore.

## Scope

The service provides two public operations: `POST /shorten` creates a stored URL mapping, and `GET /{code}` responds with `301 Moved Permanently` and redirects to the original URL. It uses Spring Boot, JPA, PostgreSQL, and Flyway. Responses make the main outcomes explicit: `201` for creation, `400` for invalid input, `409` for an unavailable alias, `404` for an unknown code, and `410` for an expired mapping.

## Design principles

- Separation of concerns: controllers handle HTTP, services handle rules, repositories handle persistence.
- Explicit behavior: duplicate URLs, custom aliases, and invalid inputs all have defined outcomes.
- Database-backed invariants: uniqueness is enforced in PostgreSQL, not only in memory.
- Safe defaults: normal retries are idempotent when no custom alias is supplied.
- Minimal surface area: one datastore, a small API, and a focused code path for the core use case.

## Key decisions

### Random Base62 codes with a database invariant

Generated codes are seven-character Base62 values from `SecureRandom`. This keeps links compact and URL-safe without exposing database IDs or insertion order. The service rejects an already-used candidate before saving. PostgreSQL has a unique constraint on `short_code`; if two concurrent requests still select the same value, the losing request retries the save in a separate transaction with a new candidate. No two mappings can be persisted with the same code.

Base62 encoding a database sequence was the main alternative. It gives deterministic uniqueness, but exposes approximate creation order and requires a different persistence flow. Random values better fit the desired public behavior here.

### Intentional duplicate behavior

Requests without a custom alias are idempotent: shortening the same URL returns the existing mapping. This gives callers a stable result when they retry after a timeout. A custom alias is different: it is an explicit request for a particular code and must be unused, even if that destination URL already has another mapping. This avoids pretending an alias was created when it was not.

### PostgreSQL as the single datastore

PostgreSQL provides durable storage, indexed lookups, and the uniqueness invariant needed for codes and aliases. A cache could improve read latency at larger scale, but adds invalidation rules and operational complexity. It is intentionally out of scope for this service.

## SDE practices

- Versioned migrations instead of ad hoc schema edits.
- Automated tests for validation, duplicates, aliases, collision retries, redirects, and missing mappings.
- Clear transaction boundaries around the persistence flow.
- Defensive input validation before any write.
- Repository-backed persistence with explicit database constraints.
- Documentation that matches the runtime behavior.

## Testing and next steps

The test suite covers validation, duplicate reuse, alias behavior, generated-code retries, concurrent collision retries, redirects, and missing mappings. With another day, I would add PostgreSQL integration tests with Testcontainers, rate limiting and abuse controls, structured metrics, and a retention policy for expired mappings.
