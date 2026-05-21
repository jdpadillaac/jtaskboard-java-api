package com.jdpadillac.jtaskboard.tasks.domain.port.out;

import com.jdpadillac.jtaskboard.tasks.domain.model.Task;

public interface SaveTaskPort {

    Task save(Task task);
}

