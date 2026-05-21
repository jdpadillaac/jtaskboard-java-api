package com.jdpadillac.jtaskboard.tasks.infrastructure.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jdpadillac.jtaskboard.tasks.domain.model.Task;
import com.jdpadillac.jtaskboard.tasks.domain.model.TaskStatus;
import com.jdpadillac.jtaskboard.tasks.domain.usecase.CreateTaskUseCase;
import com.jdpadillac.jtaskboard.tasks.infrastructure.in.web.mapper.TaskWebMapper;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(TaskController.class)
@Import(TaskWebMapper.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateTaskUseCase createTaskUseCase;

    @Test
    void shouldReturn201WhenRequestIsValid() throws Exception {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-05-21T10:30:00Z");

        Task createdTask = new Task(
                id,
                "TASK-A7X2K9",
                "Configurar CI",
                "Pipeline con GitHub Actions",
                TaskStatus.TODO,
                createdAt
        );

        when(createTaskUseCase.create(any())).thenReturn(createdTask);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Configurar CI\",\"description\":\"Pipeline con GitHub Actions\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.taskKey").value("TASK-A7X2K9"))
                .andExpect(jsonPath("$.status").value("TODO"));

        verify(createTaskUseCase).create(any());
    }

    @Test
    void shouldReturn400WhenTitleIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"   \",\"description\":\"desc\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenDescriptionExceedsLimit() throws Exception {
        String tooLongDescription = "a".repeat(32768);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Titulo\",\"description\":\"" + tooLongDescription + "\"}"))
                .andExpect(status().isBadRequest());
    }
}


