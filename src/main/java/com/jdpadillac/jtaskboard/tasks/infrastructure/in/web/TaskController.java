package com.jdpadillac.jtaskboard.tasks.infrastructure.in.web;

import com.jdpadillac.jtaskboard.tasks.domain.model.JTask;
import com.jdpadillac.jtaskboard.tasks.domain.usecase.CreateTaskUseCase;
import com.jdpadillac.jtaskboard.tasks.domain.usecase.ListTasksUseCase;
import com.jdpadillac.jtaskboard.tasks.domain.usecase.UpdateTaskUseCase;
import com.jdpadillac.jtaskboard.tasks.infrastructure.in.web.dto.CreateTaskRequest;
import com.jdpadillac.jtaskboard.tasks.infrastructure.in.web.dto.TaskResponse;
import com.jdpadillac.jtaskboard.tasks.infrastructure.in.web.dto.UpdateTaskRequest;
import com.jdpadillac.jtaskboard.tasks.infrastructure.in.web.mapper.TaskWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final CreateTaskUseCase createTaskUseCase;
    private final ListTasksUseCase listTasksUseCase;
    private final UpdateTaskUseCase updateTaskUseCase;
    private final TaskWebMapper taskWebMapper;

    @PostMapping
    public ResponseEntity<TaskResponse> create(@Validated @RequestBody CreateTaskRequest request) {
        JTask created = createTaskUseCase.create(taskWebMapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(taskWebMapper.toResponse(created));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> list() {
        List<JTask> tasks = listTasksUseCase.list();
        return ResponseEntity.ok(taskWebMapper.toResponseList(tasks));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(
            @PathVariable UUID id,
            @Validated @RequestBody UpdateTaskRequest request
    ) {
        JTask updated = updateTaskUseCase.update(taskWebMapper.toCommand(id, request));
        return ResponseEntity.ok(taskWebMapper.toResponse(updated));
    }
}


