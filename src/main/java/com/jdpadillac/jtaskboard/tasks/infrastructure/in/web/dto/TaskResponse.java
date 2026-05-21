package com.jdpadillac.jtaskboard.tasks.infrastructure.in.web.dto;

import com.jdpadillac.jtaskboard.tasks.domain.model.TaskStatus;
import java.time.Instant;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        String taskKey,
        String title,
        String description,
        TaskStatus status,
        Instant createdAt
) {
}

