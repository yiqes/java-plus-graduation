package ru.practicum.mapper;

import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;

public interface UserMapper {
    User toUser(NewUserRequest newUserRequest);

    User toUser(UserDto userDto);

    UserDto toUserDto(User user);

    UserShortDto toUserShortDto(User user);
}
