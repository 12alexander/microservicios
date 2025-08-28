package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.r2dbc.user.data.UserData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyReactiveRepositoryAdapterTest {

    @InjectMocks
    MyReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    MyReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    @Test
    void mustFindValueById() {
        UserData userData = UserData.builder()
                .id("1")
                .name("Test")
                .lastName("User")
                .emailAddress("test@email.com")
                .baseSalary(BigDecimal.valueOf(1000000))
                .build();

        User user = User.builder()
                .id("1")
                .name("Test")
                .lastName("User")
                .emailAddress("test@email.com")
                .baseSalary(BigDecimal.valueOf(1000000))
                .build();

        when(repository.findById("1")).thenReturn(Mono.just(userData));
        when(mapper.map(userData, User.class)).thenReturn(user);

        Mono<User> result = repositoryAdapter.findById("1");

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getId().equals("1"))
                .verifyComplete();
    }

    @Test
    void mustFindAllValues() {
        UserData userData = UserData.builder()
                .id("1")
                .name("Test")
                .lastName("User")
                .emailAddress("test@email.com")
                .baseSalary(BigDecimal.valueOf(1000000))
                .build();

        User user = User.builder()
                .id("1")
                .name("Test")
                .lastName("User")
                .emailAddress("test@email.com")
                .baseSalary(BigDecimal.valueOf(1000000))
                .build();

        when(repository.findAll()).thenReturn(Flux.just(userData));
        when(mapper.map(userData, User.class)).thenReturn(user);

        Flux<User> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getId().equals("1"))
                .verifyComplete();
    }

    @Test
    void mustFindByExample() {
        UserData userData = UserData.builder()
                .id("1")
                .name("Test")
                .lastName("User")
                .emailAddress("test@email.com")
                .baseSalary(BigDecimal.valueOf(1000000))
                .build();

        User user = User.builder()
                .id("1")
                .name("Test")
                .lastName("User")
                .emailAddress("test@email.com")
                .baseSalary(BigDecimal.valueOf(1000000))
                .build();

        when(repository.findAll(any(Example.class))).thenReturn(Flux.just(userData));
        when(mapper.map(userData, User.class)).thenReturn(user);
        // Agregar stub para User -> UserData
        when(mapper.map(any(User.class), eq(UserData.class))).thenReturn(userData);

        Flux<User> result = repositoryAdapter.findByExample(user);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getId().equals("1"))
                .verifyComplete();
    }

    @Test
    void mustSaveValue() {
        UserData userData = UserData.builder()
                .id("1")
                .name("Test")
                .lastName("User")
                .emailAddress("test@email.com")
                .baseSalary(BigDecimal.valueOf(1000000))
                .build();

        User user = User.builder()
                .id("1")
                .name("Test")
                .lastName("User")
                .emailAddress("test@email.com")
                .baseSalary(BigDecimal.valueOf(1000000))
                .build();

        when(repository.save(any(UserData.class))).thenReturn(Mono.just(userData));
        when(mapper.map(userData, User.class)).thenReturn(user);
        // Agregar stub para User -> UserData
        when(mapper.map(any(User.class), eq(UserData.class))).thenReturn(userData);

        Mono<User> result = repositoryAdapter.save(user);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getId().equals("1"))
                .verifyComplete();
    }
}