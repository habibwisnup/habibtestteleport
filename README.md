# SmartLoad Optimization API

A stateless REST API that selects the optimal combination of shipment orders for a truck, maximizing carrier payout while respecting real-world logistics constraints such as weight, volume, hazmat rules, route compatibility, and time windows.

This project is designed as a backend take-home assignment and focuses on correctness, performance, and clean architecture.

---

## 🚀 Features

- Maximizes total payout to carrier (integer cents, no floating-point money)
- Respects truck constraints:
    - Max weight
    - Max volume
- Enforces order compatibility:
    - Same origin & destination (lane)
    - Pickup date ≤ delivery date
    - Hazmat orders cannot be mixed with non-hazmat
- Stateless service (no database)
- Fast optimization using **Dynamic Programming with bitmask**
- Proper HTTP status codes & global error handling
- Unit tests for business logic and edge cases
---

## 🛠 Tech Stack

- Java 21
- Spring Boot (Spring Initializr)
- Maven
- JUnit 5 + AssertJ
- Docker & Docker Compose

---
## ▶️ How to Run (Docker)

### Prerequisites
- Docker
- Docker Compose

### Run the service

```bash
docker compose up --build
