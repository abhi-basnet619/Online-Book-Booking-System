# Online Bookstore (Spring Boot + Thymeleaf + JWT + MySQL)

Production-style **Layered architecture** with:
- Thymeleaf UI (main)
- REST API in parallel
- Spring Security + **JWT (stateless)** with expiration
  - UI stores JWT in **HTTP-only cookie**
  - API uses `Authorization: Bearer <token>`
- MySQL + Spring Data JPA
- DTO pattern
- Global exception handling (`@ControllerAdvice`)
- Role-based authorization: `GUEST`, `USER`, `ADMIN`

## Quick Start (MySQL)

1) Create DB user (or update `application.properties`):
- `spring.datasource.username`
- `spring.datasource.password`

2) Run:
```bash
mvn spring-boot:run
```

3) Open:
- UI: `http://localhost:8080/`
- Admin: `http://localhost:8080/admin`

## Default Admin Credentials
- Email: `admin@bookstore.com`
- Password: `Admin@123`

> Change the admin password and JWT secret before any deployment.

## Key URLs

### Guest
- `/` (all books + search)
- `/books/{id}`
- `/books/deals`
- `/api/books` (GET)
- `/api/books?category=BUSINESS_INVESTING`
- `/api/books?q=clean`
- `/api/books?deals=true`

### Auth
- UI: `/auth/login`, `/auth/register`, `/auth/logout`
- API: `/api/auth/login`, `/api/auth/register`

### User (after login)
- UI: `/cart`, `/orders`
- API: `/api/cart/*`, `/api/orders/*`

### Admin
- UI: `/admin/*`
- API: `/api/admin/*`

## Stock & Order rules
- Placing an order validates item availability/stock (soft check)
- **Stock is deducted when Admin sets status to CONFIRMED**
- If stock is insufficient at confirmation time, confirmation fails

## Security Notes
- Set `app.security.jwt.secret` to a strong secret (>= 64 chars, ideally Base64)
- Set cookie `secure=true` behind HTTPS
