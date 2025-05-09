package ru.practicum.handler;

import ru.practicum.ewm.stats.proto.UserActionProto;

public interface ActionsHandlers {
    void handle(UserActionProto userActionProto);
}