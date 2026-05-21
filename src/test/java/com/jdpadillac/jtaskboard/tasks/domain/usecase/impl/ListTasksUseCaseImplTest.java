package com.jdpadillac.jtaskboard.tasks.domain.usecase.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.jdpadillac.jtaskboard.tasks.domain.model.JTask;
import com.jdpadillac.jtaskboard.tasks.domain.model.TaskStatus;
import com.jdpadillac.jtaskboard.tasks.domain.port.out.FindAllTasksPort;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ListTasksUseCaseImplTest {

    private final FindAllTasksPort findAllTasksPort = Mockito.mock(FindAllTasksPort.class);
    private final ListTasksUseCaseImpl useCase = new ListTasksUseCaseImpl(findAllTasksPort);

    @Test
    void shouldReturnTasksFromPort() {
        JTask task = new JTask(UUID.randomUUID(), "TASK-A1B2C3", "Title", "Desc", TaskStatus.TODO, Instant.now());
        when(findAllTasksPort.findAll()).thenReturn(List.of(task));

        List<JTask> result = useCase.list();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).taskKey()).isEqualTo("TASK-A1B2C3");
    }

    @Test
    void shouldReturnEmptyListWhenNoTasks() {
        when(findAllTasksPort.findAll()).thenReturn(List.of());

        List<JTask> result = useCase.list();

        assertThat(result).isEmpty();
    }
}

