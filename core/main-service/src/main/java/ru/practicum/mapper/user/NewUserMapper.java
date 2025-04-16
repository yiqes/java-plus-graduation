package ru.practicum.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.model.User;

/**
 * The interface New user mapper.
 */
@Mapper(componentModel = "spring")
public interface NewUserMapper {
    /**
     * From new user request user.
     *
     * @param newUserRequest the new user request
     * @return the user
     */
    @Mapping(target = "id", ignore = true)
    User fromNewUserRequest(NewUserRequest newUserRequest);
}
