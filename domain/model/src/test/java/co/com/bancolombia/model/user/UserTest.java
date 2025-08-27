package co.com.bancolombia.model.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Domain Model Tests")
class UserTest {

    @Test
    @DisplayName("Should create user with valid data")
    void createUser_ValidData_Success() {
        User user = User.builder()
                .name("Juan")
                .lastName("Perez")
                .emailAddress("juan@email.com")
                .baseSalary(new BigDecimal("1000000"))
                .idRol("1")
                .build();

        assertNotNull(user);
        assertEquals("Juan", user.getName());
        assertEquals("juan@email.com", user.getEmailAddress());
    }

    @Test
    @DisplayName("Should throw exception with invalid name")
    void createUser_InvalidName_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                User.builder().name("").build()
        );
    }

    @Test
    @DisplayName("Should throw exception with invalid email")
    void createUser_InvalidEmail_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                User.builder()
                        .name("Juan")
                        .lastName("Perez")
                        .emailAddress("invalid-email")
                        .baseSalary(new BigDecimal("1000000"))
                        .build()
        );
    }

    @Test
    @DisplayName("Should throw exception with invalid salary")
    void createUser_InvalidSalary_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                User.builder()
                        .name("Juan")
                        .lastName("Perez")
                        .emailAddress("juan@email.com")
                        .baseSalary(new BigDecimal("-1000"))
                        .build()
        );
    }

    @Test
    @DisplayName("Should validate user data successfully")
    void validateData_ValidUser_NoException() {
        User user = User.builder()
                .name("Juan")
                .lastName("Perez")
                .emailAddress("juan@email.com")
                .baseSalary(new BigDecimal("1000000"))
                .idRol("1")
                .build();

        assertDoesNotThrow(() -> user.validateData());
    }
}