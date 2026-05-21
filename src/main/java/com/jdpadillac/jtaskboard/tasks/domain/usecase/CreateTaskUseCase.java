package com.jdpadillac.jtaskboard.tasks.domain.usecase;

import com.jdpadillac.jtaskboard.tasks.domain.model.JTask;
import com.jdpadillac.jtaskboard.tasks.domain.usecase.command.CreateTaskCommand;

public interface CreateTaskUseCase {

    JTask create(CreateTaskCommand command);
}

