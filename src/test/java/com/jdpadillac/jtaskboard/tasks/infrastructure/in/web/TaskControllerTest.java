package com.jdpadillac.jtaskboard.tasks.infrastructure.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jdpadillac.jtaskboard.tasks.domain.model.JTask;
import com.jdpadillac.jtaskboard.tasks.domain.model.TaskStatus;
import com.jdpadillac.jtaskboard.tasks.domain.usecase.CreateTaskUseCase;
import com.jdpadillac.jtaskboard.tasks.domain.usecase.ListTasksUseCase;
import com.jdpadillac.jtaskboard.tasks.infrastructure.in.web.mapper.TaskWebMapper;
import java.time.Instant;
import java.util.List;
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

    @MockitoBean
    private ListTasksUseCase listTasksUseCase;

    @Test
    void shouldReturn201WhenRequestIsValid() throws Exception {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-05-21T10:30:00Z");

        JTask createdTask = new JTask(
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

    @Test
    void shouldReturn200WithTaskListOnGet() throws Exception {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-05-21T10:15:30Z");
        JTask task = new JTask(id, "TASK-A1B2C3", "Configurar CI", "Pipeline con GitHub Actions", TaskStatus.TODO, createdAt);
        when(listTasksUseCase.list()).thenReturn(List.of(task));

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id.toString()))
                .andExpect(jsonPath("$[0].taskKey").value("TASK-A1B2C3"))
                .andExpect(jsonPath("$[0].status").value("TODO"));
    }

    @Test
    void shouldReturn200WithEmptyArrayWhenNoTasks() throws Exception {
        when(listTasksUseCase.list()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}


