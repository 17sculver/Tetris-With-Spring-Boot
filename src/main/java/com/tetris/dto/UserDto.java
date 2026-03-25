package com.tetris.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private Long highScore;
    private Integer totalGames;
    private Integer wins;
    private Boolean isOnline;
    private Long createdAt;
}
