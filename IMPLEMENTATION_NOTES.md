# Implementation Notes - Skeleton Structure

This document outlines the current skeleton structure and what remains to be implemented for a fully functional multiplayer Tetris game.

## Current Implementation Status

### ✅ Completed Components

#### Backend Infrastructure
- [x] Spring Boot 3.2 project setup with Maven
- [x] Spring Security configuration with JWT authentication
- [x] WebSocket/STOMP configuration for real-time communication
- [x] JPA/Hibernate entity mapping for Users and GameSessions
- [x] Database repositories for data persistence
- [x] REST API endpoints for authentication and game management
- [x] Service layer for business logic
- [x] DTOs for data transfer
- [x] Error handling and validation

#### Frontend Infrastructure
- [x] HTML structure with login/registration forms
- [x] Game board canvas rendering
- [x] CSS styling with responsive design
- [x] JavaScript framework for client-side logic
- [x] WebSocket client setup with STOMP
- [x] Authentication flow (login/register)
- [x] Game menu navigation
- [x] Statistics and history display

#### Multi-player Support
- [x] Session creation and joining
- [x] Real-time state synchronization via WebSocket
- [x] Player statistics tracking
- [x] Game history logging

### 🔄 Partially Implemented

- [ ] Game board rendering (basic canvas structure exists)
- [ ] Keyboard input handling (structure in place, logic incomplete)
- [ ] WebSocket message broadcasting (structure exists, needs refinement)

### ❌ Not Yet Implemented

#### Core Game Logic
1. **Tetrimino Management**
   - [ ] Random piece generation
   - [ ] Piece rotation logic with wall kick detection
   - [ ] Piece placement and locking
   - [ ] Next piece preview
   - [ ] Hold piece functionality

2. **Board Operations**
   - [ ] Collision detection
   - [ ] Line clearing detection
   - [ ] Gravity simulation (piece falling)
   - [ ] Full row elimination with cascading

3. **Game Mechanics**
   - [ ] Score calculation (with line multipliers)
   - [ ] Level progression system
   - [ ] Game speed increase per level
   - [ ] Game over detection
   - [ ] Pause/resume functionality

4. **Multiplayer Synchronization**
   - [ ] Opponent board state updates
   - [ ] Move validation before broadcasting
   - [ ] Game state reconciliation
   - [ ] Opponent piece preview/hold display
   - [ ] Real-time score synchronization

#### Advanced Features
1. **Power-ups and Special Rules**
   - [ ] Garbage lines system
   - [ ] Special attack pieces
   - [ ] Combo system
   - [ ] T-spin detection and scoring

2. **UI Enhancements**
   - [ ] Game animations (piece placement, line clearing)
   - [ ] Sound effects
   - [ ] Background music
   - [ ] Visual feedback for opponent actions
   - [ ] Countdown timer for game start

3. **Data Persistence**
   - [ ] Save game snapshots
   - [ ] Replay system
   - [ ] Leaderboard rankings
   - [ ] Achievement system

---

## Recommended Implementation Order

### Phase 1: Core Tetris Logic (High Priority)
1. **Piece Generation and Storage**
   ```java
   // Add to GameService
   class TetriminoGenerator {
       private Random random = new Random();
       
       public Tetrimino generateRandomPiece() {
           // Returns one of 7 tetromino types
       }
   }
   
   class Tetrimino {
       int[][] shape;
       int x, y;
       int rotation;
       int color;
   }
   ```

2. **Board Collision Detection**
   ```java
   class GameBoard {
       private static final int WIDTH = 10;
       private static final int HEIGHT = 20;
       private int[][] grid;
       
       public boolean canPlace(Tetrimino piece, int x, int y) {
           // Check if piece can be placed at position
       }
       
       public boolean isColliding(Tetrimino piece) {
           // Check collision with existing blocks
       }
   }
   ```

3. **Line Clearing**
   ```java
   public int clearCompleteLines() {
       // Detect and remove complete rows
       // Return number of lines cleared
   }
   ```

### Phase 2: Game Loop and Controls (High Priority)
1. **Game Tick System**
   ```java
   @Component
   class GameLoopManager {
       private static final long TICK_INTERVAL = 100; // ms
       
       @Scheduled(fixedRate = TICK_INTERVAL)
       public void gameLoop() {
           // Update game state
           // Handle gravity
           // Check for game over
       }
   }
   ```

2. **Input Processing**
   ```java
   // Complete these methods in WebSocket handler
   public void handleMove(String direction) { }
   public void handleRotate() { }
   public void handleDrop() { }
   public void handleHold() { }
   ```

### Phase 3: Multiplayer Synchronization (Medium Priority)
1. **State Serialization**
   - Ensure game state can be serialized to JSON
   - Implement efficient delta updates (only send changed blocks)

2. **Opponent Board Display**
   ```javascript
   // In app.js - Complete opponent board rendering
   function updateOpponentBoard(gameState) {
       const opponentCanvas = document.getElementById('opponent-canvas');
       // Render opponent's board state
   }
   ```

3. **Move Validation**
   - Validate moves on server before broadcasting
   - Prevent cheating through move verification

