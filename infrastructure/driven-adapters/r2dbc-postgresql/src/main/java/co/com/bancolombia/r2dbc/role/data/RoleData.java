package co.com.bancolombia.r2dbc.role.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("roles")
public class RoleData {

    @Id
    @Column("id_rol")
    private String idRol;

    @Column("nombre")
    private String nombre;

    @Column("descripcion")
    private String descripcion;
}