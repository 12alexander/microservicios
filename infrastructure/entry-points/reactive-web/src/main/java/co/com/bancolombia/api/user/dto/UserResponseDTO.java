package co.com.bancolombia.api.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Schema(description = "Información del usuario")
public class UserResponseDTO {

    private String id;
    private String name;
    private String lastName;
    private LocalDate birthDate;
    private String address;
    private String phone;
    private String emailAddress;
    private BigDecimal baseSalary;

    @JsonProperty("id_rol")
    @Schema(description = "ID del rol del usuario")
    private String idRol;

    @JsonProperty("role")
    @Schema(description = "Información detallada del rol")
    private RoleResponseDTO role;

}
