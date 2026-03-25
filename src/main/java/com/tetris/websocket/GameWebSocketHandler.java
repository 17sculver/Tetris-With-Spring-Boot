package com.tetris.websocket;

import com.tetris.dto.GameMoveMessage;
import com.tetris.dto.GameStateMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
public class GameWebSocketHandler {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final Map<String, GameSession> activeSessions = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MessageMapping("/game/{sessionId}/join")
    @SendTo("/topic/game/{sessionId}")
    public GameStateMessage playerJoined(@DestinationVariable String sessionId, 
                                        GameStateMessage message) {
        message.setMessageType("PLAYER_JOINED");
        message.setTimestamp(System.currentTimeMillis());

        // Initialize or find session
        GameSession gameSession = activeSessions.getOrDefault(sessionId, new GameSession());
        gameSession.addPlayer(message.getPlayerId());
        activeSessions.put(sessionId, gameSession);

        return message;
    }

    @MessageMapping("/game/{sessionId}/move")
    public void handleGameMove(@DestinationVariable String sessionId, GameMoveMessage message) {
        message.setTimestamp(System.currentTimeMillis());

        // Broadcast move to opponent
        messagingTemplate.convertAndSend(
                "/topic/game/" + sessionId,
                message
        );
    }

    @MessageMapping("/game/{sessionId}/update")
    public void updateGameState(@DestinationVariable String sessionId, GameStateMessage message) {
        message.setTimestamp(System.currentTimeMillis());

        // Broadcast state update to all players in the session
        messagingTemplate.convertAndSend(
                "/topic/game/" + sessionId,
                message
        );
    }

    @MessageMapping("/game/{sessionId}/game-over")
    @SendTo("/topic/game/{sessionId}")
    public GameStateMessage gameOver(@DestinationVariable String sessionId, 
                                     GameStateMessage message) {
        message.setMessageType("GAME_ENDED");
        message.setTimestamp(System.currentTimeMillis());

        // Clean up session
        GameSession gameSession = activeSessions.remove(sessionId);

        return message;
    }

    @MessageMapping("/game/{sessionId}/leave")
    public void playerLeft(@DestinationVariable String sessionId, GameStateMessage message) {
        message.setMessageType("PLAYER_LEFT");
        message.setTimestamp(System.currentTimeMillis());

        // Broadcast player left message
        messagingTemplate.convertAndSend(
                "/topic/game/" + sessionId,
                message
        );

        // Remove session if empty
        GameSession gameSession = activeSessions.get(sessionId);
        if (gameSession != null) {
            gameSession.removePlayer(message.getPlayerId());
            if (gameSession.getPlayersCount() == 0) {
                activeSessions.remove(sessionId);
            }
        }
    }

    /**
     * Internal class to track active game sessions
     */
    private static class GameSession {
        private Map<Long, String> players = new HashMap<>();
        private Integer[][] board;
        private Integer currentLevel = 1;
        private Long startTime;

        public void addPlayer(Long playerId) {
            players.put(playerId, "ACTIVE");
        }

        public void removePlayer(Long playerId) {
            players.remove(playerId);
        }

        public int getPlayersCount() {
            return players.size();
        }

        public Integer[][] getBoard() {
            return board;
        }

        public void setBoard(Integer[][] board) {
            this.board = board;
        }
    }
}
