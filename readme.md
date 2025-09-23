# Hosbee Readme

A multi-module Spring Boot application providing comprehensive hospital and healthcare management services.

## Project Structure

This project follows a microservices architecture with the following modules:

```
hosbee/
├── hosbee-common/          # Shared utilities and common components
├── hosbee-admin-api/       # Administrative API service (Port: 9031)
├── hosbee-admin-ui/        # Administrative user interface (Port: 9030)
├── hosbee-user-api/        # User-facing API service (Port: 9092)
├── hosbee-web-ui/          # Web user interface (Port: 80)
└── README.md
```

## Technology Stack

- **Java 17**
- **Spring Boot 3.4.3**
- **Spring Security**
- **MySQL** (Production)
- **Lombok**
- **Jackson** (JSON Processing)
- **Gradle** (Build Tool)

## Prerequisites

- Java 17 or higher
- Gradle 7.x or higher


### Run All Tests
```bash
./gradlew test
```

## Development

## License

[Add your license information here]
