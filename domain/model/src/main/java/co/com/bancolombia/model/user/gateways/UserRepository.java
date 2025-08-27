package co.com.bancolombia.model.user.gateways;

import co.com.bancolombia.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<User> createUser(User usuario);

    Mono<User> updateUser(User usuario);

    Mono<Boolean> emailAddressExists(String emailAddress);

    Mono<User> getUserById(String id);

    Mono<User> getUserByEmailAddress(String emailAddress);

    Flux<User> findAll();

    Mono<Void> deleteById(String id);
}
