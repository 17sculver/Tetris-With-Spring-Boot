package com.tetris.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameMoveMessage {
    private Long userId;
    private String sessionId;
    private String messageType; // MOVE, ROTATE, DROP, GAME_OVER, etc.
    private String action; // LEFT, RIGHT, DOWN, ROTATE_CW, ROTATE_CCW, etc.
    private Integer[][] boardState; // Current game board state
    private Integer score;
    private Long timestamp;
}
