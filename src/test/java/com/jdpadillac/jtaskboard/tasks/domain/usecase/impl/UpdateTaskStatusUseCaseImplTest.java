package com.jdpadillac.jtaskboard.tasks.domain.usecase.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jdpadillac.jtaskboard.tasks.domain.exception.TaskNotFoundException;
import com.jdpadillac.jtaskboard.tasks.domain.model.JTask;
import com.jdpadillac.jtaskboard.tasks.domain.model.TaskStatus;
import com.jdpadillac.jtaskboard.tasks.domain.port.out.FindTaskByIdPort;
import com.jdpadillac.jtaskboard.tasks.domain.port.out.SaveTaskPort;
import com.jdpadillac.jtaskboard.tasks.domain.usecase.command.UpdateTaskStatusCommand;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class UpdateTaskStatusUseCaseImplTest {

    private final FindTaskByIdPort findTaskByIdPort = Mockito.mock(FindTaskByIdPort.class);
    private final SaveTaskPort saveTaskPort = Mockito.mock(SaveTaskPort.class);

    private final UpdateTaskStatusUseCaseImpl useCase = new UpdateTaskStatusUseCaseImpl(findTaskByIdPort, saveTaskPort);

    @Test
    void shouldUpdateStatusAndKeepOtherFields() {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-05-21T11:00:00Z");
        Instant deletedAt = Instant.parse("2026-05-22T11:00:00Z");
        JTask existingTask = new JTask(id, "TASK-X1Y2Z3", "Old", "Old desc", TaskStatus.TODO, createdAt, deletedAt);
        UpdateTaskStatusCommand command = new UpdateTaskStatusCommand(id, TaskStatus.DONE);

        when(findTaskByIdPort.findById(id)).thenReturn(Optional.of(existingTask));
        when(saveTaskPort.save(any(JTask.class))).thenAnswer(invocation -> invocation.getArgument(0));

        JTask updated = useCase.updateStatus(command);

        ArgumentCaptor<JTask> taskCaptor = ArgumentCaptor.forClass(JTask.class);
        verify(saveTaskPort).save(taskCaptor.capture());
        JTask saved = taskCaptor.getValue();

        assertThat(saved.id()).isEqualTo(id);
        assertThat(saved.taskKey()).isEqualTo("TASK-X1Y2Z3");
        assertThat(saved.title()).isEqualTo("Old");
        assertThat(saved.description()).isEqualTo("Old desc");
        assertThat(saved.status()).isEqualTo(TaskStatus.DONE);
        assertThat(saved.createdAt()).isEqualTo(createdAt);
        assertThat(saved.deletedAt()).isEqualTo(deletedAt);
        assertThat(updated).isEqualTo(saved);
    }

    @Test
    void shouldThrowWhenTaskNotFound() {
        UUID id = UUID.randomUUID();
        UpdateTaskStatusCommand command = new UpdateTaskStatusCommand(id, TaskStatus.IN_PROGRESS);

        when(findTaskByIdPort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.updateStatus(command))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task not found: " + id);

        verify(saveTaskPort, never()).save(any());
    }
}

