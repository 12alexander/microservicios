package co.com.bancolombia.model.role;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Role {
    private String idRol;
    private String nombre;
    private String descripcion;

    public static class RoleBuilder {
        public RoleBuilder nombre(String nombre) {
            if(nombre == null || nombre.trim().isEmpty()){
                throw new IllegalArgumentException("Nombre del rol no puede ser Nulo o Vacio");
            }
            this.nombre = nombre.trim();
            return this;
        }

        public RoleBuilder descripcion(String descripcion) {
            if(descripcion == null || descripcion.trim().isEmpty()){
                throw new IllegalArgumentException("Descripcion del rol no puede ser Nulo o Vacio");
            }
            this.descripcion = descripcion.trim();
            return this;
        }
    }

    public void validateData(){
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del rol no puede ser nulo o vacío");
        }

        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción del rol no puede ser nula o vacía");
        }
    }
}