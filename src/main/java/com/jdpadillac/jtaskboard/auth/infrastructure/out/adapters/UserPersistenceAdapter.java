package com.jdpadillac.jtaskboard.auth.infrastructure.out.adapters;

import com.jdpadillac.jtaskboard.auth.domain.model.User;
import com.jdpadillac.jtaskboard.auth.domain.port.out.ExistsUserByEmailPort;
import com.jdpadillac.jtaskboard.auth.domain.port.out.FindUserByEmailPort;
import com.jdpadillac.jtaskboard.auth.domain.port.out.SaveUserPort;
import com.jdpadillac.jtaskboard.auth.infrastructure.out.persistence.UserEntity;
import com.jdpadillac.jtaskboard.auth.infrastructure.out.persistence.UserJpaRepository;
import com.jdpadillac.jtaskboard.auth.infrastructure.out.persistence.mapper.UserPersistenceMapper;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceAdapter implements SaveUserPort, FindUserByEmailPort, ExistsUserByEmailPort {

    private final UserJpaRepository userJpaRepository;
    private final UserPersistenceMapper userPersistenceMapper;

    public UserPersistenceAdapter(UserJpaRepository userJpaRepository, UserPersistenceMapper userPersistenceMapper) {
        this.userJpaRepository = userJpaRepository;
        this.userPersistenceMapper = userPersistenceMapper;
    }

    @Override
    public User save(User user) {
        UserEntity savedEntity = userJpaRepository.save(userPersistenceMapper.toEntity(user));
        return userPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(userPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }
}
