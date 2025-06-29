# Inventory Management System - Test Documentation

## Overview

This directory contains comprehensive tests for the Spring Boot Inventory Management System. The tests cover all layers of the application including services, controllers, repositories, security, and exception handling.

## Test Structure

### 1. Unit Tests (Service Layer)
- **UserServiceTest**: Tests for user management operations including registration, login, CRUD operations
- **ProductServiceTest**: Tests for product management including image handling and validation

### 2. Integration Tests (Controller Layer)
- **AuthControllerIntegrationTest**: Tests for authentication endpoints (register, login) with Spring Boot Test

### 3. Repository Tests (Data Layer)
- **UserRepositoryTest**: Tests for database operations using H2 in-memory database

### 4. Security Tests
- **JwtUtilsTest**: Tests for JWT token generation, validation, and extraction

### 5. Exception Handling Tests
- **GlobalExceptionHandlerTest**: Tests for custom exception handling and error responses

## Test Configuration

### Test Properties (`application-test.properties`)
- Uses H2 in-memory database for testing
- Configures test-specific JWT secret
- Sets up logging for debugging
- Configures file upload limits for testing

### Dependencies
- **H2 Database**: In-memory database for testing
- **Spring Boot Test**: Integration testing framework
- **Spring Security Test**: Security testing utilities
- **Mockito**: Mocking framework for unit tests

## Running Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=UserServiceTest
```

### Run Tests with Coverage
```bash
mvn test jacoco:report
```

### Run Tests in IDE
- Right-click on test class and select "Run Test"
- Use IDE's test runner to execute individual test methods

## Test Categories

### Unit Tests
- **Purpose**: Test individual components in isolation
- **Tools**: Mockito, JUnit 5
- **Coverage**: Service layer business logic
- **Location**: `service/` package

### Integration Tests
- **Purpose**: Test component interactions
- **Tools**: Spring Boot Test, MockMvc
- **Coverage**: Controller endpoints, HTTP responses
- **Location**: `controller/` package

### Repository Tests
- **Purpose**: Test database operations
- **Tools**: Spring Data JPA Test, H2 Database
- **Coverage**: CRUD operations, constraints, relationships
- **Location**: `repository/` package

### Security Tests
- **Purpose**: Test authentication and authorization
- **Tools**: JUnit 5, Mockito
- **Coverage**: JWT token operations
- **Location**: `security/` package

## Test Data

### Test Users
- **Manager User**: Role MANAGER, basic permissions
- **Admin User**: Role ADMIN, full permissions

### Test Products
- **Sample Product**: iPhone 15 with category, price, stock
- **Test Images**: Mock multipart files for image upload testing

### Test Categories
- **Electronics**: Sample category for products
- **Smartphones**: Sub-category for testing relationships

## Best Practices

### Test Naming Convention
- `methodName_scenario_expectedResult`
- Example: `loginUser_Success`, `loginUser_EmailNotFound_ShouldThrowNotFoundException`

### Test Structure (AAA Pattern)
1. **Arrange**: Set up test data and mocks
2. **Act**: Execute the method under test
3. **Assert**: Verify the expected results

### Mocking Guidelines
- Mock external dependencies (repositories, external services)
- Use real objects for simple data structures
- Verify mock interactions when relevant

### Database Testing
- Use `@DataJpaTest` for repository tests
- Use `@Transactional` to rollback changes
- Clear database state between tests

## Coverage Goals

- **Service Layer**: 90%+ coverage
- **Controller Layer**: 80%+ coverage
- **Repository Layer**: 85%+ coverage
- **Security Layer**: 90%+ coverage
- **Exception Handling**: 100% coverage

## Troubleshooting

### Common Issues

1. **H2 Database Connection Issues**
   - Ensure H2 dependency is in pom.xml
   - Check application-test.properties configuration

2. **Mockito Issues**
   - Verify @ExtendWith(MockitoExtension.class) annotation
   - Check mock setup in @BeforeEach methods

3. **Spring Boot Test Issues**
   - Ensure @SpringBootTest annotation is present
   - Check @ActiveProfiles("test") for test configuration

4. **JWT Test Issues**
   - Verify test secret key configuration
   - Check reflection usage for private methods

### Debug Mode
Add to application-test.properties:
```properties
logging.level.com.teckiz.InventorySystem=DEBUG
logging.level.org.springframework.security=DEBUG
```

## Future Enhancements

1. **Performance Tests**: Add load testing for critical endpoints
2. **Contract Tests**: Implement consumer-driven contract testing
3. **E2E Tests**: Add end-to-end testing with real browser automation
4. **Mutation Testing**: Add mutation testing for better test quality
5. **API Documentation Tests**: Test OpenAPI/Swagger documentation accuracy 