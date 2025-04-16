package ru.practicum.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.User;

/**
 * The interface User short mapper.
 */
@Mapper(componentModel = "spring")
public interface UserShortMapper {

    /**
     * To user short dto user short dto.
     *
     * @param user the user
     * @return the user short dto
     */
    UserShortDto toUserShortDto(User user);

    /**
     * To user user.
     *
     * @param userShortDto the user short dto
     * @return the user
     */
    @Mapping(target = "email", ignore = true)
    User toUser(UserShortDto userShortDto);
}
