package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.RequestStatus;

import java.time.LocalDateTime;


/**
 * The type Request.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Request {

    @Id
    @Column(name = "request_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "event_id")

    Long eventId;

    @Column(name = "requester_id")
    Long requesterId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    RequestStatus status;

    @Column(name = "created", nullable = false)
    LocalDateTime created;
}