package com.jdpadillac.jtaskboard.tasks.domain.model;

import java.time.Instant;
import java.util.UUID;

public record Task(
        UUID id,
        String taskKey,
        String title,
        String description,
        TaskStatus status,
        Instant createdAt
) {
}

