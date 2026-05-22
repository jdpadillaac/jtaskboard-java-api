package com.jdpadillac.jtaskboard.auth.infrastructure.in.web.mapper;

import com.jdpadillac.jtaskboard.auth.domain.model.AuthSession;
import com.jdpadillac.jtaskboard.auth.domain.model.User;
import com.jdpadillac.jtaskboard.auth.domain.usecase.command.LoginCommand;
import com.jdpadillac.jtaskboard.auth.domain.usecase.command.RegisterUserCommand;
import com.jdpadillac.jtaskboard.auth.infrastructure.in.web.dto.AuthResponse;
import com.jdpadillac.jtaskboard.auth.infrastructure.in.web.dto.LoginRequest;
import com.jdpadillac.jtaskboard.auth.infrastructure.in.web.dto.RegisterRequest;
import com.jdpadillac.jtaskboard.auth.infrastructure.in.web.dto.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthWebMapper {

    RegisterUserCommand toCommand(RegisterRequest request);

    LoginCommand toCommand(LoginRequest request);

    @Mapping(target = "accessToken", source = "token")
    @Mapping(target = "tokenType", constant = "Bearer")
    @Mapping(target = "user", source = "user")
    AuthResponse toResponse(AuthSession session);

    UserResponse toResponse(User user);
}
