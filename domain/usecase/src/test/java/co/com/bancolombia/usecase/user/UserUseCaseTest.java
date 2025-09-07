package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.exception.UserExistsException;
import co.com.bancolombia.model.role.gateways.RoleRepository;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserUseCase userUseCase;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .name("Juan")
                .lastName("Perez")
                .emailAddress("juan@test.com")
                .baseSalary(new BigDecimal("2000000"))
                .idRol("DEV")
                .password("password123")
                .build();
    }

    @Test
    void saveUser_WithValidData_ShouldWork() {
        when(roleRepository.existsById("DEV")).thenReturn(Mono.just(true));
        when(userRepository.emailAddressExists("juan@test.com")).thenReturn(Mono.just(false));
        
        User savedUser = testUser.toBuilder().id("123").build();
        when(userRepository.createUser(any(User.class))).thenReturn(Mono.just(savedUser));

        StepVerifier.create(userUseCase.saveUser(testUser))
                .expectNextMatches(user -> 
                    user.getId().equals("123") && 
                    user.getName().equals("Juan"))
                .verifyComplete();

        verify(userRepository).createUser(any(User.class));
    }

    @Test
    void saveUser_WithDuplicateEmail_ShouldFail() {
        when(roleRepository.existsById("DEV")).thenReturn(Mono.just(true));
        when(userRepository.emailAddressExists("juan@test.com"))
                .thenReturn(Mono.just(true));

        StepVerifier.create(userUseCase.saveUser(testUser))
                .expectError(UserExistsException.class)
                .verify();

        verify(userRepository, never()).createUser(any(User.class));
    }

    @Test
    void getAllUsers_ShouldWork() {
        when(userRepository.findAll())
                .thenReturn(Flux.just(testUser));

        StepVerifier.create(userUseCase.getAllUsers())
                .expectNextMatches(user -> user.getName().equals("Juan"))
                .verifyComplete();

        verify(userRepository).findAll();
    }
}