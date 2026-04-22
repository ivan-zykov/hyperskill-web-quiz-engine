# Web Quiz Engine (Spring Boot, Kotlin)

This project demonstrates a RESTful backend built with Kotlin and Spring Boot, focusing on maintainable architecture and testability.
Provides REST endpoints for registering users, creating, retrieving, deleting quizzes, and validating user answers.

## Tech stack

- Kotlin
- Spring Boot
- Spring Data JPA
- Spring Security
- H2
- Gradle

## Architecture

The application follows a layered architecture separating web, business logic, and persistence concerns to ensure maintainability and testability.

## Features

- RESTful API design
- Input validation using Spring validation
- Centralized exception handling
- Separation of domain models and DTOs
- Pluggable persistence layer (in-memory / JPA via Spring profiles)
- Unit and integration tests (no mocking)
- Basic HTTP authentication
- Custom user store

## Requirements

- Java 23. This version is required by the Hyperskill course checks and project setup. Higher versions not tested
- Local Gradle. Wrapper is not included due to Hyperskill submodule structure

## Run

### Linux/Mac

```bash
./gradlew bootRun
```

### Windows

```
gradlew.bat bootRun
```

## Default URL

http://localhost:8889

## API Overview

- POST /api/register — register a new user
- POST /api/quizzes — create a quiz
- GET /api/quizzes/{id} — retrieve a quiz
- GET /api/quizzes — retrieve all quiz
- POST /api/quizzes/{id}/solve — submit an answer
- DELETE /api/quizzes/{id} - delete a quiz

## Future improvements

- Handle authentication exceptions
- Specify authorization for each /api/** endpoint explicitly
- Replace H2 with PostgreSQL for production-like persistence
- Align project structure with standard Gradle conventions

## Notes

- Under development as part of an ongoing Hyperskill course.
- Implemented as a submodule inside a larger Hyperskill project, so the file structure is constrained and differs from standard Gradle layouts.
- Some files (e.g. `root-build-backup.txt`) are intentionally kept to restore the original root `build.gradle` required by Hyperskill.
