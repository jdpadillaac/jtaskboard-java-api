package com.jdpadillac.jtaskboard.auth.domain.port.out;

public interface ExistsUserByEmailPort {

    boolean existsByEmail(String email);
}
