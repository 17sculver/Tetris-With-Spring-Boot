package com.tetris.controller;

import com.tetris.dto.AuthResponse;
import com.tetris.dto.LoginRequest;
import com.tetris.dto.RegisterRequest;
import com.tetris.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return response.getSuccess() ? ResponseEntity.ok(response) : 
               ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return response.getSuccess() ? ResponseEntity.ok(response) : 
               ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        // TODO: Implement proper JWT token blacklisting/invalidation
        // TODO: Extract token from Authorization header and validate format
        // TODO: Add token to blacklist or implement token revocation
        // Token handling would be extracted and user identified here
        return ResponseEntity.ok().build();
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        // TODO: Implement proper JWT token validation using JwtUtil or similar
        // TODO: Extract token from "Bearer " prefix and validate signature
        // TODO: Check token expiration and claims
        // TODO: Verify user exists and is active in database
        // TODO: Add proper error handling for malformed tokens
        if (token != null && token.startsWith("Bearer ")) {
            // Token validation would happen here
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);
    }
}
