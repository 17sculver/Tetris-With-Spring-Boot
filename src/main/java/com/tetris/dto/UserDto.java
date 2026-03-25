package com.tetris.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    // TODO: Add validation annotations (@NotNull, @Positive, @Min, @Max where appropriate)
    // TODO: Consider @JsonFormat for date/timestamp fields
    // TODO: Add @JsonIgnore for sensitive fields if any are added later
    // TODO: Consider separate DTOs for different use cases (create, update, response)
    // TODO: Add business logic validation (wins <= totalGames, highScore >= 0, etc.)
    private Long id;
    private String username;
    private String email;
    private Long highScore;
    private Integer totalGames;
    private Integer wins;
    private Boolean isOnline;
    private Long createdAt;
}
