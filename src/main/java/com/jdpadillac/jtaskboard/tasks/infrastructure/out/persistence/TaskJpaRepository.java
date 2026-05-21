package com.jdpadillac.jtaskboard.tasks.infrastructure.out.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskJpaRepository extends JpaRepository<TaskEntity, UUID> {

    boolean existsByTaskKey(String taskKey);
}

