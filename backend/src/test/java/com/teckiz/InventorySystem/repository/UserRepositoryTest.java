package com.teckiz.InventorySystem.repository;

import com.teckiz.InventorySystem.entity.User;
import com.teckiz.InventorySystem.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        entityManager.clear();

        // Create test users
        testUser1 = User.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("encodedPassword1")
                .phoneNumber("1234567890")
                .role(UserRole.MANAGER)
                .build();

        testUser2 = User.builder()
                .name("Jane Smith")
                .email("jane@example.com")
                .password("encodedPassword2")
                .phoneNumber("0987654321")
                .role(UserRole.ADMIN)
                .build();
    }

    @Test
    void saveUser_Success() {
        // Act
        User savedUser = userRepository.save(testUser1);

        // Assert
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals("John Doe", savedUser.getName());
        assertEquals("john@example.com", savedUser.getEmail());
        assertEquals(UserRole.MANAGER, savedUser.getRole());
        assertNotNull(savedUser.getKey());
        assertNotNull(savedUser.getCreatedAt());
        assertNotNull(savedUser.getUpdatedAt());
    }

    @Test
    void findById_Success() {
        // Arrange
        User savedUser = entityManager.persistAndFlush(testUser1);

        // Act
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals("John Doe", foundUser.get().getName());
        assertEquals("john@example.com", foundUser.get().getEmail());
    }

    @Test
    void findById_UserNotFound_ShouldReturnEmpty() {
        // Act
        Optional<User> foundUser = userRepository.findById(999L);

        // Assert
        assertFalse(foundUser.isPresent());
    }

    @Test
    void findByEmail_Success() {
        // Arrange
        entityManager.persistAndFlush(testUser1);

        // Act
        Optional<User> foundUser = userRepository.findByEmail("john@example.com");

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals("John Doe", foundUser.get().getName());
        assertEquals("john@example.com", foundUser.get().getEmail());
    }

    @Test
    void findByEmail_UserNotFound_ShouldReturnEmpty() {
        // Act
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertFalse(foundUser.isPresent());
    }

    @Test
    void findAll_Success() {
        // Arrange
        entityManager.persistAndFlush(testUser1);
        entityManager.persistAndFlush(testUser2);

        // Act
        List<User> users = userRepository.findAll();

        // Assert
        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(user -> "john@example.com".equals(user.getEmail())));
        assertTrue(users.stream().anyMatch(user -> "jane@example.com".equals(user.getEmail())));
    }

    @Test
    void findAll_EmptyDatabase_ShouldReturnEmptyList() {
        // Act
        List<User> users = userRepository.findAll();

        // Assert
        assertTrue(users.isEmpty());
    }

    @Test
    void updateUser_Success() {
        // Arrange
        User savedUser = entityManager.persistAndFlush(testUser1);
        String originalUpdatedAt = savedUser.getUpdatedAt().toString();

        // Add a small delay to ensure different timestamps
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act - Modify the existing entity and use merge to ensure update
        savedUser.setName("John Updated");
        savedUser.setPhoneNumber("5555555555");
        entityManager.merge(savedUser);
        entityManager.flush();
        entityManager.clear();
        
        // Retrieve the updated entity
        User result = userRepository.findById(savedUser.getId()).orElse(null);

        // Assert
        assertNotNull(result);
        assertEquals("John Updated", result.getName());
        assertEquals("5555555555", result.getPhoneNumber());
        assertNotEquals(originalUpdatedAt, result.getUpdatedAt().toString());
    }

    @Test
    void deleteUser_Success() {
        // Arrange
        User savedUser = entityManager.persistAndFlush(testUser1);

        // Act
        userRepository.deleteById(savedUser.getId());

        // Assert
        Optional<User> deletedUser = userRepository.findById(savedUser.getId());
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void saveUser_WithUniqueEmailConstraint_ShouldSucceed() {
        // Arrange
        User user1 = User.builder()
                .name("User 1")
                .email("unique@example.com")
                .password("password1")
                .phoneNumber("1111111111")
                .role(UserRole.MANAGER)
                .build();

        User user2 = User.builder()
                .name("User 2")
                .email("unique2@example.com")
                .password("password2")
                .phoneNumber("2222222222")
                .role(UserRole.ADMIN)
                .build();

        // Act
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        // Assert
        assertNotNull(savedUser1.getId());
        assertNotNull(savedUser2.getId());
        assertNotEquals(savedUser1.getId(), savedUser2.getId());
    }

    @Test
    void saveUser_WithDuplicateEmail_ShouldThrowException() {
        // Arrange
        User user1 = User.builder()
                .name("User 1")
                .email("duplicate@example.com")
                .password("password1")
                .phoneNumber("1111111111")
                .role(UserRole.MANAGER)
                .build();

        User user2 = User.builder()
                .name("User 2")
                .email("duplicate@example.com") // Same email
                .password("password2")
                .phoneNumber("2222222222")
                .role(UserRole.ADMIN)
                .build();

        // Act & Assert
        userRepository.save(user1);
        assertThrows(Exception.class, () -> userRepository.save(user2));
    }

    @Test
    void saveUser_WithDifferentRoles_ShouldSucceed() {
        // Arrange
        User manager = User.builder()
                .name("Manager")
                .email("manager@example.com")
                .password("password")
                .phoneNumber("1111111111")
                .role(UserRole.MANAGER)
                .build();

        User admin = User.builder()
                .name("Admin")
                .email("admin@example.com")
                .password("password")
                .phoneNumber("2222222222")
                .role(UserRole.ADMIN)
                .build();

        // Act
        User savedManager = userRepository.save(manager);
        User savedAdmin = userRepository.save(admin);

        // Assert
        assertEquals(UserRole.MANAGER, savedManager.getRole());
        assertEquals(UserRole.ADMIN, savedAdmin.getRole());
    }

    @Test
    void saveUser_WithNullRole_ShouldUseDefault() {
        // Arrange
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .phoneNumber("1234567890")
                .role(null)
                .build();

        // Act
        User savedUser = userRepository.save(user);

        // Assert
        assertNotNull(savedUser.getRole()); // Should have a default value
    }
} 