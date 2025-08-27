package co.com.bancolombia.api.user;

import co.com.bancolombia.api.user.dto.UserRequestDTO;
import co.com.bancolombia.api.user.dto.UserResponseDTO;
import co.com.bancolombia.api.user.mapper.UserDTOMapper;
import co.com.bancolombia.usecase.user.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "API para gestión de usuarios")
public class UserController {

    private final UserUseCase userUseCase;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserResponseDTO> saveUser(@Valid @RequestBody UserRequestDTO request) {
        log.info("Recibida petición de registro de usuario con correo: {}", request.getEmailAddress());

        return Mono.fromCallable(() -> UserDTOMapper.toDomain(request))
                .flatMap(userUseCase::saveUser)
                .map(UserDTOMapper::toResponse)
                .doOnSuccess(response ->
                        log.info("Usuario registrado exitosamente con ID: {}", response.getId())
                )
                .doOnError(error ->
                        log.error("Error al procesar registro de usuario: {}", error.getMessage())
                );
    }

    @PutMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<UserResponseDTO> updateUser(@PathVariable String id,
                                                      @Valid @RequestBody UserRequestDTO request) {
        log.info("Recibida petición de actualización de usuario con ID: {}", id);

        return Mono.fromCallable(() -> UserDTOMapper.toDomain(request))
                .flatMap(user -> userUseCase.updateUser(id, user))
                .map(UserDTOMapper::toResponse)
                .doOnSuccess(response ->
                        log.info("Usuario actualizado exitosamente con ID: {}", response.getId())
                )
                .doOnError(error ->
                        log.error("Error al procesar actualización de usuario: {}", error.getMessage())
                );
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<UserResponseDTO> getAllUsers() {
        log.info("Recibida petición para obtener todos los usuarios");

        return userUseCase.getAllUsers()
                .map(UserDTOMapper::toResponse)
                .doOnComplete(() -> log.info("Consulta de usuarios completada"));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<UserResponseDTO> getUserById(@PathVariable String id) {
        log.info("Recibida petición para obtener usuario con ID: {}", id);

        return userUseCase.getUserById(id)
                .map(UserDTOMapper::toResponse)
                .doOnSuccess(response ->
                        log.info("Usuario encontrado con ID: {}", response.getId())
                )
                .doOnError(error ->
                        log.error("Error al buscar usuario con ID {}: {}", id, error.getMessage())
                );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteUser(@PathVariable String id) {
        log.info("Recibida petición para eliminar usuario con ID: {}", id);

        return userUseCase.deleteUser(id)
                .doOnSuccess(unused ->
                        log.info("Usuario eliminado exitosamente con ID: {}", id)
                )
                .doOnError(error ->
                        log.error("Error al eliminar usuario con ID {}: {}", id, error.getMessage())
                );
    }
}
