package com.jdpadillac.jtaskboard.tasks.infrastructure.out.persistence.mapper;

import com.jdpadillac.jtaskboard.tasks.domain.model.JTask;
import com.jdpadillac.jtaskboard.tasks.infrastructure.out.persistence.TaskEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskPersistenceMapper {

    TaskEntity toEntity(JTask task);

    JTask toDomain(TaskEntity entity);
}
