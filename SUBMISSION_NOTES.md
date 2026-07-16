# Submission Notes

## Project Objective

The goal of this project was to build a production-oriented URL Shortener using modern Java backend development practices rather than a simple CRUD application.

The implementation emphasizes clean architecture, maintainability, predictable behavior, and clear separation of concerns while keeping the solution focused on the assignment requirements.

---

# Development Approach

The project was initially scaffolded with AI assistance to accelerate setup and experimentation. The architecture, business rules, API contracts, validation strategy, persistence model, and final implementation were designed, implemented, and reviewed manually.

Additional features such as caching and analytics were explored during development but intentionally excluded from the final request path to keep the submission focused on the required functionality while preserving a clear path for future enhancements.

---

# Scope

The application exposes two primary operations:

- **POST** `/api/v1/urls/shorten`
- **GET** `/api/v1/urls/{shortCode}`

The implementation includes:

- URL shortening
- Custom aliases
- URL expiration
- HTTP 301 redirects
- PostgreSQL persistence
- Flyway migrations
- Global exception handling
- Validation
- Swagger/OpenAPI documentation
- Docker Compose support
- Automated tests

---

# Architecture

The application follows a layered architecture.

```text
Controller
      │
      ▼
Service
      │
      ▼
Repository
      │
      ▼
PostgreSQL
```

Each layer has a single responsibility.

Business logic remains independent of HTTP and persistence concerns.

---

# Design Principles

## Separation of Concerns

- Controllers manage HTTP communication.
- Services contain business rules.
- Repositories encapsulate persistence.
- DTOs isolate API contracts.
- Entities represent the persistence model.

---

## Database as the Source of Truth

Application-level validation improves usability, while PostgreSQL guarantees data integrity through constraints and transactions.

---

## Idempotent URL Creation

Repeated requests for the same URL return the existing mapping when no custom alias is supplied.

This makes client retries deterministic without creating duplicate records.

---

## Explicit Alias Behavior

A custom alias is treated as an explicit request for a specific short code.

If the alias already exists, the service returns **409 Conflict** rather than silently replacing or reusing another mapping.

---

# Design Decisions

## Random Base62 Short Codes

Short codes are generated using SecureRandom over the Base62 character set.

Advantages:

- URL-safe
- Compact
- Non-sequential
- Does not expose database identifiers

Uniqueness is ultimately guaranteed by a database constraint.

---

## Flyway Migrations

Database schema evolution is managed using Flyway instead of automatic schema generation.

This provides:

- Versioned schema history
- Repeatable deployments
- Controlled database evolution

---

## Layered Architecture

Business logic remains isolated from transport and persistence concerns, improving maintainability and testability.

---

# SOLID Principles

The implementation follows the SOLID principles.

### Single Responsibility Principle

Each class has one clearly defined responsibility.

Examples:

- UrlController
- UrlService
- UrlMappingRepository
- GlobalExceptionHandler
- ShortCodeGenerator

---

### Open/Closed Principle

The application is designed so future features can be added with minimal modification to existing code.

Examples include:

- Redis caching
- Analytics
- Rate limiting
- QR code generation

---

### Liskov Substitution Principle

Business logic depends on interfaces rather than concrete implementations.

---

### Interface Segregation Principle

Interfaces remain small and focused.

---

### Dependency Inversion Principle

Dependencies are injected through constructors using Spring Dependency Injection.

---

# Design Patterns

The project uses several common enterprise patterns.

- Repository Pattern
- Dependency Injection
- Builder Pattern
- Factory Pattern
- Template Method Pattern

---

# Testing

Automated tests cover:

- Request validation
- URL shortening
- Duplicate requests
- Custom aliases
- Redirect behavior
- Exception handling
- Controller layer
- Service layer

---

# Trade-offs

Several implementation decisions were made intentionally.

- PostgreSQL is the only datastore.
- Flyway manages all schema evolution.
- Duplicate URL requests are idempotent.
- Custom aliases are explicit requests.
- The API surface remains intentionally small.

These choices prioritize correctness, maintainability, and simplicity.

---

# Future Improvements

Potential future enhancements include:

- Redis caching
- Click analytics
- Rate limiting
- Testcontainers integration
- Prometheus metrics
- Grafana dashboards
- GitHub Actions CI/CD
- Kubernetes deployment

---

# Conclusion

The project demonstrates modern backend engineering practices including layered architecture, SOLID principles, RESTful API design, automated testing, database versioning, and production-oriented project organization.

The implementation prioritizes correctness, maintainability, and extensibility while keeping the solution focused on the assignment requirements.