### Phase 4: Polish and Features (Medium Priority)
1. **Animations**
   - Line clearing animations
   - Piece placement feedback
   - Combo effects

2. **Additional Game Rules**
   - T-spin detection
   - B2B bonuses
   - Garbage line attacks

3. **UI/UX**
   - Game announcements
   - Pause screen
   - Game over modal
   - Spectator mode

---

## Code Structure for Tetris Logic

### Suggested Package Structure
```
com.tetris.
├── game/              (New package)
│   ├── board/
│   │   ├── GameBoard.java
│   │   ├── Cell.java
│   │   └── BoardState.java
│   ├── piece/
│   │   ├── Tetrimino.java
│   │   ├── TetriminoType.java
│   │   └── TetriminoFactory.java
│   ├── logic/
│   │   ├── GameEngine.java
│   │   ├── CollisionDetector.java
│   │   ├── LineClearing.java
│   │   └── ScoreCalculator.java
│   └── state/
│       ├── GameState.java
│       ├── GameStatus.java
│       └── GameLevel.java
```

### Example: Tetrimino Class
```java
package com.tetris.game.piece;

public class Tetrimino {
    private TetriminoType type;
    private int[][] shape;
    private int x, y;  // Grid position
    private int rotation;  // 0-3
    private Color color;
    
    public Tetrimino(TetriminoType type) {
        this.type = type;
        this.shape = type.getInitialShape();
        this.rotation = 0;
        this.color = type.getColor();
    }
    
    public void rotateClockwise() {
        // Implement rotation with wall kick detection
        int newRotation = (rotation + 1) % 4;
        if (canRotate(newRotation)) {
            rotation = newRotation;
            applyRotation();
        }
    }
    
    public void moveLeft() { x--; }
    public void moveRight() { x++; }
    public void moveDown() { y++; }
    
    public int[][] getShape() { return shape; }
    public int getX() { return x; }
    public int getY() { return y; }
}
```

### Example: Game Board
```java
package com.tetris.game.board;

public class GameBoard {
    public static final int WIDTH = 10;
    public static final int HEIGHT = 20;
    private int[][] grid;
    private Tetrimino activePiece;
    
    public GameBoard() {
        this.grid = new int[HEIGHT][WIDTH];
    }
    
    public boolean canPlace(Tetrimino piece, int x, int y) {
        int[][] shape = piece.getShape();
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 0) continue;
                
                int boardX = x + col;
                int boardY = y + row;
                
                if (boardX < 0 || boardX >= WIDTH ||
                    boardY < 0 || boardY >= HEIGHT ||
                    grid[boardY][boardX] != 0) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public void placePiece(Tetrimino piece) {
        int[][] shape = piece.getShape();
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 0) continue;
                grid[piece.getY() + row][piece.getX() + col] = 1;
            }
        }
    }
    
    public int clearCompleteLines() {
        int cleared = 0;
        for (int row = HEIGHT - 1; row >= 0; row--) {
            if (isLineComplete(row)) {
                removeLine(row);
                cleared++;
            }
        }
        return cleared;
    }
}
```

---

## Testing Strategy for Game Logic

### Unit Tests to Add
```java
@SpringBootTest
class GameEngineTest {
    
    @Test
    void testPiecePlacement() {
        // Test piece can be placed at valid position
    }
    
    @Test
    void testCollisionDetection() {
        // Test collision with walls and blocks
    }
    
    @Test
    void testLineClearing() {
        // Test complete line detection and removal
    }
    
    @Test
    void testRotationLogic() {
        // Test piece rotation with wall kick
    }
    
    @Test
    void testScoreCalculation() {
        // Test score formula with multipliers
    }
}
```

---

## Known Issues and Limitations

### Current Skeleton Limitations
1. **No Actual Game Logic**: Board is rendered but pieces don't fall or collide
2. **No Authentication Tokens in WebSocket**: WebSocket subscriptions don't validate auth
3. **Placeholder User IDs**: User lookup uses hardcoded ID (1) instead of from auth context
4. **No Game Loop**: No server-side game state progression
5. **Static UI**: Frontend UI doesn't update based on game events

### Future Improvements
- Implement server-side game loop for authoritative game state
- Add client-side prediction for better responsiveness
- Implement game statistics tracking
- Add anti-cheat measures
- Optimize WebSocket message frequency
- Add support for more than 2 players per session

---

## Development Tips

1. **Start with backend logic first** - Implement game engine and validate with unit tests
2. **Use test-driven development** - Write tests before implementing game logic
3. **Keep state synchronized** - Always broadcast complete state to avoid inconsistencies
4. **Test locally first** - Use single-player mode before testing multiplayer
5. **Monitor WebSocket traffic** - Use browser DevTools to inspect WebSocket messages
6. **Version your API** - Plan for future changes with versioning (/api/v1/...)

---

## References

- Tetris Game Guidelines: https://tetris.com/
- SEGA Tetris Engine Documentation
- Standard Tetris Mechanics: https://en.wikipedia.org/wiki/Tetris
- Wall Kick System: https://tetris.fandom.com/wiki/SRS

---

For questions about the skeleton structure, refer to the [DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md) or [README.md](README.md).
