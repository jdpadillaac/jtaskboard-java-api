package com.jdpadillac.jtaskboard.auth.domain.port.out;

import com.jdpadillac.jtaskboard.auth.domain.model.User;

public interface GenerateTokenPort {

    String generate(User user);
}
