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
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class DeleteTaskUseCaseImplTest {

    private final FindTaskByIdPort findTaskByIdPort = Mockito.mock(FindTaskByIdPort.class);
    private final SaveTaskPort saveTaskPort = Mockito.mock(SaveTaskPort.class);

    private final DeleteTaskUseCaseImpl useCase = new DeleteTaskUseCaseImpl(findTaskByIdPort, saveTaskPort);

    @Test
    void shouldSoftDeleteTask() {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-05-21T11:00:00Z");
        JTask existingTask = new JTask(id, "TASK-X1Y2Z3", "Title", "Desc", TaskStatus.TODO, createdAt, null);

        when(findTaskByIdPort.findById(id)).thenReturn(Optional.of(existingTask));
        when(saveTaskPort.save(any(JTask.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.delete(id);

        ArgumentCaptor<JTask> taskCaptor = ArgumentCaptor.forClass(JTask.class);
        verify(saveTaskPort).save(taskCaptor.capture());
        JTask saved = taskCaptor.getValue();

        assertThat(saved.id()).isEqualTo(id);
        assertThat(saved.taskKey()).isEqualTo("TASK-X1Y2Z3");
        assertThat(saved.title()).isEqualTo("Title");
        assertThat(saved.description()).isEqualTo("Desc");
        assertThat(saved.status()).isEqualTo(TaskStatus.TODO);
        assertThat(saved.createdAt()).isEqualTo(createdAt);
        assertThat(saved.deletedAt()).isNotNull();
    }

    @Test
    void shouldThrowWhenTaskNotFound() {
        UUID id = UUID.randomUUID();

        when(findTaskByIdPort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.delete(id))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task not found: " + id);

        verify(saveTaskPort, never()).save(any());
    }
}

