package com.tetris.service;

import com.tetris.dto.AuthResponse;
import com.tetris.dto.LoginRequest;
import com.tetris.dto.RegisterRequest;
import com.tetris.dto.UserDto;
import com.tetris.entity.User;
import com.tetris.repository.UserRepository;
import com.tetris.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        // Validate request
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return new AuthResponse(false, "Passwords do not match", null, null);
        }

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            return new AuthResponse(false, "Username already exists", null, null);
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return new AuthResponse(false, "Email already exists", null, null);
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIsOnline(false);

        User savedUser = userRepository.save(user);

        // Generate token
        String token = jwtTokenProvider.generateTokenFromUsername(savedUser.getUsername());

        UserDto userDto = new UserDto(
                savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(),
                savedUser.getHighScore(), savedUser.getTotalGames(), savedUser.getWins(),
                savedUser.getIsOnline(), savedUser.getCreatedAt()
        );

        return new AuthResponse(true, "Registration successful", token, userDto);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            String token = jwtTokenProvider.generateToken(authentication);

            User user = userRepository.findByUsername(request.getUsername()).orElse(null);

            if (user != null) {
                user.setIsOnline(true);
                userRepository.save(user);

                UserDto userDto = new UserDto(
                        user.getId(), user.getUsername(), user.getEmail(),
                        user.getHighScore(), user.getTotalGames(), user.getWins(),
                        user.getIsOnline(), user.getCreatedAt()
                );

                return new AuthResponse(true, "Login successful", token, userDto);
            }

            return new AuthResponse(false, "User not found", null, null);

        } catch (Exception e) {
            return new AuthResponse(false, "Invalid username or password", null, null);
        }
    }

    public void logout(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            user.setIsOnline(false);
            userRepository.save(user);
        }
    }
}
