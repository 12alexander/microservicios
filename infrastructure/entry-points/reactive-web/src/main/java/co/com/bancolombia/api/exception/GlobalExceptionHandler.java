package co.com.bancolombia.api.exception;

import co.com.bancolombia.api.user.dto.ErrorResponseDTO;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.InvalidDataException;
import co.com.bancolombia.model.exception.UserExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserExistsException.class)
    public Mono<ResponseEntity<ErrorResponseDTO>> handleUserExists(
            UserExistsException ex,
            ServerWebExchange exchange) {

        log.warn("Usuario ya existe: {}", ex.getMessage());

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(error));
    }

    @ExceptionHandler(InvalidDataException.class)
    public Mono<ResponseEntity<ErrorResponseDTO>> handleInvalidData(
            InvalidDataException ex,
            ServerWebExchange exchange) {

        log.warn("Datos inválidos: {}", ex.getMessage());

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponseDTO>> handleValidationErrors(
            WebExchangeBindException ex,
            ServerWebExchange exchange) {

        String errorsMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Errores de validación: {}", errorsMessage);

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .code("VALIDATION_ERROR")
                .message("Errores de validación: " + errorsMessage)
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
    }

    @ExceptionHandler(BusinessException.class)
    public Mono<ResponseEntity<ErrorResponseDTO>> handleBusinessException(
            BusinessException ex,
            ServerWebExchange exchange) {

        log.warn("Error de negocio: {} - {}", ex.getCode(), ex.getMessage());

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponseDTO>> handleGenericException(
            Exception ex,
            ServerWebExchange exchange) {

        log.error("Error interno del servidor", ex);

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .code("INTERNAL_ERROR")
                .message("Ha ocurrido un error interno. Intente nuevamente.")
                .timestamp(LocalDateTime.now())
                .path(exchange.getRequest().getPath().value())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
    }
}
