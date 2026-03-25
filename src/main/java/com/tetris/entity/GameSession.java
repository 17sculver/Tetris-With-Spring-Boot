package com.tetris.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "game_sessions", indexes = {
    // TODO: Add database indexes for commonly queried fields (session_id, status, player_one_id, player_two_id)
    // TODO: Consider composite indexes for complex queries
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: Add validation annotations (@NotBlank, @Size, @Pattern for session ID format)
    // TODO: Consider UUID type instead of String for better uniqueness guarantees
    @Column(name = "session_id", unique = true, nullable = false)
    private String sessionId;

    // TODO: Replace with @ManyToOne relationship to User entity
    // TODO: Add @JoinColumn and foreign key constraints
    // TODO: Add validation that player exists and is active
    @Column(name = "player_one_id", nullable = false)
    private Long playerOneId;

    // TODO: Replace with @ManyToOne relationship to User entity
    // TODO: Add business logic validation (playerTwoId != playerOneId when present)
    @Column(name = "player_two_id")
    private Long playerTwoId;

    // TODO: Add validation annotations (@Min(2), @Max for reasonable player limits)
    @Column(name = "max_players")
    private Integer maxPlayers = 2;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private GameStatus status = GameStatus.WAITING;

    // TODO: Replace with @ManyToOne relationship to User entity
    // TODO: Add validation that winner is one of the players in the session
    @Column(name = "winner_id")
    private Long winnerId;

    // TODO: Change to proper timestamp type (@Temporal(TemporalType.TIMESTAMP))
    // TODO: Add @Column(updatable = false) and @CreatedDate
    @Column(name = "created_at")
    private Long createdAt;

    // TODO: Change to proper timestamp type (@Temporal(TemporalType.TIMESTAMP))
    // TODO: Add business logic validation (startedAt >= createdAt)
    @Column(name = "started_at")
    private Long startedAt;

    // TODO: Change to proper timestamp type (@Temporal(TemporalType.TIMESTAMP))
    // TODO: Add business logic validation (endedAt >= startedAt when present)
    @Column(name = "ended_at")
    private Long endedAt;

    // TODO: Add @LastModifiedDate and @Version for optimistic locking
    // TODO: Add @OneToMany relationships for game moves/events if needed

    @PrePersist
    protected void onCreate() {
        createdAt = System.currentTimeMillis();
    }

    public enum GameStatus {
        WAITING, IN_PROGRESS, COMPLETED, CANCELLED
    }
}
