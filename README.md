# Vaaskel — Vaadin Walking Skeleton

![Java](https://img.shields.io/badge/Java-25-007396?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot)
![Vaadin](https://img.shields.io/badge/Vaadin-24-blue?logo=vaadin)
![Docker](https://img.shields.io/badge/Docker-enabled-2496ED?logo=docker)
![License](https://img.shields.io/badge/License-MIT-green)

A minimal but fully functional **Vaadin 24 + Spring Boot** application designed as a **walking skeleton**.

This repository provides a **complete, production-grade end-to-end architecture**: UI, security, persistence, infrastructure, CI/CD and deployment — intentionally reduced to the smallest meaningful scope.

This is **not** a prototype and **not** a demo. It is a **finished architectural baseline**.

---

# 📚 Table of Contents
- [Features](#-features)
- [Architecture Overview](#-architecture-overview)
- [Docker & Environments](#-docker--environments)
- [GitHub Container Registry (GHCR)](#-github-container-registry-ghcr)
- [Deployment Workflow](#-deployment-workflow)
- [Development](#️-development)
- [Production Build](#-production-build)
- [Security](#-security)
- [Walking Skeleton Status](#-walking-skeleton-status)
- [Reference End-to-End Flow](#-reference-end-to-end-flow)
- [Explicit Non-Goals](#-explicit-non-goals)
- [Next Phase](#-next-phase)
- [Branching Strategy](#-branching-strategy-github-flow)
- [License](#-license)

---

# 🚀 Features

- Vaadin 24 UI (SSR/SPA)
- Spring Boot backend
- Layered, maintainable architecture
- Authentication & authorization
- PostgreSQL persistence
- Flyway database migrations
- Multi-environment setup (dev / int / prod)
- Docker & docker-compose
- CI/CD pipeline using GitHub Actions and GHCR

---

# 🧱 Architecture Overview

```
com.vaaskel
 ├── api/          → DTOs & boundary objects
 ├── domain/       → Entities & domain logic
 ├── repository/   → Spring Data repositories
 ├── service/      → Business logic
 ├── security/     → Authentication & authorization
 └── ui/           → Vaadin views, layouts, components
```

The structure follows classic layered architecture principles with a clear separation of concerns.

---

# 🐳 Docker & Environments

Vaaskel defines three runtime environments:

- **dev**  — local development
- **int**  — integration environment (`app_int`)
- **prod** — production environment (`app_prod`)

Application containers (`app_int`, `app_prod`) use images published to **GitHub Container Registry (GHCR)**:

```yaml
image: ghcr.io/${GHCR_OWNER}/vaaskel:latest
```

### `.env` configuration

Create a `.env` file:

```env
GHCR_OWNER=<GHCR_OWNER>
```

Adjust this value if the project is forked or hosted under a different namespace.

### Starting the stack

```bash
docker compose pull
docker compose up -d
```

### Runtime endpoints

The walking skeleton exposes the application containers directly:

```text
http://localhost:8081  -> app_int
http://localhost:8082  -> app_prod
```

---

# 🔐 GitHub Container Registry (GHCR)

To pull images from GHCR on Windows, configure credentials:

```powershell
setx GITHUB_USERNAME "<YOUR GITHUB USERNAME>"
setx GITHUB_TOKEN "<YOUR TOKEN WITH read:packages>"
```

Login:

```powershell
$env:GITHUB_TOKEN | docker login ghcr.io -u $env:GITHUB_USERNAME --password-stdin
```

Required token scopes:

- `read:packages` — pulling images
- `write:packages` — only required for manual pushes

GitHub Actions uses its internal token automatically.

---

# 🚀 Deployment Workflow

The deployment pipeline is intentionally simple and reproducible:

## 1️⃣ Push to GitHub

Whenever you push to **main**, GitHub Actions automatically:

1. Builds the project (`mvn verify`)
2. Builds the Docker image
3. Pushes it to GHCR under:

```
ghcr.io/${GHCR_OWNER}/vaaskel:latest
```

## 2️⃣ Update runtime environment

On your host machine (Windows, Linux, or a server):

```bash
docker compose pull
docker compose up -d
```

This will:

- download the newest Docker image from GHCR
- restart only the containers whose images have changed

## 3️⃣ Verify containers

```bash
docker compose ps
```

## 4️⃣ Inspect logs

```bash
docker logs app_int --follow
```

This ensures clean separation between build and runtime, with zero manual image handling.

---

# ⚙️ Development

Run locally using the dev profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

---

# 🏭 Production Build

```bash
mvn clean package -Pproduction
```

Run the artifact:

```bash
java -jar target/vaaskel-*.jar
```

---

# 🔐 Security

- Form-based authentication
- Role-based authorization
- Password hashing
- Navigation guards
- Secure defaults

---

# ✅ Walking Skeleton Status

**Status:** ✔ Completed

This repository intentionally represents a **finished walking skeleton**.

It proves the technical viability of the system by providing a **minimal but complete end-to-end path** across all layers:

- UI
- Security
- Business services
- Persistence
- Infrastructure

No additional infrastructure work is required to start feature development.

---

# 🔁 Reference End-to-End Flow

The canonical skeleton flow:

1. Application startup via Docker
2. User authentication
3. Role-based authorization
4. Domain interaction (e.g. user settings)
5. Persistence to PostgreSQL
6. Reload and verification

This flow serves as the **baseline contract** for all future development.

---

# 🚧 Explicit Non-Goals

The following topics are intentionally **out of scope** for the walking skeleton:

- Business-specific features
- Complex UI workflows
- Public REST APIs
- Performance tuning
- Horizontal scaling
- Cloud-specific infrastructure

These belong to later feature phases.

---

# 🧭 Next Phase

With the walking skeleton completed, the project is ready for:

- Vertical feature slices
- Domain-driven extensions
- UI refinement
- API exposure
- Modularization

All future work builds on a stable, proven foundation.

---

# 🧭 Branching Strategy (GitHub Flow)

- `main`       → stable baseline
- `feature/*`  → new development
- `fix/*`      → bug fixes
- Pull Request → merge into `main`

---

# 📄 License

MIT License
