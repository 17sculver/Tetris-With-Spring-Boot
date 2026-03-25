package com.tetris.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    // TODO: Add validation annotations (@NotBlank, @Size, @Email, @Pattern for username/email)
    // TODO: Add password strength validation (@Pattern or custom validator)
    // TODO: Add custom validation to ensure password matches confirmPassword
    // TODO: Consider password hashing requirements (though this is request DTO)
    // TODO: Add rate limiting considerations (this DTO is used for registration endpoints)
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
}
