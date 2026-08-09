package co.com.pragma.api;

import co.com.pragma.api.config.UserPath;
import co.com.pragma.api.dto.UserRequestDTO;
import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.UserUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@ContextConfiguration(classes = {RouterRest.class, UserHandler.class})
@EnableConfigurationProperties(UserPath.class)
@WebFluxTest
class RouterRestTest {

    private WebTestClient webTestClient;
    private UserHandler userHandler;
    private UserPath userPath;

    @BeforeEach
    void setUp() {
        // Mock dependencies
        userHandler = Mockito.mock(UserHandler.class);
        userPath = Mockito.mock(UserPath.class);; // Lambda for interface UserPath

        // Create RouterRest instance with mocks
        RouterRest routerRest = new RouterRest(userPath, userHandler);

        RouterFunction<ServerResponse> routerFunction = routerRest.routerFunction(userHandler);

        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
    }

    @Test
    void shouldRouteToGetAllUsers() {
        User user = User.builder()
                .name("javier")
                .lastName("duarte")
                .email("javier@gmail.com")
                .build();


        when(userHandler.listenGetAllUsers(any())).thenReturn(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Flux.just(user), User.class)
        );

        webTestClient.get()
                .uri("/users")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("javier")
                .jsonPath("$[0].email").isEqualTo("javier@gmail.com");
    }

    @Test
    void shouldRouteToSaveUser() {
        User savedUser = User.builder()
                .name("javier")
                .lastName("duarte")
                .email("javier@gmail.com")
                .build();
        UserRequestDTO requestDTO = UserRequestDTO.builder()
                .name("javier")
                .lastName("duarte")
                .email("javier@gmail.com")
                .build();

        when(userHandler.listenSaveUser(any())).thenReturn(
                ServerResponse.created(null)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(savedUser), User.class)
        );

        webTestClient.post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(requestDTO))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("javier")
                .jsonPath("$.email").isEqualTo("javier@gmail.com");
    }
}
