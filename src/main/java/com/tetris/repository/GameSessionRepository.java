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
}
