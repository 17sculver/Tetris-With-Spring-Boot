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

    // Email validation: not blank, valid email format, max length 255
    @NotBlank(message = "Email is required")    
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must be less than 255 characters")   
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Min(value = 0, message = "High score cannot be negative")
    @Max(value = 999999999, message = "High score exceeds maximum limit")
    @Column(name = "high_score")
    private Long highScore = 0L;

    @Min(value = 0, message = "Total games cannot be negative")
    @Column(name = "total_games")
    private Integer totalGames = 0;

    @Min(value = 0, message = "Wins cannot be negative")
    @Column(name = "wins")
    private Integer wins = 0;

    @Column(name = "is_online")
    private Boolean isOnline = false;

    // Consider @CreatedDate with Spring Data auditing for automatic timestamp management
    @Column(name = "created_at", nullable = false, updatable = false)
    private Long createdAt;

    // Future enhancement: Add @LastModifiedDate and @Version for optimistic locking with Spring Data auditing
    // Future enhancement: Add @OneToMany relationship to GameSession (mappedBy = "user")

    @PrePersist
    protected void onCreate() {
        createdAt = System.currentTimeMillis();
    }
}
