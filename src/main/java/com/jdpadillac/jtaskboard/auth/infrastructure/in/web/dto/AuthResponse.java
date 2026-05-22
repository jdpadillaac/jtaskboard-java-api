package com.jdpadillac.jtaskboard.auth.infrastructure.in.web.dto;

public record AuthResponse(
        String accessToken,
        String tokenType,
        UserResponse user
) {
}
