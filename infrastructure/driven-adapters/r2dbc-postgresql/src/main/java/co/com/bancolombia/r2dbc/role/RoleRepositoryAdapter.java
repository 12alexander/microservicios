package co.com.bancolombia.r2dbc.role;

import co.com.bancolombia.model.role.Role;
import co.com.bancolombia.model.role.gateways.RoleRepository;
import co.com.bancolombia.r2dbc.role.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepository {

    private final RoleR2dbcRepository repository;

    @Override
    public Mono<Role> createRole(Role role) {
        log.debug("Creando rol: {}", role.getNombre());
        return Mono.fromCallable(() -> RoleMapper.toData(role))
                .flatMap(repository::save)
                .map(RoleMapper::toDomain)
                .doOnSuccess(savedRole ->
                        log.debug("Rol creado exitosamente: {}", savedRole.getIdRol())
                )
                .doOnError(error ->
                        log.error("Error al crear rol: {}", error.getMessage())
                );
    }

    @Override
    public Mono<Role> updateRole(Role role) {
        log.debug("Actualizando rol: {}", role.getIdRol());
        return Mono.fromCallable(() -> RoleMapper.toData(role))
                .flatMap(repository::save)
                .map(RoleMapper::toDomain)
                .doOnSuccess(updatedRole ->
                        log.debug("Rol actualizado exitosamente: {}", updatedRole.getIdRol())
                );
    }

    @Override
    public Mono<Role> getRoleById(String idRol) {
        log.debug("Obteniendo rol por ID: {}", idRol);
        return repository.findById(idRol)
                .map(RoleMapper::toDomain)
                .doOnSuccess(role ->
                        log.debug("Rol encontrado: {}", role != null ? role.getIdRol() : "null")
                );
    }

    @Override
    public Mono<Role> getRoleByNombre(String nombre) {
        log.debug("Obteniendo rol por nombre: {}", nombre);
        return repository.findByNombre(nombre)
                .map(RoleMapper::toDomain);
    }

    @Override
    public Flux<Role> findAll() {
        log.debug("Obteniendo todos los roles");
        return repository.findAll()
                .map(RoleMapper::toDomain)
                .doOnComplete(() -> log.debug("Consulta de roles completada"));
    }

    @Override
    public Mono<Void> deleteById(String idRol) {
        log.debug("Eliminando rol con ID: {}", idRol);
        return repository.deleteById(idRol)
                .doOnSuccess(unused ->
                        log.debug("Rol eliminado exitosamente: {}", idRol)
                );
    }

    @Override
    public Mono<Boolean> existsById(String idRol) {
        log.debug("Verificando existencia de rol: {}", idRol);
        return repository.existsByIdRol(idRol)
                .defaultIfEmpty(false)
                .doOnNext(exists -> log.debug("Rol {} existe: {}", idRol, exists));
    }
}