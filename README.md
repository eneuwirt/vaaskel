# Vaaskel — Vaadin Walking Skeleton

![Java](https://img.shields.io/badge/Java-25-007396?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot)
![Vaadin](https://img.shields.io/badge/Vaadin-24-blue?logo=vaadin)
![Docker](https://img.shields.io/badge/Docker-enabled-2496ED?logo=docker)
![License](https://img.shields.io/badge/License-MIT-green)

A minimal but fully functional **Vaadin 24 + Spring Boot** application designed as a *walking skeleton*: a complete end-to-end architecture with a clean domain model, security, PostgreSQL persistence, Docker-based environments, and optional Nginx reverse proxy with HTTPS.

---

# 📚 Table of Contents
- [Features](#-features)
- [Architecture Overview](#-architecture-overview)
- [Docker & Environments](#-docker--environments)
- [HTTPS & Nginx Reverse Proxy](#-https--nginx-reverse-proxy)
- [Certificate Generation](#-certificate-generation)
- [Development](#️-development)
- [Production Build](#-production-build)
- [Security](#-security)
- [Branching Strategy](#-branching-strategy-github-flow)
- [Roadmap](#-roadmap)
- [License](#-license)

---

# 🚀 Features

- Vaadin 24 UI (SSR/SPA)
- Spring Boot backend
- Layered, maintainable architecture
- Authentication & authorization
- PostgreSQL + Flyway migration
- Multi-environment setup (dev, test, prod)
- Docker & docker-compose ready
- Optional HTTPS reverse proxy
- Production build pipeline

---

# 🧱 Architecture Overview

```
com.vaaskel
 ├── api/          → DTOs & boundary objects
 ├── domain/       → Entities & domain logic
 ├── repository/   → Spring Data repositories
 ├── service/      → Business logic
 ├── security/     → Auth + authorization
 └── ui/           → Vaadin views, layouts, components
```

---

# 🐳 Docker & Environments

Vaaskel includes three environments:

- **dev** — local development, no proxy  
- **test** — Docker integration environment (`vaaskel.test`)  
- **prod** — Docker production environment (`vaaskel.prod`)

### docker-compose services

- `app_int` (test)
- `app_prod` (production)
- `postgres`
- `proxy` (optional Nginx reverse proxy)

Start all services:

```bash
docker compose up --build -d
```

---

# 🔐 HTTPS & Nginx Reverse Proxy

Domains used locally:

```
https://vaaskel.test → app_int:8080  
https://vaaskel.prod → app_prod:8080
```

Nginx handles:

- TLS termination  
- Domain-based routing  
- Clean separation of front-facing and internal services  

Configuration lives under:

```
scripts/nginx/default.conf
scripts/nginx/includes/
scripts/nginx/ssl/
```

---

# 🔐 Certificate Generation

Two self-signed certificates are required:

```
scripts/nginx/ssl/vaaskel.test.pem
scripts/nginx/ssl/vaaskel.test-key.pem
scripts/nginx/ssl/vaaskel.prod.pem
scripts/nginx/ssl/vaaskel.prod-key.pem
```

---

# ⚙️ Development

Run locally:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

---

# 🏭 Production Build

Build optimized artifact:

```bash
mvn clean package -Pproduction
```

Run:

```bash
java -jar target/vaaskel-*.jar
```

---

# 🔐 Security

Includes:

- Login view  
- User + role entities  
- Custom UserDetailsService  
- Password hashing  
- Role-based access control  
- UI navigation guard  

---

# 🧭 Branching Strategy (GitHub Flow)

- `main` → always stable  
- `feature/*` → new features  
- `fix/*` → bug fixes  
- `chore/*` → maintenance  
- PR merging  
- Releases via Git tags (`v1.0.0` etc.)

---

# 🛣 Roadmap

- REST API  
- Admin console  
- Internationalization  
- Modularity  
- Extended domain model  
- Cloud deployment guides  

---

# 📄 License

MIT License — free to use, modify, and distribute.
