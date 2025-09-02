package co.com.bancolombia.usecase.auth;

import co.com.bancolombia.model.auth.Auth;
import co.com.bancolombia.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.BiFunction;

@RequiredArgsConstructor
public class AuthUseCase {
    private final UserUseCase userUseCase;

    public Mono<Auth> login(String email,
                            String password,
                            BiFunction<UUID, UUID, String> tokenGenerator,
                            BiFunction<String, String, Boolean> passwordMatches){
        return userUseCase.findByEmailAddress(email)
                .switchIfEmpty(Mono.error(() -> new AuthenticationException("User not found for email: " + email)))
                .flatMap(user -> {
                    if (!passwordMatches.apply(password, user.getPassword())) {
                        return Mono.error(() -> new AuthenticationException("Invalid credentials"));
                    }
                    String token = tokenGenerator.apply(user.getIdUser(), user.getIdRol());
                    return Mono.just(new Auth(user.getIdUser(), user.getIdRol(), user.getFirstName(), token));
                });
    }
}
