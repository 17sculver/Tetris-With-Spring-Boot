package com.tetris.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "game_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", unique = true, nullable = false)
    private String sessionId;

    @Column(name = "player_one_id", nullable = false)
    private Long playerOneId;

    @Column(name = "player_two_id")
    private Long playerTwoId;

    @Column(name = "max_players")
    private Integer maxPlayers = 2;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private GameStatus status = GameStatus.WAITING;

    @Column(name = "winner_id")
    private Long winnerId;

    @Column(name = "created_at")
    private Long createdAt;

    @Column(name = "started_at")
    private Long startedAt;

    @Column(name = "ended_at")
    private Long endedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = System.currentTimeMillis();
    }

    public enum GameStatus {
        WAITING, IN_PROGRESS, COMPLETED, CANCELLED
    }
}
