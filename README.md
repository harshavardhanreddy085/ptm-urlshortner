# 🔗 URL Shortener

> A production-ready URL Shortener built with **Java 17**, **Spring Boot 3**, **PostgreSQL**, and **Flyway**, following **Clean Architecture**, **SOLID principles**, and modern backend engineering practices.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/SpringBoot-3.5-success)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![Flyway](https://img.shields.io/badge/Flyway-Database_Migrations-red)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)
![Swagger](https://img.shields.io/badge/OpenAPI-3-brightgreen)
![JUnit5](https://img.shields.io/badge/JUnit5-Tested-success)

---

# 📖 Overview

URL Shortener is a RESTful backend service that converts long URLs into compact, shareable links and redirects users to their original destination using HTTP **301 Moved Permanently** redirects.

The application is designed using enterprise backend development practices including layered architecture, dependency injection, centralized exception handling, database versioning, automated testing, and clean separation of concerns.

---

# ✨ Features

- 🔗 Generate unique short URLs
- ✍️ Custom alias support
- ⏳ URL expiration
- ↪️ HTTP 301 permanent redirects
- ✅ Input validation
- 🛡 Global exception handling
- 🗄 PostgreSQL persistence
- 📜 Flyway database migrations
- 🐳 Docker Compose support
- 📖 OpenAPI / Swagger documentation
- 📝 Structured logging
- 🧪 Unit & Controller tests

---

# 🏗 High Level Architecture

```text
                    Client
                       │
                       ▼
                REST Controller
                       │
               Request Validation
                       │
                       ▼
                Service Layer
             (Business Rules)
                       │
                       ▼
          Spring Data JPA Repository
                       │
                       ▼
                 PostgreSQL
                       │
                       ▼
              Flyway Migrations
```

---

# 🛠 Tech Stack

| Technology | Purpose |
|------------|---------|
| Java 17 | Programming Language |
| Spring Boot 3 | Backend Framework |
| Spring MVC | REST API |
| Spring Data JPA | ORM |
| PostgreSQL | Database |
| Flyway | Database Versioning |
| Docker Compose | Local Development |
| Maven | Build Tool |
| Lombok | Boilerplate Reduction |
| Swagger/OpenAPI | API Documentation |
| JUnit 5 | Testing |
| Mockito | Mocking |
| MockMvc | Controller Testing |

---

# 📂 Project Structure

```text
src
├── main
│   ├── java
│   │   └── com.ptmharsha.urlshortener
│   │       ├── config
│   │       ├── controller
│   │       ├── dto
│   │       │   ├── request
│   │       │   └── response
│   │       ├── entity
│   │       ├── exception
│   │       ├── repository
│   │       ├── service
│   │       │   └── impl
│   │       ├── util
│   │       └── UrlShortenerApplication
│   │
│   └── resources
│       ├── db
│       │   └── migration
│       ├── application.yml
│       └── logback-spring.xml
│
└── test
```

---

# 🚀 API Endpoints

## Create Short URL

```
POST /api/v1/urls/shorten
```

### Request

```json
{
  "url":"https://google.com"
}
```

### Response

```json
{
  "originalUrl":"https://google.com",
  "shortCode":"AbC123X",
  "shortUrl":"http://localhost:8081/AbC123X"
}
```

---

## Create Custom Alias

```json
{
  "url":"https://google.com",
  "customAlias":"google"
}
```

---

## Redirect

```
GET /api/v1/urls/{shortCode}
```

Returns

```
301 Moved Permanently
```

---

# ⏳ URL Expiration

Example

```json
{
    "url":"https://example.com",
    "expiresAt":"2027-12-31T23:59:59"
}
```

Expired URLs return

```
410 Gone
```

---

# 🗄 Database

Schema is managed entirely through Flyway migrations.

## url_mapping

| Column | Description |
|----------|-------------|
| id | Primary Key |
| original_url | Original URL |
| short_code | Generated Short Code |
| custom_alias | Optional Alias |
| expires_at | Expiration Timestamp |
| click_count | Redirect Counter |
| active | Active Status |
| created_at | Created Timestamp |
| updated_at | Updated Timestamp |

---

# 🧩 Engineering Practices

## Clean Layered Architecture

Business logic is isolated from HTTP and persistence layers.

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
Database
```

---

## SOLID Principles

### Single Responsibility Principle

Each class has one responsibility.

Examples

- UrlController → HTTP requests
- UrlService → Business logic
- UrlMappingRepository → Persistence
- GlobalExceptionHandler → Error handling
- ShortCodeGenerator → Short code generation

---

### Open Closed Principle

The application can be extended with new functionality without modifying existing components.

Examples

- QR Code generation
- Rate limiting
- Redis caching
- Analytics

---

### Liskov Substitution Principle

Business logic depends on interfaces.

```text
UrlService
     ▲
     │
UrlServiceImpl
```

---

### Interface Segregation Principle

Interfaces remain small and focused.

Example

```
UrlService
```

instead of one large service interface.

---

### Dependency Inversion Principle

Dependencies are injected through constructors.

```java
@RequiredArgsConstructor
```

This improves testability and reduces coupling.

---

# 🎯 Design Patterns

### Repository Pattern

Separates persistence logic from business logic.

```text
Controller

↓

Service

↓

Repository
```

---

### Dependency Injection

Managed by the Spring IoC Container.

---

### Builder Pattern

Used to construct DTOs and entities.

```java
UrlMapping.builder()
```

---

### Factory Pattern

Spring Boot automatically creates and manages application beans.

---

### Template Method Pattern

Spring Data JPA provides repository implementations automatically.

---

# 🛡 Exception Handling

Centralized exception handling is implemented using

```java
@RestControllerAdvice
```

Supported responses

| Status | Meaning |
|---------|---------|
| 400 | Invalid Request |
| 404 | Resource Not Found |
| 409 | Alias Already Exists |
| 410 | URL Expired |

---

# 📝 Logging

Application logging uses **SLF4J** with **Logback**.

Example events

- Request received
- URL validation
- Short code generation
- Database persistence
- Redirect request
- Expired URL detection
- Exception handling

---

# 🧪 Testing

Testing includes

- Unit Tests
- Service Layer Tests
- Controller Tests
- Validation Tests
- Exception Handling Tests

Frameworks

- JUnit 5
- Mockito
- MockMvc

---

# 📄 Additional Documentation

For implementation details, architecture decisions, design trade-offs, and engineering rationale, see:

- 📘 [Submission Notes](./SUBMISSION_NOTES.md)

The submission notes document explains the architectural decisions, design principles, testing strategy, and future enhancements considered during development.

---

# 🐳 Running the Application

Start PostgreSQL

```bash
docker compose up -d
```

Run Spring Boot

```bash
mvn spring-boot:run
```

Application

```
http://localhost:8081
```

---

# 📖 API Documentation

Swagger UI

```
http://localhost:8081/swagger-ui/index.html
```

OpenAPI JSON

```
http://localhost:8081/v3/api-docs
```

---

# 🚀 Future Improvements

Planned enhancements

- Redis Cache
- Click Analytics Dashboard
- QR Code Generation
- Rate Limiting
- Password-Protected URLs
- Testcontainers Integration
- Prometheus Metrics
- Grafana Dashboard
- Kubernetes Deployment
- GitHub Actions CI/CD

---

# 💡 Highlights

This project demonstrates production-oriented backend engineering concepts including:

- Clean Architecture
- SOLID Principles
- Layered Design
- RESTful API Design
- Constructor-based Dependency Injection
- Global Exception Handling
- Flyway Database Versioning
- Bean Validation
- Dockerized Development Environment
- OpenAPI Documentation
- Automated Testing
- Maintainable Code Structure

---

## 👨‍💻 Author

**Banka Harsha Reddy**

Java Backend Developer | Spring Boot | PostgreSQL | Docker | REST APIs | System Design
