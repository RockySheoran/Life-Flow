# Life-Flow Backend System Design

This document provides a comprehensive overview of the Life-Flow backend architecture, including the API Gateway, Service Discovery, and the interaction between various microservices.

## System Design

The Life-Flow backend is built upon a microservice architecture. This design pattern structures the application as a collection of loosely coupled services. Each service is self-contained, responsible for a specific business capability, and can be developed, deployed, and scaled independently.

Here's a high-level overview of the architecture:

![System Design Diagram](https://i.imgur.com/3y2k5V2.png)

The primary components of our system are:

*   **API Gateway:** The single entry point for all client requests.
*   **Discovery Service:** A service registry that allows microservices to find and communicate with each other.
*   **Microservices:** Individual services that implement specific business logic (e.g., User Service, Donor Service).
*   **Client:** The frontend application that interacts with the backend.

## API Gateway

The API Gateway is a crucial component of our microservice architecture. It acts as a reverse proxy, providing a unified interface for client applications to interact with the various backend services.

### Responsibilities

*   **Single Point of Entry:** All incoming client requests are routed through the API Gateway. This simplifies the client-side code, as it only needs to know the URL of the API Gateway.
*   **Routing:** The API Gateway routes incoming requests to the appropriate microservice based on the request path.
*   **Authentication and Authorization:** The API Gateway is responsible for authenticating users and ensuring they have the necessary permissions to access the requested resources.
*   **Load Balancing:** The API Gateway can distribute incoming requests across multiple instances of a microservice, improving scalability and reliability.
*   **Rate Limiting:** To prevent abuse, the API Gateway can limit the number of requests a client can make in a given period.
*   **CORS:** The API Gateway handles Cross-Origin Resource Sharing (CORS) to allow requests from different domains.

### Implementation

Our API Gateway is built using Spring Cloud Gateway. It is configured to route requests to the various microservices based on their service IDs. The routing rules are defined in the `application.yml` file of the `api-gateway` service.

## Service Discovery

In a microservice architecture, services need a way to find and communicate with each other. This is where the Discovery Service comes in. It acts as a service registry, maintaining a list of all available microservices and their network locations.

### Responsibilities

*   **Service Registration:** When a microservice starts up, it registers itself with the Discovery Service, providing its service ID and network location.
*   **Service Discovery:** When a microservice needs to communicate with another service, it queries the Discovery Service to get the network location of the target service.
*   **Health Checking:** The Discovery Service periodically checks the health of the registered services and removes any unhealthy instances from the registry.

### Implementation

Our Discovery Service is built using Netflix Eureka. Each microservice is configured as a Eureka client, and the `discovery-service` is configured as a Eureka server.

## How It Works

Here's a step-by-step breakdown of how a client request is processed:

1.  The client sends a request to the API Gateway.
2.  The API Gateway receives the request and performs authentication and authorization checks.
3.  The API Gateway queries the Discovery Service to get the network location of the target microservice.
4.  The Discovery Service returns the network location of the target microservice.
5.  The API Gateway forwards the request to the target microservice.
6.  The microservice processes the request and returns a response to the API Gateway.
7.  The API Gateway forwards the response to the client.

## Services

The following is a list of the microservices in the Life-Flow backend:

*   **user_service:** Manages user accounts, authentication, and authorization.
*   **donor_service:** Manages blood donors and their information.
*   **request_service:** Manages blood requests.
*   **inventory_service:** Manages the blood inventory.
*   **geolocation_service:** Provides geolocation services.
*   **discovery-service:** The service registry.
*   **api-gateway:** The API Gateway.
