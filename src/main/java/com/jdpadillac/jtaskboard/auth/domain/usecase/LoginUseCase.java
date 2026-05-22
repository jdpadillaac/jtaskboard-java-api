package com.jdpadillac.jtaskboard.auth.domain.usecase;

import com.jdpadillac.jtaskboard.auth.domain.model.AuthSession;
import com.jdpadillac.jtaskboard.auth.domain.usecase.command.LoginCommand;

public interface LoginUseCase {

    AuthSession login(LoginCommand command);
}
