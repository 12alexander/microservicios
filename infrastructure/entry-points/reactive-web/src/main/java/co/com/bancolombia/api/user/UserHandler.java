package co.com.bancolombia.api.user;

import co.com.bancolombia.api.user.dto.UserRequestDTO;
import co.com.bancolombia.api.user.mapper.UserDTOMapper;
import co.com.bancolombia.usecase.user.interfaces.IUserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserHandler {

    private final IUserUseCase userUseCaseI;


    public Mono<ServerResponse> saveUser(ServerRequest request) {
        log.info("Recibida petición de registro de usuario");

        return request.bodyToMono(UserRequestDTO.class)
                .doOnNext(dto -> log.info("Usuario a registrar con correo: {}",
                        dto.getEmailAddress()))
                .map(UserDTOMapper::toDomain)
                .flatMap(userUseCaseI::saveUser)
                .map(UserDTOMapper::toResponse)
                .flatMap(response -> ServerResponse.status(201)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .doOnSuccess(response -> log.info("Usuario registrado exitosamente"))
                .doOnError(error -> log.error("Error al registrar usuario: {}",
                        error.getMessage()));
    }


}