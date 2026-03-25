package com.tetris.controller;

import com.tetris.dto.AuthResponse;
import com.tetris.dto.LoginRequest;
import com.tetris.dto.RegisterRequest;
import com.tetris.dto.UserDto;
import com.tetris.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AuthController Integration Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthResponse successResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        UserDto userDto = new UserDto(1L, "testuser", "test@example.com", 0L, 0, 0, false, System.currentTimeMillis());
        successResponse = new AuthResponse(true, "Success", "jwt-token-123", userDto);
    }

    @Test
    @DisplayName("POST /auth/register - successful registration")
    void testRegisterSuccess() throws Exception {
        // Arrange
        when(authService.register(any(RegisterRequest.class))).thenReturn(successResponse);

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value("jwt-token-123"))
                .andExpect(jsonPath("$.user.username").value("testuser"));
    }

    @Test
    @DisplayName("POST /auth/register - password mismatch")
    void testRegisterPasswordMismatch() throws Exception {
        // Arrange
        AuthResponse failResponse = new AuthResponse(false, "Passwords do not match", null, null);
        when(authService.register(any(RegisterRequest.class))).thenReturn(failResponse);

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Passwords do not match"));
    }

    @Test
    @DisplayName("POST /auth/login - successful login")
    void testLoginSuccess() throws Exception {
        // Arrange
        when(authService.login(any(LoginRequest.class))).thenReturn(successResponse);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value("jwt-token-123"))
                .andExpect(jsonPath("$.user.username").value("testuser"));
    }

    @Test
    @DisplayName("POST /auth/login - invalid credentials")
    void testLoginInvalidCredentials() throws Exception {
        // Arrange
        AuthResponse failResponse = new AuthResponse(false, "Invalid username or password", null, null);
        when(authService.login(any(LoginRequest.class))).thenReturn(failResponse);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    @DisplayName("POST /auth/logout - success")
    void testLogout() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer jwt-token-123"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /auth/validate - valid token")
    void testValidateToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/auth/validate")
                        .header("Authorization", "Bearer jwt-token-123"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /auth/register - missing required fields")
    void testRegisterMissingFields() throws Exception {
        // Arrange
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setUsername(""); // Missing username
        invalidRequest.setEmail("test@example.com");
        invalidRequest.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/login - missing credentials")
    void testLoginMissingCredentials() throws Exception {
        // Arrange
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setUsername(""); // Missing username

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
