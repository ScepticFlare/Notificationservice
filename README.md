# Notification Service

A backend notification service built with **Java, Spring Boot, PostgreSQL, Redis, and RabbitMQ**. The project focuses on REST API design, database persistence, caching, asynchronous message processing, and automated testing.

## Features

- **Notification REST APIs** — Create, retrieve, paginate, mark as read, and delete notifications.
- **Unread notification tracking** — Fetch unread notifications and retrieve unread counts.
- **PostgreSQL persistence** — Store notification records using Spring Data JPA and Hibernate.
- **Redis caching** — Cache unread notification counts and invalidate the cache when relevant notification data changes.
- **Asynchronous processing with RabbitMQ** — Publish notification IDs to a message queue so processing can happen asynchronously.
- **Retry and failure handling** — Configure bounded retries with exponential backoff and dead-letter routing for messages that repeatedly fail processing.
- **Input validation and error handling** — Validate API requests and handle invalid input and missing resources.
- **Pagination** — Support paginated notification retrieval with capped page sizes.
- **Optimistic locking** — Use JPA versioning to detect conflicting concurrent updates.
- **Automated testing** — Test service logic, REST controllers, validation, and database interactions.

## Architecture

```text
                    Client
                      |
                      v
                 REST Controller
                      |
                      v
                NotificationService
                 /      |       \
                v       v        v
         PostgreSQL   Redis   RabbitMQ
         (persistence) (cache) (async queue)
                                  |
                                  v
                         NotificationConsumer
                                  |
                                  v
                              PostgreSQL
                         (load notification)

RabbitMQ failure path:
Consumer failure -> Retry with backoff -> Dead-letter queue
```

PostgreSQL is the source of truth for notification records. Redis accelerates unread-count queries, while RabbitMQ decouples notification creation from subsequent asynchronous processing.

## Tech Stack

- **Language:** Java
- **Backend:** Spring Boot, Spring Web
- **Database:** PostgreSQL, Spring Data JPA, Hibernate
- **Caching:** Redis
- **Messaging:** RabbitMQ
- **Testing:** JUnit, Mockito, MockMvc
- **Build Tool:** Maven
- **Local Development:** Docker

## Running Locally

The application requires PostgreSQL, Redis, and RabbitMQ to be available.

Configure the following environment variables before starting the application:

```text
DB_USERNAME
DB_PASSWORD
RABBITMQ_USERNAME
RABBITMQ_PASSWORD
```

Start the application using Maven:

```bash
./mvnw spring-boot:run
```

On Windows Command Prompt:

```cmd
mvnw.cmd spring-boot:run
```

Ensure the database and message broker are running and the connection settings in `application.properties` match your local setup.

## Project Goals

This project is being developed to deepen practical understanding of backend engineering concepts, including REST API design, relational persistence, caching strategies, asynchronous messaging, failure handling, concurrency control, and automated testing.
