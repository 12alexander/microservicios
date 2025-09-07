package co.com.bancolombia.api.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Schema(description = "Información del rol del usuario")
public class RoleResponseDTO {

    @JsonProperty("id_rol")
    @Schema(description = "ID único del rol", example = "1")
    private String idRol;

    @JsonProperty("nombre")
    @Schema(description = "Nombre del rol", example = "ADMIN")
    private String nombre;

    @JsonProperty("descripcion")
    @Schema(description = "Descripción del rol", example = "Administrador del sistema")
    private String descripcion;
}