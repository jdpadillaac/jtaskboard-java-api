package com.jdpadillac.jtaskboard.tasks.domain.usecase;

import com.jdpadillac.jtaskboard.tasks.domain.model.JTask;
import com.jdpadillac.jtaskboard.tasks.domain.usecase.command.UpdateTaskStatusCommand;

public interface UpdateTaskStatusUseCase {

    JTask updateStatus(UpdateTaskStatusCommand command);
}

