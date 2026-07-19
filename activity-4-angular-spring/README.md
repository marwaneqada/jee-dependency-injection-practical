# Activity 4 - Angular and Spring Boot

This activity is an independent full-stack product application. An Angular standalone frontend consumes a Java 17 Spring Boot REST API backed by Spring Data JPA, Hibernate, and an in-memory H2 database.

## Features

- Home and product catalogue routes
- Typed Angular HTTP service
- Responsive product list with loading and error states
- Persistent selected/priority state
- Product deletion with confirmation
- Spring Boot REST API with validation, CORS, and clear 404 responses
- H2 sample data recreated on each backend restart

## Structure

```text
activity-4-angular-spring/
├── backend/   # Spring Boot REST API
└── frontend/  # Angular standalone application
```

## Run

Start the backend (JDK 17 and Maven required):

```bash
cd activity-4-angular-spring/backend
mvn spring-boot:run
```

In another terminal, start the frontend (Node.js and npm required):

```bash
cd activity-4-angular-spring/frontend
npm install
npm start
```

Open `http://localhost:4200`. The Angular development proxy forwards `/api` requests to the backend on port `8080`.

## REST API

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `GET` | `/api/products` | List products |
| `POST` | `/api/products` | Create a validated product |
| `PUT` | `/api/products/{id}/selected?value=true` | Update selected state |
| `DELETE` | `/api/products/{id}` | Delete a product |

The H2 console is available at `http://localhost:8080/h2-console` using JDBC URL `jdbc:h2:mem:productdb`, username `sa`, and an empty password.

## Verify

```bash
cd backend
mvn test

cd ../frontend
npm test
npm run build
```
