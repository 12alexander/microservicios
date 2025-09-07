package co.com.bancolombia.r2dbc.user;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.r2dbc.role.RoleR2dbcRepository;
import co.com.bancolombia.r2dbc.user.data.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserR2dbcRepository repository;

    @Mock
    private RoleR2dbcRepository roleRepository;

    @InjectMocks
    private UserRepositoryAdapter userRepositoryAdapter;

    private User usuario;
    private UserData userData;

    @BeforeEach
    void setUp() {
        usuario = User.builder()
                .id("123")
                .name("Juan")
                .lastName("Perez")
                .emailAddress("juan@test.com")
                .baseSalary(new BigDecimal("2000000"))
                .idRol("DEV")
                .password("encodedPassword")
                .build();

        userData = UserData.builder()
                .id("123")
                .name("Juan")
                .lastName("Perez")
                .emailAddress("juan@test.com")
                .baseSalary(new BigDecimal("2000000"))
                .idRol("DEV")
                .password("encodedPassword")
                .creationDate(LocalDateTime.now())
                .build();
    }

    @Test
    void emailExists() {
        when(repository.existsByEmailAddress("juan@test.com"))
                .thenReturn(Mono.just(true));

        StepVerifier.create(userRepositoryAdapter.emailAddressExists("juan@test.com"))
                .expectNext(true)
                .verifyComplete();

        verify(repository).existsByEmailAddress("juan@test.com");
    }

    @Test
    void getUserById() {
        when(repository.findById("123"))
                .thenReturn(Mono.just(userData));
        when(roleRepository.findById("DEV"))
                .thenReturn(Mono.empty());

        StepVerifier.create(userRepositoryAdapter.getUserById("123"))
                .expectNextMatches(user -> user.getName().equals("Juan"))
                .verifyComplete();

        verify(repository).findById("123");
    }

    @Test
    void deleteUser() {
        when(repository.deleteById("123"))
                .thenReturn(Mono.empty());

        StepVerifier.create(userRepositoryAdapter.deleteById("123"))
                .expectComplete()
                .verify();

        verify(repository).deleteById("123");
    }
}