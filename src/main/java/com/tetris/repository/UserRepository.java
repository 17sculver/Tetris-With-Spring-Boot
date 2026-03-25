package com.tetris.repository;

import com.tetris.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // TODO: Add method to find all online users for real-time features
    // TODO: Add leaderboard queries with pagination and sorting
    // TODO: Add method to find users by high score range for rankings
    // TODO: Add custom query for user statistics (win rate, games played, etc.)
    // TODO: Add soft delete support with @Query methods that exclude deleted users
    // TODO: Add method to find users created within date range for analytics
    // TODO: Add @Query with JOIN for user's active game sessions
    // TODO: Add method to find users by multiple criteria (username OR email)
    // TODO: Add pagination support for large user lists
    // TODO: Add @Modifying @Query for bulk operations (mark users offline, etc.)
}
