package com.jdpadillac.jtaskboard.tasks.infrastructure.in.web.mapper;

import com.jdpadillac.jtaskboard.tasks.domain.model.JTask;
import com.jdpadillac.jtaskboard.tasks.domain.usecase.command.CreateTaskCommand;
import com.jdpadillac.jtaskboard.tasks.infrastructure.in.web.dto.CreateTaskRequest;
import com.jdpadillac.jtaskboard.tasks.infrastructure.in.web.dto.TaskResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskWebMapper {

    CreateTaskCommand toCommand(CreateTaskRequest request);

    TaskResponse toResponse(JTask task);

    List<TaskResponse> toResponseList(List<JTask> tasks);
}
