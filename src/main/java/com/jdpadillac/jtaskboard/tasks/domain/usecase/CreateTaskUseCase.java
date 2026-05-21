package com.jdpadillac.jtaskboard.tasks.domain.usecase;

import com.jdpadillac.jtaskboard.tasks.domain.model.Task;
import com.jdpadillac.jtaskboard.tasks.domain.usecase.command.CreateTaskCommand;

public interface CreateTaskUseCase {

    Task create(CreateTaskCommand command);
}

