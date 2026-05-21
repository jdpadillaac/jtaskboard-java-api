package com.jdpadillac.jtaskboard.tasks.domain.usecase.impl;

import com.jdpadillac.jtaskboard.tasks.domain.model.Task;
import com.jdpadillac.jtaskboard.tasks.domain.model.TaskStatus;
import com.jdpadillac.jtaskboard.tasks.domain.port.out.ExistsTaskByKeyPort;
import com.jdpadillac.jtaskboard.tasks.domain.port.out.GenerateTaskKeyPort;
import com.jdpadillac.jtaskboard.tasks.domain.port.out.SaveTaskPort;
import com.jdpadillac.jtaskboard.tasks.domain.usecase.CreateTaskUseCase;
import com.jdpadillac.jtaskboard.tasks.domain.usecase.command.CreateTaskCommand;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateTaskUseCaseImpl implements CreateTaskUseCase {

    private static final int MAX_KEY_GENERATION_ATTEMPTS = 10;

    private final SaveTaskPort saveTaskPort;
    private final ExistsTaskByKeyPort existsTaskByKeyPort;
    private final GenerateTaskKeyPort generateTaskKeyPort;

    @Override
    public Task create(CreateTaskCommand command) {
        String taskKey = generateUniqueTaskKey();

        Task taskToCreate = new Task(
                null,
                taskKey,
                command.title(),
                command.description(),
                TaskStatus.TODO,
                Instant.now()
        );

        return saveTaskPort.save(taskToCreate);
    }

    private String generateUniqueTaskKey() {
        for (int attempt = 0; attempt < MAX_KEY_GENERATION_ATTEMPTS; attempt++) {
            String candidate = generateTaskKeyPort.generate();
            if (!existsTaskByKeyPort.existsByTaskKey(candidate)) {
                return candidate;
            }
        }

        throw new IllegalStateException("Could not generate a unique task key");
    }
}


