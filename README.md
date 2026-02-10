# Spring Boot Security Starter - Take-Home Assessment

A production-ready, reusable Spring Boot starter library for JWT-based authentication and authorization, demonstrating clean architecture and modular design principles.

## 📋 Table of Contents

- [Architecture Overview](#architecture-overview)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Building the Project](#building-the-project)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Design Decisions](#design-decisions)
- [Security Features](#security-features)

## 🏗 Architecture Overview

This project demonstrates a **multi-module Maven architecture** with clear separation of concerns:

### Module 1: `core-security-starter`
A reusable Spring Boot starter library that provides:
- JWT token generation and validation
- BCrypt password hashing
- Role-based authorization
- Global exception handling (401, 403)
- Request logging for authenticated endpoints
- Auto-configuration via Spring Boot mechanisms

### Module 2: `sample-application`
A demo application that consumes the starter library, showcasing:
- Three endpoint types (public, authenticated, admin-only)
- Clean separation - no cross-cutting concerns in application code
- Integration tests demonstrating security functionality

## 📁 Project Structure

```
spring-security-assessment/
├── core-security-starter/          # Reusable security library
│   ├── src/main/java/
│   │   └── com/assessment/security/
│   │       ├── config/             # Auto-configuration
│   │       ├── filter/             # JWT authentication filter
│   │       ├── util/               # JWT utilities
│   │       ├── dto/                # Request/response DTOs
│   │       ├── exception/          # Exception handling
│   │       └── properties/         # Configuration properties
│   └── src/main/resources/
│       └── META-INF/
│           └── spring.factories    # Auto-configuration registration
│
└── sample-application/             # Demo application
    ├── src/main/java/
    │   └── com/assessment/demo/
    │       ├── controller/         # REST endpoints
    │       ├── service/            # Business logic
    │       ├── repository/         # Data access
    │       └── model/              # Domain entities
    └── src/test/java/              # Integration tests
```

## ✅ Prerequisites

- **Java 21** (JDK 21)
- **Maven 3.8+**
- No additional setup required - H2 in-memory database is used

## 🔨 Building the Project

Navigate to the project root and build both modules:

```bash
cd spring-security-assessment
mvn clean install
```

This will:
1. Build the `core-security-starter` library
2. Install it to your local Maven repository
3. Build the `sample-application` using the starter
4. Run all tests

## 🚀 Running the Application

### Option 1: Using Maven

```bash
cd sample-application
mvn spring-boot:run
```

### Option 2: Using Java

```bash
cd sample-application
java -jar target/sample-application-1.0.0.jar
```

The application will start on **http://localhost:8080**

### Default Test Users

The application initializes with two test users:

| Username | Password  | Role        |
|----------|-----------|-------------|
| admin    | admin123  | ROLE_ADMIN  |
| user     | user123   | ROLE_USER   |

## 📡 API Documentation

### 1. Public Health Check

No authentication required.

**Request:**
```bash
curl -X GET http://localhost:8080/api/public/health
```

**Response:**
```json
{
  "status": "UP",
  "message": "Application is running"
}
```

---

### 2. Login (Authentication)

Authenticates user and returns JWT token.

**Request:**
```bash
curl -X POST http://localhost:8080/api/public/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user",
    "password": "user123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "username": "user",
  "roles": ["ROLE_USER"],
  "expiresIn": 1707955200000
}
```

---

### 3. Get Current User Info

Requires authentication (any valid user).

**Request:**
```bash
# First, get token from login
TOKEN="your-jwt-token-here"

curl -X GET http://localhost:8080/api/user/me \
  -H "Authorization: Bearer $TOKEN"
```

**Response:**
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "username": "user",
  "role": "ROLE_USER"
}
```

**Error (without token):**
```bash
curl -X GET http://localhost:8080/api/user/me
```

```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/user/me",
  "timestamp": "2025-02-10T12:30:45"
}
```

---

### 4. Get All Users (Admin Only)

Requires `ROLE_ADMIN` authority.

**Request:**
```bash
# Login as admin first
curl -X POST http://localhost:8080/api/public/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'

# Use the admin token
ADMIN_TOKEN="admin-jwt-token-here"

curl -X GET http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

**Response:**
```json
{
  "total": 2,
  "users": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "username": "admin",
      "role": "ROLE_ADMIN"
    },
    {
      "id": "650e8400-e29b-41d4-a716-446655440001",
      "username": "user",
      "role": "ROLE_USER"
    }
  ]
}
```

**Error (regular user attempting admin endpoint):**
```bash
# Using regular user token
curl -X GET http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer $USER_TOKEN"
```

```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "You do not have permission to access this resource",
  "path": "/api/admin/users",
  "timestamp": "2025-02-10T12:35:20"
}
```

## 🧪 Testing

### Run All Tests

```bash
mvn test
```

### Integration Test Coverage

The `SecurityIntegrationTest` class covers:

- ✅ Public endpoint access (no auth)
- ✅ Successful login with valid credentials
- ✅ Login failure with invalid credentials
- ✅ Authenticated endpoint without token (401)
- ✅ Authenticated endpoint with valid token
- ✅ Admin endpoint with regular user (403)
- ✅ Admin endpoint with admin user
- ✅ Admin endpoint without authentication

### Manual Testing with Postman

Import the following endpoints:

1. **Health Check**: `GET http://localhost:8080/api/public/health`
2. **Login**: `POST http://localhost:8080/api/public/auth/login`
3. **Current User**: `GET http://localhost:8080/api/user/me` (add Bearer token)
4. **All Users**: `GET http://localhost:8080/api/admin/users` (add admin Bearer token)

## 🎯 Design Decisions

### 1. **Modular Architecture**

**Decision:** Split into library (`core-security-starter`) and application (`sample-application`)

