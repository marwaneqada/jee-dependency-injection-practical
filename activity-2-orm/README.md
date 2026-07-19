# Activity 2 - ORM with JPA, Hibernate, and Spring Data

This independent Spring Boot project demonstrates object-relational mapping with Java 17, Spring Data JPA, Hibernate, and an in-memory H2 database.

## What it covers

The product section uses `Product` and `ProductRepository` to demonstrate create, read, update, delete, and derived query methods.

The hospital section models:

- `Patient` one-to-many `Appointment`
- `Doctor` one-to-many `Appointment`
- `Appointment` many-to-one `Patient` and `Doctor`
- `Appointment` one-to-one `Consultation`
- `AppUser` many-to-many `AppRole` through `user_roles`

On startup, a command-line runner inserts sample records and prints product, hospital, and user-role operations. Data is stored only in memory and is recreated on each run.

## Run

Requirements: JDK 17 and Maven 3.6 or newer.

```bash
cd activity-2-orm
mvn spring-boot:run
```

The application runs at `http://localhost:8080`. The H2 console is available at `http://localhost:8080/h2-console` with JDBC URL `jdbc:h2:mem:ormdb`, user `sa`, and an empty password.

Run the repository and relationship tests with:

```bash
mvn test
```
