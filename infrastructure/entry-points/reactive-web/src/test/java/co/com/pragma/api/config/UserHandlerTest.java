package co.com.pragma.api.config;

import co.com.pragma.api.RouterRest;
import co.com.pragma.api.UserHandler;
import co.com.pragma.api.dto.UserDTO;
import co.com.pragma.api.dto.UserRequestDTO;
import co.com.pragma.api.exception.ValidationException;
import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.UserUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {RouterRest.class, UserHandler.class})
@EnableConfigurationProperties(UserPath.class)
@WebFluxTest
public class UserHandlerTest {

    private WebTestClient webTestClient;
    private UserUseCase userUseCase;
    private Validator validator;
    private ObjectMapper objectMapper;
    private UserPath userPath;

    @BeforeEach
    void setUp() {
        userUseCase = mock(UserUseCase.class);
        validator = mock(Validator.class);
        objectMapper = new ObjectMapper();
        userPath = mock(UserPath.class);

        UserHandler handler = new UserHandler(userUseCase, validator, objectMapper);

        RouterFunction<ServerResponse> router = new RouterRest(userPath, handler).routerFunction(handler);

        webTestClient = WebTestClient.bindToRouterFunction(router).build();
    }

    @Test
    void testGetAllUsers_shouldReturnList() {
        User user = User.builder()
                .idUser(1L)
                .name("javier")
                .lastName("duarte")
                .email("javier@gmail.com")
                .baseSalary(new BigDecimal(7000000))
                .build();
        when(userUseCase.findAll()).thenReturn(Flux.just(user));

        webTestClient.get()
                .uri("/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("Jane")
                .jsonPath("$[0].email").isEqualTo("jane@example.com");

        verify(userUseCase).findAll();
    }

    @Test
    void testSaveUser_shouldReturnCreatedUser() {
        UserRequestDTO requestDTO = UserRequestDTO.builder()
                .name("javier")
                .lastName("duarte")
                .email("javier@gmail.com")
                .baseSalary(new BigDecimal(7000000))
                .build();
        User savedUser = User.builder()
                .idUser(99L)
                .name("javier")
                .lastName("duarte")
                .email("javier@gmail.com")
                .baseSalary(new BigDecimal(7000000))
                .build();

        // Simula que la validación pasa
        doNothing().when(validator).validate(any(), any());

        when(userUseCase.save(any(User.class))).thenReturn(Mono.just(savedUser));

        webTestClient.post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestDTO))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.idUser").isEqualTo(99)
                .jsonPath("$.name").isEqualTo("John")
                .jsonPath("$.email").isEqualTo("john@example.com");

        verify(userUseCase).save(any(User.class));
    }

    @Test
    void testSaveUser_shouldReturnBadRequest_whenValidationFails() {
        UserRequestDTO invalidDTO = new UserRequestDTO(); // nombre y correo vacíos

        // Simula que hay errores de validación
        doAnswer(invocation -> {
            Object target = invocation.getArgument(0);
            org.springframework.validation.Errors errors = invocation.getArgument(1);
            errors.rejectValue("email", "Invalid", "Email is required");
            return null;
        }).when(validator).validate(any(), any());

        webTestClient.post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidDTO)
                .exchange()
                .expectStatus().is5xxServerError(); // O personalizar con un handler global

        verify(validator).validate(any(), any());
        verify(userUseCase, never()).save(any());
    }


}
