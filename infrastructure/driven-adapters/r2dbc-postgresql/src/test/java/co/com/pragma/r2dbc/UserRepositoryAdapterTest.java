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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {
    // TODO: change four you own tests

    @InjectMocks
    UserRepositoryAdapter repositoryAdapter;

    @Mock
    UserReactiveRepository repository;

    @Mock
    ObjectMapper mapper;


    @Test
    void mustFindValueById() {

        UserEntity user = UserEntity.builder()
                .idUser(1L)
                .email("example@gmail.com")
                .build();
        when(repository.findById(1L)).thenReturn(Mono.just(user));
        when(mapper.map("test", Object.class)).thenReturn("test");

        Mono<User> result = repositoryAdapter.findById(1L);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(user))
                .verifyComplete();
    }

    @Test
    void mustFindAllValues() {
        UserEntity user = UserEntity.builder()
                .idUser(1L)
                .email("example@gmail.com")
                .build();
        when(repository.findAll()).thenReturn(Flux.just(user));
        when(mapper.map(user, UserEntity.class)).thenReturn(user);

        Flux<User> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(user))
                .verifyComplete();
    }

    @Test
    void mustFindByExample() {
        User user = User.builder()
                .idUser(1L)
                .email("example@gmail.com")
                .build();

        when(repository.findAll(any(Example.class))).thenReturn(Flux.just(user));
        when(mapper.map(user, User.class)).thenReturn(user);

        Flux<User> result = repositoryAdapter.findByExample(user);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(user))
                .verifyComplete();
    }

    @Test
    void mustSaveValue() {
        UserEntity user = UserEntity.builder()
                .idUser(1L)
                .email("example@gmail.com")
                .build();
        when(repository.save(user)).thenReturn(Mono.just(user));
        when(mapper.map("test", Object.class)).thenReturn("test");
        User user1 = User.builder()
                .idUser(1L)
                .email("example@gmail.com")
                .build();
        Mono<User> result = repositoryAdapter.save(user1);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(user1))
                .verifyComplete();
    }
}
