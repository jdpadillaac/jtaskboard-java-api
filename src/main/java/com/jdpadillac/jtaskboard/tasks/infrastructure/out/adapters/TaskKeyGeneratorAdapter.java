package com.jdpadillac.jtaskboard.tasks.infrastructure.out.adapters;

import com.jdpadillac.jtaskboard.tasks.domain.port.out.GenerateTaskKeyPort;
import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class TaskKeyGeneratorAdapter implements GenerateTaskKeyPort {

    private static final String PREFIX = "TASK-";
    private static final int RANDOM_PART_LENGTH = 6;
    private static final char[] ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generate() {
        StringBuilder randomPart = new StringBuilder(RANDOM_PART_LENGTH);
        for (int i = 0; i < RANDOM_PART_LENGTH; i++) {
            randomPart.append(ALPHANUMERIC[secureRandom.nextInt(ALPHANUMERIC.length)]);
        }
        return PREFIX + randomPart;
    }
}

