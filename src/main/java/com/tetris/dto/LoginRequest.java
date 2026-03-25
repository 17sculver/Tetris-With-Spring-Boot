package com.tetris.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    // TODO: Add validation annotations (@NotBlank, @Size for username and password)
    // TODO: Consider @JsonProperty for custom field names if needed
    // TODO: Add rate limiting considerations (this DTO is used for auth endpoints)
    private String username;
    private String password;
}
