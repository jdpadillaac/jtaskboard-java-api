package com.jdpadillac.jtaskboard.tasks.domain.port.out;

public interface ExistsTaskByKeyPort {

    boolean existsByTaskKey(String taskKey);
}

