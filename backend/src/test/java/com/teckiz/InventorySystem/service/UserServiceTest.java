package com.teckiz.InventorySystem.service;

import com.teckiz.InventorySystem.dto.LoginRequest;
import com.teckiz.InventorySystem.dto.RegisterRequest;
import com.teckiz.InventorySystem.dto.Response;
import com.teckiz.InventorySystem.dto.UserDTO;
import com.teckiz.InventorySystem.entity.User;
import com.teckiz.InventorySystem.enums.UserRole;
import com.teckiz.InventorySystem.exceptions.InvalidCredentialsException;
import com.teckiz.InventorySystem.exceptions.NotFoundException;
import com.teckiz.InventorySystem.repository.UserRepository;
import com.teckiz.InventorySystem.security.JwtUtils;
import com.teckiz.InventorySystem.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDTO userDTO;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .phoneNumber("1234567890")
                .role(UserRole.MANAGER)
                .transactions(new ArrayList<>())
                .build();

        userDTO = new UserDTO();
        userDTO.setName("John Doe");
        userDTO.setEmail("john@example.com");
        userDTO.setPhoneNumber("1234567890");
        userDTO.setRole(UserRole.MANAGER);
        userDTO.setTransactions(new ArrayList<>());

        loginRequest = new LoginRequest();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("password123");

        registerRequest = new RegisterRequest();
        registerRequest.setName("John Doe");
        registerRequest.setEmail("john@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setPhoneNumber("1234567890");
        registerRequest.setRole(UserRole.MANAGER);
    }

    @Test
    @DisplayName("Should register user successfully")
    void registerUser_Success() {
        // Given
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        Response response = userService.registerUser(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("user created successfully", response.getMessage());
        
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should register user with default role when role is null")
    void registerUser_WithDefaultRole() {
        // Given
        registerRequest.setRole(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        Response response = userService.registerUser(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("user created successfully", response.getMessage());
        
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should login user successfully")
    void loginUser_Success() {
        // Given
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtils.generateToken(testUser.getEmail())).thenReturn("jwtToken");

        // When
        Response response = userService.loginUser(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("user logged in successfully", response.getMessage());
        assertEquals(testUser.getRole(), response.getRole());
        assertEquals("jwtToken", response.getToken());
        assertEquals("6 month", response.getExpirationTime());
        
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), testUser.getPassword());
        verify(jwtUtils).generateToken(testUser.getEmail());
    }

    @Test
    @DisplayName("Should throw NotFoundException when email not found during login")
    void loginUser_EmailNotFound() {
        // Given
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> userService.loginUser(loginRequest));
        verify(userRepository).findByEmail(loginRequest.getEmail());
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when password doesn't match")
    void loginUser_PasswordMismatch() {
        // Given
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // When & Then
        assertThrows(InvalidCredentialsException.class, () -> userService.loginUser(loginRequest));
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), testUser.getPassword());
    }

    @Test
    @DisplayName("Should return all users successfully")
    void getAllUsers_Success() {
        // Given
        List<User> users = Arrays.asList(
            User.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .phoneNumber("1234567890")
                .role(UserRole.MANAGER)
                .transactions(new ArrayList<>())
                .build(),
            User.builder()
                .name("Jane Smith")
                .email("jane@example.com")
                .password("encodedPassword")
                .phoneNumber("0987654321")
                .role(UserRole.ADMIN)
                .transactions(new ArrayList<>())
                .build()
        );

        List<UserDTO> userDTOs = Arrays.asList(
            createUserDTO(1L, "John Doe", "john@example.com", "1234567890", UserRole.MANAGER),
            createUserDTO(2L, "Jane Smith", "jane@example.com", "0987654321", UserRole.ADMIN)
        );

        when(userRepository.findAll(any(Sort.class))).thenReturn(users);
        when(modelMapper.map(any(List.class), any(java.lang.reflect.Type.class))).thenReturn(userDTOs);

        // When
        Response response = userService.getAllUsers();

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("success", response.getMessage());
        assertNotNull(response.getUsers());
        assertEquals(2, response.getUsers().size());
        
        verify(userRepository).findAll(any(Sort.class));
        verify(modelMapper).map(any(List.class), any(java.lang.reflect.Type.class));
    }

    private UserDTO createUserDTO(Long id, String name, String email, String phoneNumber, UserRole role) {
        UserDTO dto = new UserDTO();
        dto.setId(id);
        dto.setName(name);
        dto.setEmail(email);
        dto.setPhoneNumber(phoneNumber);
        dto.setRole(role);
        dto.setTransactions(new ArrayList<>());
        return dto;
    }

    @Test
    @DisplayName("Should get current logged in user successfully")
    void getCurrentLoggedInUser_Success() {
        // Given
        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("john@example.com");
            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));

            // When
            User result = userService.getCurrentLoggedInUser();

            // Then
            assertNotNull(result);
            assertEquals(testUser.getName(), result.getName());
            assertEquals(testUser.getEmail(), result.getEmail());
            assertNull(result.getTransactions());
            
            mockedSecurityContext.verify(SecurityContextHolder::getContext);
            verify(securityContext).getAuthentication();
            verify(authentication).getName();
            verify(userRepository).findByEmail("john@example.com");
        }
    }

    @Test
    @DisplayName("Should throw NotFoundException when current user not found")
    void getCurrentLoggedInUser_UserNotFound() {
        // Given
        try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("john@example.com");
            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(NotFoundException.class, () -> userService.getCurrentLoggedInUser());
            mockedSecurityContext.verify(SecurityContextHolder::getContext);
            verify(securityContext).getAuthentication();
            verify(authentication).getName();
            verify(userRepository).findByEmail("john@example.com");
        }
    }

    @Test
    @DisplayName("Should update user successfully")
    void updateUser_Success() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        Response response = userService.updateUser(userId, userDTO);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("User Successfully updated", response.getMessage());
        
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw NotFoundException when updating non-existent user")
    void updateUser_UserNotFound() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> userService.updateUser(userId, userDTO));
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Should delete user successfully")
    void deleteUser_Success() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        Response response = userService.deleteUser(userId);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("User Successfully Deleted", response.getMessage());
        
        verify(userRepository).findById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    @DisplayName("Should throw NotFoundException when deleting non-existent user")
    void deleteUser_UserNotFound() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserTransactions_Success() {
        // Arrange
        User userWithTransactions = User.builder()
                .name("John Doe")
                .email("john@example.com")
                .transactions(Arrays.asList()) // Empty list to avoid NPE
                .build();
        
        UserDTO userDTOWithTransactions = new UserDTO();
        userDTOWithTransactions.setId(1L);
        userDTOWithTransactions.setTransactions(Arrays.asList()); // Empty list to avoid NPE
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(userWithTransactions));
        when(modelMapper.map(userWithTransactions, UserDTO.class)).thenReturn(userDTOWithTransactions);

        // Act
        Response response = userService.getUserTransactions(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("success", response.getMessage());
        assertNotNull(response.getUser());
        verify(userRepository).findById(1L);
        verify(modelMapper).map(userWithTransactions, UserDTO.class);
    }

    @Test
    void getUserTransactions_UserNotFound_ShouldThrowNotFoundException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.getUserTransactions(1L));
        verify(userRepository).findById(1L);
        verifyNoInteractions(modelMapper);
    }
} 