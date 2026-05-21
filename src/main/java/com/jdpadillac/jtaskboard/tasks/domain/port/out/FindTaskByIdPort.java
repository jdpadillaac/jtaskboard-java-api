package com.jdpadillac.jtaskboard.tasks.domain.port.out;

import com.jdpadillac.jtaskboard.tasks.domain.model.JTask;
import java.util.Optional;
import java.util.UUID;

public interface FindTaskByIdPort {

    Optional<JTask> findById(UUID id);
}

