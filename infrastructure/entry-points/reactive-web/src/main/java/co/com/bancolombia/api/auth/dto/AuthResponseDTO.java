package co.com.bancolombia.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication response containing user info and JWT token")
public class AuthResponseDTO {

    @Schema(description = "User ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID idUser;

    @Schema(description = "Role ID", example = "80e86d27-20a4-44be-b90d-44eeb378d409")
    private UUID idRol;

    @Schema(description = "User name", example = "John Doe")
    private String nameUser;

    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
}