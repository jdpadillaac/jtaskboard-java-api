package com.jdpadillac.jtaskboard.tasks.domain.usecase.impl;

import com.jdpadillac.jtaskboard.tasks.domain.exception.TaskNotFoundException;
import com.jdpadillac.jtaskboard.tasks.domain.model.JTask;
import com.jdpadillac.jtaskboard.tasks.domain.port.out.FindTaskByIdPort;
import com.jdpadillac.jtaskboard.tasks.domain.port.out.SaveTaskPort;
import com.jdpadillac.jtaskboard.tasks.domain.usecase.UpdateTaskStatusUseCase;
import com.jdpadillac.jtaskboard.tasks.domain.usecase.command.UpdateTaskStatusCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateTaskStatusUseCaseImpl implements UpdateTaskStatusUseCase {

    private final FindTaskByIdPort findTaskByIdPort;
    private final SaveTaskPort saveTaskPort;

    @Override
    public JTask updateStatus(UpdateTaskStatusCommand command) {
        JTask existingTask = findTaskByIdPort.findById(command.id())
                .orElseThrow(() -> new TaskNotFoundException(command.id()));

        JTask updatedTask = new JTask(
                existingTask.id(),
                existingTask.taskKey(),
                existingTask.title(),
                existingTask.description(),
                command.status(),
                existingTask.createdAt(),
                existingTask.deletedAt()
        );

        return saveTaskPort.save(updatedTask);
    }
}

