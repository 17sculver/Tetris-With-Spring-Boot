package com.tetris.entity;

/**
 * Enum representing the status of a game session.
 * Defines the lifecycle states through which a game progresses.
 */
public enum GameStatus {
    /**
     * Game session created, waiting for second player to join
     */
    WAITING("Waiting for opponent"),

    /**
     * Both players have joined, game is actively in progress
     */
    IN_PROGRESS("Game in progress"),

    /**
     * Game has ended, winner determined
     */
    FINISHED("Game finished"),

    /**
     * Game was abandoned/cancelled before completion
     */
    CANCELLED("Game cancelled");

    private final String displayName;

    GameStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
