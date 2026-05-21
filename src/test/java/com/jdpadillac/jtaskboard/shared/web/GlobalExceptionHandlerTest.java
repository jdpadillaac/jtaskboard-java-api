package com.jdpadillac.jtaskboard.shared.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.jdpadillac.jtaskboard.tasks.domain.exception.TaskNotFoundException;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldReturn404WhenTaskNotFoundExceptionIsHandled() {
        UUID id = UUID.randomUUID();

        ResponseEntity<Map<String, Object>> response = handler.handleTaskNotFound(new TaskNotFoundException(id));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(404);
        assertThat(response.getBody().get("error")).isEqualTo("Not Found");
        assertThat(response.getBody().get("message")).isEqualTo("Task not found: " + id);
        assertThat(response.getBody().get("timestamp")).isNotNull();
    }
}

