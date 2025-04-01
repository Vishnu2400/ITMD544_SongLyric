# SongLyric Spring Boot Application

## Overview

This project is a Spring Boot REST API with GraphQL endpoints for managing song lyrics. It uses PostgreSQL as the database and integrates with an external API from TheHive.ai for generating names for lyrics based on the content.

## Table of Contents

1. [Installation](#installation)
2. [Configuration](#configuration)
3. [Usage](#usage)
4. [API Documentation](#api-documentation)
5. [Database Schema](#database-schema)
6. [Demonstration](#demonstration)
7. [Report](#report)

## Installation

### Prerequisites

- Java 21
- PostgreSQL
- Maven

### Steps

1. Clone the repository:
   ```sh
   git clone https://github.com/Vishnu2400/ITMD544_SongLyric.git
   cd ITMD544_SongLyric
   ```

2. Set up PostgreSQL:
   - Create a database named `song_lyric`.
   - Update the database configuration in `src/main/resources/application.properties` if necessary.

3. Build the project:
   ```sh
   mvn clean install
   ```

4. Run the application:
   ```sh
   mvn spring-boot:run
   ```

## Configuration

### Database Configuration

The database configuration is located in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/song_lyric
spring.datasource.username=postgres
spring.datasource.password=root@123
spring.jpa.hibernate.ddl-auto=update
```

### Security Configuration

<!-- Placeholder for Security Configuration -->
Update your security configuration details here.

### GraphQL Configuration

<!-- Placeholder for GraphQL Configuration -->
Update your GraphQL configuration details here.

### External API Configuration

To use TheHive.ai services, set up your API key in the configuration file:
```properties
hive.api.key=YOUR_API_KEY
hive.api.url=https://api.thehive.ai/generate-names
```

## Usage

### Accessing the API

- The REST API is accessible at `http://localhost:8080/api`.
- The GraphQL endpoint is accessible at `http://localhost:8080/graphql`.

## API Documentation

### REST API

Detailed API documentation is available through Swagger UI at `http://localhost:8080/swagger-ui.html`.

### GraphQL API

Use GraphiQL at `http://localhost:8080/graphiql` to interact with the GraphQL API.

## Database Schema

Below is the Entity-Relationship Diagram (ERD) of the database schema:

[ERD Diagram]
![erd song_lyric](https://github.com/user-attachments/assets/1c5bdf73-a5a4-491a-8e1d-c8c57647d053)

## Demonstration

### Live Demo

<!-- Placeholder for Live Demo URL -->
Add your live demo URL here.

### Demo Script

<!-- Placeholder for Demo Script -->
Add your demo script showcasing all implemented features.

### Example API Calls

<!-- Placeholder for Example API Calls -->
Provide a collection of example API calls (Postman collection or equivalent).

## Report

### Design Decisions

Initially, I created the design in Structurizr. My initial design included various components, but later on, I reduced some components like external authentication with Firebase. Instead, I used an external API from TheHive.ai in the middle of the project after clarifications with requirements.

### Challenges Encountered

There were a lot of configurations from JWT and Spring Security which initially did not work. After getting JWT to work, it became hard to use tokens for different operations based on the roles. Additionally, configuring GraphQL and Swagger was a bit tough because of the dependency versions that needed to be used.

### Performance Analysis

Initially, while developing the REST controllers, I wrote all the logic in controllers. Later on, I shifted some of the database logic to service layers to optimize and reduce the code multiple times for both the REST controllers and GraphQL resolvers. Performance-wise, GraphQL was superior to REST due to more efficient querying and reduced data over-fetching.

### Security Considerations

I have implemented JWT and Spring Security for token-based authorization.

### Future Improvements

There are many things to improve, such as implementing lyrics generation with AI and adding more models for version control, similar to GitHub, for song lyrics.
