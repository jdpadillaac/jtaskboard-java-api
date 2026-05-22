package com.jdpadillac.jtaskboard.auth.domain.usecase.impl;

import com.jdpadillac.jtaskboard.auth.domain.exception.InvalidCredentialsException;
import com.jdpadillac.jtaskboard.auth.domain.model.AuthSession;
import com.jdpadillac.jtaskboard.auth.domain.model.User;
import com.jdpadillac.jtaskboard.auth.domain.port.out.FindUserByEmailPort;
import com.jdpadillac.jtaskboard.auth.domain.port.out.GenerateTokenPort;
import com.jdpadillac.jtaskboard.auth.domain.port.out.PasswordHasherPort;
import com.jdpadillac.jtaskboard.auth.domain.usecase.LoginUseCase;
import com.jdpadillac.jtaskboard.auth.domain.usecase.command.LoginCommand;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUseCaseImpl implements LoginUseCase {

    private final FindUserByEmailPort findUserByEmailPort;
    private final PasswordHasherPort passwordHasherPort;
    private final GenerateTokenPort generateTokenPort;

    @Override
    public AuthSession login(LoginCommand command) {
        String email = command.email().trim().toLowerCase(Locale.ROOT);

        User user = findUserByEmailPort.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordHasherPort.matches(command.password(), user.passwordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = generateTokenPort.generate(user);

        return new AuthSession(token, user);
    }
}
