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
- [Certificate Generation](#-certificate-generation-windows--docker--openssl)
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

# 🔐 Certificate Generation (Windows + Docker + OpenSSL)

Certificates are stored in:

```
scripts/nginx/ssl/
```

No local OpenSSL installation is required.  
Use Docker to generate certificates.

---

## 1. Navigate to SSL directory

```powershell
cd D:\Dev\vaaskel\scripts\nginx\ssl
```

---

## 2. Generate certificate for `vaaskel.test`

```powershell
docker run --rm -v D:\Dev\vaaskel\scripts\nginx\ssl:/ssl alpine sh -c "apk add --no-cache openssl && openssl req -x509 -nodes -newkey rsa:2048 -keyout /ssl/vaaskel.test-key.pem -out /ssl/vaaskel.test.pem -days 4096 -subj '/CN=vaaskel.test'"
```

---

## 3. Generate certificate for `vaaskel.prod`

```powershell
docker run --rm -v D:\Dev\vaaskel\scripts\nginx\ssl:/ssl alpine sh -c "apk add --no-cache openssl && openssl req -x509 -nodes -newkey rsa:2048 -keyout /ssl/vaaskel.prod-key.pem -out /ssl/vaaskel.prod.pem -days 4096 -subj '/CN=vaaskel.prod'"
```

This produces:

```
vaaskel.test.pem
vaaskel.test-key.pem
vaaskel.prod.pem
vaaskel.prod-key.pem
```

Restart proxy:

```powershell
docker compose restart proxy
```

---

## 4. (Optional) Trust certificates on Windows

To avoid browser warnings:

1. Rename `.pem` → `.crt`
2. Double-click  
3. Install certificate  
4. Choose **Local Machine**
5. Store under **Trusted Root Certification Authorities**
6. Restart browser

---

# ⚙️ Development

Run locally:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Features:

- Vaadin hot reload  
- Dev DB  
- Verbose logging  

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
