package ru.practicum.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.model.UserAction;
import ru.practicum.repository.UserActionRepository;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserActionServiceImpl implements UserActionService {

    private final UserActionRepository userActionRepository;

    @Override
    public void updateUserAction(UserActionAvro userActionAvro) {
        long userId = userActionAvro.getUserId();
        long eventId = userActionAvro.getEventId();
        double newWeight = convertWeight(userActionAvro.getActionType());
        long timestamp = userActionAvro.getTimestamp();

        Instant interactionTime = Instant.ofEpochMilli(timestamp);

        UserAction userAction = userActionRepository.findByUserIdAndEventId(userId, eventId);

        if (userAction == null) {
            userAction = new UserAction();
            userAction.setUserId(userId);
            userAction.setEventId(eventId);
            userAction.setMaxWeight(newWeight);
            userAction.setLastInteraction(interactionTime);
            userActionRepository.save(userAction);
            return;
        }
        if (newWeight > userAction.getMaxWeight()) {
            userAction.setMaxWeight(newWeight);
        }
        if (interactionTime.isAfter(userAction.getLastInteraction())) {
            userAction.setLastInteraction(interactionTime);
        }
        userActionRepository.save(userAction);


    }

    private double convertWeight(ActionTypeAvro actionType) {
        return switch (actionType) {
            case REGISTER -> 0.8;
            case LIKE -> 1;
            default -> 0.4;
        };
    }
}
