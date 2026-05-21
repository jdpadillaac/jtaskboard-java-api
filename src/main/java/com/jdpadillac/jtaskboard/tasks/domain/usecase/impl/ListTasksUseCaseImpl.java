package com.jdpadillac.jtaskboard.tasks.domain.usecase.impl;

import com.jdpadillac.jtaskboard.tasks.domain.model.JTask;
import com.jdpadillac.jtaskboard.tasks.domain.port.out.FindAllTasksPort;
import com.jdpadillac.jtaskboard.tasks.domain.usecase.ListTasksUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListTasksUseCaseImpl implements ListTasksUseCase {

    private final FindAllTasksPort findAllTasksPort;

    @Override
    public List<JTask> list() {
        return findAllTasksPort.findAll();
    }
}

