package com.jdpadillac.jtaskboard.tasks.infrastructure.in.web.dto;

import com.jdpadillac.jtaskboard.tasks.domain.model.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskStatusRequest(
        @NotNull(message = "status must not be null")
        TaskStatus status
) {
}

