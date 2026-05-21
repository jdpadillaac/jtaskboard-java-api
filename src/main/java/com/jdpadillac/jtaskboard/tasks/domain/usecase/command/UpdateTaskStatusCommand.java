package com.jdpadillac.jtaskboard.tasks.domain.usecase.command;

import com.jdpadillac.jtaskboard.tasks.domain.model.TaskStatus;
import java.util.UUID;

public record UpdateTaskStatusCommand(UUID id, TaskStatus status) {
}

