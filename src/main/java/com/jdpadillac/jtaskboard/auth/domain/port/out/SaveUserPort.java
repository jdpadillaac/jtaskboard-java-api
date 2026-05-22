package com.jdpadillac.jtaskboard.auth.domain.port.out;

import com.jdpadillac.jtaskboard.auth.domain.model.User;

public interface SaveUserPort {

    User save(User user);
}
