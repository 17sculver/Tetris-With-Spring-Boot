package com.tetris.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "users", indexes = {
    // TODO: Add database indexes for commonly queried fields (username, email, is_online)
    // TODO: Consider composite indexes for performance optimization
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: Add validation annotations (@NotBlank, @Size, @Pattern for username constraints)
    // TODO: Consider @Column(length = X) for optimal storage
    @Column(nullable = false, unique = true)
    private String username;

    // TODO: CRITICAL: Password should be hashed using BCrypt - never store plain text
    // TODO: Add @JsonIgnore to prevent password from being serialized in responses
    // TODO: Consider separate field for password reset tokens/salt
    @Column(nullable = false)
    private String password;

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
