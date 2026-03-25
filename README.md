# Multiplayer Tetris Game - Spring Boot Edition

A modern, real-time multiplayer Tetris game built with **Spring Boot**, **Spring Security**, and **WebSockets**. This project demonstrates enterprise-grade Java development practices with authentication, real-time communication, and game state management.

## 🎮 Features

- **User Authentication**: Secure registration and login using JWT tokens
- **Real-time Multiplayer**: WebSocket-based communication for instant game updates
- **Player Statistics**: Track high scores, wins, game history
- **Spring Security**: Token-based authentication and authorization
- **RESTful API**: Complete game management endpoints
- **Responsive UI**: Modern, mobile-friendly frontend
- **Database Persistence**: JPA/Hibernate for user and game data
- **Tetriminos**: Seven unique geometric shapes (I, O, T, S, Z, J, L)
- **Game Mechanics**: Line clearing, rotation, movement, speed scaling
- **Leaderboard**: Global leaderboard with player statistics
- **Multiplayer Mode**: Competitive gameplay with real-time opponent tracking

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Modern web browser with WebSocket support

## 🚀 Getting Started

### 1. Clone the Repository

```bash
cd "Tetris-With-Spring-Boot"
```

### 2. Build the Project

```bash
mvn clean install
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Access the Game

Open your browser and navigate to:
```
http://localhost:8080
```

## 📁 Project Structure

```
Tetris-With-Spring-Boot/
├── pom.xml                           # Maven configuration with all dependencies
├── README.md                         # This file
└── src/
    ├── main/
    │   ├── java/com/tetris/
    │   │   ├── TetrisMultiplayerApplication.java    # Main Spring Boot application
    │   │   ├── entity/
    │   │   │   ├── User.java                        # User entity with stats
    │   │   │   └── GameSession.java                 # Game session management
    │   │   ├── repository/
    │   │   │   ├── UserRepository.java              # User data access
    │   │   │   └── GameSessionRepository.java       # Game session data access
    │   │   ├── service/
    │   │   │   ├── AuthService.java                 # Authentication logic
    │   │   │   ├── GameService.java                 # Game management logic
    │   │   │   └── CustomUserDetailsService.java    # Spring Security integration
    │   │   ├── controller/
    │   │   │   ├── AuthController.java              # Auth REST endpoints
    │   │   │   ├── GameController.java              # Game REST endpoints
    │   │   │   └── HomeController.java              # Frontend serving
    │   │   ├── config/
    │   │   │   ├── SecurityConfig.java              # Spring Security configuration
    │   │   │   └── WebSocketConfig.java             # WebSocket configuration
    │   │   ├── security/
    │   │   │   ├── JwtTokenProvider.java            # JWT token generation/validation
    │   │   │   └── JwtAuthenticationFilter.java     # JWT token verification filter
    │   │   ├── websocket/
    │   │   │   └── GameWebSocketHandler.java        # Real-time game communication
    │   │   └── dto/
    │   │       ├── LoginRequest.java
    │   │       ├── RegisterRequest.java
    │   │       ├── AuthResponse.java
    │   │       ├── UserDto.java
    │   │       ├── GameMoveMessage.java
    │   │       └── GameStateMessage.java
    │   └── resources/
    │       ├── application.properties               # Application configuration
    │       ├── templates/
    │       │   └── index.html                       # Main game UI
    │       └── static/
    │           ├── css/
    │           │   └── style.css                    # Game styling
    │           └── js/
    │               └── app.js                       # Game logic & WebSocket client
