package com.jdpadillac.jtaskboard.tasks.infrastructure.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskJpaRepository extends JpaRepository<TaskEntity, UUID> {

    boolean existsByTaskKey(String taskKey);

    List<TaskEntity> findByDeletedAtIsNull(Sort sort);

    Optional<TaskEntity> findByIdAndDeletedAtIsNull(UUID id);
}

