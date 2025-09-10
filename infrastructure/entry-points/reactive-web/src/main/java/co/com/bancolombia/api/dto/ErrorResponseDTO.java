package co.com.bancolombia.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Unified error response DTO for all API endpoints.
 * Consolidates error responses across the entire application.
 * 
 * Follows Single Responsibility Principle and DRY principle by 
 * centralizing error response structure.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Unified error response model for all API endpoints")
public class ErrorResponseDTO {

    @Schema(description = "Error code", example = "USER_EXISTS")
    private String code;

    @Schema(description = "Error message", example = "User already exists with this email")
    private String message;

    @Schema(description = "Error timestamp", example = "2024-01-01T12:00:00Z")
    private Instant timestamp;

    @Schema(description = "Request path where error occurred", example = "/api/users")
    private String path;
}