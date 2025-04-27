package ru.practicum.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User toUser(NewUserRequest newUserRequest);

    User toUser(UserDto userDto);

    UserDto toUserDto(User user);

    UserShortDto toUserShortDto(User user);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", ignore = true)
    UserShortDto toUserShortDto(Long id);

    UserShortDto toUserShortDto(UserDto userDto);

}
