# T-Shirt Stock Management Backend

Production-ready Spring Boot backend for T-shirt inventory, paint consumption, print-job tracking, dashboard analytics, and printable statements.
Now includes a polished browser frontend for live inventory operations.

## Stack

- Java 17
- Spring Boot 3
- Spring Data JPA
- Spring Security with JWT
- PostgreSQL
- OpenAPI / Swagger
- OpenPDF + Commons CSV
- Docker + Docker Compose

## Project Structure

```text
src/main/java/com/tshirtprinting/stockmanagement
├── config
├── controller
├── dto
├── entity
├── exception
├── frontend
├── mapper
├── repository
├── security
├── service
└── specification
```

## Features

- Product inventory with categories, variants, stock levels, pricing, and barcode field support
- Stock add, remove, adjust, and auditable transaction history
- Paint inventory with low-stock awareness and per-print-job consumption
- Print job costing with production cost, retail value, and profit estimate
- Dashboard summary with stock value, category totals, and low-stock alerts
- Report APIs for stock movement, paint usage, and print jobs
- PDF and CSV exports
- JWT authentication with `Admin` and `Staff` roles
- Validation, centralized exception handling, logging, pagination, and Swagger docs
- Soft delete behavior on products and paints

## Run Locally

### Maven

```bash
mvn spring-boot:run
```

### Docker

```bash
docker compose up --build
```

The API is published on `${BACKEND_PORT:-8080}`. PostgreSQL stays on the internal Docker network by default, which avoids the common `5432 already in use` startup error on machines with a local Postgres instance.
The frontend is published on `${FRONTEND_PORT:-3000}`.

## Default Credentials

- Admin: `admin@tshirt.local` / `Admin@123`
- Staff: `staff@tshirt.local` / `Staff@123`

## Swagger

- UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Frontend

- App: `http://localhost:3000`
- Sign in with the seeded admin credentials and the default API URL `http://localhost:8080`

## Example API Flow

See [docs/api-examples.md](/home/babisto/tshirt-printing/docs/api-examples.md).