```

## 🔐 Authentication Flow

1. **Registration**: Create a new account with username, email, and password
2. **JWT Token Generation**: Upon successful login, receive a JWT token
3. **Token Storage**: Token is stored in browser localStorage
4. **API Requests**: Token is sent in `Authorization: Bearer <token>` header
5. **Token Validation**: JwtAuthenticationFilter validates each request
6. **Session Management**: User status (online/offline) is tracked

## 🎯 API Endpoints

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login with credentials
- `POST /api/auth/logout` - Logout current user
- `GET /api/auth/validate` - Validate JWT token

### Game Management
- `POST /api/game/create-session` - Create a new game session
- `POST /api/game/join-session/{sessionId}` - Join an existing game
- `GET /api/game/session/{sessionId}` - Get game session details
- `GET /api/game/waiting-sessions` - List available games
- `POST /api/game/end-game/{sessionId}` - End a game session
- `GET /api/game/history` - Get player's game history

## 🌐 WebSocket Events

### Subscribe to
- `/topic/game/{sessionId}` - Game state updates

### Send to
- `/app/game/{sessionId}/join` - Join a game
- `/app/game/{sessionId}/move` - Send game move
- `/app/game/{sessionId}/update` - Update game state
- `/app/game/{sessionId}/game-over` - End game
- `/app/game/{sessionId}/leave` - Leave game

## 🎮 Game Controls

- **Left Arrow** - Move piece left
- **Right Arrow** - Move piece right
- **Down Arrow** - Drop piece faster
- **Space** - Rotate piece clockwise
- **P** - Pause/Resume game

## 🗄️ Database Configuration

The application uses **H2 Database** by default for development:

- **URL**: `http://localhost:8080/api/h2-console`
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: (leave empty)

### Switch to MySQL (Production)

Update `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/tetris_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

## 🧪 Testing the Application

### Manual Testing Steps

1. **Create Two User Accounts**
   - Register with User1 credentials
   - In a new browser/incognito window, register with User2 credentials

2. **Create and Join Game**
   - User1: Click "Create Game"
   - Copy the session ID
   - User2: Click "Join Game" and paste the session ID
   - Game should start automatically

3. **Play the Game**
   - Use arrow keys to control pieces
   - Watch real-time updates from opponent
   - Complete lines to increase score

4. **View Statistics**
   - Click "View Statistics" to see player stats
   - Click "Game History" to see past games

## 📦 Key Dependencies

```
Spring Boot 3.2.0
- spring-boot-starter-web (Web MVC)
- spring-boot-starter-websocket (WebSocket support)
- spring-boot-starter-security (Authentication & Authorization)
- spring-boot-starter-data-jpa (Database ORM)

JWT & Security
- jjwt (JSON Web Tokens)
- Spring Security

Database
- H2 Database (Development)
- MySQL Connector (Production)

Utilities
- Lombok (Reduce boilerplate)
- Jackson (JSON processing)
```

## 🛠️ Development Guide

### Adding New Features

#### 1. Create a New Entity
```java
@Entity
@Table(name = "table_name")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // ... fields
}
```

#### 2. Create Repository
```java
@Repository
public interface NewEntityRepository extends JpaRepository<NewEntity, Long> {
    // Custom queries
}
```

#### 3. Create Service
```java
@Service
public class NewEntityService {
    private final NewEntityRepository repository;
    // Business logic
}
```

#### 4. Create Controller
```java
@RestController
@RequestMapping("/api/endpoint")
public class NewEntityController {
    private final NewEntityService service;
    // REST endpoints
}
```

## 🐛 Troubleshooting

### WebSocket Connection Issues
- Check browser console for connection errors
- Ensure WebSocket endpoint is accessible
- Verify CORS configuration in SecurityConfig.java

### JWT Token Errors
- Token might be expired - login again to get new token
- Check token format: `Bearer <token>`
- Verify JWT secret matches in config

### Database Connection Issues
- For H2: Ensure in-memory database is not cleared between requests
- For MySQL: Verify database exists and credentials are correct
- Check database URL in application.properties

## 📚 Learning Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Guide](https://spring.io/projects/spring-security)
- [Spring WebSocket Guide](https://spring.io/guides/gs/messaging-stomp-websocket/)
- [JWT Tutorial](https://jwt.io/introduction)
- [Tetris Game Logic](https://en.wikipedia.org/wiki/Tetris)

## 📝 Future Enhancements

- [ ] Implement actual Tetris game logic with collision detection
- [ ] Add AI-powered single-player mode
- [ ] Implement chat system between players
- [ ] Add tournament/ranking system
- [ ] Mobile app using Flutter or React Native
- [ ] Sound effects and background music
- [ ] Achievements and badges system
- [ ] Live spectator mode
- [ ] Replay system to watch past games
- [ ] Cloud deployment (AWS, Azure, GCP)

## 📄 License

This project is open source and available under the MIT License.

## 🤝 Contributing

This is a personal practice project. However, suggestions and feedback are welcome!

## 👤 Contact

**Author**: Samantha Culver  
**GitHub**: [@17sculver](https://github.com/17sculver)

---

**Happy Gaming!** 🎮✨
