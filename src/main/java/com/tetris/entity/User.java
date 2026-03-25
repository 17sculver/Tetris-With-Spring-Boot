package com.tetris.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;

/**
 * User entity representing a player in the multiplayer Tetris game system.
 * This entity manages user authentication, profile information, and game statistics.
 * 
 * Key Features:
 * - Secure password storage with BCrypt hashing
 * - Unique username and email constraints for authentication
 * - Game statistics tracking (high score, games played, wins)
 * - Online status for matchmaking and real-time features
 * - Password reset token support for account recovery
 * 
 * Database Indexes:
 * - Single column indexes on username, email, is_online for fast lookups
 * - Composite indexes for combined queries (username+online, email+online)
 * - Optimized for authentication, matchmaking, and user management operations
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_username", columnList = "username"),
    @Index(name = "idx_user_email", columnList = "email"),
    @Index(name = "idx_user_is_online", columnList = "is_online"),
    @Index(name = "idx_user_username_online", columnList = "username, is_online"),
    @Index(name = "idx_user_email_online", columnList = "email, is_online")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Username validation: not blank, 3-20 chars, alphanumeric with underscore/hyphen
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username can only contain letters, numbers, underscores, and hyphens")
    @Column(nullable = false, unique = true, length = 20)
    private String username;

    // Password is now hashed using BCrypt in AuthService
    // @JsonIgnore prevents password from being serialized in responses
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    // Separate field for password reset tokens (can be used for password recovery)
    @JsonIgnore
    @Column(name = "password_reset_token")
    private String passwordResetToken;

    // TODO: Add validation annotations (@NotBlank, @Email, @Size)
    // TODO: Consider @Column(length = X) for email storage optimization
    @Column(nullable = false, unique = true)
    private String email;

    // TODO: Add validation annotations (@Min(0), @Max for reasonable score limits)
    @Column(name = "high_score")
    private Long highScore = 0L;

    // TODO: Add validation annotations (@Min(0))
    // TODO: Consider business logic validation (wins <= totalGames)
    @Column(name = "total_games")
    private Integer totalGames = 0;

    // TODO: Add validation annotations (@Min(0))
    @Column(name = "wins")
    private Integer wins = 0;

    @Column(name = "is_online")
    private Boolean isOnline = false;

    // TODO: Change to proper timestamp type (@Temporal(TemporalType.TIMESTAMP))
    // TODO: Add @Column(updatable = false) to prevent manual updates
    // TODO: Consider @CreatedDate with Spring Data auditing
    @Column(name = "created_at")
    private Long createdAt;

    // TODO: Add @LastModifiedDate and @Version for optimistic locking with Spring Data auditing
    // TODO: Add relationships to GameSession (@OneToMany with mappedBy)

    @PrePersist
    protected void onCreate() {
        createdAt = System.currentTimeMillis();
    }
}
