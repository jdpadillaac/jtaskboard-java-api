package com.jdpadillac.jtaskboard.tasks.domain.usecase.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jdpadillac.jtaskboard.tasks.domain.model.Task;
import com.jdpadillac.jtaskboard.tasks.domain.model.TaskStatus;
import com.jdpadillac.jtaskboard.tasks.domain.port.out.ExistsTaskByKeyPort;
import com.jdpadillac.jtaskboard.tasks.domain.port.out.GenerateTaskKeyPort;
import com.jdpadillac.jtaskboard.tasks.domain.port.out.SaveTaskPort;
import com.jdpadillac.jtaskboard.tasks.domain.usecase.command.CreateTaskCommand;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class CreateTaskUseCaseImplTest {

    private final SaveTaskPort saveTaskPort = Mockito.mock(SaveTaskPort.class);
    private final ExistsTaskByKeyPort existsTaskByKeyPort = Mockito.mock(ExistsTaskByKeyPort.class);
    private final GenerateTaskKeyPort generateTaskKeyPort = Mockito.mock(GenerateTaskKeyPort.class);

    private final CreateTaskUseCaseImpl useCase = new CreateTaskUseCaseImpl(
            saveTaskPort,
            existsTaskByKeyPort,
            generateTaskKeyPort
    );

    @Test
    void shouldCreateTaskWithDefaultStatusTodo() {
        CreateTaskCommand command = new CreateTaskCommand("Configurar CI", "Pipeline con GitHub Actions");

        when(generateTaskKeyPort.generate()).thenReturn("TASK-A1B2C3");
        when(existsTaskByKeyPort.existsByTaskKey("TASK-A1B2C3")).thenReturn(false);
        when(saveTaskPort.save(any(Task.class))).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            return new Task(UUID.randomUUID(), task.taskKey(), task.title(), task.description(), task.status(), task.createdAt());
        });

        Task result = useCase.create(command);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(saveTaskPort).save(taskCaptor.capture());
        Task saved = taskCaptor.getValue();

        assertThat(saved.id()).isNull();
        assertThat(saved.taskKey()).isEqualTo("TASK-A1B2C3");
        assertThat(saved.title()).isEqualTo("Configurar CI");
        assertThat(saved.description()).isEqualTo("Pipeline con GitHub Actions");
        assertThat(saved.status()).isEqualTo(TaskStatus.TODO);
        assertThat(saved.createdAt()).isBeforeOrEqualTo(Instant.now());

        assertThat(result.id()).isNotNull();
        assertThat(result.status()).isEqualTo(TaskStatus.TODO);
    }

    @Test
    void shouldRetryWhenTaskKeyCollides() {
        CreateTaskCommand command = new CreateTaskCommand("Task", "Desc");

        when(generateTaskKeyPort.generate()).thenReturn("TASK-AAAAAA", "TASK-BBBBBB");
        when(existsTaskByKeyPort.existsByTaskKey("TASK-AAAAAA")).thenReturn(true);
        when(existsTaskByKeyPort.existsByTaskKey("TASK-BBBBBB")).thenReturn(false);
        when(saveTaskPort.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = useCase.create(command);

        assertThat(result.taskKey()).isEqualTo("TASK-BBBBBB");
        verify(generateTaskKeyPort, times(2)).generate();
    }
}

