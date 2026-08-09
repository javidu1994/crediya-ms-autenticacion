package co.com.pragma.usecase.user;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class UserUseCase {

    private static final Logger LOGGER = Logger.getLogger(UserUseCase.class.getName());
    private final UserRepository userRepository;

    public Mono<User> save(User user) {
        return userRepository.existUserByEmail(user.getEmail())
                .doOnNext(u -> LOGGER.info("listenSaveUser with data: {} " + user.toString()))
                .filter(Boolean.FALSE::equals)
                .switchIfEmpty(Mono.error(new IllegalStateException("The email " + user.getEmail() + " is already registered")))
                .flatMap(exist -> userRepository.save(user));
    }

    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    public Mono<User> update(User user) {
        return userRepository.save(user);
    }

    public Mono<User> findByEmail(String email) { return userRepository.findByEmail(email); }

    public Mono<Void> deleteById(Long userId) {
        return userRepository.deleteById(userId);
    }

    public Mono<Boolean> existByDni(String dni) {
        return userRepository.existUserByDni(dni);
    }

}
