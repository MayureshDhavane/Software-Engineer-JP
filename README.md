# Midas Core - Financial Transaction Processor

## Overview
Midas Core is a robust backend financial processing system developed as part of the J.P. Morgan Chase & Co. Advanced Software Engineering Job Simulation. It is designed to handle high volumes of financial transactions in real-time, ensuring data integrity, incentive application, and instant balance updates.

## Architecture
The application follows an event-driven architecture using **Apache Kafka** to decouple transaction ingestion from processing.

1.  **Ingestion**: Transactions are produced to a Kafka topic (`trader-updates`).
2.  **Processing**: A Kafka Listener consumes these messages asynchronously.
3.  **Business Logic**:
    *   Validates the transaction.
    *   Queries an external **Incentive API** to apply bonuses/incentives.
    *   Updates sender and receiver balances atomically.
4.  **Persistence**: Stores transaction records and user details in an H2 in-memory database using **Spring Data JPA**.
5.  **Access**: Exposes a REST API implementation to allow external systems to query user balances.

## Key Features
*   **Real-time Stream Processing**: Utilizes `Spring Kafka` to consume and process transaction events immediately as they occur.
*   **External Service Integration**: Integrates with a separate Incentive microservice via `RestTemplate` to enrich transaction data.
*   **Database Management**: Implements `UserRecord` and `TransactionRecord` entities with JPA repositories for efficient data handling.
*   **REST API**: Provides a secure and fast endpoint (`/balance`) for querying real-time user account balances.

## Technologies Used
*   **Java 17+**
*   **Spring Boot** (Web, Data JPA, Kafka)
*   **Apache Kafka**
*   **H2 Database**
*   **Maven**

## Endpoints
### Get User Balance
*   **URL**: `/balance`
*   **Method**: `GET`
*   **Query Param**: `userId` (Long)
*   **Response**:
    ```json
    {
      "amount": 1234.56
    }
    ```
