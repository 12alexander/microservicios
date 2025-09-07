package co.com.bancolombia.model.role.gateways;

import co.com.bancolombia.model.role.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RoleRepository {

    Mono<Role> createRole(Role role);

    Mono<Role> updateRole(Role role);

    Mono<Role> getRoleById(String idRol);

    Mono<Role> getRoleByNombre(String nombre);

    Flux<Role> findAll();

    Mono<Void> deleteById(String idRol);

    Mono<Boolean> existsById(String idRol);
}