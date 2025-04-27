package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.RequestStatus;

import java.time.LocalDateTime;

@Entity
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Table(name = "REQUESTS")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REQUEST_ID")
    Long id;

    @Column(name = "EVENT_ID")
    Long eventId;

    @Column(name = "REQUESTER_ID")
    Long requesterId;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    RequestStatus status;

    @Column(name = "CREATED")
    LocalDateTime created;

}