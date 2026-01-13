# MiniAuthorizer

A Spring Boot REST API for card management and transaction authorization, inspired by real-world payment authorization systems. Supports user authentication (with roles), card creation, balance inquiry, and transaction processing.

## Features
- **User authentication** with HTTP Basic Auth (roles: USER, ADMIN)
- **Card management**: create cards, check balance
- **Transaction authorization**: process transactions with business rules
- **Role-based access control**
- **H2 in-memory database** for development/testing
- **MySQL support** via Docker Compose
- **Custom error handling**

## Endpoints

### Authentication
All endpoints require HTTP Basic authentication with a registered user (see `import.sql` for default users).

### Card Management
- `POST /cards` — Create a new card
- `GET /cards/{cardNumber}` — Get card balance

### Transactions
- `POST /transactions` — Authorize a transaction

## Business Rules
A transaction is authorized if:
- The card exists
- The password is correct
- The card has sufficient balance

## Project Structure
- `entity/` — JPA entities (`User`, `Role`, `Card`)
- `repository/` — Spring Data repositories
- `service/` — Business logic and user details service
- `controller/` — REST controllers
- `dto/` — Data transfer objects
- `exception/` — Custom exceptions
- `config/` — Security configuration
- `resources/` — Properties, SQL scripts
- `test/` — Unit and integration tests

## Database
- **H2**: Used by default for development and tests. Console at `/h2/console`.
- **MySQL**: Provided via Docker Compose (`docker-compose.yaml`).
- Initial users and roles are loaded from `import.sql`.

## Running the Application

### Prerequisites
- Java 21
- Maven 3.8+

### Start with H2 (default)
```bash
mvn spring-boot:run
```

### Start with MySQL (Docker Compose)
```bash
docker-compose up -d
# Then configure `application.properties` to use MySQL
```

## Testing
Run all tests:
```bash
mvn test
```

## Default Users (see `import.sql`)
- **Maria Brown** — maria@gmail.com / (bcrypt hash in import.sql)
- **Alex Green** — alex@gmail.com / (bcrypt hash in import.sql)

## Design Patterns, Architectural Decisions & Good Practices

### Design Patterns
- **DTO (Data Transfer Object):** Used for transferring data between layers (e.g., `CardDTO`, `TransactionDTO`).
- **Service Layer Pattern:** Encapsulates business logic in service classes (e.g., `CardService`).
- **Repository Pattern:** Abstracts data access via Spring Data JPA repositories (e.g., `CardRepository`, `UserRepository`).
- **Exception Handling (Controller Advice):** Centralized error handling using `@ControllerAdvice` (e.g., `ControllerExceptionHandler`).
- **Builder Pattern:** Used by Spring Security for building `UserDetails` objects.

### Architectural Decisions
- **Layered Architecture:** Clear separation between controller, service, repository, and entity layers for maintainability and testability.
- **Spring Security:** HTTP Basic authentication with role-based access control (USER, ADMIN).
- **Stateless REST API:** No session state is stored on the server; all requests are independent.
- **Validation:** Input validation using Jakarta Bean Validation annotations in DTOs.
- **Database Agnostic:** Supports both H2 (for dev/test) and MySQL (for production) with easy switching via profiles and properties.
- **Profile-based Configuration:** Security and database settings can be adjusted via Spring profiles (e.g., dev, test, prod).

### Good Practices
- **Password Hashing:** Passwords are stored using BCrypt hashing for security.
- **Environment Separation:** Different properties for dev, test, and production environments.
- **Custom Error Responses:** Consistent error structure for API clients using a custom error DTO.
- **Unit & Integration Testing:** Tests for controllers, services, and repositories to ensure reliability.
- **Dependency Injection:** All dependencies are injected via constructors or annotations for loose coupling.
- **Open/Closed Principle:** Business rules and error handling are easily extendable without modifying existing code.
- **Test Driven Development:** Create tests before implementation for some methods and classes.
