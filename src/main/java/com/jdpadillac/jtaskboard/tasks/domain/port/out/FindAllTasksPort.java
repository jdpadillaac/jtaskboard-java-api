package com.jdpadillac.jtaskboard.tasks.domain.port.out;

import com.jdpadillac.jtaskboard.tasks.domain.model.JTask;

import java.util.List;

public interface FindAllTasksPort {
    List<JTask> findAll();
}
