package com.jdpadillac.jtaskboard.tasks.domain.usecase;

import com.jdpadillac.jtaskboard.tasks.domain.model.JTask;
import com.jdpadillac.jtaskboard.tasks.domain.usecase.command.UpdateTaskCommand;

public interface UpdateTaskUseCase {

    JTask update(UpdateTaskCommand command);
}

