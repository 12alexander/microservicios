package co.com.bancolombia.r2dbc.role;

import co.com.bancolombia.r2dbc.role.data.RoleData;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface RoleR2dbcRepository extends ReactiveCrudRepository<RoleData, String> {

    Mono<RoleData> findByNombre(String nombre);

    Mono<Boolean> existsByIdRol(String idRol);
}