# Notification Service

A backend notification service built with **Java, Spring Boot, PostgreSQL, and REST APIs**.

The project started as a simple notification API and is being developed step by step into a more complete backend system, with a focus on clean architecture, database design, caching, asynchronous processing, and deployment.

## What it does

The service currently provides APIs to:

- Create notifications
- Fetch notifications
- Update notification status
- Delete notifications
- Fetch unread notifications
- Paginate notification results
- Handle missing resources and API errors cleanly

The application follows a layered architecture that separates the API, business logic, and database access.

```text
Client
  |
  v
Controller
  |
  v
Service
  |
  v
Repository
  |
  v
PostgreSQL
