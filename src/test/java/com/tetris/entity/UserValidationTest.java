package com.tetris.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Entity Validation Tests")
class UserValidationTest {

    private Validator validator;
    private User user;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("hashedPassword123");
        user.setHighScore(0);
        user.setTotalGames(0);
        user.setWins(0);
        user.setIsOnline(false);
    }

    @Test
    @DisplayName("Valid user should pass validation")
    void testValidUserPasses() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Valid user should have no violations");
    }

    @Test
    @DisplayName("Username cannot be blank")
    void testUsernameCannotBeBlank() {
        user.setUsername("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Blank username should fail validation");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Username is required")));
    }

    @Test
    @DisplayName("Username must be between 3 and 20 characters")
    void testUsernameLengthValidation() {
        // Too short
        user.setUsername("ab");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Username too short should fail");

        // Too long
        user.setUsername("abcdefghijklmnopqrstu");
        violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Username too long should fail");

        // Valid length
        user.setUsername("validuser");
        violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Username with valid length should pass");
    }

    @Test
    @DisplayName("Username pattern validation")
    void testUsernamePatternValidation() {
        // Valid patterns
        user.setUsername("user123");
        assertTrue(validator.validate(user).isEmpty(), "Alphanumeric username should pass");

        user.setUsername("user_name");
        assertTrue(validator.validate(user).isEmpty(), "Username with underscore should pass");

        user.setUsername("user-name");
        assertTrue(validator.validate(user).isEmpty(), "Username with hyphen should pass");

        // Invalid patterns
        user.setUsername("user@name");
        assertFalse(validator.validate(user).isEmpty(), "Username with @ should fail");

        user.setUsername("user name");
        assertFalse(validator.validate(user).isEmpty(), "Username with space should fail");

        user.setUsername("user!name");
        assertFalse(validator.validate(user).isEmpty(), "Username with ! should fail");
    }

    @Test
    @DisplayName("Email validation")
    void testEmailValidation() {
        // Valid emails
        user.setEmail("test@example.com");
        assertTrue(validator.validate(user).isEmpty(), "Valid email should pass");

        user.setEmail("user.name@example.co.uk");
        assertTrue(validator.validate(user).isEmpty(), "Email with dots should pass");

        // Invalid emails
        user.setEmail("invalid-email");
        assertFalse(validator.validate(user).isEmpty(), "Invalid email format should fail");

        user.setEmail("");
        assertFalse(validator.validate(user).isEmpty(), "Empty email should fail");

        user.setEmail("test@");
        assertFalse(validator.validate(user).isEmpty(), "Incomplete email should fail");
    }

    @Test
    @DisplayName("High score validation")
    void testHighScoreValidation() {
        // Valid scores
        user.setHighScore(0);
        assertTrue(validator.validate(user).isEmpty(), "Zero score should pass");

        user.setHighScore(999999);
        assertTrue(validator.validate(user).isEmpty(), "Valid max score should pass");

        // Invalid scores
        user.setHighScore(-1);
        assertFalse(validator.validate(user).isEmpty(), "Negative score should fail");

        user.setHighScore(1000000);
        assertFalse(validator.validate(user).isEmpty(), "Score exceeding max should fail");
    }

    @Test
    @DisplayName("Total games validation")
    void testTotalGamesValidation() {
        user.setTotalGames(0);
        assertTrue(validator.validate(user).isEmpty(), "Zero games should pass");

        user.setTotalGames(100);
        assertTrue(validator.validate(user).isEmpty(), "Positive games should pass");

        user.setTotalGames(-1);
        assertFalse(validator.validate(user).isEmpty(), "Negative games should fail");
    }

    @Test
    @DisplayName("Wins validation")
    void testWinsValidation() {
        user.setWins(0);
        assertTrue(validator.validate(user).isEmpty(), "Zero wins should pass");

        user.setWins(50);
        assertTrue(validator.validate(user).isEmpty(), "Positive wins should pass");

        user.setWins(-1);
        assertFalse(validator.validate(user).isEmpty(), "Negative wins should fail");
    }

    @Test
    @DisplayName("Game statistics business logic validation")
    void testGameStatisticsBusinessLogic() {
        // Valid: wins <= totalGames
        user.setTotalGames(10);
        user.setWins(5);
        assertDoesNotThrow(() -> {
            user.validateGameStatistics();
        }, "Valid game statistics should not throw");

        // Invalid: wins > totalGames
        user.setTotalGames(5);
        user.setWins(10);
        assertThrows(IllegalArgumentException.class, () -> {
            user.validateGameStatistics();
        }, "Wins greater than total games should throw exception");
    }
}
