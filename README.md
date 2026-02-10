# Midas Core: Financial Transaction & Incentive Engine

Midas Core is a distributed financial processing system built with **Spring Boot** and **Apache Kafka**. It handles high-volume transaction processing, real-time balance management, and integrates with external microservices to apply transaction incentives.

## 🚀 System Architecture

The project follows a microservices-oriented architecture:
* **Producer:** Simulates financial transactions being sent to a Kafka topic.
* **Midas Core (Consumer):** Listens to Kafka, processes transactions, and updates the PostgreSQL/H2 database via Hibernate.
* **Incentive API (External Service):** A separate REST service that determines if a transaction qualifies for a reward/bonus.
* **REST API:** A balance inquiry endpoint that allows external systems to query user accounts in real-time.



---

## 🛠️ Tech Stack
* **Java 17**
* **Spring Boot 3.x** (Web, Data JPA, Validation)
* **Apache Kafka** (Message Broker)
* **Hibernate** (ORM)
* **JUnit 5 & Embedded Kafka** (Testing)
* **Lombok** (Boilerplate reduction)

---

## 📋 Features implemented

### Task 1-2: Core Infrastructure
* Configured Kafka listeners to ingest `Transaction` objects.
* Implemented JPA entities for `User` and `Transaction` records.
* Established a relational database schema to track historical balances.

### Task 3-4: The Incentive Logic
* Integrated an external **Incentive API** using `RestTemplate`.
* Developed logic to adjust transaction amounts based on external "bonuses" before updating account balances.

### Task 5: RESTful Inquiry
* Created a `GET /balance` endpoint to retrieve real-time user data.
* Implemented a custom `Balance` foundation object for standardized API responses.
* Verified the full end-to-end flow (Kafka -> Processing -> Incentive -> REST) using a dedicated verifier test suite.

---

## ⚙️ How to Run

1. **Start the Incentive API:**
   ```bash
   java -jar incentives-api.jar
