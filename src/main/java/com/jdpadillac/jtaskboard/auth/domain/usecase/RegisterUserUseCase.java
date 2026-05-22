package com.jdpadillac.jtaskboard.auth.domain.usecase;

import com.jdpadillac.jtaskboard.auth.domain.model.AuthSession;
import com.jdpadillac.jtaskboard.auth.domain.usecase.command.RegisterUserCommand;

public interface RegisterUserUseCase {

    AuthSession register(RegisterUserCommand command);
}
