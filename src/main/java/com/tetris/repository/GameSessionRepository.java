package com.tetris.repository;

import com.tetris.entity.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
    Optional<GameSession> findBySessionId(String sessionId);
    List<GameSession> findByStatus(GameSession.GameStatus status);
    List<GameSession> findByPlayerOneId(Long playerId);
    List<GameSession> findByPlayerTwoId(Long playerId);

    // TODO: Add method to find all active games for a specific player (playerOne OR playerTwo)
    // TODO: Add method to find available games to join (status = WAITING, playerTwo IS NULL)
    // TODO: Add method to find games won by a specific player
    // TODO: Add method to find games within date range (created between dates)
    // TODO: Add method to find games by multiple statuses (IN_PROGRESS OR COMPLETED)
    // TODO: Add pagination support for large game lists
    // TODO: Add @Query with JOIN to fetch player details with game sessions
    // TODO: Add method to count games by status for statistics
    // TODO: Add method to find longest running active games
    // TODO: Add @Modifying @Query for bulk status updates
    // TODO: Add method to find games by player with specific outcomes
}
