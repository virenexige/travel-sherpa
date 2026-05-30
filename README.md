# AI Travel Smart Planner

Full-stack AI-assisted travel watch application.

## Stack

- Backend: Spring Boot 3, Java 21, PostgreSQL, Flyway, Spring Security JWT, Spring Scheduler
- Frontend: React, TypeScript, Vite, Recharts
- Providers: pluggable adapter interface with a complete mock provider for local MVP use

## Local Development

### Backend

```bash
cd backend
mvn spring-boot:run
```

Set PostgreSQL variables as needed:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/smart_travel_planner
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
export JWT_SECRET=change-me-to-a-long-random-secret
```

Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend expects the backend at `http://localhost:8080` unless `VITE_API_BASE_URL` is set.

## Compliance

This project is designed around official, partner, affiliate, public, or permitted travel APIs. The real provider adapters are placeholders until API credentials and commercial terms are configured. The MVP uses `MockTravelProviderClient` and does not scrape websites.
