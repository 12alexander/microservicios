package co.com.bancolombia.api;

import co.com.bancolombia.api.user.UserHandler;
import co.com.bancolombia.api.user.dto.UserRequestDTO;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.usecase.user.interfaces.IUserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class SimpleRouterTest {

    private WebTestClient webTestClient;
    private IUserUseCase userUseCase;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userUseCase = mock(IUserUseCase.class);
        passwordEncoder = mock(PasswordEncoder.class);
        
        // Configure password encoder mock with lenient for tests that don't use it
        lenient().when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        
        UserHandler userHandler = new UserHandler(userUseCase, passwordEncoder);
        RouterRest routerRest = new RouterRest();
        RouterFunction<ServerResponse> routes = routerRest.userRoutes(userHandler)
                .and(routerRest.healthRoutes());
        this.webTestClient = WebTestClient.bindToRouterFunction(routes).build();
    }

    @Test
    void healthCheck() {
        webTestClient.get()
                .uri("/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");
    }

    @Test
    void createUser() {
        UserRequestDTO request = UserRequestDTO.builder()
                .name("Juan")
                .lastName("Perez")
                .emailAddress("juan@test.com")
                .baseSalary(new BigDecimal("2000000"))
                .idRol("DEV")
                .password("testpass")
                .build();

        User savedUser = User.builder()
                .id("123")
                .name("Juan")
                .lastName("Perez")
                .emailAddress("juan@test.com")
                .baseSalary(new BigDecimal("2000000"))
                .password("encodedPassword")
                .build();

        when(userUseCase.saveUser(any(User.class))).thenReturn(Mono.just(savedUser));

        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("123")
                .jsonPath("$.name").isEqualTo("Juan");

        verify(userUseCase).saveUser(any(User.class));
    }

    @Test
    void createUser_WithError() {
        UserRequestDTO request = UserRequestDTO.builder()
                .name("Juan")
                .lastName("Perez")
                .emailAddress("duplicado@test.com")
                .baseSalary(new BigDecimal("2000000"))
                .idRol("DEV")
                .password("testpass")
                .build();

        when(userUseCase.saveUser(any(User.class)))
                .thenReturn(Mono.error(new RuntimeException("Email duplicado")));

        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}