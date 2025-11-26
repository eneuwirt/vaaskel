# vaaskel --- Vaadin Flow Walking Skeleton

A minimal but complete **Vaadin Flow** walking skeleton. A clean
structural baseline showing how a Vaadin + Spring Boot application is
wired together --- nothing more, nothing less.

## Purpose

This project provides a technically minimal, production-ready baseline:

-   Vaadin **Flow** UI\
-   Spring Boot backend\
-   Simple navigation and starter view\
-   Basic service demonstrating backend → UI integration\
-   Clean Maven structure with Maven Wrapper\
-   Dockerfile + Docker Compose setup\
-   GitHub Actions CI pipeline (build + test)

It is intentionally small: a skeleton with all layers connected.

## Project Structure

    .
    ├─ src/main/java/...   # Vaadin Flow views + backend services
    ├─ src/main/resources/ # application.properties
    ├─ src/test/java/...   # basic sanity tests
    ├─ Dockerfile
    ├─ docker-compose.yml
    └─ pom.xml

## Getting Started

Run locally:

    ./mvnw clean spring-boot:run

Then open:

    http://localhost:8080

## Build for Production

    ./mvnw clean package -Pproduction

Artifact:

    target/vaaskel.jar

## Docker Support

### Build image

    docker build -t vaaskel:latest .

### Run with Docker Compose

    docker compose up --build

Service runs on:

    http://localhost:8080

**docker-compose.yml**

``` yaml
services:
  vaaskel:
    build: .
    container_name: vaaskel
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
```

## GitHub Actions CI Pipeline

Runs on every push: Maven build + tests.

**.github/workflows/ci.yml**

``` yaml
name: CI

on:
  push:
    branches: [ "main" ]
  pull_request:

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Build with Maven
        run: ./mvnw -B verify
```

## Requirements

-   Java **21+**\
-   Maven Wrapper (included)\
-   No Node.js required (Vaadin Flow uses the built-in toolchain)

## License

MIT License.\
Free to use, adapt, extend.
