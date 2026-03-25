# Developer's Guide - Multiplayer Tetris Game

This document provides an in-depth guide for developers working on the Multiplayer Tetris Game project.

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Technology Stack](#technology-stack)
3. [Authentication System](#authentication-system)
4. [WebSocket Real-time Communication](#websocket-real-time-communication)
5. [Game State Management](#game-state-management)
6. [Database Schema](#database-schema)
7. [API Documentation](#api-documentation)
8. [Testing Guide](#testing-guide)
9. [Deployment Considerations](#deployment-considerations)

## Architecture Overview

### Layered Architecture

The project follows a classic **Spring Boot layered architecture**:

```
┌─────────────────────────────────────┐
│         Frontend (HTML/JS)          │  Browser-based UI
├─────────────────────────────────────┤
│    REST API + WebSocket Gateway     │  Controllers, WebSocket Handlers
├─────────────────────────────────────┤
│    Business Logic Layer             │  Services, Game Logic
├─────────────────────────────────────┤
│    Data Access Layer                │  Repositories, JPA
├─────────────────────────────────────┤
│    Database (H2/MySQL)              │  Persistent Storage
└─────────────────────────────────────┘
```

### Component Interaction Diagram

```
┌─────────────┐                    ┌──────────────┐
│   Browser   │───────HTTP──────>  │   Spring     │
│ (HTML/JS)   │<────JSON/Events──  │   Boot App   │
└─────────────┘                    └──────────────┘
                                        │
                    ┌───────────────────┼───────────────────┐
                    │                   │                   │
              ┌──────────┐         ┌─────────┐        ┌────────────┐
              │ REST API │         │WebSocket│        │ Services   │
              │Endpoints │  <───>  │ Handler │  <───> │ & Logic    │
              └──────────┘         └─────────┘        └────────────┘
                    │                   │                   │
                    └───────────────────┼───────────────────┘
                                        │
                              ┌──────────────────┐
                              │ JPA Repositories │
                              │   & Entities     │
                              └──────────────────┘
                                        │
                              ┌──────────────────┐
                              │  H2/MySQL DB     │
                              │     Storage      │
                              └──────────────────┘
```

## Technology Stack

### Backend Technologies

| Technology | Version | Purpose |
|-----------|---------|---------|
| Java | 17+ | Programming Language |
| Spring Boot | 3.2.0 | Application Framework |
| Spring Security | 6.2.0 | Authentication & Authorization |
| Spring WebSocket | 6.2.0 | Real-time Communication |
| Spring Data JPA | 3.2.0 | Database ORM |
| JWT (jjwt) | 0.12.3 | Token-based Authentication |
| H2 Database | 2.1+ | Development Database |
| MySQL | 8.0+ | Production Database |
| Maven | 3.6+ | Build Tool |

### Frontend Technologies

| Technology | Purpose |
|-----------|---------|
| HTML5 | Markup & Structure |
| CSS3 | Styling & Layout |
| JavaScript ES6+ | Client-side Logic |
| SockJS | WebSocket Fallback |
| STOMP | WebSocket Protocol |
| Canvas API | Game Rendering |

## Authentication System

### JWT (JSON Web Token) Implementation

#### Token Structure

```
eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTcwMDAwMDAwMCwiZXhwIjoxNzAwMDg2NDAwfQ.signature
    │                         │                                                                           │
   Header                      Payload                                                                Signature
```

#### Authentication Flow

```
User Registration/Login
        │
        ▼
┌────────────────────┐
│ Validate Credentials│
│ (username/password) │
└────────────────────┘
        │
        ▼
┌────────────────────┐
│ Hash Check         │
│ (BCrypt)           │
└────────────────────┘
        │
        ▼ (Success)
┌────────────────────┐
│ Generate JWT Token │
│ (HS512 Algorithm)  │
└────────────────────┘
        │
        ▼
┌────────────────────┐
│ Store Token        │
│ (Client-side)      │
└────────────────────┘
        │
        ▼ (Subsequent Requests)
┌────────────────────────┐
│ Extract Token from     │
│ Authorization Header   │
└────────────────────────┘
        │
        ▼
┌────────────────────────┐
│ Validate Token Signature│
│ & Expiration           │
└────────────────────────┘
        │
        ▼
Allow/Deny Request
```

#### Key Components

1. **JwtTokenProvider.java**
   - Generates tokens upon authentication
   - Validates token signatures and expiration
   - Extracts username from tokens

2. **JwtAuthenticationFilter.java**
   - Intercepts incoming requests
   - Extracts JWT from Authorization header
   - Validates and sets authentication context

3. **SecurityConfig.java**
   - Configures Spring Security chain
   - Sets up filter registration
   - Defines public/protected endpoints

### Password Security

- **Algorithm**: BCrypt with automatic salt generation
- **Cost Factor**: Default (10 rounds)
- **Storage**: Only hashed passwords stored in database

## WebSocket Real-time Communication

### WebSocket Configuration

The application uses **STOMP** (Simple Text-Oriented Message Protocol) over WebSocket for real-time communication.

#### Endpoint Configuration

```java
// WebSocketConfig.java
registry.addEndpoint("/ws/tetris")
        .setAllowedOrigins("*")
        .withSockJS();  // Fallback for browsers without WebSocket support
```

#### Message Broker Configuration

```
Application Prefix: /app
Broker Destinations: /topic, /queue
```

### Message Flow

```
Client Browser                          Spring Boot Server
     │                                         │
     │  1. Connect to /ws/tetris               │
     ├─────────────────────────────────────>  │
     │                                         │ 2. Upgrade to WebSocket
     │                                         │
     │  3. Send game move to /app/game/...     │
     ├─────────────────────────────────────>  │
     │                                         │ 4. Process & Broadcast
     │  5. Receive updates from /topic/game/.. │
     │<─────────────────────────────────────┤  │
     │                                         │
     │  6. Update game board                   │
     │  7. Re-render canvas                    │
     │                                    (Peer receives same message)
     │                                    via /topic subscription
```

### Message Types

#### GameMoveMessage
Sent when player makes a move:
```json
{
  "userId": 1,
  "sessionId": "abc123",
  "messageType": "MOVE",
  "action": "LEFT",
  "boardState": null,
  "score": 100,
  "timestamp": 1700000000000
}
```

#### GameStateMessage
Broadcast for state updates:
```json
{
  "messageType": "GAME_STATE_UPDATE",
  "sessionId": "abc123",
  "playerId": 1,
  "boardState": [[0,1,0,...], ...],
  "nextPiece": 2,
  "score": 100,
  "lines": 5,
  "level": 2,
  "gameOver": false,
  "timestamp": 1700000000000
}
```

### STOMP Subscription Destinations

| Destination | Purpose | Frequency |
|-------------|---------|-----------|
| `/topic/game/{sessionId}` | Broadcast game state | Every game tick |
| `/queue/game/{sessionId}/private` | Private messages | On-demand |

## Game State Management

### Game Session States

```
┌─────────────┐
│  WAITING    │  Waiting for second player
└──────┬──────┘
       │ (Player 2 joins)
       ▼
┌─────────────┐
│ IN_PROGRESS │  Game is active
└──────┬──────┘
       │ (Game ends)
       ▼
┌─────────────┐
│  COMPLETED  │  Game finished with winner
└─────────────┘
```

### Game Board State

- **Representation**: 2D Array (20 rows × 10 columns)
- **Values**: 0 = empty, 1-7 = tetrimino colors
- **Updates**: Synchronized via WebSocket

### Score Calculation

```
Base Points = Lines Cleared × Squares Cleared
Level Multiplier = Current Level × 1.1
Final Points = Base Points × Level Multiplier
```

## Database Schema

### Users Table

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    high_score BIGINT DEFAULT 0,
    total_games INT DEFAULT 0,
    wins INT DEFAULT 0,
    is_online BOOLEAN DEFAULT false,
    created_at BIGINT NOT NULL
);

CREATE INDEX idx_username ON users(username);
CREATE INDEX idx_email ON users(email);
```

### Game Sessions Table

```sql
CREATE TABLE game_sessions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id VARCHAR(255) NOT NULL UNIQUE,
    player_one_id BIGINT NOT NULL,
    player_two_id BIGINT,
    max_players INT DEFAULT 2,
    status VARCHAR(50) DEFAULT 'WAITING',
    winner_id BIGINT,
    created_at BIGINT NOT NULL,
    started_at BIGINT,
    ended_at BIGINT,
    FOREIGN KEY (player_one_id) REFERENCES users(id),
    FOREIGN KEY (player_two_id) REFERENCES users(id),
    FOREIGN KEY (winner_id) REFERENCES users(id)
);

CREATE INDEX idx_session_id ON game_sessions(session_id);
CREATE INDEX idx_status ON game_sessions(status);
CREATE INDEX idx_player_one ON game_sessions(player_one_id);
```

## API Documentation

### Authentication Endpoints

#### POST /api/auth/register

Register a new user account.

**Request Body:**
```json
{
  "username": "player123",
  "email": "player@example.com",
  "password": "securePassword123",
  "confirmPassword": "securePassword123"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Registration successful",
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "user": {
    "id": 1,
    "username": "player123",
    "email": "player@example.com",
    "highScore": 0,
    "totalGames": 0,
    "wins": 0,
    "isOnline": false,
    "createdAt": 1700000000000
  }
}
```

#### POST /api/auth/login

Authenticate and receive JWT token.

**Request Body:**
```json
{
  "username": "player123",
  "password": "securePassword123"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "user": { ... }
}
```

### Game Endpoints

#### POST /api/game/create-session

Create a new multiplayer game session.

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "playerOneId": 1,
  "playerTwoId": null,
  "maxPlayers": 2,
  "status": "WAITING",
  "createdAt": 1700000000000
}
```

#### POST /api/game/join-session/{sessionId}

Join an existing waiting game.

**Response (200 OK):**
```json
{
  "id": 1,
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "playerOneId": 1,
  "playerTwoId": 2,
  "status": "IN_PROGRESS",
  "startedAt": 1700000001000
}
```

## Testing Guide

### Unit Testing

Create test classes for services:

```java
@SpringBootTest
class AuthServiceTest {
    @MockBean
    private UserRepository userRepository;
    
    @InjectMocks
    private AuthService authService;

    @Test
    void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest(
            "testuser", 
            "test@example.com", 
            "password123", 
            "password123"
        );
        
        AuthResponse response = authService.register(request);
        
        assertTrue(response.getSuccess());
        assertNotNull(response.getToken());
    }
}
```

### Integration Testing

Test API endpoints:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerIntegrationTest {
    @LocalServerPort
    private int port;

    @Test
    void testLoginEndpoint() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:" + port + "/api/auth/login";
        
        LoginRequest request = new LoginRequest("user", "password");
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
            url, 
            request, 
            AuthResponse.class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
```

### WebSocket Testing

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameWebSocketTest {
    @LocalServerPort
    private int port;

    @Test
    void testWebSocketConnection() {
        StompSession session = stompClient.connect(
            "ws://localhost:" + port + "/api/ws/tetris",
            new StompSessionHandlerAdapter() {},
            null
        ).get();
        
        assertTrue(session.isConnected());
    }
}
```

## Deployment Considerations

### Production Configuration

Update `application.properties` for production:

```properties
# Database
spring.datasource.url=jdbc:mysql://db.example.com:3306/tetris_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# JWT
jwt.secret=${JWT_SECRET_KEY}
jwt.expiration=86400000

# Server
server.ssl.key-store=${KEYSTORE_PATH}
server.ssl.key-store-password=${KEYSTORE_PASSWORD}

# Logging
logging.level.root=WARN
logging.level.com.tetris=INFO
```

### Docker Deployment

Create `Dockerfile`:

```dockerfile
FROM eclipse-temurin:17-jre-alpine
COPY target/tetris-multiplayer.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

Create `docker-compose.yml`:

```yaml
version: '3.8'
services:
  tetris-app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - DB_USERNAME=root
      - DB_PASSWORD=password
      - JWT_SECRET_KEY=your-secret-key
    depends_on:
      - mysql-db
  
  mysql-db:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=password
      - MYSQL_DATABASE=tetris_db
    ports:
      - "3306:3306"
```

### Scalability Considerations

1. **Database Replication**: Use MySQL master-slave replication
2. **Load Balancing**: Implement sticky sessions for WebSocket connections
3. **Caching**: Add Redis for user session caching
4. **Message Queue**: Use RabbitMQ for game event distribution
5. **Microservices**: Consider separating game logic from auth service

### Security Checklist

- [ ] Use HTTPS in production
- [ ] Implement rate limiting on auth endpoints
- [ ] Add CSRF protection for stateful endpoints
- [ ] Rotate JWT secret regularly
- [ ] Implement audit logging
- [ ] Use environment variables for secrets
- [ ] Enable CORS appropriately
- [ ] Implement request validation
- [ ] Add API versioning
- [ ] Monitor and alert on security events

---

**For more information, see the main [README.md](README.md)**
