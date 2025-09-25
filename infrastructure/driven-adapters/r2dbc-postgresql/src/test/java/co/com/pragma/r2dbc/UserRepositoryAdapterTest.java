package co.com.pragma.r2dbc;

import co.com.pragma.model.user.User;
import co.com.pragma.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @InjectMocks
    UserRepositoryAdapter repositoryAdapter;

    @Mock
    UserReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    private final Long userId = 1L;

    private final UserEntity userEntity = UserEntity.builder()
            .idUser(userId)
            .name("javier")
            .lastName("duarte")
            .email("javier@gmail.com")
            .baseSalary(new BigDecimal(7000000))
            .build();

    private final User user = User.builder()
            .idUser(userId)
            .name("javier")
            .lastName("duarte")
            .email("javier@gmail.com")
            .baseSalary(new BigDecimal(7000000))
            .build();

    @Test
    void shouldFindTaskById() {

        when(mapper.map(userEntity, User.class)).thenReturn(user);

        when(repository.findById(userId)).thenReturn(Mono.just(userEntity));

        Mono<User> result = repositoryAdapter.findById(userId);

        StepVerifier.create(result)
                .expectNextMatches(t -> t.getIdUser().equals(userId))
                .verifyComplete();
    }

    @Test
    void shouldFindAllTask() {
        when(mapper.map(userEntity, User.class)).thenReturn(user);
        when(repository.findAll()).thenReturn(Flux.just(userEntity));

        Flux<User> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void shouldSaveTask() {
        when(mapper.map(userEntity, User.class)).thenReturn(user);
        when(mapper.map(user, UserEntity.class)).thenReturn(userEntity);
        when(repository.save(userEntity)).thenReturn(Mono.just(userEntity));

        Mono<User> result = repositoryAdapter.save(user);

        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();
    }
}
