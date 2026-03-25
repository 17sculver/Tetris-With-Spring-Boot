package com.tetris.service;

import com.tetris.entity.GameSession;
import com.tetris.entity.User;
import com.tetris.repository.GameSessionRepository;
import com.tetris.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GameService {

    private final GameSessionRepository gameSessionRepository;
    private final UserRepository userRepository;

    public GameService(GameSessionRepository gameSessionRepository, UserRepository userRepository) {
        this.gameSessionRepository = gameSessionRepository;
        this.userRepository = userRepository;
    }

    public GameSession createGameSession(Long playerOneId) {
        GameSession gameSession = new GameSession();
        gameSession.setSessionId(UUID.randomUUID().toString());
        gameSession.setPlayerOneId(playerOneId);
        gameSession.setStatus(GameSession.GameStatus.WAITING);

        return gameSessionRepository.save(gameSession);
    }

    public GameSession joinGameSession(String sessionId, Long playerTwoId) {
        Optional<GameSession> gameSessionOpt = gameSessionRepository.findBySessionId(sessionId);

        if (gameSessionOpt.isPresent()) {
            GameSession gameSession = gameSessionOpt.get();

            if (gameSession.getPlayerTwoId() == null && 
                !gameSession.getPlayerOneId().equals(playerTwoId)) {
                gameSession.setPlayerTwoId(playerTwoId);
                gameSession.setStatus(GameSession.GameStatus.IN_PROGRESS);
                gameSession.setStartedAt(System.currentTimeMillis());

                return gameSessionRepository.save(gameSession);
            }
        }

        return null;
    }

    public GameSession getGameSession(String sessionId) {
        return gameSessionRepository.findBySessionId(sessionId).orElse(null);
    }

    public List<GameSession> getWaitingGameSessions() {
        return gameSessionRepository.findByStatus(GameSession.GameStatus.WAITING);
    }

    public GameSession endGame(String sessionId, Long winnerId) {
        Optional<GameSession> gameSessionOpt = gameSessionRepository.findBySessionId(sessionId);

        if (gameSessionOpt.isPresent()) {
            GameSession gameSession = gameSessionOpt.get();
            gameSession.setStatus(GameSession.GameStatus.COMPLETED);
            gameSession.setWinnerId(winnerId);
            gameSession.setEndedAt(System.currentTimeMillis());

            // Update user stats
            User winner = userRepository.findById(winnerId).orElse(null);
            if (winner != null) {
                winner.setWins(winner.getWins() + 1);
                winner.setTotalGames(winner.getTotalGames() + 1);
                userRepository.save(winner);
            }

            return gameSessionRepository.save(gameSession);
        }

        return null;
    }

    public List<GameSession> getPlayerGameHistory(Long playerId) {
        List<GameSession> playerOneGames = gameSessionRepository.findByPlayerOneId(playerId);
        List<GameSession> playerTwoGames = gameSessionRepository.findByPlayerTwoId(playerId);

        playerOneGames.addAll(playerTwoGames);
        return playerOneGames;
    }

    // TODO: Implement core Tetris game logic methods
    // TODO: validateMove(String sessionId, Long playerId, GameMove move) - validate piece movement
    // TODO: detectCollision(int[][] board, Tetrimino piece, int x, int y) - collision detection
    // TODO: clearCompleteLines(int[][] board) - line clearing and cascading
    // TODO: generateRandomPiece() - random Tetrimino generation
    // TODO: calculateScore(int linesCleared, int level) - score calculation with multipliers
    // TODO: rotatePiece(Tetrimino piece, int rotation) - piece rotation with wall kick
    // TODO: updateGameStateAfterMove(String sessionId, GameMove move) - server-side game state update
    // TODO: progressGameLevel(String sessionId) - level progression and speed increase
    // TODO: checkGameOverCondition(String sessionId) - detect game over state
}
