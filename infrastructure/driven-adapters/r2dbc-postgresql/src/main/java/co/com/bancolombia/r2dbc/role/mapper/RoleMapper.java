package co.com.bancolombia.r2dbc.role.mapper;

import co.com.bancolombia.model.role.Role;
import co.com.bancolombia.r2dbc.role.data.RoleData;

public class RoleMapper {

    public static Role toDomain(RoleData roleData) {
        if (roleData == null) {
            return null;
        }
        return Role.builder()
                .idRol(roleData.getIdRol())
                .nombre(roleData.getNombre())
                .descripcion(roleData.getDescripcion())
                .build();
    }

    public static RoleData toData(Role role) {
        if (role == null) {
            return null;
        }
        return RoleData.builder()
                .idRol(role.getIdRol())
                .nombre(role.getNombre())
                .descripcion(role.getDescripcion())
                .build();
    }
}