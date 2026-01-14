# MiniAuthorizer

A Spring Boot REST API for card management and transaction authorization, inspired by real-world payment authorization systems. Supports user authentication (with roles), card creation, balance inquiry, and transaction processing.

## Features
- User authentication with HTTP Basic Auth (roles: USER, ADMIN)
- Card management: create cards, check balance
- Transaction authorization: process transactions with business rules
- Role-based access control
- H2 in-memory database for development/testing
- MySQL support via Docker Compose
- Custom error handling with consistent JSON responses
- Custom authentication provider for flexible login logic
- Custom authentication entry point for tailored unauthorized error responses
- Profile-based security configuration (e.g., H2 console access in test profile)
- Validation for DTOs and request bodies
- Centralized exception handling via `@ControllerAdvice`
- Unit & integration tests for controllers, services, and repositories

## Profiles & Environments

This project uses Spring profiles to separate configuration for different environments:

### Available Profiles
- **dev** (default):
  - Uses MySQL (see `application-dev.properties`)
  - Connects to the MySQL instance defined in `docker-compose.yaml`
  - Use for local development with persistent data
- **test**:
  - Uses H2 in-memory database (see `application-test.properties`)
  - Used automatically when running tests
  - Data is ephemeral and reset between test runs

### How to Select a Profile
- The active profile is set in `application.properties`:
  - `spring.profiles.active=${APP_PROFILE:dev}`
  - By default, the `dev` profile is used unless you set the `APP_PROFILE` environment variable.
- To run with a specific profile:
  - Set the environment variable: `export APP_PROFILE=test` (Linux/macOS) or `set APP_PROFILE=test` (Windows)
  - Or pass as a JVM argument: `-Dspring.profiles.active=test`

### Running in Each Environment

#### Development (MySQL)
1. Start MySQL with Docker Compose:
   ```bash
   docker-compose up -d
   ```
2. Run the application (default profile is `dev`):
   ```bash
   mvn spring-boot:run
   ```
   - Uses `application-dev.properties` for MySQL connection.

#### Testing (H2)
- Run tests (automatically uses `test` profile and H2):
  ```bash
  mvn test
  ```
- You can also run the app with H2 for local testing:
  ```bash
  export APP_PROFILE=test
  mvn spring-boot:run
  ```

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

## Error Handling
- All errors return a consistent JSON structure (`CustomError` DTO)
- Authentication failures return a custom JSON error via `CustomAuthenticationEntryPoint`
- Controller exceptions (e.g., validation, not found, already exists) handled by `ControllerExceptionHandler`

## Project Structure
- `entity/` — JPA entities (`User`, `Role`, `Card`)
- `repository/` — Spring Data repositories
- `service/` — Business logic and user details service
- `controller/` — REST controllers
- `dto/` — Data transfer objects
- `exception/` — Custom exceptions
- `config/` — Security configuration and custom authentication
- `controller/handlers/` — Centralized exception handling
- `resources/` — Properties, SQL scripts
- `test/` — Unit and integration tests

## Database
- **H2**: Used by default for tests and optionally for local development. Console at `/h2-console`.
- **MySQL**: Provided via Docker Compose (`docker-compose.yaml`). Used in `dev` profile.
- Initial users and roles are loaded from `import.sql`.

## Running the Application

### Prerequisites
- Java 21
- Maven 3.8+

### Start with H2 (test profile)
```bash
export APP_PROFILE=test
mvn spring-boot:run
```

### Start with MySQL (dev profile, default)
```bash
docker-compose up -d
mvn spring-boot:run
```

## Running with Docker Compose

You can run the full stack (MySQL + application) using Docker Compose:

1. Build the application jar:
   ```bash
   mvn clean package -DskipTests
   ```
2. Start the stack:
   ```bash
   docker-compose up --build
   ```
3. The API will be available at [http://localhost:8080](http://localhost:8080)

- MySQL data is persisted in a Docker volume (`db_data`).
- The application runs with the `dev` profile and connects to the MySQL container.
- To stop and remove containers/volumes:
   ```bash
   docker-compose down -v
   ```

## Testing
Run all tests:
```bash
mvn test
```

## Default User (see `import.sql`)
- **Maria Brown** — maria@gmail.com / (bcrypt hash in import.sql)

## Design Patterns, Architectural Decisions & Good Practices

### Design Patterns
- **DTO (Data Transfer Object):** Used for transferring data between layers
- **Service Layer Pattern:** Encapsulates business logic
- **Repository Pattern:** Abstracts data access
- **Exception Handling (Controller Advice):** Centralized error handling
- **Builder Pattern:** Used by Spring Security for building `UserDetails` objects

### Architectural Decisions
- **Layered Architecture:** Clear separation between controller, service, repository, and entity layers
- **Spring Security:** HTTP Basic authentication with role-based access control
- **Stateless REST API:** No session state is stored on the server
- **Validation:** Input validation using Jakarta Bean Validation
- **Database Agnostic:** Supports both H2 and MySQL
- **Profile-based Configuration:** Security and database settings via Spring profiles

### Good Practices
- **Password Hashing:** Passwords stored using BCrypt
- **Environment Separation:** Different properties for dev, test, and production
- **Custom Error Responses:** Consistent error structure for API clients
- **Unit & Integration Testing:** Tests for controllers, services, and repositories
- **Dependency Injection:** All dependencies injected via constructors or annotations
- **Open/Closed Principle:** Easily extendable business rules and error handling
- **Test Driven Development:** Create tests before implementation for some methods and classes

---
