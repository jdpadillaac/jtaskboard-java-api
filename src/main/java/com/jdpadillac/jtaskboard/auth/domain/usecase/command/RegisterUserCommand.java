package com.jdpadillac.jtaskboard.auth.domain.usecase.command;

public record RegisterUserCommand(
        String firstName,
        String lastName,
        String email,
        String password
) {
}
