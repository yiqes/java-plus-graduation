package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "similarities")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventSimilarity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "event_id_a")
    Long eventA;
    @Column(name = "event_id_b")
    Long eventB;
    @Column(name = "score")
    Double score;
    @Column(name = "timestamp")
    Instant timestamp;
}