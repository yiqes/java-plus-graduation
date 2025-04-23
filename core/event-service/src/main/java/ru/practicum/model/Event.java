package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.user.UserDto;
import ru.practicum.enums.EventState;

import java.time.LocalDateTime;

/**
 * The type Event.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"})
@Table(name = "EVENTS")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {

    @Id
    @Column(name = "EVENT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(name = "ANNOTATION")
    String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID")
    Category category;

    @Transient
    Long confirmedRequests;

    @Column(name = "CREATED_ON")
    LocalDateTime createOn;

    @Column(name = "DESCRIPTION")
    String description;

    @Column(name = "EVENT_DATE")
    LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "LOCATION_ID")
    Location location;

    @Column(name = "PAID")
    Boolean paid;

    @Column(name = "INITIATOR_ID")
    Long initiatorId;

    @Transient
    UserDto initiator;

    @Column(name = "PARTICIPANT_LIMIT")
    Long participantLimit;

    @Column(name = "PUBLISHED_ON")
    LocalDateTime publishedOn;

    @Column(name = "REQUEST_MODERATION")
    Boolean requestModeration;

    @Column(name = "STATE")
    @Enumerated(EnumType.STRING)
    EventState state;

    @Column(name = "TITLE")
    String title;

    @Transient
    Long views;

    @Transient
    Long likes;
}
