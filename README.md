# Midas Core – JPMorgan Chase Software Engineering Simulation (Forage)

This repository contains my implementation of **Midas Core**, developed as part of the **JPMorgan Chase & Co. Advanced Software Engineering Virtual Experience Program** on Forage.

The project simulates a real-world, production-style backend system responsible for ingesting, validating, processing, and exposing financial transaction data using modern backend technologies.

---

## 🧠 Project Overview

Midas Core is a Spring Boot–based microservice that:

- Consumes high-volume transaction messages from **Apache Kafka**
- Validates and persists transactions using **Spring Data JPA**
- Maintains user balances in a relational database
- Integrates with an external **Incentive REST API**
- Exposes a REST endpoint for querying user balances
- Is fully verified through automated and debugger-driven test suites

The system follows an **event-driven architecture** and demonstrates service-to-service communication, persistence, and API design.

---

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot**
- **Apache Kafka**
- **Spring Data JPA**
- **H2 In-Memory Database**
- **REST APIs (RestTemplate)**
- **Maven**
- **JUnit & Embedded Kafka for testing**

---

## 📌 Features Implemented (Task Breakdown)

### Task 1 – Project Setup
- Configured Spring Boot project with required dependencies
- Verified build and test environment using Maven

### Task 2 – Kafka Integration
- Implemented a Kafka listener to consume transactions from a configurable topic
- Used embedded Kafka for test-driven validation
- Deserialized messages into domain-level `Transaction` objects

### Task 3 – Validation & Persistence
- Modeled relational entities using JPA
- Validated transactions based on user existence and balance constraints
- Persisted valid transactions and updated sender/recipient balances
- Discarded invalid transactions without modifying state

### Task 4 – Incentive API Integration
- Integrated an external REST-based Incentive API
- Posted validated transactions and processed incentive responses
- Applied incentives only to recipients (not senders)
- Persisted incentive amounts alongside transaction records

### Task 5 – REST API Exposure
- Implemented a Spring REST controller exposing `GET /balance`
- Returned JSON-serialized balance responses
- Defaulted balance to `0` for non-existent users
- Configured application to run on port **33400**

---

## 🌐 API Endpoint

### Get User Balance 
http://localhost:33400/balance?userId=<userId>



### Example Response
```json
{
  "balance": 1234.56
}
```

## 🔗 Simulation Reference

JPMorgan Chase & Co. – Advanced Software Engineering Virtual Experience
https://www.theforage.com/simulations/jpmorgan/advanced-software-engineering-r0fm

