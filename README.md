# Locker Service – SmartLockerHub

The **Locker Service** is one of the core microservices in the **SmartLockerHub** system — a distributed application for managing smart package lockers. This service is responsible for managing locker locations, units, availability, and locker unit status updates upon package placement or retrieval.

---

## 🔧 Tech Stack

- **Java 17**
- **Spring Boot 3**
- **Spring Data JPA**
- **MySQL**
- **Apache Kafka**
- **Lombok**
- **MapStruct**
- **Validation (Jakarta Bean Validation)**
- **Spring Web**

---

## 📦 Features

- Manage locker locations and units (small, medium, large).
- Track availability by location and locker size.
- Listen to Kafka events (e.g., `package-placed`) to mark locker units as occupied.
- Generate and store alphanumeric access codes.
- Send access codes via Kafka to the User Service.
- Provide REST APIs for availability checking and unit allocation.

---

## ✅ Endpoints

| Method | Endpoint                         | Description                                    |
|--------|----------------------------------|------------------------------------------------|
| GET    | `/lockers/availability`          | Get count of available units by size/location |
| GET    | `/lockers/available-units`       | Get list of available unit IDs                |
| POST   | `/lockers/create`                | Create a new locker with predefined units     |

---

## 📥 Kafka Integration

- **Consumer Topic:** `package-placed`  
  Marks a locker unit as occupied when a courier delivers a package.

- **Producer Topic:** `access-code-created`  
  Sends access code info to the User Service for further processing and email dispatch.

