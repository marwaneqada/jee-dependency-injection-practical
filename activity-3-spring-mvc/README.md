# Activity 3 - Secured Spring MVC Web Application

This independent Java 17 project is a server-rendered product management application built with Spring Boot, Spring MVC, Thymeleaf, Spring Data JPA, Hibernate, Spring Security, Bean Validation, and H2.

## Features

- Searchable and paginated product catalogue
- Validated create and edit forms
- Safe POST-based product deletion
- Low-stock indicators
- Shared Thymeleaf navigation and footer fragments
- In-memory authentication with role-based authorization
- H2 database and sample data for local execution

The `USER` role can browse and search products. The `ADMIN` role can also create, edit, and delete products.

## Run

Requirements: JDK 17 and Maven 3.6 or newer.

```bash
cd activity-3-spring-mvc
mvn spring-boot:run
```

Open `http://localhost:8080` and use one of these demo accounts:

| Role | Username | Password |
| --- | --- | --- |
| Administrator | `admin` | `admin123` |
| Read-only user | `viewer` | `viewer123` |

The H2 console is available at `http://localhost:8080/h2-console` with JDBC URL `jdbc:h2:mem:productsdb`, username `sa`, and an empty password.

Run the repository, validation, controller, and security tests with:

```bash
mvn test
```
