package co.com.bancolombia.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Error response model")
public class ErrorResponseDTO {

    @Schema(description = "Error code", example = "USER_EXISTS")
    private String code;

    @Schema(description = "Error message", example = "User already exists with this email")
    private String message;

    @Schema(description = "Error timestamp", example = "2024-01-01T12:00:00Z")
    private Instant timestamp;

    @Schema(description = "Request path", example = "/api/users")
    private String path;
}
