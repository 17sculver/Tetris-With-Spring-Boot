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
        Long userId = 1L; // This should be fetched from the User entity using username

        GameSession gameSession = gameService.createGameSession(userId);
        return ResponseEntity.ok(gameSession);
    }

    @PostMapping("/join-session/{sessionId}")
    public ResponseEntity<GameSession> joinGameSession(@PathVariable String sessionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        // TODO: Fetch user ID from database using username from authentication
        Long userId = 1L; // This should be fetched from the User entity using username

        GameSession gameSession = gameService.joinGameSession(sessionId, userId);

        if (gameSession != null) {
            return ResponseEntity.ok(gameSession);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<GameSession> getGameSession(@PathVariable String sessionId) {
        GameSession gameSession = gameService.getGameSession(sessionId);

        if (gameSession != null) {
            return ResponseEntity.ok(gameSession);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/waiting-sessions")
    public ResponseEntity<List<GameSession>> getWaitingGameSessions() {
        List<GameSession> waitingSessions = gameService.getWaitingGameSessions();
        return ResponseEntity.ok(waitingSessions);
    }

    @PostMapping("/end-game/{sessionId}")
    public ResponseEntity<GameSession> endGame(@PathVariable String sessionId, 
                                               @RequestBody Map<String, Long> request) {
        Long winnerId = request.get("winnerId");
        GameSession gameSession = gameService.endGame(sessionId, winnerId);

        if (gameSession != null) {
            return ResponseEntity.ok(gameSession);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<GameSession>> getGameHistory() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        // TODO: Fetch user ID from database using username from authentication
        Long userId = 1L; // This should be fetched from the User entity using username

        List<GameSession> history = gameService.getPlayerGameHistory(userId);
        return ResponseEntity.ok(history);
    }
}
