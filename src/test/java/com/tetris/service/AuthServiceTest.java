package com.tetris.service;

import com.tetris.dto.AuthResponse;
import com.tetris.dto.LoginRequest;
import com.tetris.dto.RegisterRequest;
import com.tetris.entity.User;
import com.tetris.repository.UserRepository;
import com.tetris.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;

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

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashedPassword");
        testUser.setHighScore(0);
        testUser.setTotalGames(0);
        testUser.setWins(0);
        testUser.setIsOnline(false);
    }

    @Test
    @DisplayName("Successful user registration")
    void testSuccessfulRegistration() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtTokenProvider.generateTokenFromUsername("testuser")).thenReturn("jwt-token");

        // Act
        AuthResponse response = authService.register(registerRequest);

        // Assert
        assertTrue(response.getSuccess(), "Registration should be successful");
        assertEquals("Registration successful", response.getMessage());
        assertNotNull(response.getToken(), "Token should be generated");
        assertNotNull(response.getUser(), "User data should be returned");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Registration fails when passwords do not match")
    void testRegistrationPasswordMismatch() {
        // Arrange
        registerRequest.setConfirmPassword("differentPassword");

        // Act
        AuthResponse response = authService.register(registerRequest);

        // Assert
        assertFalse(response.getSuccess(), "Registration should fail");
        assertEquals("Passwords do not match", response.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Registration fails when username already exists")
    void testRegistrationDuplicateUsername() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act
        AuthResponse response = authService.register(registerRequest);

        // Assert
        assertFalse(response.getSuccess(), "Registration should fail");
        assertEquals("Username already exists", response.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Registration fails when email already exists")
    void testRegistrationDuplicateEmail() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act
        AuthResponse response = authService.register(registerRequest);

        // Assert
        assertFalse(response.getSuccess(), "Registration should fail");
        assertEquals("Email already exists", response.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Successful user login")
    void testSuccessfulLogin() {
        // Arrange
        Authentication mockAuth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateToken(mockAuth)).thenReturn("jwt-token");

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertTrue(response.getSuccess(), "Login should be successful");
        assertEquals("Login successful", response.getMessage());
        assertNotNull(response.getToken(), "Token should be generated");
        assertNotNull(response.getUser(), "User data should be returned");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Login fails with invalid credentials")
    void testLoginInvalidCredentials() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertFalse(response.getSuccess(), "Login should fail");
        assertEquals("Invalid username or password", response.getMessage());
    }

    @Test
    @DisplayName("Login fails when user not found after authentication")
    void testLoginUserNotFound() {
        // Arrange
        Authentication mockAuth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertFalse(response.getSuccess(), "Login should fail");
        assertEquals("User not found", response.getMessage());
    }

    @Test
    @DisplayName("User online status is updated after login")
    void testUserOnlineStatusUpdated() {
        // Arrange
        Authentication mockAuth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateToken(mockAuth)).thenReturn("jwt-token");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertTrue(response.getSuccess());
        verify(userRepository).save(argThat(user -> user.getIsOnline() == true));
    }
}
