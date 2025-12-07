# Vaaskel -- Vaadin Walking Skeleton

**Vaaskel** is a minimal but fully functional **Vaadin 24 + Spring
Boot** application designed as a *walking skeleton* --- a complete
end-to-end architecture with clean layers, security, database
integration, and Docker support.\
It provides a solid foundation for building larger enterprise
applications.

------------------------------------------------------------------------

## 🚀 Features

-   Vaadin 24 UI (SSR/SPA)
-   Spring Boot backend
-   Layered architecture (API, Domain, Repository, Service, Security,
    UI)
-   User authentication + role model
-   PostgreSQL persistence
-   Docker & docker-compose setup
-   Production-ready Vaadin build pipeline
-   Clean, maintainable package structure

------------------------------------------------------------------------

## 🧱 Architecture Overview

    com.vaaskel
     ├── api/          → DTOs (UI/REST boundary)
     ├── domain/       → Entities & domain objects
     ├── repository/   → Spring Data repositories
     ├── service/      → Business logic layer
     ├── security/     → Auth + authorization
     └── ui/           → Vaadin views, layouts, components

The project embraces a traditional, proven architecture that scales well
for real business systems.

------------------------------------------------------------------------

## 🐳 Docker Setup

Vaaskel ships with a docker-compose environment supporting:

-   **app** -- Spring Boot + Vaadin container\
-   **postgres** -- database\
-   **nginx (optional)** -- reverse proxy for production

Run everything:

``` bash
docker compose up --build -d
```

------------------------------------------------------------------------

## ⚙️ Development

Start the app in development mode:

``` bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**Dev mode includes:**

-   Vaadin hot reload\
-   Development DB\
-   Verbose logging\
-   No UI minification

------------------------------------------------------------------------

## 🏭 Production Build

Vaadin requires optimized frontend compilation for production.

Build production JAR:

``` bash
mvn clean package -Pproduction
```

This runs:

-   Vaadin frontend build\
-   CSS/JS minification\
-   Tree shaking\
-   Packaging into a single runnable JAR

Run:

``` bash
java -jar target/vaaskel-*.jar
```

------------------------------------------------------------------------

## 🐳 Docker Production Build

Multi-stage Dockerfile recommended:

``` bash
docker compose up --build -d
```

-   Stage 1: Maven + JDK → builds Vaadin production JAR\
-   Stage 2: Slim JRE → runs the final artifact

------------------------------------------------------------------------

## 🔐 Security

Included:

-   Login view\
-   User & role entities\
-   UserRepository + UserRoleRepository\
-   Custom UserDetailsService\
-   Access control via annotations\
-   UI navigation guard

Production-ready authentication pipeline.

------------------------------------------------------------------------

## Branching Strategy (GitHub Flow)

Vaaskel uses a lightweight GitHub Flow model:

- `main` is the only long-lived branch.  
  It always contains a stable and releasable state of the application.

- All development happens in short-lived branches created from `main`:
    - `feature/<description>` for new functionality
    - `fix/<description>` for bug fixes
    - `chore/<description>` for maintenance or cleanup

- Each change is merged back into `main` via Pull Request.  
  Feature branches are deleted after merging.

- Releases are created from `main` using annotated Git tags  
  (`v1.0.0`, `v1.1.0`, …).  
  Deployments should reference these tags.

This strategy keeps the repository simple, predictable, and fully compatible with standard GitHub tooling and CI workflows.

------------------------------------------------------------------------

## 🛣 Roadmap

-   REST API module\
-   Actuator endpoints\
-   Role-based admin console\
-   Internationalization\
-   Modularization\
-   Extended domain packages

------------------------------------------------------------------------

## 📄 License

MIT License. You can use, modify, and distribute freely.
