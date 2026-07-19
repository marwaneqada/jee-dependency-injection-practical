# Activity 5 - Spring, Angular, JWT, and Spring Security

This activity completes the e-banking domain introduced by the external `Projet Spring Angular JWT Spring Security` skeleton. It is an independent full-stack application with an Angular frontend, a Spring Boot REST API, stateless JWT authentication, role-based authorization, Spring Data JPA, and H2.

## Features

- JWT login backed by Spring Security and BCrypt passwords
- `USER` access to accounts, histories, credits, debits, and transfers
- `ADMIN`-only customer and account administration endpoints
- Current accounts with overdrafts and saving accounts with interest rates
- Transactional banking operations with balance validation
- Responsive Angular dashboard, account history, operations, and customer administration
- Validation, structured API errors, sample data, and integration tests

## Structure

```text
activity-5-angular-jwt-security/
├── backend/   # Spring Boot API on port 8085
└── frontend/  # Angular standalone application on port 4200
```

## Run

Start the backend with JDK 17 and Maven:

```bash
cd activity-5-angular-jwt-security/backend
mvn spring-boot:run
```

Start the frontend in another terminal:

```bash
cd activity-5-angular-jwt-security/frontend
npm install
npm start
```

Open `http://localhost:4200`. The Angular proxy sends `/api` requests to `http://localhost:8085`.

## Demo users

| Username | Password | Roles |
| --- | --- | --- |
| `admin` | `admin123` | `USER`, `ADMIN` |
| `user` | `user123` | `USER` |

These credentials are development sample data only.

## Main API endpoints

| Method | Endpoint | Access | Purpose |
| --- | --- | --- | --- |
| `POST` | `/api/auth/login` | Public | Obtain a JWT |
| `GET` | `/api/accounts` | User | List accounts |
| `GET` | `/api/accounts/{id}/operations` | User | Paginated history |
| `POST` | `/api/accounts/{id}/credit` | User | Credit an account |
| `POST` | `/api/accounts/{id}/debit` | User | Debit an account |
| `POST` | `/api/accounts/transfer` | User | Transfer funds |
| `GET/POST` | `/api/admin/customers` | Admin | List or create customers |
| `POST` | `/api/admin/customers/{id}/current-account` | Admin | Open a current account |
| `POST` | `/api/admin/customers/{id}/saving-account` | Admin | Open a saving account |

The H2 console is at `http://localhost:8085/h2-console` with JDBC URL `jdbc:h2:mem:bankdb`, user `sa`, and an empty password.

## Verify

```bash
cd backend
mvn test

cd ../frontend
npm run build
```
