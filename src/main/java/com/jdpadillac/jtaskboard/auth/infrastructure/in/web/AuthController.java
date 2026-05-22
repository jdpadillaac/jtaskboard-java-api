package com.jdpadillac.jtaskboard.auth.infrastructure.in.web;

import com.jdpadillac.jtaskboard.auth.domain.model.AuthSession;
import com.jdpadillac.jtaskboard.auth.domain.usecase.LoginUseCase;
import com.jdpadillac.jtaskboard.auth.domain.usecase.RegisterUserUseCase;
import com.jdpadillac.jtaskboard.auth.infrastructure.in.web.dto.AuthResponse;
import com.jdpadillac.jtaskboard.auth.infrastructure.in.web.dto.LoginRequest;
import com.jdpadillac.jtaskboard.auth.infrastructure.in.web.dto.RegisterRequest;
import com.jdpadillac.jtaskboard.auth.infrastructure.in.web.mapper.AuthWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final AuthWebMapper authWebMapper;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Validated @RequestBody RegisterRequest request) {
        AuthSession session = registerUserUseCase.register(authWebMapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(authWebMapper.toResponse(session));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Validated @RequestBody LoginRequest request) {
        AuthSession session = loginUseCase.login(authWebMapper.toCommand(request));
        return ResponseEntity.ok(authWebMapper.toResponse(session));
    }
}
