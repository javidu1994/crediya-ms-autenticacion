package co.com.pragma.r2dbc;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.r2dbc.entity.UserEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class UserRepositoryAdapter extends ReactiveAdapterOperations<User, UserEntity, Long, UserReactiveRepository>
        implements UserRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRepositoryAdapter.class);

    public UserRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, User.class));
    }

    @Override
    public Mono<User> save(User user) {
        return super.save(user)
                .doOnSuccess(u -> LOGGER.debug("save user: {}", u));
    }

    @Override
    public Flux<User> findAll() {
        return super.findAll()
                .doOnNext(u -> LOGGER.debug("users returned: {}", u))
                .map(u -> u);
    }

    @Override
    public Mono<User> update(User user) {
        return super.save(user)
                .doOnSuccess(u -> LOGGER.debug("update user: {}", u));
    }

    @Override
    public Mono<User> findById(Long id) {
        return super.findById(id)
                .doOnNext(u -> LOGGER.debug("findById with param: {}", id));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id)
                .doOnSuccess(u -> LOGGER.debug("delete user: {}", id));
    }

    @Override
    public Mono<Boolean> existUserByEmail(String email) {
        User user = User.builder()
                .email(email)
                .build();
        return super.findByExample(user)
                .doOnNext(exists -> LOGGER.debug("existUserByEmail with param: {} -> {}", email, exists))
                .next()
                .map(u -> true)
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return super.repository.findByEmail(email)
                .map(super::toEntity)
                .doOnNext(u -> LOGGER.debug("findByEmail with param: {} -> {}", email, u));
    }

}
