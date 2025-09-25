package co.com.pragma.usecase.user;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    private UserUseCase userUseCase;

    private final User mockUser = User.builder()
            .name("javier")
            .lastName("duarte")
            .email("javier@gmail.com")
            .baseSalary(new BigDecimal(7000000))
            .birthdate(LocalDate.now())
            .build();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userUseCase = new UserUseCase(userRepository);
    }

    @Test
    void saveUser_whenEmailNotExists_shouldSaveUser() {
        when(userRepository.existUserByEmail(mockUser.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.save(mockUser)).thenReturn(Mono.just(mockUser));

        StepVerifier.create(userUseCase.save(mockUser))
                .expectNext(mockUser)
                .verifyComplete();

        verify(userRepository).existUserByEmail(mockUser.getEmail());
        verify(userRepository).save(mockUser);
    }

    @Test
    void saveUser_whenEmailExists_shouldReturnError() {
        when(userRepository.existUserByEmail(mockUser.getEmail())).thenReturn(Mono.just(true));

        StepVerifier.create(userUseCase.save(mockUser))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalStateException &&
                                throwable.getMessage().equals("The email " + mockUser.getEmail() + " is already registered"))
                .verify();

        verify(userRepository).existUserByEmail(mockUser.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    void findAllUsers_shouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(Flux.just(mockUser));

        StepVerifier.create(userUseCase.findAll())
                .expectNext(mockUser)
                .verifyComplete();

        verify(userRepository).findAll();
    }

    @Test
    void updateUser_shouldSaveUser() {
        when(userRepository.save(mockUser)).thenReturn(Mono.just(mockUser));

        StepVerifier.create(userUseCase.update(mockUser))
                .expectNext(mockUser)
                .verifyComplete();

        verify(userRepository).save(mockUser);
    }

    @Test
    void findByEmail_shouldReturnUser() {
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Mono.just(mockUser));

        StepVerifier.create(userUseCase.findByEmail(mockUser.getEmail()))
                .expectNext(mockUser)
                .verifyComplete();

        verify(userRepository).findByEmail(mockUser.getEmail());
    }

    @Test
    void deleteUserById_shouldCallRepository() {
        when(userRepository.deleteById(mockUser.getIdUser())).thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.deleteById(mockUser.getIdUser()))
                .verifyComplete();

        verify(userRepository).deleteById(mockUser.getIdUser());
    }
}