**Rationale:**
- Enables reusability across multiple projects
- Clear separation of concerns
- Library can be versioned and distributed independently
- Application code remains clean and focused on business logic

### 2. **Spring Boot Auto-Configuration**

**Decision:** Use `spring.factories` and `@AutoConfiguration` for automatic setup

**Rationale:**
- Zero-configuration approach for consuming applications
- Follows Spring Boot best practices
- Seamless integration - just add the dependency
- Configuration properties allow customization without code changes

### 3. **JWT over Session-Based Authentication**

**Decision:** Stateless JWT tokens instead of server-side sessions

**Rationale:**
- Scalability: No server-side session storage required
- Microservices-ready: Tokens can be validated independently
- Mobile-friendly: Simpler token management
- RESTful: Aligns with stateless API design

**Trade-offs:**
- Cannot invalidate tokens before expiry (unless implementing token blacklist)
- Slightly larger request size due to token in headers
- Token refresh logic needed for long-lived sessions

### 4. **BCrypt Password Hashing**

**Decision:** Use BCrypt with Spring Security's default strength (10 rounds)

**Rationale:**
- Industry-standard, adaptive hashing algorithm
- Built-in salt generation
- Resistant to rainbow table attacks
- Configurable work factor for future security needs

### 5. **Method-Level Authorization**

**Decision:** Use `@PreAuthorize` annotations on controller methods

**Rationale:**
- Fine-grained access control
- Declarative security - easy to audit
- Flexible: Can combine multiple conditions
- Alternative to URL-based security patterns

**Trade-offs:**
- Requires method security to be enabled
- Less visible than URL-based rules in security config
- Potential for missed annotations (mitigated by tests)

### 6. **Centralized Exception Handling**

**Decision:** Global `@RestControllerAdvice` for all security exceptions

**Rationale:**
- Consistent error response format across all endpoints
- Single source of truth for error handling
- Logging in one place
- Clean controller code

### 7. **In-Memory H2 Database**

**Decision:** Use H2 for demo purposes instead of external database

**Rationale:**
- Zero setup required for reviewers
- Perfect for demonstrations and testing
- Easy to swap for production database (PostgreSQL, MySQL, etc.)
- Data initializer shows real BCrypt password hashing

### 8. **Request Logging**

**Decision:** Log authenticated requests with user info

**Rationale:**
- Audit trail for security events
- Debugging authentication issues
- Configurable via properties
- Follows security best practices

**Trade-offs:**
- Slight performance overhead (minimal)
- Log volume increases with traffic
- PII considerations in logs (can be disabled)

### 9. **Simple DTO Structure**

**Decision:** Plain POJOs without Lombok in DTOs (kept in library)

**Rationale:**
- Explicit code - easier to understand
- No annotation magic for reviewers
- Better IDE support
- Lombok kept optional in dependencies

### 10. **Integration Over Unit Tests**

**Decision:** Focus on integration tests with `MockMvc`

**Rationale:**
- Tests the entire security flow end-to-end
- Validates Spring Security configuration
- More confidence in production behavior
- Catches integration issues between modules

## 🔒 Security Features

### Authentication
- ✅ Username/password authentication
- ✅ BCrypt password hashing (10 rounds)
- ✅ JWT token generation with claims
- ✅ Token expiration (24 hours default)
- ✅ Secure token signing (HMAC-SHA256)

### Authorization
- ✅ Role-based access control (RBAC)
- ✅ Method-level security (`@PreAuthorize`)
- ✅ URL-level protection
- ✅ Public/authenticated/admin endpoint segregation

### Cross-Cutting Concerns (in starter)
- ✅ JWT authentication filter
- ✅ Global exception handling (401, 403)
- ✅ Standardized error responses
- ✅ Request logging with user context
- ✅ Externalized configuration

### Security Best Practices
- ✅ Stateless session management
- ✅ CSRF disabled (appropriate for stateless JWT)
- ✅ Secure password storage (BCrypt)
- ✅ Token validation on every request
- ✅ Minimal token payload (no sensitive data)
- ✅ Configurable secret keys (externalized)

## 🔧 Configuration

All security settings can be customized in `application.yml`:

```yaml
security:
  jwt:
    secret: your-secret-key-here          # Change in production!
    expiration: 86400000                   # 24 hours in milliseconds
    enable-request-logging: true           # Enable/disable audit logging
```

## 📝 Notes for Production

### Before deploying to production:

1. **Change JWT Secret**: Use a cryptographically strong, randomly generated secret
2. **Enable HTTPS**: JWT tokens should only be transmitted over secure connections
3. **Database**: Replace H2 with PostgreSQL, MySQL, or other production database
4. **Token Refresh**: Implement refresh token mechanism for long-lived sessions
5. **Rate Limiting**: Add rate limiting to prevent brute force attacks
6. **Token Blacklist**: Implement token revocation if needed
7. **Environment Variables**: Externalize all secrets via environment variables
8. **Monitoring**: Add metrics and alerting for failed authentication attempts

## 🎓 Learning Outcomes

This project demonstrates:

- ✅ Multi-module Maven project structure
- ✅ Spring Boot starter library development
- ✅ Spring Security configuration and customization
- ✅ JWT implementation with modern libraries (jjwt 0.12.x)
- ✅ Clean architecture principles
- ✅ Separation of concerns
- ✅ Integration testing best practices
- ✅ Production-ready code patterns
- ✅ Comprehensive documentation

## 🤝 Contributing

For questions or improvements, feel free to reach out!

## 📄 License

This is a technical assessment project. All rights reserved.

---

**Built with ☕ and Spring Boot**
#   f c m b - s p r i n g - s e c u r i t y - j w t - s t a r t e r  
 