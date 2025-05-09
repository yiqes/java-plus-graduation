package ru.practicum.handler;


import ru.practicum.grpc.stats.action.UserActionMessage;

public interface ActionsHandlers {
    void handle(UserActionMessage.UserActionRequest userActionProto);
}