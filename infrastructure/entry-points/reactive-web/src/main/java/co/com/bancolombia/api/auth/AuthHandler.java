package co.com.bancolombia.api.auth;

import co.com.bancolombia.api.auth.dto.AuthRequestDTO;
import co.com.bancolombia.api.auth.dto.AuthResponseDTO;
import co.com.bancolombia.api.jwt.JwtProvider;
import co.com.bancolombia.model.exception.AuthException;
import co.com.bancolombia.usecase.auth.AuthUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthHandler {

    private final AuthUseCase authUseCase;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(AuthRequestDTO.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Request body cannot be empty")))
                .flatMap(dto -> authUseCase.login(
                        dto.getEmail(),
                        dto.getPassword(),
                        jwtProvider::generateToken,
                        (password, hashedPassword) -> {
                            log.debug("[AUTH] Comparing password - Raw: '{}', Hashed: '{}'", password, hashedPassword);
                            if (hashedPassword == null || hashedPassword.trim().isEmpty()) {
                                log.error("[AUTH] Hashed password is null or empty");
                                return false;
                            }
                            return passwordEncoder.matches(password, hashedPassword);
                        }
                ))
                .map(auth -> AuthResponseDTO.builder()
                        .idUser(auth.getIdUser())
                        .idRol(auth.getIdRole())
                        .nameUser(auth.getNameUser())
                        .token(auth.getToken())
                        .build())
                .flatMap(authResponse -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(authResponse))
                .doOnSuccess(resp -> log.info("[AUTH] Login attempt processed successfully"))
                .doOnError(error -> log.error("[AUTH] Login attempt failed: {}", error.getMessage()));
    }

    public Mono<ServerResponse> validateToken(ServerRequest request) {
        String authHeader = request.headers().firstHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        if (!jwtProvider.isTokenValid(token)) {
            throw new co.com.bancolombia.model.exception.AuthException("Invalid or expired token");
        }

        UUID idUser = jwtProvider.extractUserId(token);
        UUID idRol = jwtProvider.extractRoleId(token);

        AuthResponseDTO response = AuthResponseDTO.builder()
                .idUser(idUser)
                .idRol(idRol)
                .token(token)
                .build();

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(response);
    }

}