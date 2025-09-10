package co.com.bancolombia.usecase.user.interfaces;

import co.com.bancolombia.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IUserUseCase {
    Mono<User> saveUser(User user);
    Mono<User> updateUser(String id, User usuario);
    Flux<User> getAllUsers();
    Mono<User> getUserById(String id);
    Mono<User> getUserByEmail(String email);
    Mono<User> findById(Long id);
    Flux<User> findAll();
    Mono<Void> deleteUser(String id);
}
