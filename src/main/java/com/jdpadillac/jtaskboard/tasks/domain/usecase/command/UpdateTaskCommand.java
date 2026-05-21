package com.jdpadillac.jtaskboard.tasks.domain.usecase.command;

import java.util.UUID;

public record UpdateTaskCommand(UUID id, String title, String description) {
}

