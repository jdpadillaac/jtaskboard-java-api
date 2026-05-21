package com.jdpadillac.jtaskboard.tasks.infrastructure.in.web;

import com.jdpadillac.jtaskboard.tasks.domain.model.JTask;
import com.jdpadillac.jtaskboard.tasks.domain.usecase.CreateTaskUseCase;
import com.jdpadillac.jtaskboard.tasks.domain.usecase.ListTasksUseCase;
import com.jdpadillac.jtaskboard.tasks.infrastructure.in.web.dto.CreateTaskRequest;
import com.jdpadillac.jtaskboard.tasks.infrastructure.in.web.dto.TaskResponse;
import com.jdpadillac.jtaskboard.tasks.infrastructure.in.web.mapper.TaskWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final CreateTaskUseCase createTaskUseCase;
    private final ListTasksUseCase listTasksUseCase;
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
}


