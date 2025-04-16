package ru.practicum.mapper.user;

import org.mapstruct.Mapper;
import ru.practicum.dto.user.UserDto;
import ru.practicum.model.User;

import java.util.List;

/**
 * The interface User mapper.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    /**
     * To user dto user dto.
     *
     * @param user the user
     * @return the user dto
     */
    UserDto toUserDto(User user);

    /**
     * To user dtos list.
     *
     * @param userList the user list
     * @return the list
     */
    List<UserDto> toUserDtos(List<User> userList);
}
