package com.jdpadillac.jtaskboard.tasks.infrastructure.in.web.mapper;

import com.jdpadillac.jtaskboard.tasks.domain.model.JTask;
import com.jdpadillac.jtaskboard.tasks.domain.usecase.command.CreateTaskCommand;
import com.jdpadillac.jtaskboard.tasks.domain.usecase.command.UpdateTaskCommand;
import com.jdpadillac.jtaskboard.tasks.domain.usecase.command.UpdateTaskStatusCommand;
import com.jdpadillac.jtaskboard.tasks.infrastructure.in.web.dto.CreateTaskRequest;
import com.jdpadillac.jtaskboard.tasks.infrastructure.in.web.dto.TaskResponse;
import com.jdpadillac.jtaskboard.tasks.infrastructure.in.web.dto.UpdateTaskRequest;
import com.jdpadillac.jtaskboard.tasks.infrastructure.in.web.dto.UpdateTaskStatusRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface TaskWebMapper {

    CreateTaskCommand toCommand(CreateTaskRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "request.title")
    @Mapping(target = "description", source = "request.description")
    UpdateTaskCommand toCommand(UUID id, UpdateTaskRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "status", source = "request.status")
    UpdateTaskStatusCommand toStatusCommand(UUID id, UpdateTaskStatusRequest request);

    TaskResponse toResponse(JTask task);

    List<TaskResponse> toResponseList(List<JTask> tasks);
}
