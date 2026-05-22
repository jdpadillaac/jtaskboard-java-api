package com.jdpadillac.jtaskboard.auth.domain.usecase.impl;

import com.jdpadillac.jtaskboard.auth.domain.exception.EmailAlreadyRegisteredException;
import com.jdpadillac.jtaskboard.auth.domain.model.AuthSession;
import com.jdpadillac.jtaskboard.auth.domain.model.User;
import com.jdpadillac.jtaskboard.auth.domain.port.out.ExistsUserByEmailPort;
import com.jdpadillac.jtaskboard.auth.domain.port.out.GenerateTokenPort;
import com.jdpadillac.jtaskboard.auth.domain.port.out.PasswordHasherPort;
import com.jdpadillac.jtaskboard.auth.domain.port.out.SaveUserPort;
import com.jdpadillac.jtaskboard.auth.domain.usecase.RegisterUserUseCase;
import com.jdpadillac.jtaskboard.auth.domain.usecase.command.RegisterUserCommand;
import java.time.Instant;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    private final SaveUserPort saveUserPort;
    private final ExistsUserByEmailPort existsUserByEmailPort;
    private final PasswordHasherPort passwordHasherPort;
    private final GenerateTokenPort generateTokenPort;

    @Override
    public AuthSession register(RegisterUserCommand command) {
        String email = normalizeEmail(command.email());

        if (existsUserByEmailPort.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException(email);
        }

        User userToCreate = new User(
                null,
                command.firstName().trim(),
                command.lastName().trim(),
                email,
                passwordHasherPort.hash(command.password()),
                Instant.now()
        );

        User savedUser = saveUserPort.save(userToCreate);
        String token = generateTokenPort.generate(savedUser);

        return new AuthSession(token, savedUser);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
