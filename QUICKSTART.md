# Quick Start Guide

## Prerequisites

- Java 17 or higher
- Maven 3.8.1 or higher

## Clone the Repository

```bash
git clone https://github.com/17sculver/Tetris-With-Spring-Boot.git
cd Tetris-With-Spring-Boot
```

## Build the Project

```bash
mvn clean install
```

This command will:
- Clean previous builds
- Download all dependencies
- Compile the Java source code
- Run tests (if any)
- Package the application as a JAR file

## Run the Application

### Option 1: Using Maven

```bash
mvn spring-boot:run
```

### Option 2: Using the JAR file

```bash
java -jar target/tetris-multiplayer-1.0.0.jar
```

## Access the Application

Open your browser and navigate to:

```
http://localhost:8080
```

## Features

- **User Authentication**: Register and login with JWT authentication
- **Real-time Multiplayer**: Play Tetris with another player using WebSocket
- **Game Management**: Create game sessions and join existing games
- **Player Statistics**: Track high scores, total games, and wins

## Database

The application uses:
- **Development**: H2 in-memory database (auto-configured)
- **Production**: MySQL 8.2.0 (configure in `application.properties`)

### H2 Console (Development Only)

Access the H2 database console at:

```
http://localhost:8080/h2-console
```

Default credentials:
- URL: `jdbc:h2:mem:tetrisdb`
- Username: `sa`
- Password: (leave empty)

## API Endpoints

### Authentication

- **POST** `/api/auth/register` - Register a new player
- **POST** `/api/auth/login` - Login and receive JWT token

### Game Management

- **POST** `/api/game/create` - Create a new game session
- **POST** `/api/game/join/{sessionId}` - Join an existing game
- **GET** `/api/game/sessions` - Get all active game sessions
- **GET** `/api/game/history` - Get player's game history

## WebSocket Endpoints

The application uses STOMP over WebSocket for real-time communication:

- **Connect**: `/ws/tetris` (with SockJS fallback)
- **Subscribe**: `/topic/game/{sessionId}` - Receive game state updates
- **Send**: `/app/game.move` - Send player moves

## Development

### Project Structure

```
src/main/
├── java/com/tetris/
│   ├── TetrisMultiplayerApplication.java (Main entry point)
│   ├── config/          (Spring configurations)
│   ├── controller/      (REST endpoints)
│   ├── entity/          (JPA entities)
│   ├── repository/      (Data access layer)
│   ├── service/         (Business logic)
│   ├── security/        (JWT & authentication)
│   └── websocket/       (WebSocket handlers)
├── resources/
│   ├── application.properties
│   ├── application-test.properties
│   └── templates/index.html
└── static/
    ├── css/style.css
    └── js/app.js
```

### Configuration

Edit `src/main/resources/application.properties` to configure:

- Server port: `server.port=8080`
- Database: `spring.datasource.*`
- JWT secret: `app.jwt.secret`
- JWT expiration: `app.jwt.expiration`

## Troubleshooting

### Maven not found

If you see "mvn is not recognized", ensure Maven is installed and in your PATH:

```bash
mvn --version
```

### Java not found

If you see "java is not recognized", ensure Java is installed and JAVA_HOME is set:

```bash
java -version
```

### Port 8080 already in use

Change the port in `application.properties`:

```properties
server.port=8081
```

### H2 console not accessible

Ensure H2 is enabled in `application.properties`:

```properties
spring.h2.console.enabled=true
```

## Next Steps

1. **Test Authentication**: Register a new account and login
2. **Create a Game**: Start a new multiplayer Tetris game
3. **Implement Game Logic**: Add piece movement, rotation, and collision detection (see IMPLEMENTATION_NOTES.md)
4. **Connect Frontend**: Test WebSocket communication between two browser instances

## Documentation

For more detailed information, see:

- [README.md](README.md) - Project overview and architecture
- [DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md) - Development guidelines and best practices
- [IMPLEMENTATION_NOTES.md](IMPLEMENTATION_NOTES.md) - Implementation details and TODOs

## Support

For issues or questions, please check the documentation files or review the inline code comments.
