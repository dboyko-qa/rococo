# Project Rococo

## About the Project

This project is a production-like backend system, developed from scratch for an existing frontend application.  
It demonstrates the full backend workflow: from design and implementation to comprehensive automated testing.

The project focuses on quality and reliability. It has multi-level test coverage, which allows  
verifying the system at different levels and detecting errors early in the development process.

This repository can serve as an example of a backend project with a well-thought-out architecture  
and a full testing strategy close to real-world product requirements.

## Rococo Project Diagram

<img src="rococo_scheme_dark.png" width="600">

## Architecture

The backend is built using a **microservices architecture** and consists of independent services with clearly separated responsibilities.  
The system has two entry points, each handling a separate request flow.

All services are implemented with **Java 21** using **Spring Boot** and work with a **PostgreSQL** database.

---

### Gateway

The **Gateway** is the main entry point for the frontend application.  
The frontend communicates with the gateway via **REST**, and the gateway routes requests to internal services and acts as an orchestrator.

Communication between the gateway and internal microservices is implemented using **gRPC**.  
All internal services are accessible only through the gateway and do not have direct external access.

---

### Auth Service

The **Auth service** is a separate microservice responsible for user authentication and authorization.  
It does not participate in the main request flow through the gateway and serves as an independent entry point.

During user registration, the Auth service sends data to the **Userdata service** asynchronously via the **Kafka** message broker.

---

### Service Communication

Internal services do not interact directly with external clients.  
The **Auth service** communicates with the **Userdata service** through **Kafka**, reducing service coupling and improving system resilience.

---

### Architectural Focus

The architecture is designed for maintainability, scalability, testability, and isolation of key functional areas.

---

## Testing

The project includes **unit, integration, and end-to-end tests**.

---

### Unit and Integration Tests

Unit and integration tests are located inside the microservices.

**Unit tests** use **JUnit 5** and **Mockito** to verify the business logic of services in isolation from external dependencies.

**Integration tests** use **WireMock** to stabilize external integrations and an **in-memory H2 database**.  
They verify component interactions within the service and the correctness of data handling.

---

### End-to-End Tests

End-to-end tests are implemented in a separate subproject and support parallel execution with isolation of threads and data.

- **API tests** are implemented with **RestAssured** and verify key business scenarios and access rights.
- **UI tests** are implemented with **Selenide** and run in the **Chrome** browser.

All e2e tests use data and session isolation mechanisms to ensure parallel execution without conflicts.

---

### Test Data Preparation

Test data preparation and test state management are implemented via custom **JUnit 5 extensions**.  
The project uses extensions for:

- selecting users,
- automatic login via API,
- generating test data and passing it into tests via parameters.

---

### Running Tests and Reports

Tests run locally. Planned improvements include running tests in **Docker containers** and integrating with **GitHub Actions** for automated CI execution.

Test results are visualized with **Allure** for easy navigation through scenarios.

---
