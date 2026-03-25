package com.tetris.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameStateMessage {
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
