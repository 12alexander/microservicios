package co.com.bancolombia.r2dbc.user;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.r2dbc.role.RoleR2dbcRepository;
import co.com.bancolombia.r2dbc.user.data.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRepositoryAdapter Tests")
class UserRepositoryAdapterTest {

    @Mock
    private UserR2dbcRepository repository;

    @Mock
    private RoleR2dbcRepository roleRepository;

    @InjectMocks
    private UserRepositoryAdapter userRepositoryAdapter;

    private User user;
    private UserData userData;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id("123")
                .name("Juan")
                .lastName("Perez")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("Calle 123")
                .phone("1234567890")
                .emailAddress("juan.perez@email.com")
                .baseSalary(new BigDecimal("1000000"))
                .idRol("ROL_USER")
                .build();

        userData = UserData.builder()
                .id("123")
                .name("Juan")
                .lastName("Perez")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("Calle 123")
                .phone("1234567890")
                .emailAddress("juan.perez@email.com")
                .baseSalary(new BigDecimal("1000000"))
                .idRol("ROL_USER")
                .creationDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should create user successfully")
    void createUser_Success() {
        when(repository.createUser(anyString(), anyString(), anyString(), any(LocalDate.class),
                anyString(), anyString(), anyString(), any(BigDecimal.class),
                anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Mono.just(1));
        when(repository.findById(user.getId()))
                .thenReturn(Mono.just(userData));

        StepVerifier.create(userRepositoryAdapter.createUser(user))
                .expectNextMatches(savedUser -> savedUser.getId().equals("123") &&
                        savedUser.getName().equals("Juan") &&
                        savedUser.getEmailAddress().equals("juan.perez@email.com"))
                .verifyComplete();

        verify(repository).createUser(anyString(), anyString(), anyString(), any(LocalDate.class),
                anyString(), anyString(), anyString(), any(BigDecimal.class),
                anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(repository).findById(user.getId());
    }

    @Test
    @DisplayName("Should update user successfully")
    void updateUser_Success() {
        when(repository.save(any(UserData.class)))
                .thenReturn(Mono.just(userData));

        StepVerifier.create(userRepositoryAdapter.updateUser(user))
                .expectNextMatches(updatedUser -> updatedUser.getId().equals("123") &&
                        updatedUser.getName().equals("Juan"))
                .verifyComplete();

        verify(repository).save(any(UserData.class));
    }

    @Test
    @DisplayName("Should check if email exists")
    void emailAddressExists_EmailExists_ReturnsTrue() {
        when(repository.existsByEmailAddress("juan.perez@email.com"))
                .thenReturn(Mono.just(true));

        StepVerifier.create(userRepositoryAdapter.emailAddressExists("juan.perez@email.com"))
                .expectNext(true)
                .verifyComplete();

        verify(repository).existsByEmailAddress("juan.perez@email.com");
    }

    @Test
    @DisplayName("Should return false when email does not exist")
    void emailAddressExists_EmailNotExists_ReturnsFalse() {
        when(repository.existsByEmailAddress("nonexistent@email.com"))
                .thenReturn(Mono.empty());

        StepVerifier.create(userRepositoryAdapter.emailAddressExists("nonexistent@email.com"))
                .expectNext(false)
                .verifyComplete();

        verify(repository).existsByEmailAddress("nonexistent@email.com");
    }

    @Test
    @DisplayName("Should get user by id successfully")
    void getUserById_UserExists_Success() {
        when(repository.findById("123"))
                .thenReturn(Mono.just(userData));
        when(roleRepository.findById("ROL_USER"))
                .thenReturn(Mono.empty());

        StepVerifier.create(userRepositoryAdapter.getUserById("123"))
                .expectNextMatches(foundUser -> foundUser.getId().equals("123") &&
                        foundUser.getName().equals("Juan"))
                .verifyComplete();

        verify(repository).findById("123");
    }

    @Test
    @DisplayName("Should return empty when user not found by id")
    void getUserById_UserNotFound_ReturnsEmpty() {
        when(repository.findById("999"))
                .thenReturn(Mono.empty());

        StepVerifier.create(userRepositoryAdapter.getUserById("999"))
                .verifyComplete();

        verify(repository).findById("999");
    }

    @Test
    @DisplayName("Should get user by email successfully")
    void getUserByEmailAddress_UserExists_Success() {
        when(repository.findByEmailAddress("juan.perez@email.com"))
                .thenReturn(Mono.just(userData));

        StepVerifier.create(userRepositoryAdapter.getUserByEmailAddress("juan.perez@email.com"))
                .expectNextMatches(foundUser -> foundUser.getEmailAddress().equals("juan.perez@email.com"))
                .verifyComplete();

        verify(repository).findByEmailAddress("juan.perez@email.com");
    }

    @Test
    @DisplayName("Should find all users successfully")
    void findAll_Success() {
        UserData userData2 = userData.toBuilder()
                .id("456")
                .emailAddress("otro@email.com")
                .build();

        when(repository.findAll())
                .thenReturn(Flux.just(userData, userData2));

        when(roleRepository.findById("ROL_USER"))
                .thenReturn(Mono.empty());

        StepVerifier.create(userRepositoryAdapter.findAll())
                .expectNextCount(2)
                .verifyComplete();

        verify(repository).findAll();
    }

    @Test
    @DisplayName("Should delete user by id successfully")
    void deleteById_Success() {
        when(repository.deleteById("123"))
                .thenReturn(Mono.empty());

        StepVerifier.create(userRepositoryAdapter.deleteById("123"))
                .verifyComplete();

        verify(repository).deleteById("123");
    }
}
