package com.jdpadillac.jtaskboard.tasks.domain.usecase.impl;

import com.jdpadillac.jtaskboard.tasks.domain.exception.TaskNotFoundException;
import com.jdpadillac.jtaskboard.tasks.domain.model.JTask;
import com.jdpadillac.jtaskboard.tasks.domain.port.out.FindTaskByIdPort;
import com.jdpadillac.jtaskboard.tasks.domain.port.out.SaveTaskPort;
import com.jdpadillac.jtaskboard.tasks.domain.usecase.DeleteTaskUseCase;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteTaskUseCaseImpl implements DeleteTaskUseCase {

    private final FindTaskByIdPort findTaskByIdPort;
    private final SaveTaskPort saveTaskPort;

    @Override
    public void delete(UUID id) {
        JTask existingTask = findTaskByIdPort.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        JTask deletedTask = new JTask(
                existingTask.id(),
                existingTask.taskKey(),
                existingTask.title(),
                existingTask.description(),
                existingTask.status(),
                existingTask.createdAt(),
                Instant.now()
        );

        saveTaskPort.save(deletedTask);
    }
}

