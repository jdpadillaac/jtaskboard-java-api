package com.jdpadillac.jtaskboard.auth.infrastructure.out.persistence.mapper;

import com.jdpadillac.jtaskboard.auth.domain.model.User;
import com.jdpadillac.jtaskboard.auth.infrastructure.out.persistence.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    UserEntity toEntity(User user);

    User toDomain(UserEntity entity);
}
