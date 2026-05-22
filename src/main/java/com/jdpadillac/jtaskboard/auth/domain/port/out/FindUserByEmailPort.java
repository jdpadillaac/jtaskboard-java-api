package com.jdpadillac.jtaskboard.auth.domain.port.out;

import com.jdpadillac.jtaskboard.auth.domain.model.User;
import java.util.Optional;

public interface FindUserByEmailPort {

    Optional<User> findByEmail(String email);
}
