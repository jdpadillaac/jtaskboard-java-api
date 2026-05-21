package com.jdpadillac.jtaskboard.tasks.infrastructure.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateTaskRequest(
        @NotBlank(message = "title must not be blank")
        @Size(max = 100, message = "title must not exceed 100 characters")
        String title,

        @Size(max = 32767, message = "description must not exceed 32767 characters")
        String description
) {
}

