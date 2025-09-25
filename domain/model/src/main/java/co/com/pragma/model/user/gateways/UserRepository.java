package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<User> save(User user);

    Flux<User> findAll();

    Mono<User> update(User user);

    Mono<User> findById(Long id);

    Mono<Void> deleteById(Long userId);

    Mono<Boolean> existUserByEmail(String email);

    Mono<User> findByEmail(String email);
}
