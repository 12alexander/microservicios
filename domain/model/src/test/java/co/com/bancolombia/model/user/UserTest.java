package co.com.bancolombia.model.user;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void createUser_WithValidData_ShouldWork() {
        User user = User.builder()
                .name("Juan")
                .lastName("Perez")
                .emailAddress("juan@email.com")
                .baseSalary(new BigDecimal("1000000"))
                .idRol("DEV")
                .password("testpass123")
                .build();

        assertNotNull(user);
        assertEquals("Juan", user.getName());
        assertEquals("juan@email.com", user.getEmailAddress());
    }

    @Test
    void createUser_WithNegativeSalary_ShouldFail() {
        assertThrows(Exception.class, () -> {
            User.builder()
                    .name("Ana")
                    .lastName("Lopez")
                    .emailAddress("ana@email.com")
                    .baseSalary(new BigDecimal("-1000"))
                    .idRol("DEV")
                    .password("testpass123")
                    .build();
        });
    }

    @Test
    void createUser_WithEmptyName_ShouldFail() {
        assertThrows(Exception.class, () -> {
            User.builder()
                    .name("")
                    .lastName("Garcia")
                    .emailAddress("test@email.com")
                    .baseSalary(new BigDecimal("1000000"))
                    .password("testpass123")
                    .build();
        });
    }
}