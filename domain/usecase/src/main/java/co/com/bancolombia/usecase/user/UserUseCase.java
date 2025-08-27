package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.exception.InvalidDataException;
import co.com.bancolombia.model.exception.UserExistsException;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class UserUseCase {
    private final UserRepository userRepository;

    public Mono<User> saveUser(User user) {
        return Mono.fromRunnable(() -> validateData(user))
                .then(confirmEmailNotRegistered(user.getEmailAddress()))
                .then(Mono.fromCallable(() -> asignarId(user)))
                .flatMap(this::createUser);
    }

    public Mono<User> updateUser(String id, User usuario) {
        return Mono.fromRunnable(() -> validateData(usuario))
                .then(userRepository.getUserById(id))
                .switchIfEmpty(Mono.error(new UserExistsException(id)))
                .then(Mono.fromCallable(() -> usuario.toBuilder().id(id).build()))
                .flatMap(this::updateUser);
    }

    private void validateData(User user) {
        try {
            user.validateData();
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException(e.getMessage());
        }
    }

    private Mono<Void> confirmEmailNotRegistered(String emailAddress) {
        return userRepository.emailAddressExists(emailAddress)
                .flatMap(existe -> {
                    if (existe) {
                        return Mono.error(new UserExistsException(emailAddress));
                    }
                    return Mono.empty();
                });
    }

    private User asignarId(User user) {
        return user.toBuilder()
                .id(UUID.randomUUID().toString())
                .build();
    }

    private Mono<User> createUser(User user) {
        return userRepository.createUser(user)
                .onErrorMap(error ->
                        new InvalidDataException("Error interno al guardar usuario", error)
                );
    }

    private Mono<User> updateUser(User user) {
        return userRepository.updateUser(user)
                .onErrorMap(error ->
                        new InvalidDataException("Error interno al actualizar usuario", error)
                );
    }
}
