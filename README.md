# Web Quiz Engine (Kotlin + Spring Boot)

REST API for creating and solving quizzes.  
Supports quiz creation, retrieval, and answer validation.

## Stack

Kotlin · Spring Boot · Spring Data JPA · H2 · Gradle

## Architecture

Layered design (Controller → Service → Repository) with interchangeable repository implementations (in-memory and JPA via Spring profiles).

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

## Notes

- Under development as part of an ongoing Hyperskill course.
- Implemented as a submodule inside a larger Hyperskill project, so the file structure is constrained and differs from standard Gradle layouts.
- Some files (e.g. `root-build-backup.txt`) are intentionally kept to restore the original root `build.gradle` required by Hyperskill.