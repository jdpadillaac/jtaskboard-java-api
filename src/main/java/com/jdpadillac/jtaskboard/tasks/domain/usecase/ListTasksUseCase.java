package com.jdpadillac.jtaskboard.tasks.domain.usecase;

import com.jdpadillac.jtaskboard.tasks.domain.model.JTask;

import java.util.List;

public interface ListTasksUseCase {
    List<JTask> list();
}

