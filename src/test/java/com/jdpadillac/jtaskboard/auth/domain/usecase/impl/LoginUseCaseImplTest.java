package com.jdpadillac.jtaskboard.auth.domain.usecase.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jdpadillac.jtaskboard.auth.domain.exception.InvalidCredentialsException;
import com.jdpadillac.jtaskboard.auth.domain.model.AuthSession;
import com.jdpadillac.jtaskboard.auth.domain.model.User;
import com.jdpadillac.jtaskboard.auth.domain.port.out.FindUserByEmailPort;
import com.jdpadillac.jtaskboard.auth.domain.port.out.GenerateTokenPort;
import com.jdpadillac.jtaskboard.auth.domain.port.out.PasswordHasherPort;
import com.jdpadillac.jtaskboard.auth.domain.usecase.command.LoginCommand;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class LoginUseCaseImplTest {

    private final FindUserByEmailPort findUserByEmailPort = Mockito.mock(FindUserByEmailPort.class);
    private final PasswordHasherPort passwordHasherPort = Mockito.mock(PasswordHasherPort.class);
    private final GenerateTokenPort generateTokenPort = Mockito.mock(GenerateTokenPort.class);

    private final LoginUseCaseImpl useCase = new LoginUseCaseImpl(
            findUserByEmailPort,
            passwordHasherPort,
            generateTokenPort
    );

    private final User existingUser = new User(
            UUID.randomUUID(), "Ada", "Lovelace", "ada@example.com", "hashed-secret", Instant.now());

    @Test
    void shouldLoginAndReturnTokenWhenCredentialsAreValid() {
        LoginCommand command = new LoginCommand("Ada@Example.com", "secret123");

        when(findUserByEmailPort.findByEmail("ada@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordHasherPort.matches("secret123", "hashed-secret")).thenReturn(true);
        when(generateTokenPort.generate(existingUser)).thenReturn("jwt-token");

        AuthSession session = useCase.login(command);

        assertThat(session.token()).isEqualTo("jwt-token");
        assertThat(session.user()).isEqualTo(existingUser);
    }

    @Test
    void shouldThrowWhenUserDoesNotExist() {
        LoginCommand command = new LoginCommand("missing@example.com", "secret123");

        when(findUserByEmailPort.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.login(command))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(generateTokenPort, never()).generate(any(User.class));
    }

    @Test
    void shouldThrowWhenPasswordDoesNotMatch() {
        LoginCommand command = new LoginCommand("ada@example.com", "wrong-password");

        when(findUserByEmailPort.findByEmail("ada@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordHasherPort.matches("wrong-password", "hashed-secret")).thenReturn(false);

        assertThatThrownBy(() -> useCase.login(command))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(generateTokenPort, never()).generate(any(User.class));
    }
}
