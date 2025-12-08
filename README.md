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
- [GitHub Container Registry (GHCR)](#-github-container-registry-ghcr)
- [Deployment Workflow](#-deployment-workflow)
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
- Multi-environment setup (dev, int, prod)
- Docker & docker-compose ready
- Optional HTTPS reverse proxy
- Production build pipeline (GitHub Actions + GHCR)

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

- **dev** — local development  
- **int** — integration environment (`app_int`)  
- **prod** — production environment (`app_prod`)  

The application containers (`app_int`, `app_prod`) use a Docker image hosted on **GitHub Container Registry (GHCR)**:

```yaml
image: ghcr.io/${GHCR_OWNER}/vaaskel:latest
```

## `.env` configuration for GHCR

Create a `.env` file:

```env
GHCR_OWNER=eneuwirt
```

You may change this if the project is forked or used under a different namespace.

### Starting the stack

```bash
docker compose pull
docker compose up -d
```

---

# 🔐 GitHub Container Registry (GHCR)

To pull images from GHCR on Windows, create permanent environment variables:

```powershell
setx GITHUB_USERNAME "<YOUR GITHUB USERNAME>"
setx GITHUB_TOKEN "<YOUR TOKEN WITH read:packages>"
```

Then log in:

```powershell
$env:GITHUB_TOKEN | docker login ghcr.io -u $env:GITHUB_USERNAME --password-stdin
```

Required permissions for the token:

- `read:packages` → pulling images  
- `write:packages` → only needed if you push manually

GitHub Actions uses its own internal token and does not rely on your local token.

---

# 🚀 Deployment Workflow

This project follows a clean and modern deployment pipeline using **GitHub Actions → GHCR → docker-compose**.

## 1️⃣ Push to GitHub

Whenever you push to **main**, GitHub Actions automatically:

1. Builds the project (`mvn verify`)
2. Builds the Docker image
3. Pushes it to GHCR under:

```
ghcr.io/${GHCR_OWNER}/vaaskel:latest
```

## 2️⃣ Update your server / local environment

On your host machine (Windows, Linux, or a server):

```bash
docker compose pull
docker compose up -d
```

This will:

- download the newest Docker image from GHCR  
- restart only the containers whose images have changed  

## 3️⃣ Verify running containers

```bash
docker compose ps
```

## 4️⃣ View logs

```bash
docker logs app_int --follow
```

This workflow ensures:

- zero manual builds  
- reproducible deployments  
- clean separation of CI and runtime  
- secure distribution via GHCR  

---

# 🔐 HTTPS & Nginx Reverse Proxy

Domains for proxy routing:

```
https://vaaskel.test  → app_int  
https://vaaskel.prod  → app_prod
```

Configurations are in:

```
scripts/nginx/
```

---

# 🔐 Certificate Generation

Required certificates:

```
scripts/nginx/ssl/vaaskel.test.pem
scripts/nginx/ssl/vaaskel.test-key.pem
scripts/nginx/ssl/vaaskel.prod.pem
scripts/nginx/ssl/vaaskel.prod-key.pem
```

---

# ⚙️ Development

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

---

# 🏭 Production Build

```bash
mvn clean package -Pproduction
```

Run:

```bash
java -jar target/vaaskel-*.jar
```

---

# 🔐 Security

- User authentication  
- Roles & permissions  
- Password hashing  
- Navigation guards  

---

# 🧭 Branching Strategy (GitHub Flow)

- `main` → stable  
- `feature/*` → new development  
- `fix/*` → bug fixes  
- PR → merge  

---

# 🛣 Roadmap

- REST API  
- Admin console  
- Modularization  
- Cloud deployment  

---

# 📄 License

MIT License  
