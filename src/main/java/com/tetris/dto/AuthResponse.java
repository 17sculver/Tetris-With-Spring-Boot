package com.tetris.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    // TODO: Add validation annotations (@NotNull for required fields)
    // TODO: Consider using enum for standardized response types
    // TODO: Add proper error codes instead of generic success/message
    private Boolean success;
    private String message;
    private String token;
    private UserDto user;
}
