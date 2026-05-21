package com.jdpadillac.jtaskboard.tasks.infrastructure.out.adapters;

import com.jdpadillac.jtaskboard.tasks.domain.model.JTask;
import com.jdpadillac.jtaskboard.tasks.domain.port.out.ExistsTaskByKeyPort;
import com.jdpadillac.jtaskboard.tasks.domain.port.out.FindAllTasksPort;
import com.jdpadillac.jtaskboard.tasks.domain.port.out.SaveTaskPort;
import com.jdpadillac.jtaskboard.tasks.infrastructure.out.persistence.TaskEntity;
import com.jdpadillac.jtaskboard.tasks.infrastructure.out.persistence.TaskJpaRepository;
import com.jdpadillac.jtaskboard.tasks.infrastructure.out.persistence.mapper.TaskPersistenceMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskPersistenceAdapter implements SaveTaskPort, ExistsTaskByKeyPort, FindAllTasksPort {

    private final TaskJpaRepository taskJpaRepository;
    private final TaskPersistenceMapper taskPersistenceMapper;

    public TaskPersistenceAdapter(TaskJpaRepository taskJpaRepository, TaskPersistenceMapper taskPersistenceMapper) {
        this.taskJpaRepository = taskJpaRepository;
        this.taskPersistenceMapper = taskPersistenceMapper;
    }

    @Override
    public JTask save(JTask task) {
        TaskEntity savedEntity = taskJpaRepository.save(taskPersistenceMapper.toEntity(task));
        return taskPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public boolean existsByTaskKey(String taskKey) {
        return taskJpaRepository.existsByTaskKey(taskKey);
    }

    @Override
    public List<JTask> findAll() {
        return taskJpaRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(taskPersistenceMapper::toDomain)
                .toList();
    }
}

