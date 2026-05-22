package com.jdpadillac.jtaskboard.auth.domain.usecase.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jdpadillac.jtaskboard.auth.domain.exception.EmailAlreadyRegisteredException;
import com.jdpadillac.jtaskboard.auth.domain.model.AuthSession;
import com.jdpadillac.jtaskboard.auth.domain.model.User;
import com.jdpadillac.jtaskboard.auth.domain.port.out.ExistsUserByEmailPort;
import com.jdpadillac.jtaskboard.auth.domain.port.out.GenerateTokenPort;
import com.jdpadillac.jtaskboard.auth.domain.port.out.PasswordHasherPort;
import com.jdpadillac.jtaskboard.auth.domain.port.out.SaveUserPort;
import com.jdpadillac.jtaskboard.auth.domain.usecase.command.RegisterUserCommand;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class RegisterUserUseCaseImplTest {

    private final SaveUserPort saveUserPort = Mockito.mock(SaveUserPort.class);
    private final ExistsUserByEmailPort existsUserByEmailPort = Mockito.mock(ExistsUserByEmailPort.class);
    private final PasswordHasherPort passwordHasherPort = Mockito.mock(PasswordHasherPort.class);
    private final GenerateTokenPort generateTokenPort = Mockito.mock(GenerateTokenPort.class);

    private final RegisterUserUseCaseImpl useCase = new RegisterUserUseCaseImpl(
            saveUserPort,
            existsUserByEmailPort,
            passwordHasherPort,
            generateTokenPort
    );

    @Test
    void shouldRegisterUserHashPasswordAndReturnToken() {
        RegisterUserCommand command = new RegisterUserCommand("Ada", "Lovelace", "Ada@Example.com", "secret123");

        when(existsUserByEmailPort.existsByEmail("ada@example.com")).thenReturn(false);
        when(passwordHasherPort.hash("secret123")).thenReturn("hashed-secret");
        when(saveUserPort.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return new User(UUID.randomUUID(), user.firstName(), user.lastName(),
                    user.email(), user.passwordHash(), user.createdAt());
        });
        when(generateTokenPort.generate(any(User.class))).thenReturn("jwt-token");

        AuthSession session = useCase.register(command);

        assertThat(session.token()).isEqualTo("jwt-token");
        assertThat(session.user().email()).isEqualTo("ada@example.com");
        assertThat(session.user().passwordHash()).isEqualTo("hashed-secret");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(saveUserPort).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.firstName()).isEqualTo("Ada");
        assertThat(saved.lastName()).isEqualTo("Lovelace");
        assertThat(saved.email()).isEqualTo("ada@example.com");
        assertThat(saved.passwordHash()).isEqualTo("hashed-secret");
        assertThat(saved.createdAt()).isBeforeOrEqualTo(Instant.now());
    }

    @Test
    void shouldThrowWhenEmailIsAlreadyRegistered() {
        RegisterUserCommand command = new RegisterUserCommand("Ada", "Lovelace", "ada@example.com", "secret123");

        when(existsUserByEmailPort.existsByEmail("ada@example.com")).thenReturn(true);

        assertThatThrownBy(() -> useCase.register(command))
                .isInstanceOf(EmailAlreadyRegisteredException.class);

        verify(saveUserPort, never()).save(any(User.class));
        verify(generateTokenPort, never()).generate(any(User.class));
    }
}
