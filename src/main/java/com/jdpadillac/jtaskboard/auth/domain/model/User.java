package com.jdpadillac.jtaskboard.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

public record User(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String passwordHash,
        Instant createdAt
) {
}
