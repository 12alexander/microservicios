package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.exception.InvalidDataException;
import co.com.bancolombia.model.exception.UserExistsException;
import co.com.bancolombia.model.role.gateways.RoleRepository;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserUseCase Tests")
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserUseCase userUseCase;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = User.builder()
                .name("Juan")
                .lastName("Perez")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("Calle 123")
                .phone("1234567890")
                .emailAddress("juan.perez@email.com")
                .baseSalary(new BigDecimal("1000000"))
                .idRol("1")
                .build();
    }

    @Test
    @DisplayName("Should save user successfully when valid data and email not exists")
    void saveUser_ValidDataAndEmailNotExists_Success() {
        when(userRepository.emailAddressExists(validUser.getEmailAddress()))
                .thenReturn(Mono.just(false));
        when(roleRepository.existsById("1"))
                .thenReturn(Mono.just(true));
        when(userRepository.createUser(any(User.class)))
                .thenReturn(Mono.just(validUser.toBuilder().id("123").build()));

        StepVerifier.create(userUseCase.saveUser(validUser))
                .expectNextMatches(user -> user.getId() != null &&
                        user.getName().equals("Juan") &&
                        user.getEmailAddress().equals("juan.perez@email.com"))
                .verifyComplete();

        verify(userRepository).emailAddressExists(validUser.getEmailAddress());
        verify(roleRepository).existsById("1");
        verify(userRepository).createUser(any(User.class));
    }

    @Test
    @DisplayName("Should throw UserExistsException when email already exists")
    void saveUser_EmailExists_ThrowsUserExistsException() {
        when(userRepository.emailAddressExists(validUser.getEmailAddress()))
                .thenReturn(Mono.just(true));

        StepVerifier.create(userUseCase.saveUser(validUser))
                .expectError(UserExistsException.class)
                .verify();

        verify(userRepository).emailAddressExists(validUser.getEmailAddress());
        verify(userRepository, never()).createUser(any(User.class));
    }

    @Test
    @DisplayName("Should throw InvalidDataException when user data is invalid")
    void saveUser_InvalidData_ThrowsInvalidDataException() {
        User invalidUser = User.builder()
                .name("")
                .lastName("Perez")
                .emailAddress("invalid-email")
                .baseSalary(new BigDecimal("-1000"))
                .build();

        StepVerifier.create(userUseCase.saveUser(invalidUser))
                .expectError(InvalidDataException.class)
                .verify();

        verify(userRepository, never()).emailAddressExists(anyString());
        verify(userRepository, never()).createUser(any(User.class));
    }

    @Test
    @DisplayName("Should update user successfully when user exists")
    void updateUser_UserExists_Success() {
        String userId = "123";
        User existingUser = validUser.toBuilder().id(userId).build();
        User updatedUser = validUser.toBuilder()
                .id(userId)
                .name("Juan Carlos")
                .build();

        when(userRepository.getUserById(userId))
                .thenReturn(Mono.just(existingUser));
        when(userRepository.updateUser(any(User.class)))
                .thenReturn(Mono.just(updatedUser));

        StepVerifier.create(userUseCase.updateUser(userId, validUser))
                .expectNextMatches(user -> user.getId().equals(userId) &&
                        user.getName().equals("Juan Carlos"))
                .verifyComplete();

        verify(userRepository).getUserById(userId);
        verify(userRepository).updateUser(any(User.class));
    }

    @Test
    @DisplayName("Should throw UserExistsException when user not found for update")
    void updateUser_UserNotFound_ThrowsUserExistsException() {
        String userId = "123";

        when(userRepository.getUserById(userId))
                .thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.updateUser(userId, validUser))
                .expectError(UserExistsException.class)
                .verify();

        verify(userRepository).getUserById(userId);
        verify(userRepository, never()).updateUser(any(User.class));
    }

    @Test
    @DisplayName("Should get all users successfully")
    void getAllUsers_Success() {
        User user1 = validUser.toBuilder().id("1").build();
        User user2 = validUser.toBuilder()
                .id("2")
                .emailAddress("otro@email.com")
                .build();

        when(userRepository.findAll())
                .thenReturn(Flux.just(user1, user2));

        StepVerifier.create(userUseCase.getAllUsers())
                .expectNext(user1)
                .expectNext(user2)
                .verifyComplete();

        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Should get user by id successfully when user exists")
    void getUserById_UserExists_Success() {
        String userId = "123";
        User user = validUser.toBuilder().id(userId).build();

        when(userRepository.getUserById(userId))
                .thenReturn(Mono.just(user));

        StepVerifier.create(userUseCase.getUserById(userId))
                .expectNext(user)
                .verifyComplete();

        verify(userRepository).getUserById(userId);
    }

    @Test
    @DisplayName("Should throw UserExistsException when user not found by id")
    void getUserById_UserNotFound_ThrowsUserExistsException() {
        String userId = "123";

        when(userRepository.getUserById(userId))
                .thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.getUserById(userId))
                .expectError(UserExistsException.class)
                .verify();

        verify(userRepository).getUserById(userId);
    }

    @Test
    @DisplayName("Should delete user successfully when user exists")
    void deleteUser_UserExists_Success() {
        String userId = "123";
        User user = validUser.toBuilder().id(userId).build();

        when(userRepository.getUserById(userId))
                .thenReturn(Mono.just(user));
        when(userRepository.deleteById(userId))
                .thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.deleteUser(userId))
                .verifyComplete();

        verify(userRepository).getUserById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    @DisplayName("Should throw UserExistsException when user not found for delete")
    void deleteUser_UserNotFound_ThrowsUserExistsException() {
        String userId = "123";

        when(userRepository.getUserById(userId))
                .thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.deleteUser(userId))
                .expectError(UserExistsException.class)
                .verify();

        verify(userRepository).getUserById(userId);
        verify(userRepository, never()).deleteById(userId);
    }
}