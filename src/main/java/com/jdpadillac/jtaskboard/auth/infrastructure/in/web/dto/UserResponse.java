package com.jdpadillac.jtaskboard.auth.infrastructure.in.web.dto;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        Instant createdAt
) {
}
