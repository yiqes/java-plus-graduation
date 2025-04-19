package ru.practicum.service.user;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.controller.admin.AdminUsersGetAllParams;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.user.NewUserMapper;
import ru.practicum.mapper.user.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.Optional;

/**
 * The type User service.
 */
@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserMapper userMapper;
    UserRepository userRepository;
    NewUserMapper newUserRequestMapper;

    @Override
    public UserDto add(NewUserRequest newUserRequest) {
        Optional<User> userByEmail = userRepository.findByEmail(newUserRequest.getEmail());
        if (userByEmail.isPresent()) {
            throw new ConflictException("could not execute statement; SQL [n/a]; constraint [uq_email]; " +
                    "nested exception is org.hibernate.exception.ConstraintViolationException: could not execute " +
                    "statement", "Integrity constraint has been violated.");
        }
        User user = newUserRequestMapper.fromNewUserRequest(newUserRequest);
        user = userRepository.save(user);
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(AdminUsersGetAllParams adminUsersGetAllParams) {
        PageRequest pageRequest = PageRequest.of(
                adminUsersGetAllParams.from(), adminUsersGetAllParams.size());
        List<User> userSearchList = adminUsersGetAllParams.ids() == null
                ? userRepository.findAll(pageRequest).stream().toList()
                : userRepository.findAllByIdIn(adminUsersGetAllParams.ids(), pageRequest);

        return userSearchList.stream()
                .map(userMapper::toUserDto).toList();
    }

    @Override
    public void delete(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found",
                        "The required object was not found."));
        userRepository.deleteById(userId);
    }
}
