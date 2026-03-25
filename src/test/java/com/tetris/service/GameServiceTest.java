package com.tetris.service;

import com.tetris.entity.GameSession;
import com.tetris.entity.GameStatus;
import com.tetris.repository.GameSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GameService Tests")
class GameServiceTest {

    @Mock
    private GameSessionRepository gameSessionRepository;

    @InjectMocks
    private GameService gameService;

    private GameSession testSession;
    private Long testUserId;

    @BeforeEach
    void setUp() {
        testUserId = 1L;
        testSession = new GameSession();
        testSession.setId(1L);
        testSession.setSessionId(UUID.randomUUID().toString());
        testSession.setPlayerOneId(testUserId);
        testSession.setMaxPlayers(2);
        testSession.setStatus(GameStatus.WAITING);
        testSession.setWinnerId(null);
    }

    @Test
    @DisplayName("Create game session successfully")
    void testCreateGameSession() {
        // Arrange
        when(gameSessionRepository.save(any(GameSession.class))).thenReturn(testSession);

        // Act
        GameSession result = gameService.createGameSession(testUserId);

        // Assert
        assertNotNull(result, "Game session should be created");
        assertEquals(testUserId, result.getPlayerOneId(), "Player one ID should match");
        assertEquals(GameStatus.WAITING, result.getStatus(), "Initial status should be WAITING");
        assertNull(result.getPlayerTwoId(), "Player two should be null initially");
        verify(gameSessionRepository, times(1)).save(any(GameSession.class));
    }

    @Test
    @DisplayName("Join game session successfully")
    void testJoinGameSession() {
        // Arrange
        Long playerTwoId = 2L;
        testSession.setPlayerTwoId(playerTwoId);
        testSession.setStatus(GameStatus.IN_PROGRESS);

        when(gameSessionRepository.findBySessionId(anyString())).thenReturn(Optional.of(testSession));
        when(gameSessionRepository.save(any(GameSession.class))).thenReturn(testSession);

        // Act
        GameSession result = gameService.joinGameSession(testSession.getSessionId(), playerTwoId);

        // Assert
        assertNotNull(result, "Game session should be retrieved");
        assertEquals(playerTwoId, result.getPlayerTwoId(), "Player two should be set");
        assertEquals(GameStatus.IN_PROGRESS, result.getStatus(), "Status should be IN_PROGRESS");
        verify(gameSessionRepository, times(1)).save(any(GameSession.class));
    }

    @Test
    @DisplayName("Cannot join non-existent game session")
    void testJoinNonExistentSession() {
        // Arrange
        when(gameSessionRepository.findBySessionId(anyString())).thenReturn(Optional.empty());

        // Act
        GameSession result = gameService.joinGameSession("non-existent-id", 2L);

        // Assert
        assertNull(result, "Result should be null for non-existent session");
    }

    @Test
    @DisplayName("Cannot join game if already full")
    void testJoinFullGame() {
        // Arrange
        testSession.setPlayerTwoId(2L);
        testSession.setStatus(GameStatus.IN_PROGRESS);

        when(gameSessionRepository.findBySessionId(anyString())).thenReturn(Optional.of(testSession));

        // Act
        GameSession result = gameService.joinGameSession(testSession.getSessionId(), 3L);

        // Assert
        assertEquals(2L, result.getPlayerTwoId(), "Player two should still be original player");
        verify(gameSessionRepository, never()).save(any(GameSession.class));
    }

    @Test
    @DisplayName("End game session successfully")
    void testEndGameSession() {
        // Arrange
        Long winnerId = testUserId;
        testSession.setWinnerId(winnerId);
        testSession.setStatus(GameStatus.FINISHED);

        when(gameSessionRepository.findBySessionId(anyString())).thenReturn(Optional.of(testSession));
        when(gameSessionRepository.save(any(GameSession.class))).thenReturn(testSession);

        // Act
        GameSession result = gameService.endGameSession(testSession.getSessionId(), winnerId);

        // Assert
        assertNotNull(result, "Ended game session should be returned");
        assertEquals(winnerId, result.getWinnerId(), "Winner ID should be set");
        assertEquals(GameStatus.FINISHED, result.getStatus(), "Status should be FINISHED");
        verify(gameSessionRepository, times(1)).save(any(GameSession.class));
    }

    @Test
    @DisplayName("Get game session by ID")
    void testGetGameSessionById() {
        // Arrange
        when(gameSessionRepository.findBySessionId(testSession.getSessionId()))
                .thenReturn(Optional.of(testSession));

        // Act
        GameSession result = gameService.getGameSessionById(testSession.getSessionId());

        // Assert
        assertNotNull(result, "Game session should be found");
        assertEquals(testSession.getSessionId(), result.getSessionId(), "Session IDs should match");
    }

    @Test
    @DisplayName("Get non-existent game session returns null")
    void testGetNonExistentGameSession() {
        // Arrange
        when(gameSessionRepository.findBySessionId(anyString()))
                .thenReturn(Optional.empty());

        // Act
        GameSession result = gameService.getGameSessionById("non-existent-id");

        // Assert
        assertNull(result, "Non-existent session should return null");
    }

    @Test
    @DisplayName("Cannot join own game session")
    void testCannotJoinOwnSession() {
        // Arrange
        when(gameSessionRepository.findBySessionId(anyString())).thenReturn(Optional.of(testSession));

        // Act
        GameSession result = gameService.joinGameSession(testSession.getSessionId(), testUserId);

        // Assert
        assertNull(result, "Cannot join own session");
        verify(gameSessionRepository, never()).save(any(GameSession.class));
    }

    @Test
    @DisplayName("Game status transitions correctly through lifecycle")
    void testGameStatusLifecycle() {
        // Create -> WAITING
        assertEquals(GameStatus.WAITING, testSession.getStatus());

        // Join -> IN_PROGRESS
        testSession.setPlayerTwoId(2L);
        testSession.setStatus(GameStatus.IN_PROGRESS);
        assertEquals(GameStatus.IN_PROGRESS, testSession.getStatus());

        // End -> FINISHED
        testSession.setStatus(GameStatus.FINISHED);
        testSession.setWinnerId(1L);
        assertEquals(GameStatus.FINISHED, testSession.getStatus());
    }
}
