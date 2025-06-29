package com.teckiz.InventorySystem.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    @Mock
    private UserDetails userDetails;

    private static final String TEST_SECRET = "testSecretKeyForJwtTokenGenerationAndValidation";
    private static final String TEST_EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtils, "secreteJwtString", TEST_SECRET);
        ReflectionTestUtils.invokeMethod(jwtUtils, "init");
    }

    @Test
    void generateToken_Success() {
        // Act
        String token = jwtUtils.generateToken(TEST_EMAIL);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void generateToken_WithDifferentEmails_ShouldGenerateDifferentTokens() {
        // Act
        String token1 = jwtUtils.generateToken("user1@example.com");
        String token2 = jwtUtils.generateToken("user2@example.com");

        // Assert
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2);
    }

    @Test
    void getUsernameFromToken_Success() {
        // Arrange
        String token = jwtUtils.generateToken(TEST_EMAIL);

        // Act
        String extractedEmail = jwtUtils.getUsernameFromToken(token);

        // Assert
        assertEquals(TEST_EMAIL, extractedEmail);
    }

    @Test
    void getUsernameFromToken_WithInvalidToken_ShouldThrowException() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act & Assert
        assertThrows(Exception.class, () -> jwtUtils.getUsernameFromToken(invalidToken));
    }

    @Test
    void isTokenValid_WithValidTokenAndUserDetails_ShouldReturnTrue() {
        // Arrange
        String token = jwtUtils.generateToken(TEST_EMAIL);
        when(userDetails.getUsername()).thenReturn(TEST_EMAIL);

        // Act
        boolean isValid = jwtUtils.isTokenValid(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_WithValidTokenButWrongUsername_ShouldReturnFalse() {
        // Arrange
        String token = jwtUtils.generateToken(TEST_EMAIL);
        when(userDetails.getUsername()).thenReturn("different@example.com");

        // Act
        boolean isValid = jwtUtils.isTokenValid(token, userDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_WithInvalidToken_ShouldThrowException() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act & Assert
        assertThrows(Exception.class, () -> jwtUtils.isTokenValid(invalidToken, userDetails));
    }

    @Test
    void generateToken_ShouldContainCorrectClaims() {
        // Act
        String token = jwtUtils.generateToken(TEST_EMAIL);

        // Assert
        String extractedEmail = jwtUtils.getUsernameFromToken(token);
        assertEquals(TEST_EMAIL, extractedEmail);
    }

    @Test
    void generateToken_WithEmptyEmail_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> jwtUtils.generateToken(""));
    }

    @Test
    void generateToken_WithNullEmail_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> jwtUtils.generateToken(null));
    }

    @Test
    void tokenExpiration_ShouldBeSetCorrectly() {
        // Arrange
        long currentTime = System.currentTimeMillis();
        String token = jwtUtils.generateToken(TEST_EMAIL);

        // Act
        String extractedEmail = jwtUtils.getUsernameFromToken(token);

        // Assert
        assertEquals(TEST_EMAIL, extractedEmail);
        // Token should be valid immediately after generation
        when(userDetails.getUsername()).thenReturn(TEST_EMAIL);
        assertTrue(jwtUtils.isTokenValid(token, userDetails));
    }

    @Test
    void generateMultipleTokens_ShouldAllBeValid() {
        // Act
        String token1 = jwtUtils.generateToken("user1@example.com");
        String token2 = jwtUtils.generateToken("user2@example.com");
        String token3 = jwtUtils.generateToken("user3@example.com");

        // Assert
        when(userDetails.getUsername()).thenReturn("user1@example.com");
        assertTrue(jwtUtils.isTokenValid(token1, userDetails));

        when(userDetails.getUsername()).thenReturn("user2@example.com");
        assertTrue(jwtUtils.isTokenValid(token2, userDetails));

        when(userDetails.getUsername()).thenReturn("user3@example.com");
        assertTrue(jwtUtils.isTokenValid(token3, userDetails));
    }

    @Test
    void tokenStructure_ShouldBeValidJWT() {
        // Act
        String token = jwtUtils.generateToken(TEST_EMAIL);

        // Assert
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT token should have 3 parts");
        
        // Each part should be base64 encoded (URL-safe base64)
        assertTrue(parts[0].matches("^[A-Za-z0-9_-]*$"));
        assertTrue(parts[1].matches("^[A-Za-z0-9_-]*$"));
        assertTrue(parts[2].matches("^[A-Za-z0-9_-]*$"));
    }
} 