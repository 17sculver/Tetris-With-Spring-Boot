package com.tetris.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameMoveMessage {
    // TODO: Add validation annotations (@NotNull, @NotBlank, @Min, @Max)
    // TODO: Define messageType and action as enums instead of strings for type safety
    // TODO: Consider more efficient boardState representation (byte[] or compressed format)
    // TODO: Add documentation for all messageType values (MOVE, ROTATE, DROP, GAME_OVER, etc.)
    // TODO: Add documentation for all action values (LEFT, RIGHT, DOWN, ROTATE_CW, ROTATE_CCW, etc.)
    // TODO: Add validation to ensure timestamp is not in the future
    private Long userId;
    private String sessionId;
    private String messageType; // MOVE, ROTATE, DROP, GAME_OVER, etc.
    private String action; // LEFT, RIGHT, DOWN, ROTATE_CW, ROTATE_CCW, etc.
    private Integer[][] boardState; // Current game board state
    private Integer score;
    private Long timestamp;
}
