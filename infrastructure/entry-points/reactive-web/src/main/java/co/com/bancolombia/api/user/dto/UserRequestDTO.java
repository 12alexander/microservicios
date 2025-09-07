package co.com.bancolombia.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request data for creating a new user")
public class UserRequestDTO {

    @NotBlank(message = "El nombre es requerido")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;

    @NotBlank(message = "Los apellidos son requeridos")
    @Size(min = 2, max = 100, message = "Los apellidos deben tener entre 2 y 100 caracteres")
    private String lastName;

    private LocalDate birthDate;

    private String address;

    private String phone;

    @NotBlank(message = "El correo electrónico es requerido")
    @Email(message = "El formato del correo electrónico no es válido")
    private String emailAddress;

    @NotNull(message = "El salario base es requerido")
    @DecimalMin(value = "0.01", message = "El salario base debe ser mayor a 0")
    @DecimalMax(value = "15000000", message = "El salario base no puede ser mayor a 15,000,000")
    private BigDecimal baseSalary;

    @NotBlank(message = "El ID del rol es requerido")
    private String idRol;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 3, message = "La contraseña debe tener al menos 3 caracteres")
    @Schema(description = "User password", example = "123", minLength = 3)
    private String password;

}
