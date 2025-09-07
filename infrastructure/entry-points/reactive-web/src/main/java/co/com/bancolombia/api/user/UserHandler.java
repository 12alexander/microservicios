package co.com.bancolombia.api.user;

import co.com.bancolombia.api.exception.GlobalExceptionHandler;
import co.com.bancolombia.api.user.dto.ErrorResponseDTO;
import co.com.bancolombia.api.user.dto.UserRequestDTO;
import co.com.bancolombia.api.user.mapper.UserDTOMapper;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.InvalidDataException;
import co.com.bancolombia.model.exception.UserExistsException;
import co.com.bancolombia.usecase.user.interfaces.IUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Operations related to user management")
public class UserHandler {

    private final IUserUseCase userUseCase;
    private final PasswordEncoder passwordEncoder;
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);

    @Operation(summary = "Create a new user", description = "Creates a new user in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = co.com.bancolombia.api.user.dto.UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "User already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public Mono<ServerResponse> saveUser(ServerRequest request) {
        log.info("Processing user registration request from IP: {}",
                request.remoteAddress().map(addr -> addr.getHostString()).orElse("unknown"));

        return request.bodyToMono(UserRequestDTO.class)
                .timeout(REQUEST_TIMEOUT)
                .doOnNext(this::logUserCreationRequest)
                .doOnNext(this::validateRequestData)
                .map(this::encryptPassword)
                .map(UserDTOMapper::toDomain)
                .flatMap(userUseCase::saveUser)
                .map(UserDTOMapper::toResponse)
                .flatMap(response -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .doOnSuccess(response -> log.info("User registered successfully"))
                .doOnError(throwable -> log.error("Error processing user registration: {}",
                        throwable.getMessage(), throwable))
                .onErrorResume(this::handleError);
    }

    public Mono<ServerResponse> getUserById(ServerRequest request) {
        String userId = request.pathVariable("id");
        log.info("Fetching user with ID: {}", userId);

        return userUseCase.getUserById(userId)
                .map(UserDTOMapper::toResponse)
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .doOnSuccess(response -> log.info("User retrieved successfully: {}", userId))
                .doOnError(throwable -> log.error("Error retrieving user {}: {}",
                        userId, throwable.getMessage()))
                .onErrorResume(this::handleError);
    }

    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        log.info("Fetching all users");

        return userUseCase.findAll()
                .map(UserDTOMapper::toResponse)
                .collectList()
                .flatMap(users -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(users))
                .doOnSuccess(response -> log.info("All users retrieved successfully"))
                .doOnError(throwable -> log.error("Error retrieving users: {}",
                        throwable.getMessage()))
                .onErrorResume(this::handleError);
    }

    private void logUserCreationRequest(UserRequestDTO dto) {
        log.info("User registration requested - Email: {}, Name: {} {}",
                dto.getEmailAddress(), dto.getName(), dto.getLastName());
    }

    private void validateRequestData(UserRequestDTO dto) {
        if (dto.getEmailAddress() == null || dto.getEmailAddress().isBlank()) {
            throw new InvalidDataException("Email address is required");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new InvalidDataException("Name is required");
        }
    }

    private UserRequestDTO encryptPassword(UserRequestDTO dto) {
        String encryptedPassword = passwordEncoder.encode(dto.getPassword());
        return UserRequestDTO.builder()
                .name(dto.getName())
                .lastName(dto.getLastName())
                .birthDate(dto.getBirthDate())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .emailAddress(dto.getEmailAddress())
                .baseSalary(dto.getBaseSalary())
                .idRol(dto.getIdRol())
                .password(encryptedPassword)
                .build();
    }

    private Mono<ServerResponse> handleError(Throwable throwable) {
        log.error("Error processing request: {}", throwable.getMessage(), throwable);

        return switch (throwable) {
            case UserExistsException userExists ->
                    buildErrorResponse(HttpStatus.CONFLICT, "USER_EXISTS", 
                        "El email ya está registrado en el sistema");
            case InvalidDataException invalidData ->
                    buildErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_DATA", 
                        "Los datos proporcionados no son válidos");
            case BusinessException businessError ->
                    buildErrorResponse(HttpStatus.BAD_REQUEST, "BUSINESS_ERROR", 
                        "Error en las reglas de negocio");
            case NumberFormatException numberFormat ->
                    buildErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_ID_FORMAT", 
                        "Formato de ID inválido");
            default ->
                    buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", 
                        "Error interno del servidor");
        };
    }

    private Mono<ServerResponse> buildErrorResponse(HttpStatus status, String code, String message) {
        var errorResponse = ErrorResponseDTO.builder()
                .code(code)
                .message(message)
                .timestamp(java.time.Instant.now())
                .build();

        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }
}