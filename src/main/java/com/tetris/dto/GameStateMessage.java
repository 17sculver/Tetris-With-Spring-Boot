package com.tetris.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameStateMessage {
    // TODO: Add validation annotations (@NotNull, @NotBlank, @Min, @Max)
    // TODO: Define messageType as enum instead of string for type safety
    // TODO: Consider more efficient boardState representation (byte[] or compressed format)
    // TODO: Add documentation for all messageType values (GAME_STARTED, GAME_STATE_UPDATE, OPPONENT_MOVE, GAME_ENDED, etc.)
    // TODO: Consider more complex nextPiece representation (Tetrimino object with rotation, position)
    // TODO: Add validation to ensure timestamp is not in the future
    // TODO: Add business logic validation (score should increase, lines should be non-negative, etc.)
    private String messageType; // GAME_STARTED, GAME_STATE_UPDATE, OPPONENT_MOVE, GAME_ENDED, etc.
    private String sessionId;
    private Long playerId;
    private Integer[][] boardState;
    private Integer nextPiece;
    private Integer score;
    private Integer lines;
    private Integer level;
    private Boolean gameOver;
    private Long winnerId;
    private Long timestamp;
}
