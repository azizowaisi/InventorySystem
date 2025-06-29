package com.teckiz.InventorySystem.exceptions;

import com.teckiz.InventorySystem.dto.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleNotFoundException_ShouldReturnNotFoundResponse() {
        // Arrange
        NotFoundException exception = new NotFoundException("User not found");

        // Act
        ResponseEntity<Response> response = exceptionHandler.handleNotFoundException(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("User not found", response.getBody().getMessage());
    }

    @Test
    void handleInvalidCredentialsException_ShouldReturnBadRequestResponse() {
        // Arrange
        InvalidCredentialsException exception = new InvalidCredentialsException("Invalid password");

        // Act
        ResponseEntity<Response> response = exceptionHandler.handleInvalidCredentialsException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Invalid password", response.getBody().getMessage());
    }

    @Test
    void handleNameValueRequiredException_ShouldReturnBadRequestResponse() {
        // Arrange
        NameValueRequiredException exception = new NameValueRequiredException("Name is required");

        // Act
        ResponseEntity<Response> response = exceptionHandler.handleNameValueRequiredException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Name is required", response.getBody().getMessage());
    }

    @Test
    void handleAllExceptions_ShouldReturnInternalServerErrorResponse() {
        // Arrange
        Exception exception = new Exception("Unexpected error occurred");

        // Act
        ResponseEntity<Response> response = exceptionHandler.handleAllExceptions(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Unexpected error occurred", response.getBody().getMessage());
    }

    @Test
    void handleRuntimeException_ShouldReturnInternalServerErrorResponse() {
        // Arrange
        RuntimeException exception = new RuntimeException("Runtime error");

        // Act
        ResponseEntity<Response> response = exceptionHandler.handleAllExceptions(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Runtime error", response.getBody().getMessage());
    }

    @Test
    void handleIllegalArgumentException_ShouldReturnInternalServerErrorResponse() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        // Act
        ResponseEntity<Response> response = exceptionHandler.handleAllExceptions(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Invalid argument", response.getBody().getMessage());
    }

    @Test
    void handleNullPointerException_ShouldReturnInternalServerErrorResponse() {
        // Arrange
        NullPointerException exception = new NullPointerException("Null pointer");

        // Act
        ResponseEntity<Response> response = exceptionHandler.handleAllExceptions(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Null pointer", response.getBody().getMessage());
    }

    @Test
    void handleException_WithNullMessage_ShouldReturnInternalServerErrorResponse() {
        // Arrange
        Exception exception = new Exception();

        // Act
        ResponseEntity<Response> response = exceptionHandler.handleAllExceptions(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertNull(response.getBody().getMessage());
    }

    @Test
    void handleNotFoundException_WithNullMessage_ShouldReturnNotFoundResponse() {
        // Arrange
        NotFoundException exception = new NotFoundException(null);

        // Act
        ResponseEntity<Response> response = exceptionHandler.handleNotFoundException(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertNull(response.getBody().getMessage());
    }
} 