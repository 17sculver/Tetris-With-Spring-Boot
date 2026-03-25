package com.tetris.controller;

import com.tetris.entity.GameSession;
import com.tetris.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/create-session")
    public ResponseEntity<GameSession> createGameSession() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        // TODO: Fetch user ID from database using username from authentication
        // TODO: Add proper error handling for unauthenticated users
        // TODO: Validate user exists and is active before creating session
        Long userId = 1L; // This should be fetched from the User entity using username

        GameSession gameSession = gameService.createGameSession(userId);
        return ResponseEntity.ok(gameSession);
    }

    @PostMapping("/join-session/{sessionId}")
    public ResponseEntity<GameSession> joinGameSession(@PathVariable String sessionId) {
        // TODO: Add validation for sessionId format and existence
        // TODO: Check if session is still accepting players
        // TODO: Prevent user from joining their own session
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        // TODO: Fetch user ID from database using username from authentication
        Long userId = 1L; // This should be fetched from the User entity using username

        GameSession gameSession = gameService.joinGameSession(sessionId, userId);

        if (gameSession != null) {
            return ResponseEntity.ok(gameSession);
        } else {
            // TODO: Return proper error message instead of null body
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<GameSession> getGameSession(@PathVariable String sessionId) {
        // TODO: Add validation for sessionId format
        // TODO: Check if user has permission to view this session
        GameSession gameSession = gameService.getGameSession(sessionId);

        if (gameSession != null) {
            return ResponseEntity.ok(gameSession);
        } else {
            // TODO: Return proper error message instead of null body
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/waiting-sessions")
    public ResponseEntity<List<GameSession>> getWaitingGameSessions() {
        // TODO: Add pagination for large result sets
        // TODO: Add filtering options (game type, player count, etc.)
        // TODO: Consider caching frequently accessed waiting sessions
        List<GameSession> waitingSessions = gameService.getWaitingGameSessions();
        return ResponseEntity.ok(waitingSessions);
    }

    @PostMapping("/end-game/{sessionId}")
    public ResponseEntity<GameSession> endGame(@PathVariable String sessionId, 
                                               @RequestBody Map<String, Long> request) {
        // TODO: Add validation for winnerId (not null, user exists in session)
        // TODO: Verify requesting user has permission to end the game
        // TODO: Add proper error handling and response messages
        Long winnerId = request.get("winnerId");
        GameSession gameSession = gameService.endGame(sessionId, winnerId);

        if (gameSession != null) {
            return ResponseEntity.ok(gameSession);
        } else {
            // TODO: Return proper error message instead of null body
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<GameSession>> getGameHistory() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        // TODO: Fetch user ID from database using username from authentication
        // TODO: Add pagination for large result sets
        // TODO: Add filtering options (date range, game status, etc.)
        Long userId = 1L; // This should be fetched from the User entity using username

        List<GameSession> history = gameService.getPlayerGameHistory(userId);
        return ResponseEntity.ok(history);
    }
}
