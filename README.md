# Apartment Community Issue Tracker — Backend

Java 17 · Spring Boot 3 · Spring Security (stateless JWT) · Spring Data JPA · WebSocket/STOMP · MySQL

REST API for residents, admins and technicians, plus a live community chat.
The React frontend lives in its own repository (`apartment-tracker-frontend`).

## Folder layout
```
backend/
├── database/schema.sql          MySQL tables + optional demo data
├── pom.xml
└── src/main/java/com/apartment/tracker/
    ├── config/        SecurityConfig, WebSocketConfig, DataSeeder
    ├── security/      JwtService, JwtAuthFilter, StompAuthInterceptor
    ├── controller/    Auth, Resident, Admin, Technician, Chat, ApiExceptionHandler
    ├── service/       UserService, ComplaintService, ChatService, CustomUserDetailsService
    ├── repository/    Spring Data JPA repositories
    ├── model/         User, Complaint, ChatMessage + enums
    └── dto/           Dtos (request/response shapes)
```

## Run
1. Start MySQL on your computer (port 3306).
2. Set your MySQL password (the database, tables and demo accounts are created automatically on first start):
   ```powershell
   $env:DB_PASSWORD="your-mysql-password"
   mvn spring-boot:run
   ```
   Defaults: `jdbc:mysql://localhost:3306/apartment_tracker`, user `root`. Override with `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.
   Optional: `JWT_SECRET` (long random string), `CORS_ORIGINS` (allowed frontend origins).
3. The API runs on **http://localhost:8080**.

To create the tables by hand instead, run `database/schema.sql` in MySQL Workbench.

## Demo accounts (password: `password123`)
| Role | Email |
|---|---|
| Admin | admin@apartment.com |
| Electrician | tech.electric@apartment.com |
| Plumber | tech.plumber@apartment.com |
| Resident | resident1@apartment.com |

New residents can register but cannot sign in until an admin approves them.

## API summary
| Method | Path | Who |
|---|---|---|
| POST | `/api/auth/login`, `/api/auth/register` | public |
| GET | `/api/auth/me` | any logged-in user |
| GET/POST | `/api/user/complaints` | resident |
| GET | `/api/admin/complaints`, `/residents`, `/technicians` | admin |
| POST | `/api/admin/residents/{id}/approve` | admin |
| POST | `/api/admin/complaints/{id}/assign`, `/status` | admin |
| GET | `/api/technician/complaints` | technician |
| POST | `/api/technician/complaints/{id}/status` | technician |
| GET | `/api/chat/messages` | any logged-in user |
| WebSocket | `/ws` (STOMP: `/app/chat.send`, `/topic/public`, `/topic/complaints`) | JWT checked on connect |
