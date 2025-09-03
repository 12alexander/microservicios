package co.com.bancolombia.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error response with details about authentication failures")
public class ErrorResponseDTO {

    @Schema(description = "Error type", example = "Authentication failed")
    private String error;

    @Schema(description = "Detailed error message", example = "Invalid email or password")
    private String message;

    @Schema(description = "Error code", example = "AUTH_FAILED")
    private String code;
}