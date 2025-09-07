package co.com.bancolombia.r2dbc.user;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.r2dbc.role.RoleR2dbcRepository;
import co.com.bancolombia.r2dbc.role.mapper.RoleMapper;
import co.com.bancolombia.r2dbc.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserR2dbcRepository repository;
    private final RoleR2dbcRepository roleRepository;

    @Override
    @Transactional
    public Mono<User> createUser(User user) {
        log.debug("Guardando usuario con ID: {}", user.getId());
        return Mono.fromCallable(() -> UserMapper.toDataForCreation(user))
                .flatMap(data -> repository.createUser(
                        data.getId(),
                        data.getName(),
                        data.getLastName(),
                        data.getBirthDate(),
                        data.getAddress(),
                        data.getPhone(),
                        data.getEmailAddress(),
                        data.getBaseSalary(),
                        data.getIdRol(),
                        data.getPassword(),
                        data.getCreationDate(),
                        data.getUpdateDate()
                ))
                .then(repository.findById(user.getId()))
                .map(UserMapper::toDomain)
                .doOnSuccess(userCreated ->
                        log.debug("Usuario creado exitosamente con ID: {}", userCreated.getId())
                )
                .doOnError(error ->
                        log.error("Error al guardar usuario: {}", error.getMessage())
                );
    }

    @Override
    @Transactional
    public Mono<User> updateUser(User user) {
        log.debug("Actualizar usuario con ID: {}", user.getId());
        return Mono.fromCallable(() -> UserMapper.toDataForUpdate(user))
                .flatMap(repository::save)
                .map(UserMapper::toDomain)
                .doOnSuccess(usuarioActualizado ->
                        log.debug("Usuario actualizado exitosamente con ID: {}", usuarioActualizado.getId())
                )
                .doOnError(error ->
                        log.error("Error al actualizar usuario: {}", error.getMessage())
                );
    }

    @Override
    public Mono<Boolean> emailAddressExists(String emailAddress) {
        log.debug("Verificando existencia de correo: {}", emailAddress);
        return repository.existsByEmailAddress(emailAddress)
                .defaultIfEmpty(false)
                .doOnNext(existe -> log.debug("Verificacion correo {}: {}", emailAddress, existe));
    }

    @Override
    public Mono<User> getUserById(String id) {
        log.debug("Obteniendo usuario por ID: {}", id);
        return repository.findById(id)
                .flatMap(this::enrichWithRole)
                .map(UserMapper::toDomain)
                .doOnSuccess(user ->
                        log.debug("Usuario encontrado: {}", user != null ? user.getId() : "null")
                );
    }

    @Override
    public Mono<User> getUserByEmailAddress(String emailAddress) {
        log.debug("Obteniendo usuario por correo: {}", emailAddress);
        return repository.findByEmailAddress(emailAddress)
                .map(UserMapper::toDomain)
                .doOnSuccess(user ->
                        log.debug("Usuario encontrado por correo: {}", user != null ? user.getId() : "null")
                );
    }

    @Override
    public Flux<User> findAll() {
        log.debug("Obteniendo todos los usuarios");
        return repository.findAll()
                .flatMap(this::enrichWithRole)
                .map(UserMapper::toDomain)
                .doOnComplete(() -> log.debug("Consulta de usuarios completada"));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        log.debug("Eliminando usuario con ID: {}", id);
        return repository.deleteById(id)
                .doOnSuccess(unused ->
                        log.debug("Usuario eliminado exitosamente con ID: {}", id)
                )
                .doOnError(error ->
                        log.error("Error al eliminar usuario con ID {}: {}", id, error.getMessage())
                );
    }

    private Mono<co.com.bancolombia.r2dbc.user.data.UserData> enrichWithRole(co.com.bancolombia.r2dbc.user.data.UserData userData) {
        if (userData.getIdRol() == null) {
            return Mono.just(userData);
        }
        return roleRepository.findById(userData.getIdRol())
                .map(roleData -> userData.toBuilder()
                        .role(roleData)
                        .build())
                .switchIfEmpty(Mono.just(userData));
    }

}
