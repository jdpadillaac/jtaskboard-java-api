package com.jdpadillac.jtaskboard.tasks.infrastructure.out.adapters;

import com.jdpadillac.jtaskboard.tasks.domain.model.Task;
import com.jdpadillac.jtaskboard.tasks.domain.port.out.ExistsTaskByKeyPort;
import com.jdpadillac.jtaskboard.tasks.domain.port.out.SaveTaskPort;
import com.jdpadillac.jtaskboard.tasks.infrastructure.out.persistence.TaskEntity;
import com.jdpadillac.jtaskboard.tasks.infrastructure.out.persistence.TaskJpaRepository;
import com.jdpadillac.jtaskboard.tasks.infrastructure.out.persistence.mapper.TaskPersistenceMapper;
import org.springframework.stereotype.Component;

@Component
public class TaskPersistenceAdapter implements SaveTaskPort, ExistsTaskByKeyPort {

    private final TaskJpaRepository taskJpaRepository;
    private final TaskPersistenceMapper taskPersistenceMapper;

    public TaskPersistenceAdapter(TaskJpaRepository taskJpaRepository, TaskPersistenceMapper taskPersistenceMapper) {
        this.taskJpaRepository = taskJpaRepository;
        this.taskPersistenceMapper = taskPersistenceMapper;
    }

    @Override
    public Task save(Task task) {
        TaskEntity savedEntity = taskJpaRepository.save(taskPersistenceMapper.toEntity(task));
        return taskPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public boolean existsByTaskKey(String taskKey) {
        return taskJpaRepository.existsByTaskKey(taskKey);
    }
}

