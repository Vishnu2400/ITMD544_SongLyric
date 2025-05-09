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

## Usage

### Accessing the API

- The REST API is accessible at `http://localhost:8080/api`.
- The GraphQL endpoint is accessible at `http://localhost:8080/graphql`.

## API Documentation

### REST API

Detailed API documentation is available through Swagger UI at `http://localhost:8080/swagger-ui.html`.

#### AuthController

##### Register a new user

- **Endpoint:** `POST http://localhost:8080/auth/register`
- **Request Body:**
  ```json
  {
    "username": "user123",
    "email": "user123@example.com",
    "password": "password123",
    "role": "SONG_WRITER"
  }
  ```

##### Login

- **Endpoint:** `POST http://localhost:8080/auth/login`
- **Request Body:**
  ```json
  {
    "username": "user123",
    "password": "password123"
  }
  ```

#### SongController

##### Create a new song

- **Endpoint:** `POST http://localhost:8080/songs/create`
- **Request Body:**
  ```json
  {
    "title": "New Song",
    "lyrics": "These are the lyrics"
  }
  ```

##### Get song title suggestions based on lyrics

- **Endpoint:** `POST http://localhost:8080/songs/suggest-title`
- **Request Body:**
  ```json
  {
    "lyrics": "These are some lyrics"
  }
  ```

##### Get all songs

- **Endpoint:** `GET http://localhost:8080/songs/all`
- **Request Body:** None

##### Update a song

- **Endpoint:** `PUT http://localhost:8080/songs/update/{songId}`
- **Request Body:**
  ```json
  {
    "title": "Updated Song Title",
    "lyrics": "Updated lyrics"
  }
  ```

##### Add a like to a song

- **Endpoint:** `POST http://localhost:8080/songs/like/{songId}`
- **Request Body:** None

#### SuggestionController

##### Add a suggestion to a song

- **Endpoint:** `POST http://localhost:8080/suggestions/add/{songId}`
- **Request Body:**
  ```json
  {
    "suggestionText": "This is a suggestion"
  }
  ```

##### Modify a suggestion

- **Endpoint:** `PUT http://localhost:8080/suggestions/modify/{suggestionId}`
- **Request Body:**
  ```json
  {
    "suggestionText": "Updated suggestion text"
  }
  ```

##### Delete a suggestion

- **Endpoint:** `DELETE http://localhost:8080/suggestions/delete/{suggestionId}`
- **Request Body:** None

##### Get all suggestions

- **Endpoint:** `GET http://localhost:8080/suggestions/all`
- **Request Body:** None

[Example of registering a user as SONG_WRITER]

![image](https://github.com/user-attachments/assets/88250fb5-67ec-44c7-8872-302d680cdf82)

[Above user is logged in and received token]

![image](https://github.com/user-attachments/assets/1f89accf-0665-4615-9b3c-c56283fa90c1)



### GraphQL API

Use GraphiQL at `http://localhost:8080/graphiql` to interact with the GraphQL API.

## Example Queries and Mutations

### 1. Register a New User

```graphql
mutation {
  register(
    username: "user123"
    email: "user123@example.com"
    password: "password123"
    role: "USER"
  )
}
```

### 2. Login

```graphql
mutation {
  login(
    username: "user123"
    password: "password123"
  )
}
```

### 3. Create a New Song

```graphql
mutation {
  createSong(
    title: "New Song"
    lyrics: "These are the lyrics"
  )
}
```

### 4. Get All Songs

```graphql
query {
  getAllSongs {
    id
    title
    lyrics
    author {
      username
    }
    createdAt
    updatedAt
    likesCount
  }
}
```

### 5. Add a Comment to a Song

```graphql
mutation {
  addComment(
    songId: "1"
    commentText: "Great song!"
  )
}
```
[example of graphql query for registering a user]

![image](https://github.com/user-attachments/assets/b10bc5ed-0e0c-4657-b694-b2ada22072b8)


## Database Schema

Below is the Entity-Relationship Diagram (ERD) of the database schema:

[ERD Diagram]
![erd song_lyric](https://github.com/user-attachments/assets/1c5bdf73-a5a4-491a-8e1d-c8c57647d053)

## Demonstration

### Live Link

Deployed the application in Azure also the created a Postgres Database in the Azure 

https://songlyric-hqdsdubedmeahyfn.eastus-01.azurewebsites.net

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

There are many things to improve, such as implementing lyrics generation with AI and adding more models for version control, similar to GitHub, for song lyrics
