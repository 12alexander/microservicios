package co.com.bancolombia.api.exception;

import co.com.bancolombia.api.dto.ErrorResponseDTO;
import co.com.bancolombia.model.exception.AuthException;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.InvalidDataException;
import co.com.bancolombia.model.exception.UserExistsException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.UnexpectedTypeException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * Global exception handler for the Pragma application.
 * Provides centralized error handling following SOLID principles and hexagonal architecture.
 * 
 * This class is part of the Infrastructure layer (Entry Points) and handles exceptions
 * from all application layers without coupling to domain logic.
 * 
 * @author Pragma Development Team
 */
@Slf4j
@Component
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    /**
     * Handles all exceptions following the ErrorWebExceptionHandler contract.
     * Implements low-level reactive error handling for better performance and control.
     * 
     * @param exchange the current server web exchange
     * @param ex the exception to handle
     * @return a Mono containing the error response
     */
    @Override
    public @NonNull Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex) {
        String path = exchange.getRequest().getPath().value();
        HttpStatus status;
        ErrorResponseDTO errorResponse;
        
        log.debug("Processing exception at {} -> {}: {}", path, ex.getClass().getSimpleName(), ex.getMessage());
        
        if (ex instanceof UserExistsException userException) {
            status = HttpStatus.CONFLICT;
            errorResponse = buildErrorResponse(userException.getCode(), userException.getMessage(), path);
            log.warn("409 at {} -> User conflict: {}", path, userException.getMessage());
            
        } else if (ex instanceof AuthException authException) {
            status = HttpStatus.UNAUTHORIZED;
            errorResponse = buildErrorResponse(authException.getCode(), authException.getMessage(), path);
            log.warn("401 at {} -> Authentication failed: {}", path, authException.getMessage());
            
        } else if (ex instanceof InvalidDataException dataException) {
            status = HttpStatus.BAD_REQUEST;
            errorResponse = buildErrorResponse(dataException.getCode(), dataException.getMessage(), path);
            log.warn("400 at {} -> Invalid data: {}", path, dataException.getMessage());
            
        } else if (ex instanceof BusinessException businessException) {
            status = mapBusinessExceptionToStatus(businessException);
            errorResponse = buildErrorResponse(businessException.getCode(), businessException.getMessage(), path);
            log.warn("{} at {} -> Business error: {}", status.value(), path, businessException.getMessage());
            
        } else if (ex instanceof IllegalArgumentException iae) {
            status = HttpStatus.BAD_REQUEST;
            errorResponse = buildErrorResponse("INVALID_ARGUMENT", iae.getMessage(), path);
            log.warn("400 at {} -> Invalid argument: {}", path, iae.getMessage());
            
        } else if (ex instanceof WebExchangeBindException webe) {
            status = HttpStatus.BAD_REQUEST;
            String validationMessage = extractValidationErrors(webe);
            errorResponse = buildErrorResponse("VALIDATION_ERROR", validationMessage, path);
            log.warn("400 (validation) at {} -> {}", path, validationMessage);
            
        } else if (ex instanceof ConstraintViolationException cve) {
            status = HttpStatus.BAD_REQUEST;
            String constraintMessage = extractConstraintViolations(cve);
            errorResponse = buildErrorResponse("CONSTRAINT_VIOLATION", constraintMessage, path);
            log.warn("400 (constraint) at {} -> {}", path, constraintMessage);
            
        } else if (ex instanceof UnexpectedTypeException ute) {
            status = HttpStatus.BAD_REQUEST;
            errorResponse = buildErrorResponse("VALIDATION_TYPE_ERROR", 
                "Tipo de validación inválida: " + ute.getMessage(), path);
            log.warn("400 (unexpected type) at {} -> {}", path, ute.getMessage());
            
        } else if (ex instanceof ServerWebInputException swe) {
            status = HttpStatus.BAD_REQUEST;
            errorResponse = buildErrorResponse("INVALID_REQUEST_BODY", 
                "Cuerpo de solicitud inválido: " + swe.getReason(), path);
            log.warn("400 (input) at {} -> {}", path, swe.getReason());
            
        } else if (ex instanceof DecodingException) {
            status = HttpStatus.BAD_REQUEST;
            errorResponse = buildErrorResponse("JSON_DECODE_ERROR", 
                "No se pudo procesar el JSON. Verifique el formato y los tipos de datos.", path);
            log.warn("400 (decode) at {} -> JSON decode error", path);
            
        } else if (ex instanceof UnsupportedMediaTypeStatusException) {
            status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
            errorResponse = buildErrorResponse("UNSUPPORTED_MEDIA_TYPE", 
                "Content-Type no soportado. Use application/json.", path);
            log.warn("415 at {} -> Unsupported media type", path);
            
        } else if (ex instanceof NotAcceptableStatusException) {
            status = HttpStatus.NOT_ACCEPTABLE;
            errorResponse = buildErrorResponse("NOT_ACCEPTABLE", 
                "Accept header no soportado por el servidor.", path);
            log.warn("406 at {} -> Not acceptable", path);
            
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            errorResponse = buildErrorResponse("INTERNAL_ERROR", 
                "Error interno del servidor. Por favor contacte al administrador.", path);
            log.error("500 at {} -> {}: {}", path, ex.getClass().getSimpleName(), ex.getMessage(), ex);
        }
        
        return writeErrorResponse(exchange, status, errorResponse);
    }
    
    /**
     * Maps specific business exceptions to HTTP status codes for Pragma domain.
     * Follows Open/Closed Principle - extensible without modification.
     * 
     * @param exception the business exception
     * @return the corresponding HTTP status
     */
    private HttpStatus mapBusinessExceptionToStatus(BusinessException exception) {
        return switch (exception.getCode()) {
            case "USER_EXISTS" -> HttpStatus.CONFLICT;
            case "DATA_INVALID" -> HttpStatus.BAD_REQUEST;
            case "AUTH_FAILED" -> HttpStatus.UNAUTHORIZED;
            case "USER_NOT_FOUND", "ROLE_NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "FORBIDDEN_ACCESS" -> HttpStatus.FORBIDDEN;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
    
    /**
     * Builds standardized error response following Single Responsibility Principle.
     * 
     * @param code the error code
     * @param message the error message  
     * @param path the request path
     * @return the error response DTO
     */
    private ErrorResponseDTO buildErrorResponse(String code, String message, String path) {
        return ErrorResponseDTO.builder()
                .code(code)
                .message(message)
                .path(path)
                .timestamp(Instant.now())
                .build();
    }
    
    /**
     * Extracts validation errors from WebExchangeBindException.
     * 
     * @param webe the web exchange bind exception
     * @return formatted validation error message
     */
    private String extractValidationErrors(WebExchangeBindException webe) {
        return webe.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Errores de validación en la solicitud");
    }
    
    /**
     * Extracts constraint violations from ConstraintViolationException.
     * 
     * @param cve the constraint violation exception
     * @return formatted constraint violation message
     */
    private String extractConstraintViolations(ConstraintViolationException cve) {
        return cve.getConstraintViolations().stream()
                .map(v -> (v.getPropertyPath() != null ? v.getPropertyPath().toString() + ": " : "") + v.getMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Errores de restricciones en la solicitud");
    }
    
    /**
     * Writes the error response to the exchange following reactive patterns.
     * Implements proper response handling with committed response check.
     * 
     * @param exchange the server web exchange
     * @param status the HTTP status
     * @param errorResponse the error response DTO
     * @return Mono<Void> representing the write operation
     */
    private Mono<Void> writeErrorResponse(ServerWebExchange exchange, HttpStatus status, 
                                         ErrorResponseDTO errorResponse) {
        var response = exchange.getResponse();
        
        if (response.isCommitted()) {
            log.warn("Response already committed for path {}. Cannot write error response.", 
                    errorResponse.getPath());
            return Mono.error(new RuntimeException("Response already committed"));
        }
        
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        String jsonResponse = buildJsonResponse(errorResponse);
        DataBufferFactory bufferFactory = response.bufferFactory();
        var buffer = bufferFactory.wrap(jsonResponse.getBytes(StandardCharsets.UTF_8));
        
        return response.writeWith(Mono.just(buffer));
    }
    
    /**
     * Builds a JSON response from the error response DTO.
     * Manual JSON construction for better control and performance.
     * 
     * @param errorResponse the error response DTO
     * @return the JSON string
     */
    private String buildJsonResponse(ErrorResponseDTO errorResponse) {
        return String.format("""
                {
                  "code": "%s",
                  "message": "%s",
                  "path": "%s",
                  "timestamp": "%s"
                }
                """, 
                escapeJson(errorResponse.getCode()),
                escapeJson(errorResponse.getMessage()),
                escapeJson(errorResponse.getPath()),
                errorResponse.getTimestamp().toString()
        );
    }
    
    /**
     * Escapes special characters in JSON strings to prevent injection.
     * 
     * @param str the string to escape
     * @return the escaped string
     */
    private String escapeJson(String str) {
        return str == null ? "" : str.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}