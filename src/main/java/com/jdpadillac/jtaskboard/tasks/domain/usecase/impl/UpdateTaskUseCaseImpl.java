package com.jdpadillac.jtaskboard.tasks.domain.usecase.impl;

import com.jdpadillac.jtaskboard.tasks.domain.exception.TaskNotFoundException;
import com.jdpadillac.jtaskboard.tasks.domain.model.JTask;
import com.jdpadillac.jtaskboard.tasks.domain.port.out.FindTaskByIdPort;
import com.jdpadillac.jtaskboard.tasks.domain.port.out.SaveTaskPort;
import com.jdpadillac.jtaskboard.tasks.domain.usecase.UpdateTaskUseCase;
import com.jdpadillac.jtaskboard.tasks.domain.usecase.command.UpdateTaskCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateTaskUseCaseImpl implements UpdateTaskUseCase {

    private final FindTaskByIdPort findTaskByIdPort;
    private final SaveTaskPort saveTaskPort;

    @Override
    public JTask update(UpdateTaskCommand command) {
        JTask existingTask = findTaskByIdPort.findById(command.id())
                .orElseThrow(() -> new TaskNotFoundException(command.id()));

        JTask updatedTask = new JTask(
                existingTask.id(),
                existingTask.taskKey(),
                command.title(),
                command.description(),
                existingTask.status(),
                existingTask.createdAt()
        );

        return saveTaskPort.save(updatedTask);
    }
}

