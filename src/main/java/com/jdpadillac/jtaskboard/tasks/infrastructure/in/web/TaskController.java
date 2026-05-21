package com.jdpadillac.jtaskboard.tasks.infrastructure.in.web;

import com.jdpadillac.jtaskboard.tasks.domain.model.Task;
import com.jdpadillac.jtaskboard.tasks.domain.usecase.CreateTaskUseCase;
import com.jdpadillac.jtaskboard.tasks.infrastructure.in.web.dto.CreateTaskRequest;
import com.jdpadillac.jtaskboard.tasks.infrastructure.in.web.dto.TaskResponse;
import com.jdpadillac.jtaskboard.tasks.infrastructure.in.web.mapper.TaskWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final CreateTaskUseCase createTaskUseCase;
    private final TaskWebMapper taskWebMapper;

    @PostMapping
    public ResponseEntity<TaskResponse> create(@Validated @RequestBody CreateTaskRequest request) {
        Task created = createTaskUseCase.create(taskWebMapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(taskWebMapper.toResponse(created));
    }
}


